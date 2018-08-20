package org.persekutuankarlsruhe.webapp.remindpersekutuan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.persekutuankarlsruhe.webapp.email.EmailSendFailedException;
import org.persekutuankarlsruhe.webapp.email.IEmailService;
import org.persekutuankarlsruhe.webapp.email.MailjetEmailService;
import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

public class RemindPersekutuanUtil {

	private static final String URL_MAIN = "http://persekutuan-karlsruhe.appspot.com";
	private static final String PREFIX_LINK_AKTIVASI = URL_MAIN + "/reminder/aktivasi";
	private static final String PREFIX_LINK_UBAH = URL_MAIN + "/reminder/ubah";
	private static final String PREFIX_LINK_DAFTAR = URL_MAIN + "/reminder/daftar";
	private static final String SUBJECT_EMAIL_AKTIVASI = "[Persekutuan Karlsruhe] Aktivasi Reminder Acara Persekutuan";
	private static final String SUBJECT_EMAIL_UBAH = "[Persekutuan Karlsruhe] Ubah Data Reminder Acara Persekutuan";
	private static final String SUBJECT_EMAIL_REMINDER = "[Persekutuan Karlsruhe] Reminder Acara Persekutuan";
	private static final String SUBJECT_EMAIL_STATUS = "[Persekutuan Karlsruhe] Reminder Acara Persekutuan Berhasil Diubah";
	private static final String SUBJECT_EMAIL_HAPUS = "[Persekutuan Karlsruhe] Reminder Acara Persekutuan Berhasil Dihapus";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	private static final String REQUEST_PARAM_EMAIL = "email";
	private static final String REQUEST_PARAM_KEY = "key";

	private IEmailService emailService;
	private static final String ENCODING_UTF_8 = "UTF-8";

	public void sendEmailAktivasi(String nama, String email) throws EmailSendFailedException {
		List<Orang> recipients = Arrays.asList(new Orang(nama, email));
		ReminderPersekutuanDatastore datastore = ReminderPersekutuanDatastore.getInstance();
		Entity inactiveEntity;
		try {
			inactiveEntity = datastore.getInactiveEntity(email);
		} catch (EntityNotFoundException e) {
			throw new IllegalStateException(e);
		}
		String hashValue = datastore.getHashValue(inactiveEntity);
		List<Integer> reminderList = datastore.getReminderList(inactiveEntity);
		String activationTextMessage = createTextMessageAktivasi(nama, email, hashValue, reminderList);
		String activationHtmlMessage = createHtmlMessageAktivasi(nama, email, hashValue, reminderList);
		getEmailService().sendEmail(SUBJECT_EMAIL_AKTIVASI, activationTextMessage, activationHtmlMessage, recipients);
	}

	private String createHtmlMessageAktivasi(String nama, String email, String hashValue, List<Integer> reminderList) {
		StringBuffer message = new StringBuffer("Hallo " + nama + ",<br/><br/>" + "Kamu telah mendaftarkan email "
				+ email + " untuk menerima reminder acara persekutuan sebagai berikut:<br><ul>");
		appendInfoReminderListForHtmlMessage(message, reminderList);
		message.append("</ul><br/>");
		String linkAktivasi = createLinkAktivasi(email, hashValue);
		message.append("Untuk mengaktifkan reminder, klik <a href=\"" + linkAktivasi + "\">link ini</a>.<br/><br/>");
		message.append("Kalau link tidak berfungsi, buka link berikut ini di browser: <br/>");
		message.append(linkAktivasi + "<br/><br/>");
		message.append("Kalau kamu tidak pernah mendaftarkan email kamu, abaikan email ini.<br/><br/>");
		message.append("Salam,<br/>Admin Persekutuan");
		return message.toString();
	}

	private String createTextMessageAktivasi(String nama, String email, String hashValue, List<Integer> reminderList) {
		StringBuffer message = new StringBuffer("Hallo " + nama + ",\n\n" + "Kamu telah mendaftarkan email " + email
				+ " untuk menerima reminder acara persekutuan sebagai berikut:\n");
		appendInfoReminderListForTextMessage(message, reminderList);
		message.append("\nUntuk mengaktifkan reminder, buka link berikut ini di browser: \n");
		message.append(createLinkAktivasi(email, hashValue) + "\n\n");
		message.append("Kalau kamu tidak pernah mendaftarkan email kamu, abaikan email ini.\n\n");
		message.append("Salam,\nAdmin Persekutuan");
		return message.toString();
	}

	private String createLinkAktivasi(String email, String hashValue) {
		try {
			return PREFIX_LINK_AKTIVASI + "?" + REQUEST_PARAM_EMAIL + "=" + URLEncoder.encode(email, ENCODING_UTF_8)
					+ "&" + REQUEST_PARAM_KEY + "=" + URLEncoder.encode(hashValue, ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public IEmailService getEmailService() {
		if (emailService == null) {
			emailService = MailjetEmailService.createEmailService();
		}
		return emailService;
	}

	public void sendEmailUntukUbahPendaftaran(ReminderPersekutuanRegister register) throws EmailSendFailedException {
		String email = register.getEmail();
		String nama = register.getNama();
		String hashValue = register.getHashValue();
		List<Integer> reminderList = register.getReminderList();
		String textMessage = createTextMessageUntukUpdate(nama, email, hashValue, reminderList);
		String htmlMessage = createHtmlMessageUntukUpdate(nama, email, hashValue, reminderList);
		Orang recipient = new Orang(nama, email);
		getEmailService().sendEmail(SUBJECT_EMAIL_UBAH, textMessage, htmlMessage, Arrays.asList(recipient));
	}

	private String createHtmlMessageUntukUpdate(String nama, String email, String hashValue,
			List<Integer> reminderList) {

		StringBuffer message = new StringBuffer(
				"Hallo " + nama + ",<br/><br/>" + "Kamu telah me-request untuk mengubah reminder untuk email " + email
						+ ". Data yang tersimpan saat ini:<br><ul>");
		appendInfoReminderListForHtmlMessage(message, reminderList);
		message.append("</ul><br/>");
		String linkUbahData = createLinkUbahData(email, hashValue);
		message.append("Untuk mengubah data, klik <a href=\"" + linkUbahData + "\">link ini</a>.<br/><br/>");
		message.append("Kalau link tidak berfungsi, buka link berikut ini di browser: <br/>");
		message.append(linkUbahData + "<br/><br/>");
		message.append("Kalau kamu tidak pernah mendaftarkan email kamu, abaikan email ini.<br/><br/>");
		message.append("Salam,<br/>Admin Persekutuan");
		return message.toString();
	}

	private void appendInfoReminderListForHtmlMessage(StringBuffer message, List<Integer> reminderList) {
		for (Number offset : reminderList) {
			message.append("<li>" + offset + " hari sebelum persekutuan (H-" + offset + ")</li>");
		}
	}

	private String createTextMessageUntukUpdate(String nama, String email, String hashValue,
			List<Integer> reminderList) {

		StringBuffer message = new StringBuffer(
				"Hallo " + nama + ",\n\n" + "Kamu telah me-request untuk mengubah reminder untuk email " + email
						+ ". Data yang tersimpan saat ini:\n");
		appendInfoReminderListForTextMessage(message, reminderList);
		message.append("\nUntuk mengubah data, buka link berikut ini di browser: \n");
		message.append(createLinkUbahData(email, hashValue) + "\n\n");
		message.append("Kalau kamu tidak pernah melakukan request tersebut, abaikan email ini.\n\n");
		message.append("Salam,\nAdmin Persekutuan");
		return message.toString();
	}

	private void appendInfoReminderListForTextMessage(StringBuffer message, List<Integer> reminderList) {
		for (Number offset : reminderList) {
			message.append("- " + offset + " hari sebelum persekutuan (H-" + offset + ")\n");
		}
	}

	private String createLinkUbahData(String email, String hashValue) {
		try {
			return PREFIX_LINK_UBAH + "?" + REQUEST_PARAM_EMAIL + "=" + URLEncoder.encode(email, ENCODING_UTF_8) + "&"
					+ REQUEST_PARAM_KEY + "=" + URLEncoder.encode(hashValue, ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public void sendReminderPersekutuan(Orang recipient, JadwalPelayanan jadwal, int offsetHari)
			throws EmailSendFailedException {
		String textMessage = createTextMessageReminder(recipient, jadwal, offsetHari);
		String htmlMessage = createHtmlMessageReminder(recipient, jadwal, offsetHari);
		getEmailService().sendEmail(getSubjectUntukReminder(jadwal), textMessage, htmlMessage,
				Arrays.asList(recipient));
	}

	private String getSubjectUntukReminder(JadwalPelayanan jadwal) {
		return SUBJECT_EMAIL_REMINDER + " Tanggal " + DATE_FORMAT.format(jadwal.getTanggal());
	}

	private String createHtmlMessageReminder(Orang recipient, JadwalPelayanan jadwal, int offsetHari) {
		String message = "Hallo " + recipient.getNama() + ",<br/><br/>"
				+ (offsetHari == 1 ? "Besok" : offsetHari + " hari lagi") + ", tanggal <b>"
				+ DATE_FORMAT.format(jadwal.getTanggal())
				+ "</b>, ada acara persekutuan dengan bahan PA yang diambil dari <b>" + jadwal.getBahanRenungan()
				+ "</b>.<br/>" + "<table><tr><td>Tempat: </td><td>" + jadwal.getLokasi() + "</td></tr>"
				+ "<tr><td>Waktu:</td><td>10:00</td></table>" + "<br/><br/>Salam,<br/>Persekutuan Karlsruhe";

		return message;
	}

	private String createTextMessageReminder(Orang recipient, JadwalPelayanan jadwal, int offsetHari) {
		String message = "Hallo " + recipient.getNama() + ",\n\n" + (offsetHari == 1 ? "Besok" : offsetHari + " hari lagi")
				+ ", tanggal " + DATE_FORMAT.format(jadwal.getTanggal())
				+ ", ada acara persekutuan dengan bahan PA yang diambil dari " + jadwal.getBahanRenungan() + ".\n"
				+ "Tempat: \t" + jadwal.getLokasi() + "\n" + "Waktu: \t10:00\n\nSalam,\nPersekutuan Karlsruhe";

		return message;
	}

	public void sendEmailStatusReminder(String email) throws EntityNotFoundException, EmailSendFailedException {
		ReminderPersekutuanRegister register = ReminderPersekutuanDatastore.getInstance()
				.getReminderPersekutuanRegister(email);
		String nama = register.getNama();
		List<Integer> reminderList = register.getReminderList();

		String textMessage = createTextMessageStatus(nama, reminderList);
		String htmlMessage = createHtmlMessageStatus(nama, reminderList);
		getEmailService().sendEmail(SUBJECT_EMAIL_STATUS, textMessage, htmlMessage,
				Arrays.asList(new Orang(nama, email)));
	}

	private String createHtmlMessageStatus(String nama, List<Integer> reminderList) {
		StringBuffer message = new StringBuffer("Hallo " + nama + ",<br/><br/>"
				+ "Data reminder persekutuan kamu telah berhasil diubah sebagai berikut:<br/><ul>");
		appendInfoReminderListForHtmlMessage(message, reminderList);
		message.append("</ul>");
		message.append("<br/><br/>Salam,<br/>Persekutuan Karlsruhe");
		return message.toString();
	}

	private String createTextMessageStatus(String nama, List<Integer> reminderList) {
		StringBuffer message = new StringBuffer(
				"Hallo " + nama + ",\n\n" + "Data reminder persekutuan kamu telah berhasil diubah sebagai berikut:\n");
		appendInfoReminderListForTextMessage(message, reminderList);
		message.append("\n\nSalam,\nPersekutuan Karlsruhe");
		return message.toString();
	}

	public void sendEmailHapusData(ReminderPersekutuanRegister register)
			throws EntityNotFoundException, EmailSendFailedException {
		String nama = register.getNama();

		String textMessage = createTextMessageHapus(nama);
		String htmlMessage = createHtmlMessageHapus(nama);
		getEmailService().sendEmail(SUBJECT_EMAIL_HAPUS, textMessage, htmlMessage,
				Arrays.asList(new Orang(nama, register.getEmail())));

	}

	private String createHtmlMessageHapus(String nama) {
		String message = "Hallo " + nama + ",<br/><br/>"
				+ "Data reminder persekutuan kamu telah berhasil dihapus dari database.<br>"
				+ "Kalau kamu ingin mendaftar kembali, klik <a href=\"" + PREFIX_LINK_DAFTAR
				+ "\">link ini</a>, atau memasukkan alamat berikut di browser:<br/>" + PREFIX_LINK_DAFTAR
				+ "<br/><br/>Salam,<br/>Persekutuan Karlsruhe";
		return message;
	}

	private String createTextMessageHapus(String nama) {
		String message = "Hallo " + nama + ",\n\n"
				+ "Data reminder persekutuan kamu telah berhasil dihapus dari database."
				+ "Kalau kamu ingin mendaftar kembali, masukkan alamat berikut di browser:\n" + PREFIX_LINK_DAFTAR
				+ "\n\nSalam,\nPersekutuan Karlsruhe";
		return message;
	}

}
