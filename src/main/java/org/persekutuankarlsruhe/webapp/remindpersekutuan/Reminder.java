package org.persekutuankarlsruhe.webapp.remindpersekutuan;

import java.util.Date;

import org.persekutuankarlsruhe.webapp.sheets.Orang;

public class Reminder {
	Orang orang;
	Date tanggal;

	public Reminder(Orang orang, Date tanggal) {
		this.orang = orang;
		this.tanggal = tanggal;
	}

	public Orang getOrang() {
		return this.orang;
	}

	public Date getTanggal() {
		return this.tanggal;
	}
}
