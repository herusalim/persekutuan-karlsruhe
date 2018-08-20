package org.persekutuankarlsruhe.webapp.email;

import java.util.List;

import org.persekutuankarlsruhe.webapp.sheets.Orang;

public interface IEmailService {

	// private static final String EMAIL_PERSEKUTUAN_ADMIN_ADDRESS =
	// "admin@persekutuan-karlsruhe.appspotmail.com";
	// private static InternetAddress EMAIL_PERSEKUTUAN_ADMIN_INET_ADDRESS;
	public static final String EMAIL_PERSEKUTUAN_ADDRESS = "persekutuan.ka@gmail.com";
	public static final String EMAIL_PERSEKUTUAN_NAME = "Persekutuan Karlsruhe";
    public static final String EMAIL_PERSEKUTUAN_IVENA_REMINDER_NAME = "Reminder Pelayanan PA Umum";
    public static final String EMAIL_PERSEKUTUAN_IVENA_REMINDER_ADDRESS = "ivenathania@gmail.com";

	public void sendEmail(String subject, String textMessage, String htmlMessage, List<Orang> recipients) throws EmailSendFailedException;
}
