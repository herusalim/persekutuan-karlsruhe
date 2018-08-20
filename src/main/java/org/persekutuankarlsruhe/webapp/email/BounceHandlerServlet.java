package org.persekutuankarlsruhe.webapp.email;

import java.io.IOException;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.mail.BounceNotification;
import com.google.appengine.api.mail.BounceNotificationParser;

public class BounceHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 3140457830747562549L;
	private static final Logger LOG = Logger.getLogger(BounceHandlerServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			BounceNotification bounce = BounceNotificationParser.parse(req);
			LOG.warning("Bounced email notification.");
			// The following data is available in a BounceNotification object
			LOG.warning("Original From: " + bounce.getOriginal().getFrom());
			LOG.warning("Original To: " + bounce.getOriginal().getTo());
			LOG.warning("Original Subject: " + bounce.getOriginal().getSubject());
			LOG.warning("Original Text: " + bounce.getOriginal().getText());
			LOG.warning("Notification From: " + bounce.getNotification().getFrom());
			LOG.warning("Notification To: " + bounce.getNotification().getTo());
			LOG.warning("Notification Subject: " + bounce.getNotification().getSubject());
			LOG.warning("Notification Text: " + bounce.getNotification().getText());
		} catch (MessagingException e) {
			throw new IOException(e);
		}
	}
}