package org.persekutuankarlsruhe.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CalendarGenerationInfoController {

	@RequestMapping(value = "/calendargen/info")
	public String info() {
		return "calendar_gen_info";
	}
}
