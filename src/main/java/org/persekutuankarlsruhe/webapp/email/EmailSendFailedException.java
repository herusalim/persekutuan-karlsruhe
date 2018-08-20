package org.persekutuankarlsruhe.webapp.email;

public class EmailSendFailedException extends Exception {

	private static final long serialVersionUID = -205655306835404514L;

	public EmailSendFailedException() {
		super();
	}

	public EmailSendFailedException(Throwable cause) {
		super(cause);
	}

	public EmailSendFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmailSendFailedException(String message) {
		super(message);
	}

}
