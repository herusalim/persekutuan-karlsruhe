package org.persekutuankarlsruhe.webapp.email;

import java.util.List;
import java.util.logging.Logger;

import org.persekutuankarlsruhe.webapp.sheets.Orang;

public class DummyDevelopmentEmailService implements IEmailService {

	private static final Logger LOG = Logger.getLogger(DummyDevelopmentEmailService.class.getName());

	@Override
	public void sendEmail(String subject, String textMessage, String htmlMessage, List<Orang> recipients)
			throws EmailSendFailedException {
		LOG.info("Send email Subject: \"" + subject + "\"\n" + "Content: " + textMessage);
		for (Orang orang : recipients) {
			LOG.info("Send email to [" + orang + "]");
		}
	}

}
