package org.persekutuankarlsruhe.webapp.remindpersekutuan;

public class ReminderRegisterAlreadyExistException extends Exception {

	public enum RegisterType {
		ACTIVE, INACTIVE
	}

	RegisterType registerType;

	private static final long serialVersionUID = 5687447935451589390L;

	public ReminderRegisterAlreadyExistException(String message, RegisterType registerType) {
		super(message);
		this.registerType = registerType;
	}

	public ReminderRegisterAlreadyExistException(String message, Throwable cause, RegisterType registerType) {
		super(message, cause);
		this.registerType = registerType;
	}

	public ReminderRegisterAlreadyExistException(RegisterType registerType) {
		super();
		this.registerType = registerType;
	}

	public RegisterType getRegisterType() {
		return this.registerType;
	}

}
