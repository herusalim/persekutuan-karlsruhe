package org.persekutuankarlsruhe.webapp.service;

import com.google.appengine.api.utils.SystemProperty;

public class SystemPropertyUtil {

	public static boolean isProductive() {
		return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
	}

}
