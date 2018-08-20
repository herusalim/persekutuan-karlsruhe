package org.persekutuankarlsruhe.webapp.service;

public class EmptyCacheException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8760183226936454754L;

	public EmptyCacheException() {
		super();
	}

	public EmptyCacheException(String message) {
		super(message);
	}

	public EmptyCacheException(Throwable cause) {
		super(cause);
	}

	public EmptyCacheException(String message, Throwable cause) {
		super(message, cause);
	}
}
