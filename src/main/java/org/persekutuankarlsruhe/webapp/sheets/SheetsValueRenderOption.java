package org.persekutuankarlsruhe.webapp.sheets;

public enum SheetsValueRenderOption {
	FORMATTED_VALUE("FORMATTED_VALUE"), UNFORMATTED_VALUE("UNFORMATTED_VALUE"), FORMULA("FORMULA");
	private String value;

	private SheetsValueRenderOption(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
