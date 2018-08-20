package org.persekutuankarlsruhe.webapp.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

public class GoogleAPIServiceProvider {

	private static final Logger LOG = Logger.getLogger(GoogleAPIServiceProvider.class.getName());

	/** Application name. */
	private static final String APPLICATION_NAME = "Persekutuan Webapp";

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Scope for spreadsheet read only
	 */
	private static final List<String> SPREADSHEET_READONLY_SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

	/**
	 * Scope for calendar
	 */
	private static final List<String> CALENDAR_SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static GoogleCredential authorize() throws IOException {
		GoogleCredential credential = GoogleCredential.getApplicationDefault();

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Menggunakan Credential " + credential + " untuk mengakses Google API");
		}

		return credential;
	}

	/**
	 * Build and return an authorized Sheets API client service.
	 * 
	 * @return an authorized Sheets API client service
	 * @throws IOException
	 */
	public static Sheets getSheetsService() throws IOException {
		GoogleCredential credential = authorize();
		if (credential.createScopedRequired()) {
			credential = credential.createScoped(SPREADSHEET_READONLY_SCOPES);
		}
		Sheets sheetsService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Mengakses Sheets API dengan scope " + credential.getServiceAccountScopes() + " untuk aplikasi "
					+ APPLICATION_NAME);
		}

		return sheetsService;
	}

	/**
	 * Build and return an authorized Calendar API client service.
	 * 
	 * @return an authorized Calendar API client service
	 * @throws IOException
	 */
	public static Calendar getCalendarService() throws IOException {

		GoogleCredential credential = authorize();
		if (credential.createScopedRequired()) {
			credential = credential.createScoped(CALENDAR_SCOPES);
		}
		Calendar calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Mengakses Calendar API dengan scope " + credential.getServiceAccountScopes() + " untuk aplikasi "
					+ APPLICATION_NAME);
		}

		return calendarService;
	}

}
