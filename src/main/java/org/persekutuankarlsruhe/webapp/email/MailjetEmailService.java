package org.persekutuankarlsruhe.webapp.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.InternetAddress;

import org.json.JSONArray;
import org.json.JSONObject;
import org.persekutuankarlsruhe.webapp.service.SystemPropertyUtil;
import org.persekutuankarlsruhe.webapp.sheets.Orang;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Email;

public class MailjetEmailService implements IEmailService {

	private static final String MAILJET_API_KEY_RESOURCE_NAME = "Mailjet_API_KEY";
	private static final String MAILJET_API_SECRET_RESOURCE_NAME = "Mailjet_API_SECRET";
	private static final Logger LOG = Logger.getLogger(MailjetEmailService.class.getName());
	public static InternetAddress EMAIL_PERSEKUTUAN_INET_ADDRESS;
	MailjetClient client;
	private String senderName = EMAIL_PERSEKUTUAN_NAME;
	private String senderEmail = EMAIL_PERSEKUTUAN_ADDRESS;

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

	public static IEmailService createEmailService() {
		if (SystemPropertyUtil.isProductive()) {
			// return new GoogleEmailService();
			return new MailjetEmailService();
		} else {
			return new DummyDevelopmentEmailService();
		}
	}

	public MailjetEmailService() {
		client = new MailjetClient(getApiKey(), getApiSecret());
	}

	private static String getApiKey() {
		return readResourceFile(MAILJET_API_KEY_RESOURCE_NAME);
	}

	private static String getApiSecret() {
		return readResourceFile(MAILJET_API_SECRET_RESOURCE_NAME);
	}

	private static String readResourceFile(String fileName) {
		try {
			InputStream resource = MailjetEmailService.class.getResourceAsStream("/" + fileName);
			byte[] byteString = new byte[32];
			resource.read(byteString);
			return new String(byteString, "UTF-8");
		} catch (IOException e) {
			throw new IllegalStateException("Resource file " + fileName + " tidak bisa ditemukan");
		}
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public static void main(String[] args) throws Exception {
		MailjetClient client = new MailjetClient(getApiKey(), getApiSecret());
		MailjetRequest email = new MailjetRequest(Email.resource).property(Email.FROMEMAIL, "persekutuan.ka@gmail.com")
				.property(Email.FROMNAME, "pandora").property(Email.SUBJECT, "Your email flight plan!")
				.property(Email.TEXTPART, "Dear passenger, welcome to Mailjet! May the delivery force be with you!")
				.property(Email.HTMLPART,
						"<h3>Dear passenger, welcome to Mailjet!</h3><br/>May the delivery force be with you!")
				.property(Email.RECIPIENTS,
						new JSONArray().put(new JSONObject().put("Email", "herumartinus.salim@yahoo.de")));

		// trigger the API call
		MailjetResponse response = client.post(email);
		System.out.println(response.getStatus());
		System.out.println(response.getData());

	}

	@Override
	public void sendEmail(String subject, String textMessage, String htmlMessage, List<Orang> recipients)
			throws EmailSendFailedException {
		MailjetRequest request = new MailjetRequest(Email.resource).property(Email.FROMEMAIL, senderEmail)
				.property(Email.FROMNAME, senderName).property(Email.SUBJECT, subject)
				.property(Email.TEXTPART, textMessage).property(Email.HTMLPART, htmlMessage);

		request.property(Email.RECIPIENTS, buildRecipients(recipients));

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Mengirim email " + request);
		}

		try {
			MailjetResponse response = client.post(request);
			LOG.info("Status: " + response.getStatus() + "\t" + response.getData());
		} catch (MailjetException e) {
			throw new EmailSendFailedException(e);
		} catch (MailjetSocketTimeoutException e) {
			throw new EmailSendFailedException(e);
		}

	}

	private JSONArray buildRecipients(List<Orang> recipients) {
		JSONArray recipientJsonArray = new JSONArray();
		for (Orang orang : recipients) {
			recipientJsonArray.put(new JSONObject().put("Name", orang.getNama()).put("Email", orang.getEmail()));
		}
		return recipientJsonArray;
	}
}
