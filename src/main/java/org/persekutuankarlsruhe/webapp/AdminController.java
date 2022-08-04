package org.persekutuankarlsruhe.webapp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

	@GetMapping(value = "/robots.txt", produces = { MediaType.TEXT_PLAIN_VALUE })
	public String getRobotsTxt() {
		return "admin/robotstxt";
	}

	@GetMapping(value = "/admin")
	public String showUrlList(HttpServletRequest request, Model model) {
		return "admin/urlList";
	}

}
