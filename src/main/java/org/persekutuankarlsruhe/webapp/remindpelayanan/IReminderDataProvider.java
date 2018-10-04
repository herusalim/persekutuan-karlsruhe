package org.persekutuankarlsruhe.webapp.remindpelayanan;

import org.persekutuankarlsruhe.webapp.sheets.JadwalPelayanan;
import org.persekutuankarlsruhe.webapp.sheets.Orang;

public interface IReminderDataProvider {
    public String getMailSubject(ReminderType reminderType, String pelayanan);
    public String getMailText(JadwalPelayanan jadwal, Orang orang, String pelayanan);
    public String getMailTextHtml(JadwalPelayanan jadwal, Orang orang, String pelayanan);
    public String getSenderName();
	public String getSenderEmail();
	public Orang getAdmin();
	public String getSheetIdDaftarAnggota();

}
