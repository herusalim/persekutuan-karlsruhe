package org.persekutuankarlsruhe.webapp.tasks;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenerateCalendarServlet extends HttpServlet {

	private static final String GENERATE_CALENDAR_URI = "/calendargen?scope=all";

	private static final Logger LOG = Logger.getLogger(GenerateCalendarServlet.class.getName());

	private static final long serialVersionUID = 2843923184707196792L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher(GENERATE_CALENDAR_URI).forward(request, response);

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Forward request ke URI: " + GENERATE_CALENDAR_URI);
		}
	}

}
