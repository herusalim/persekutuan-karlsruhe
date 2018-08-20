package org.persekutuankarlsruhe.webapp.calendar;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.persekutuankarlsruhe.webapp.service.GoogleAPIServiceProvider;
import org.persekutuankarlsruhe.webapp.service.SystemPropertyUtil;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class CalendarDataProvider {

	private static final String CALENDAR_ID_PERSEKUTUAN_KARLSRUHE_PROD = "persekutuan.ka@gmail.com";
	private static final String CALENDAR_ID_PERSEKUTUAN_KARLSRUHE_DEV = "k3i0nonahntnr68n91sa9rk1d0@group.calendar.google.com";

	Calendar service;

	private Calendar getService() throws IOException {
		if (service == null) {
			service = GoogleAPIServiceProvider.getCalendarService();
		}
		return service;
	}

	public String getCalendarId() {
		if (SystemPropertyUtil.isProductive()) {
			return CALENDAR_ID_PERSEKUTUAN_KARLSRUHE_PROD;
		} else {
			return CALENDAR_ID_PERSEKUTUAN_KARLSRUHE_DEV;
		}
	}

	public List<Event> getCalendarEvents(Date tanggalTerakhir) throws IOException {

		java.util.Calendar tempCalendar = java.util.Calendar.getInstance();
		tempCalendar.setTime(tanggalTerakhir);
		tempCalendar.add(java.util.Calendar.DATE, 1);
		Date tanggalTerakhirPlusSatu = tempCalendar.getTime();
		DateTime terakhir = new DateTime(tanggalTerakhirPlusSatu);

		DateTime now = new DateTime(System.currentTimeMillis());
		Events events = getService().events().list(getCalendarId()).setTimeMin(now).setTimeMax(terakhir).execute();
		return events.getItems();

	}

	public void addEventToCalendar(Event event) throws IOException {
		getService().events().insert(getCalendarId(), event).execute();
	}

	public void updateEventToCalendar(Event fromEvent, Event toEvent) throws IOException {
		getService().events().update(getCalendarId(), fromEvent.getId(), toEvent).execute();
	}

}
