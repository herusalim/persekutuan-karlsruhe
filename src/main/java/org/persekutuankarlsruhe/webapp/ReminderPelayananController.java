package org.persekutuankarlsruhe.webapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.persekutuankarlsruhe.webapp.calendar.CalendarUtil;
import org.persekutuankarlsruhe.webapp.email.EmailSendFailedException;
import org.persekutuankarlsruhe.webapp.email.IEmailService;
import org.persekutuankarlsruhe.webapp.email.MailjetEmailService;
import org.persekutuankarlsruhe.webapp.remindpelayanan.IReminderDataProvider;
import org.persekutuankarlsruhe.webapp.remindpelayanan.PersekutuanReminderDataProvider;
import org.persekutuankarlsruhe.webapp.remindpelayanan.PersekutuanReminderDataProviderForIvena;
import org.persekutuankarlsruhe.webapp.remindpelayanan.ReminderPelayananDatastore;
import org.persekutuankarlsruhe.webapp.remindpelayanan.ReminderTerkirim;
import org.persekutuankarlsruhe.webapp.remindpelayanan.ReminderType;
import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;
import org.persekutuankarlsruhe.webapp.sheets.Pelayanan;
import org.persekutuankarlsruhe.webapp.sheets.SheetsDataProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.apphosting.api.ApiProxy;

@Controller
public class ReminderPelayananController {

	private static final Logger LOG = Logger.getLogger(ReminderPelayananController.class.getName());

	private IEmailService emailService;

	private ReminderPelayananDatastore reminderDatastore = ReminderPelayananDatastore.getInstance();

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ ApiProxy.ApiDeadlineExceededException.class })
	public String googleTimeoutError() {
		return "timeout_google_service";
	}

	@RequestMapping(value = "/tasks/remindpelayanan")
	public String remindPelayanan(Model model) throws Exception {
		SheetsDataProvider dataProvider = SheetsDataProvider.createProviderForPersekutuan();
		List<JadwalPelayanan> daftarPelayanan = dataProvider.getDaftarJadwalPelayananFromSheets();
		List<ReminderTerkirim> daftarTerkirim = new ArrayList<ReminderTerkirim>();
		IReminderDataProvider reminderProvider = new PersekutuanReminderDataProvider();

		JadwalPelayanan jadwalDalamDuaMinggu = CalendarUtil.getJadwalTerdekatDalamBbrpHari(daftarPelayanan, 7, 14);
		if (jadwalDalamDuaMinggu != null) {
			daftarTerkirim.add(sendEmailReminder(jadwalDalamDuaMinggu, ReminderType.SPECIAL, reminderProvider));
		}

		JadwalPelayanan jadwalDalamTujuhHari = CalendarUtil.getJadwalTerdekatDalamBbrpHari(daftarPelayanan, 2, 7);
		if (jadwalDalamTujuhHari != null) {
			daftarTerkirim.add(sendEmailReminder(jadwalDalamTujuhHari, ReminderType.FIRST, reminderProvider));
		}

		JadwalPelayanan jadwalDalamDuaHari = CalendarUtil.getJadwalTerdekatDalamBbrpHari(daftarPelayanan, 0, 2);
		if (jadwalDalamDuaHari != null) {
			daftarTerkirim.add(sendEmailReminder(jadwalDalamDuaHari, ReminderType.SECOND, reminderProvider));
		}

		model.addAttribute("daftarTerkirim", daftarTerkirim);

		return "reminder_pelayanan_summary";
	}

	@RequestMapping(value = "/tasks/remindpelayanan/ivena")
	public String remindPelayananForIvena(Model model) throws Exception {
		SheetsDataProvider dataProvider = SheetsDataProvider.createProviderForPersekutuanIvena();

		List<JadwalPelayanan> daftarPelayanan = dataProvider.getDaftarJadwalPelayananFromSheets();
		List<ReminderTerkirim> daftarTerkirim = new ArrayList<ReminderTerkirim>();
		IReminderDataProvider reminderProvider = new PersekutuanReminderDataProviderForIvena();

		// JadwalPelayanan jadwalDalamDuaMinggu =
		// CalendarUtil.getJadwalTerdekatDalamBbrpHari(daftarPelayanan, 7, 14);
		// if (jadwalDalamDuaMinggu != null) {
		// daftarTerkirim.add(sendEmailReminder(jadwalDalamDuaMinggu,
		// ReminderType.SPECIAL, reminderProvider));
		// }

		JadwalPelayanan jadwalDalamTujuhHari = CalendarUtil.getJadwalTerdekatDalamBbrpHari(daftarPelayanan, 2, 7);
		if (jadwalDalamTujuhHari != null) {
			daftarTerkirim.add(sendEmailReminder(jadwalDalamTujuhHari, ReminderType.FIRST, reminderProvider));
		}

		JadwalPelayanan jadwalDalamDuaHari = CalendarUtil.getJadwalTerdekatDalamBbrpHari(daftarPelayanan, 0, 2);
		if (jadwalDalamDuaHari != null) {
			daftarTerkirim.add(sendEmailReminder(jadwalDalamDuaHari, ReminderType.SECOND, reminderProvider));
		}

		model.addAttribute("daftarTerkirim", daftarTerkirim);

		return "reminder_pelayanan_summary";
	}

	private ReminderTerkirim sendEmailReminder(JadwalPelayanan jadwal, ReminderType reminderType,
			IReminderDataProvider reminderProvider) {

		LOG.info("Mempersiapkan untuk mengirim email untuk jadwal: " + jadwal + ";Type: " + reminderType);

		ReminderTerkirim reminderTerkirim = new ReminderTerkirim();
		reminderTerkirim.setTanggal(jadwal.getTanggal());

		Map<Orang, Set<Pelayanan>> daftarPelayananPerOrang = getDaftarPelayananPerOrang(jadwal);

		for (Entry<Orang, Set<Pelayanan>> pelayananPerOrang : daftarPelayananPerOrang.entrySet()) {
			Orang petugas = pelayananPerOrang.getKey();
			Set<Pelayanan> daftarJenisPelayanan = pelayananPerOrang.getValue();

			if (perluKirimReminder(reminderType, daftarJenisPelayanan)) {
				if (StringUtils.isEmpty(petugas.getEmail()) && petugas.getNama().length() > 2) {
					LOG.warning(petugas.getNama() + " tidak memiliki alamat email!!! Tidak bisa mengirim reminder.");
					continue;
				}
				try {
					if (!isReminderSent(jadwal, petugas, reminderType)) {
						sendEmailReminderKeOrang(jadwal, petugas, daftarJenisPelayanan, reminderType, reminderProvider);
						reminderDatastore.setReminderSent(jadwal, petugas.getEmail(), reminderType);
						reminderTerkirim.getDaftarPetugas().add(petugas);
					} else {
						if (petugas != null && !petugas.toString().equals("")) {
							LOG.info("Email ke " + petugas + " sudah pernah dikirim sebelumnya");
						}
					}
				} catch (EmailSendFailedException e) {
					// Has been checked before, that it exists
					throw new IllegalStateException(e);
				} catch (EntityNotFoundException e) {
					// Has been checked before, that it exists
					throw new IllegalStateException(e);
				}
			}
		}

		return reminderTerkirim;
	}

	/**
	 * Reminder perlu dikirimkan untuk semua orang jika jenis reminder bukan
	 * {@code ReminderType.SPECIAL}. Atau jika jenis reminder
	 * {@code ReminderType.SPECIAL}, maka reminder hanya akan dikirim untuk yang
	 * pelayanan MC atau Renungan.
	 * 
	 * @param reminderType
	 * @param daftarJenisPelayanan
	 * @return
	 */
	private boolean perluKirimReminder(ReminderType reminderType, Set<Pelayanan> daftarJenisPelayanan) {
		return reminderType != ReminderType.SPECIAL || daftarJenisPelayanan.contains(Pelayanan.MC)
				|| daftarJenisPelayanan.contains(Pelayanan.PEMIMPIN_RENUNGAN);
	}

	private Map<Orang, Set<Pelayanan>> getDaftarPelayananPerOrang(JadwalPelayanan jadwal) {
		Map<Orang, Set<Pelayanan>> daftarPelayananPerOrang = new HashMap<Orang, Set<Pelayanan>>();
		for (Entry<Pelayanan, List<Orang>> pelayanan : jadwal.listPelayanan()) {
			for (Orang orang : pelayanan.getValue()) {
				Set<Pelayanan> daftarPelayanan = daftarPelayananPerOrang.get(orang);
				if (daftarPelayanan == null) {
					daftarPelayanan = new HashSet<>();
					daftarPelayananPerOrang.put(orang, daftarPelayanan);
				}
				daftarPelayanan.add(pelayanan.getKey());
			}
		}
		return daftarPelayananPerOrang;
	}

	private void sendEmailReminderKeOrang(JadwalPelayanan jadwal, Orang orang, Set<Pelayanan> daftarPelayanan,
			ReminderType reminderType, IReminderDataProvider reminderProvider) throws EmailSendFailedException {

		LOG.info("Mengirim email ke " + orang + " untuk jadwal: " + jadwal);

		String daftarPelayananAsString = "";
		int index = 0;
		for (Pelayanan pelayanan : daftarPelayanan) {
			if (index++ != 0) {
				daftarPelayananAsString += ", ";
			}
			daftarPelayananAsString += pelayanan.getNama();
		}
		String subject = reminderProvider.getMailSubject(reminderType, daftarPelayananAsString);
		LOG.info("Mengirim email dengan subjek: " + subject);

		String textMessage = reminderProvider.getMailText(jadwal, orang, daftarPelayananAsString);

		String htmlMessage = reminderProvider.getMailTextHtml(jadwal, orang, daftarPelayananAsString);
		if (reminderType == ReminderType.SPECIAL) {
			String specialReminderFootnote = getSpecialReminderFootnote();
			textMessage += "\n\n" + specialReminderFootnote;
			htmlMessage += "<br/><br/>" + specialReminderFootnote;
		}

		MailjetEmailService emailService = (MailjetEmailService) getEmailService();
		emailService.setSenderName(reminderProvider.getSenderName());
		emailService.setSenderEmail(reminderProvider.getSenderEmail());
		emailService.sendEmail(subject, textMessage, htmlMessage, Arrays.asList(orang));
	}

	private String getSpecialReminderFootnote() {
		return "PS: SPECIAL Reminder dikirimkan supaya kamu punya cukup waktu untuk mempersiapkan";
	}

	private boolean isReminderSent(JadwalPelayanan jadwal, Orang orang, ReminderType reminderType) {

		try {
			return reminderDatastore.isReminderSent(jadwal, orang.getEmail(), reminderType);
		} catch (EntityNotFoundException e) {
			reminderDatastore.addReminderEntity(jadwal, orang.getEmail(), reminderType);
			return false;
		}
	}

	public IEmailService getEmailService() {
		if (emailService == null) {
			emailService = MailjetEmailService.createEmailService();
		}
		return emailService;
	}

}
