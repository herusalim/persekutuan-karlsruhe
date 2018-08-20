package org.persekutuankarlsruhe.webapp.remindpelayanan;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.persekutuankarlsruhe.webapp.email.IEmailService;
import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;
import org.persekutuankarlsruhe.webapp.sheets.Pelayanan;

public class PersekutuanReminderDataProvider implements IReminderDataProvider {

	private static final Logger LOG = Logger.getLogger(PersekutuanReminderDataProvider.class.getName());

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public String getMailSubject(ReminderType reminderType, String pelayanan) {
		return "[Persekutuan] " + reminderType + " Reminder Pelayanan " + pelayanan;
	}

	@Override
	public String getMailText(JadwalPelayanan jadwal, Orang orang, String namaPelayanan) {
		StringBuffer messageText = new StringBuffer();

		messageText.append("Hallo " + orang.getNama() + ",\n\n" + "Jangan lupa untuk pelayanan sebagai \""
				+ namaPelayanan + "\" di persekutuan tanggal " + DATE_FORMAT.format(jadwal.getTanggal()) + ".\n\n");

		messageText.append("Tema: " + jadwal.getBahanRenungan() + ".\n\n");

		messageText.append("Info lengkap daftar pelayanan sebagai berikut:\n");
		for (Entry<Pelayanan, List<Orang>> pelayanan : jadwal.listPelayanan()) {
			messageText.append("- " + pelayanan.getKey().getNama() + ": ");
			int index = 0;
			for (Orang petugas : pelayanan.getValue()) {
				if (index++ != 0) {
					messageText.append(", ");
				}
				messageText.append(petugas.getNama());
			}
			messageText.append("\n");
		}
		messageText.append("\nTuhan memberkati.\n\n");
		messageText.append("Salam,\n");
		messageText.append("Persekutuan Karlsruhe");
		messageText.append(
				"\n\nLink jadwal persekutuan: https://docs.google.com/spreadsheets/d/19YlSZJtEmiqaMMbxLHkrCM8PMsODlHyuZzYCmOeduBo/edit\n");

		LOG.info("Text email: " + messageText);

		return messageText.toString();

	}

	@Override
	public String getMailTextHtml(JadwalPelayanan jadwal, Orang orang, String namaPelayanan) {
		StringBuffer messageText = new StringBuffer();

		messageText.append(
				"Hallo " + orang.getNama() + ",<br/><br/>" + "Jangan lupa untuk pelayanan sebagai \"" + namaPelayanan
						+ "\" di persekutuan tanggal " + DATE_FORMAT.format(jadwal.getTanggal()) + ".<br/><br/>");

		messageText.append("Tema: " + jadwal.getBahanRenungan() + ".<br/><br/>");

		messageText.append("Info lengkap daftar pelayanan sebagai berikut:<br/>");
		for (Entry<Pelayanan, List<Orang>> pelayanan : jadwal.listPelayanan()) {
			messageText.append("- " + pelayanan.getKey().getNama() + ": ");
			int index = 0;
			for (Orang petugas : pelayanan.getValue()) {
				if (index++ != 0) {
					messageText.append(", ");
				}
				messageText.append(petugas.getNama());
			}
			messageText.append("<br/>");
		}
		messageText.append("<br/>Tuhan memberkati.<br/><br/>");
		messageText.append("Salam,<br/>");
		messageText.append("Persekutuan Karlsruhe");
		messageText.append(
				"<br/><br/>Link jadwal persekutuan: <a href=\"https://docs.google.com/spreadsheets/d/19YlSZJtEmiqaMMbxLHkrCM8PMsODlHyuZzYCmOeduBo/edit\">Link</a>");
		messageText.append(
				"<br/>Atau buka URL ini di browser kalau link tidak berfungsi: https://docs.google.com/spreadsheets/d/19YlSZJtEmiqaMMbxLHkrCM8PMsODlHyuZzYCmOeduBo/edit<br/>");

		LOG.info("Text email: " + messageText);

		return messageText.toString();
	}

	@Override
	public String getSenderName() {
		return IEmailService.EMAIL_PERSEKUTUAN_NAME;
	}

	@Override
	public String getSenderEmail() {
		return IEmailService.EMAIL_PERSEKUTUAN_ADDRESS;
	}

}
