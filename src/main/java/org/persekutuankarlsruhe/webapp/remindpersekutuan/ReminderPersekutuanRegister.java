package org.persekutuankarlsruhe.webapp.remindpersekutuan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderPersekutuanRegister {
	private String email;
	private String nama;
	private List<Integer> reminderList = new ArrayList<Integer>();
	private Map<String, Boolean> reminderListSelections = new HashMap<String, Boolean>();
	private String hashValue;

	public ReminderPersekutuanRegister(String nama, String email) {
		this.nama = nama;
		this.email = email;
	}

	public Map<String, Boolean> getReminderListSelections() {
		return reminderListSelections;
	}

	public String getNama() {
		return nama;
	}

	public String getEmail() {
		return email;
	}

	public String getHashValue() {
		return this.hashValue;
	}

	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}

	public List<Integer> getReminderList() {
		return reminderList;
	}

	public String toString() {
		return "Nama: " + nama + "\tEmail: " + email + "\tReminder List: " + reminderList;
	}
}
