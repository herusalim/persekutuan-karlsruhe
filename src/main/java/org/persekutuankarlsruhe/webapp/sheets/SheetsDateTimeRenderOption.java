package org.persekutuankarlsruhe.webapp.sheets;

public enum SheetsDateTimeRenderOption {
	SERIAL_NUMBER("SERIAL_NUMBER"), FORMATTED_STRING("FORMATTED_STRING");
	private String value;

	private SheetsDateTimeRenderOption(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
