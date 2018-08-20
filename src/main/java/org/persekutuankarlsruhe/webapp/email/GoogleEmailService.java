package org.persekutuankarlsruhe.webapp.email;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.persekutuankarlsruhe.webapp.sheets.Orang;
import org.springframework.util.StringUtils;

public class GoogleEmailService implements IEmailService {

	private static final Logger LOG = Logger.getLogger(GoogleEmailService.class.getName());
	public static InternetAddress EMAIL_PERSEKUTUAN_INET_ADDRESS;

	static {
		try {
			EMAIL_PERSEKUTUAN_INET_ADDRESS = new InternetAddress(IEmailService.EMAIL_PERSEKUTUAN_ADDRESS,
					IEmailService.EMAIL_PERSEKUTUAN_NAME);
			// EMAIL_PERSEKUTUAN_ADMIN_INET_ADDRESS = new
			// InternetAddress(EMAIL_PERSEKUTUAN_ADMIN_ADDRESS,
			// EMAIL_PERSEKUTUAN_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void sendEmail(String subject, String textMessage, String htmlMessage, List<Orang> recipients)
			throws EmailSendFailedException {

		try {

			InternetAddress[] recipientAddresses = new InternetAddress[recipients.size()];
			int index = 0;
			for (Orang recipient : recipients) {
				try {
					recipientAddresses[index++] = new InternetAddress(recipient.getEmail(), recipient.getNama());
				} catch (UnsupportedEncodingException e) {
					throw new InvalidRecipientsException("Alamat email invalid.", e);
				}
			}
			Message message = createMessage(subject, textMessage, htmlMessage, EMAIL_PERSEKUTUAN_INET_ADDRESS,
					recipientAddresses);

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Mengirim email " + message);
			}

			Transport.send(message);

		} catch (MessagingException e) {
			throw new EmailSendFailedException(e);
		} catch (UnsupportedEncodingException e) {
			throw new EmailSendFailedException(e);
		}
	}

	private Message createMessage(String subject, String textMessage, String htmlMessage, InternetAddress senderAddress,
			InternetAddress[] recipientAddresses) throws MessagingException, UnsupportedEncodingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Message message = new MimeMessage(session);
		message.setFrom(EMAIL_PERSEKUTUAN_INET_ADDRESS);
		for (InternetAddress recipientAddress : recipientAddresses) {
			message.addRecipient(Message.RecipientType.TO, recipientAddress);
		}
		if (StringUtils.isEmpty(htmlMessage)) {
			message.setText(textMessage);
		} else {
			message.setContent(htmlMessage, "text/html");
		}
		message.setSubject(subject);
		message.setReplyTo(new InternetAddress[] { EMAIL_PERSEKUTUAN_INET_ADDRESS });

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Creating email message " + message + ". Subject: " + subject + "; Sender: " + message.getFrom()
					+ ";Recipients: " + recipientAddresses + "; Message: " + textMessage);
		}

		return message;
	}
}
