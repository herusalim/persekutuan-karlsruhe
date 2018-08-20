package org.persekutuankarlsruhe.webapp.remindpelayanan;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.persekutuankarlsruhe.webapp.email.IEmailService;
import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;
import org.persekutuankarlsruhe.webapp.sheets.Pelayanan;

public class PersekutuanReminderDataProviderForIvena implements IReminderDataProvider {

    private static final Logger LOG = Logger.getLogger(PersekutuanReminderDataProviderForIvena.class.getName());

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public String getMailSubject(ReminderType reminderType, String pelayanan) {
        return "[Persekutuan] " + reminderType + " Reminder Pelayanan " + pelayanan;
    }

    @Override
    public String getMailText(JadwalPelayanan jadwal, Orang orang, String namaPelayanan) {
        StringBuffer messageText = new StringBuffer();

        messageText.append("Hallo " + orang.getNama() + ",\n\n");
        messageText.append("Jangan lupa untuk pelayanan di PA Umum tanggal " + DATE_FORMAT.format(jadwal.getTanggal())
                + " sebagai \"" + namaPelayanan + "\".\n");
        messageText.append("Harap kumpul jam 7pm untuk berdoa bersama.\n\n");

        messageText.append("Tema: " + jadwal.getBahanRenungan() + ".\n");
        messageText.append("Tempat: " + jadwal.getLokasi() + ".\n\n");

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
        messageText.append("PA Umum");
        messageText.append(
                "\n\nLink jadwal persekutuan: https://drive.google.com/open?id=16qC7KZ-nAdLKtqp59uKfMg8i1R5DWU1BX2h8hlDq3FM\n");

        LOG.info("Text email: " + messageText);

        return messageText.toString();

    }

    @Override
    public String getMailTextHtml(JadwalPelayanan jadwal, Orang orang, String namaPelayanan) {
        StringBuffer messageText = new StringBuffer();

        messageText.append("Hallo " + orang.getNama() + ",<br/><br/>");
        messageText.append("Jangan lupa untuk pelayanan di PA Umum tanggal " + DATE_FORMAT.format(jadwal.getTanggal())
                + " sebagai \"" + namaPelayanan + "\".<br/>");
        messageText.append("Harap kumpul jam 7pm untuk berdoa bersama.<br/><br/>");

        messageText.append("Tema: " + jadwal.getBahanRenungan() + ".<br/>");
        messageText.append("Tempat: " + jadwal.getLokasi() + ".<br/><br/>");

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
        messageText.append("PA Umum");
        messageText.append(
                "<br/><br/>Link jadwal persekutuan: <a href=\"https://drive.google.com/open?id=16qC7KZ-nAdLKtqp59uKfMg8i1R5DWU1BX2h8hlDq3FM\">Link</a>");
        messageText.append(
                "<br/>Atau buka URL ini di browser kalau link tidak berfungsi: https://drive.google.com/open?id=16qC7KZ-nAdLKtqp59uKfMg8i1R5DWU1BX2h8hlDq3FM<br/>");

        LOG.info("Text email: " + messageText);

        return messageText.toString();
    }

    @Override
    public String getSenderName() {
        return IEmailService.EMAIL_PERSEKUTUAN_IVENA_REMINDER_NAME;
    }

    @Override
    public String getSenderEmail() {
        return IEmailService.EMAIL_PERSEKUTUAN_IVENA_REMINDER_ADDRESS;
    }

}
