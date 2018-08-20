package org.persekutuankarlsruhe.webapp.email;

public class InvalidRecipientsException extends IllegalArgumentException {

	private static final long serialVersionUID = 7927504227705455869L;

	public InvalidRecipientsException() {
		super();
	}

	public InvalidRecipientsException(String message) {
		super(message);
	}

	public InvalidRecipientsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidRecipientsException(Throwable cause) {
		super(cause);
	}

}
