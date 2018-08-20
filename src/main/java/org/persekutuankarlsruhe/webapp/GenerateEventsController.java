package org.persekutuankarlsruhe.webapp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.persekutuankarlsruhe.webapp.calendar.CalendarDataProvider;
import org.persekutuankarlsruhe.webapp.calendar.CalendarUtil;
import org.persekutuankarlsruhe.webapp.calendar.NoJadwalSelectedException;
import org.persekutuankarlsruhe.webapp.service.CacheManager;
import org.persekutuankarlsruhe.webapp.service.EmptyCacheException;
import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.SheetsDataProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.api.services.calendar.model.Event;
import com.google.apphosting.api.ApiProxy;

@Controller
public class GenerateEventsController {

    private static final Logger LOG = Logger.getLogger(GenerateEventsController.class.getName());

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    SheetsDataProvider sheetsDataProvider;
    CalendarDataProvider calendarDataProvider;
    CacheManager cacheManager;

    @RequestMapping(value = "/calendargen", method = RequestMethod.GET)
    public String generateEvents(HttpServletRequest request, Model model) throws Exception {

        model.addAttribute("timeZone", CalendarUtil.getTimeZone());
        try {

            sheetsDataProvider = getSheetsDataProviderForPersekutuan();

            List<JadwalPelayanan> daftarJadwalPelayanan = sheetsDataProvider.getDaftarJadwalPelayananFromSheets();
            boolean overwriteExisting = "true".equals(request.getParameter("overwrite"));

            if ("all".equals(request.getParameter("scope"))) {

                LOG.info("Generating kalender untuk semua entri sejak hari ini. [Overwrite = " + overwriteExisting
                        + "] Untuk entri berikut: " + daftarJadwalPelayanan);
                // No need to show confirmation form -> simply generate the
                // events
                generateEventsDiKalender(daftarJadwalPelayanan, model, overwriteExisting);
                return "summary_created_events";

            } else {

                LOG.info("Menampilkan pilihan jadwal yang ingin di generate kalender nya. Untuk entri berikut: "
                        + daftarJadwalPelayanan);

                List<Event> existingRelevantCalendarEvents = getExistingRelevantCalendarEvents(daftarJadwalPelayanan);
                List<JadwalPelayanan> existingJadwal = new ArrayList<JadwalPelayanan>();
                for (JadwalPelayanan jadwalPelayanan : daftarJadwalPelayanan) {
                    getCacheManager().addJadwalPelayanan(jadwalPelayanan);

                    if (CalendarUtil.cariEventPersekutuan(existingRelevantCalendarEvents,
                            jadwalPelayanan.getTanggal()) != null) {
                        existingJadwal.add(jadwalPelayanan);
                    }
                }

                model.addAttribute("daftarJadwal", daftarJadwalPelayanan);
                model.addAttribute("existingJadwal", existingJadwal);
                return "confirm_generate";
            }

        } catch (ApiProxy.ApiDeadlineExceededException timeoutException) {
            LOG.severe(timeoutException.getMessage() + "\t" + timeoutException.getCause());
            return "timeout_google_service";
        }
    }

    private CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = new CacheManager();
        }
        return cacheManager;
    }

    private SheetsDataProvider getSheetsDataProviderForPersekutuan() throws IOException {
        if (sheetsDataProvider == null) {
            sheetsDataProvider = SheetsDataProvider.createProviderForPersekutuan();
        }
        return sheetsDataProvider;
    }

    @RequestMapping(value = "/calendargen", method = RequestMethod.POST)
    public String generateEventsExecute(HttpServletRequest request, HttpServletResponse response, Model model)
            throws Exception {

        LOG.info("Generating kalender untuk semua entri yang telah dipilih...");

        model.addAttribute("timeZone", CalendarUtil.getTimeZone());
        List<JadwalPelayanan> daftarJadwal = new ArrayList<JadwalPelayanan>();

        String[] parameterValuesJadwal = request.getParameterValues("jadwal");
        if (parameterValuesJadwal == null) {
            throw new NoJadwalSelectedException();
        }

        for (String selectedJadwal : parameterValuesJadwal) {
            // relevant parameters (selected entries)
            JadwalPelayanan jadwalPelayanan = (JadwalPelayanan) getCacheManager().getCacheValue(selectedJadwal);

            if (jadwalPelayanan == null) {
                // try to reload the cache
                for (JadwalPelayanan jadwal : getSheetsDataProviderForPersekutuan()
                        .getDaftarJadwalPelayananFromSheets()) {
                    getCacheManager().addJadwalPelayanan(jadwal);
                }
                jadwalPelayanan = (JadwalPelayanan) getCacheManager().getCacheValue(selectedJadwal);
                if (jadwalPelayanan == null) {
                    throw new EmptyCacheException("Cache not found for key " + selectedJadwal);
                }
            }
            daftarJadwal.add(jadwalPelayanan);
        }

        LOG.info("Jadwal yang akan di generate kalender nya: " + daftarJadwal);

        generateEventsDiKalender(daftarJadwal, model, true);

        return "summary_created_events";

    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ EmptyCacheException.class })
    public String emptyCacheError() {
        return "fail_gen_events_empty_cache";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ NoJadwalSelectedException.class })
    public String noJadwalSelectedError() {
        return "jadwal_not_selected";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ ApiProxy.ApiDeadlineExceededException.class })
    public String googleTimeoutError() {
        return "timeout_google_service";
    }

    private void generateEventsDiKalender(List<JadwalPelayanan> daftarJadwal, Model model, boolean overwriteExisting)
            throws IOException {

        if (daftarJadwal.size() > 0) {

            List<Event> existingRelevantCalendarEvents = getExistingRelevantCalendarEvents(daftarJadwal);

            List<Event> createdEvents = new ArrayList<Event>();
            List<Event> updatedEvents = new ArrayList<Event>();
            List<Event> ignoredEvents = new ArrayList<Event>();

            for (JadwalPelayanan jadwal : daftarJadwal) {
                Event existingEvent = CalendarUtil.cariEventPersekutuan(existingRelevantCalendarEvents,
                        jadwal.getTanggal());
                if (existingEvent == null) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Menambahkan event untuk jadwal ini: " + jadwal);
                    }
                    Event newEvent = CalendarUtil.createNewEvent(jadwal);
                    getCalendarDataProvider().addEventToCalendar(newEvent);
                    createdEvents.add(newEvent);
                } else if (overwriteExisting) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Memperbaharui event untuk jadwal ini: " + jadwal);
                    }
                    Event newEvent = CalendarUtil.createNewEvent(jadwal);
                    getCalendarDataProvider().updateEventToCalendar(existingEvent, newEvent);
                    updatedEvents.add(newEvent);
                } else {

                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Mengabaikan jadwal ini karena event sudah ada: " + jadwal);
                    }

                    ignoredEvents.add(existingEvent);
                }
            }

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("createdEvents: " + createdEvents);
                LOG.fine("updatedEvents: " + updatedEvents);
                LOG.fine("ignoredEvents: " + ignoredEvents);
            }

            model.addAttribute("createdEvents", createdEvents);
            model.addAttribute("updatedEvents", updatedEvents);
            model.addAttribute("ignoredEvents", ignoredEvents);
        }
    }

    private List<Event> getExistingRelevantCalendarEvents(List<JadwalPelayanan> daftarJadwal) throws IOException {

        sortDaftarJadwalPelayanan(daftarJadwal);
        Date tanggalTerakhir = daftarJadwal.get(daftarJadwal.size() - 1).getTanggal();

        return getCalendarDataProvider().getCalendarEvents(tanggalTerakhir);

    }

    private CalendarDataProvider getCalendarDataProvider() {
        if (calendarDataProvider == null) {
            calendarDataProvider = new CalendarDataProvider();
        }
        return calendarDataProvider;
    }

    private void sortDaftarJadwalPelayanan(List<JadwalPelayanan> daftarJadwal) {
        Collections.sort(daftarJadwal, new Comparator<JadwalPelayanan>() {

            @Override
            public int compare(JadwalPelayanan jadwal1, JadwalPelayanan jadwal2) {
                Date tanggal1 = jadwal1.getTanggal();
                Date tanggal2 = jadwal2.getTanggal();

                // Ambil tanggal doang tanpa waktu
                try {
                    tanggal1 = DATE_FORMAT.parse(DATE_FORMAT.format(tanggal1));
                    tanggal2 = DATE_FORMAT.parse(DATE_FORMAT.format(tanggal2));
                } catch (ParseException e) {
                    // Format and then parse -> should always work!!
                    throw new IllegalStateException(e);
                }
                return tanggal1.compareTo(tanggal2);
            }
        });
    }

}
