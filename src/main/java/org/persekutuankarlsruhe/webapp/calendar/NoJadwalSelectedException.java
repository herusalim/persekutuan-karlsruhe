package org.persekutuankarlsruhe.webapp.calendar;

public class NoJadwalSelectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -777650781017512430L;

	public NoJadwalSelectedException() {
		super();
	}

	public NoJadwalSelectedException(String message) {
		super(message);
	}

	public NoJadwalSelectedException(Throwable cause) {
		super(cause);
	}

	public NoJadwalSelectedException(String message, Throwable cause) {
		super(message, cause);
	}
}
