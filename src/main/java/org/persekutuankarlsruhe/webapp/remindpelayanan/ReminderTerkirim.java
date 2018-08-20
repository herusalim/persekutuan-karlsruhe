package org.persekutuankarlsruhe.webapp.remindpelayanan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.persekutuankarlsruhe.webapp.sheets.Orang;

public class ReminderTerkirim {
	private Date tanggal;
	private List<Orang> daftarPetugas = new ArrayList<Orang>();

	public Date getTanggal() {
		return tanggal;
	}

	public void setTanggal(Date tanggal) {
		this.tanggal = tanggal;
	}

	public List<Orang> getDaftarPetugas() {
		return daftarPetugas;
	}

	public void setDaftarPetugas(List<Orang> daftarPetugas) {
		this.daftarPetugas = daftarPetugas;
	}
}
