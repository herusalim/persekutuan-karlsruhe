package org.persekutuankarlsruhe.webapp.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;
import org.persekutuankarlsruhe.webapp.sheets.Pelayanan;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

public class CalendarUtil {

	private static final Logger LOG = Logger.getLogger(CalendarUtil.class.getName());

	private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

	private static final TimeZone TIME_ZONE_BERLIN = TimeZone.getTimeZone("Europe/Berlin");

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	public static Event createNewEvent(JadwalPelayanan jadwal) {
		Event event = new Event();
		event.setSummary("Persekutuan: " + jadwal.getBahanRenungan());
		event.setStart(createEventDateTimeWithHour(jadwal.getTanggal(), 10));
		event.setEnd(createEventDateTimeWithHour(jadwal.getTanggal(), 14));
		String description = "Petugas pelayanan:\n" + "- "
				+ getPetugasAsString(Pelayanan.PEMIMPIN_RENUNGAN, jadwal.getDaftarPelayanan()) + "\n" + "- "
				+ getPetugasAsString(Pelayanan.MC, jadwal.getDaftarPelayanan()) + "\n" + "- "
				+ getPetugasAsString(Pelayanan.MUSIK, jadwal.getDaftarPelayanan()) + "\n" + "- "
				+ getPetugasAsString(Pelayanan.SM, jadwal.getDaftarPelayanan()) + "\n" + "- "
				+ getPetugasAsString(Pelayanan.MASAK, jadwal.getDaftarPelayanan());
		event.setDescription(description);
		event.setAttendees(getPetugasSebagaiAttendee(jadwal.getDaftarPelayanan()));
		event.setLocation(jadwal.getLokasi());
		return event;
	}

	private static List<EventAttendee> getPetugasSebagaiAttendee(Map<Pelayanan, List<Orang>> daftarPelayanan) {
		Set<Orang> daftarPetugas = new HashSet<Orang>();
		for (Entry<Pelayanan, List<Orang>> pelayanan : daftarPelayanan.entrySet()) {
			for (Orang petugas : pelayanan.getValue()) {
				daftarPetugas.add(petugas);
			}
		}
		List<EventAttendee> daftarPeserta = new ArrayList<EventAttendee>();
		for (Orang petugas : daftarPetugas) {
			if (petugas.getEmail() != null && petugas.getEmail().length() > 0) {
				EventAttendee peserta = new EventAttendee();
				peserta.setDisplayName(petugas.getNama());
				peserta.setEmail(petugas.getEmail());
				daftarPeserta.add(peserta);
			}
		}
		return daftarPeserta;
	}

	private static String getPetugasAsString(Pelayanan jenisPelayanan, Map<Pelayanan, List<Orang>> daftarPelayanan) {
		List<Orang> daftarPetugas = daftarPelayanan.get(jenisPelayanan);
		String value = jenisPelayanan.getNama() + ": ";
		
		if (daftarPetugas == null) {
			value = "[Tidak Ada]";
		} else {
			int index = 0;
			for (Orang petugas : daftarPetugas) {
				if (index++ != 0) {
					value += " & ";
				}
				value += petugas.getNama();
			}
		}
		return value;
	}

	public static TimeZone getTimeZone() {
		return TIME_ZONE_BERLIN;
	}

	private static EventDateTime createEventDateTimeWithHour(Date tanggal, int hour) {
		EventDateTime dateTime = new EventDateTime();
		Calendar calendar = Calendar.getInstance(getTimeZone());
		calendar.setTime(tanggal);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		dateTime.setDateTime(new DateTime(calendar.getTime().getTime()));
		return dateTime;
	}

	public static Event cariEventPersekutuan(List<Event> events, Date tanggalPersekutuan) {
		String formattedTanggal = DATE_FORMAT.format(tanggalPersekutuan);
		for (Event event : events) {
			DateTime start = event.getStart().getDateTime();
			if (start == null) {
				start = event.getStart().getDate();
			}
			String formattedTanggalEvent = DATE_FORMAT.format(new Date(start.getValue()));

			if (formattedTanggal.equals(formattedTanggalEvent) && CalendarUtil.isAcaraPersekutuan(event)) {
				// Jam sama dan judul Event dimulai dengan "Persekutuan: "
				return event;
			}
		}

		return null;
	}

	private static boolean isAcaraPersekutuan(Event event) {
		boolean isAcaraPersekutuan = false;
		DateTime startDateTime = event.getStart().getDateTime();
		DateTime endDateTime = event.getEnd().getDateTime();
		if (startDateTime != null) {
			Calendar calendar = Calendar.getInstance(getTimeZone());
			calendar.setTime(new Date(startDateTime.getValue()));
			int startHour = calendar.get(Calendar.HOUR_OF_DAY);
			calendar.setTime(new Date(endDateTime.getValue()));
			int endHour = calendar.get(Calendar.HOUR_OF_DAY);
			if (startHour == 10 && endHour == 14 && event.getSummary().startsWith("Persekutuan: ")) {
				isAcaraPersekutuan = true;
			}
		}
		return isAcaraPersekutuan;
	}

	public static JadwalPelayanan getJadwalTerdekatDalamBbrpHari(List<JadwalPelayanan> daftarPelayanan,
			int jarakHariMinimal, int jarakHariMaximal) {

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Mencari jadwal dengan jarak hari minimal " + jarakHariMinimal + " dan jarak hari maximal "
					+ jarakHariMaximal + ". dari daftar: " + daftarPelayanan);
		}
		Calendar nowCalendar = Calendar.getInstance(getTimeZone());
		long now = nowCalendar.getTimeInMillis();
		for (JadwalPelayanan jadwal : daftarPelayanan) {
			long jadwalValue = getJadwalTimeValue(jadwal);
			long jarakWaktu = jadwalValue - now;
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Menghitung waktu sekarang (" + nowCalendar.getTime() + " - " + now + ") dengan jadwal ("
						+ jadwal + " - " + jadwalValue + "). Selisih = " + (double) jarakWaktu / ONE_DAY_IN_MILLIS
						+ " hari");
			}
			if (jarakWaktu > (jarakHariMinimal) * ONE_DAY_IN_MILLIS
					&& jarakWaktu < jarakHariMaximal * ONE_DAY_IN_MILLIS) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Jadwal yang ditemukan: " + jadwal);
				}
				return jadwal;
			}
		}
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Tidak ada jadwal yang ditemukan!!");
		}
		return null;
	}

	public static long getJadwalTimeValue(JadwalPelayanan jadwal) {
		Calendar calendar = Calendar.getInstance(getTimeZone());
		calendar.setTime(jadwal.getTanggal());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		return calendar.getTimeInMillis();
	}
}
