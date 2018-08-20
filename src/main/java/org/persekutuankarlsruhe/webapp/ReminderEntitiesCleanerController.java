package org.persekutuankarlsruhe.webapp;

import java.util.Calendar;

import org.persekutuankarlsruhe.webapp.remindpelayanan.ReminderPelayananDatastore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReminderEntitiesCleanerController {

	@RequestMapping(value = "/tasks/removeexpiredreminder")
	public String removeOldEntities() {
		ReminderPelayananDatastore datastore = ReminderPelayananDatastore.getInstance();
		Calendar now = Calendar.getInstance();
		datastore.removeExpiredEntities(now.getTime());
		return "success";
	}
}
