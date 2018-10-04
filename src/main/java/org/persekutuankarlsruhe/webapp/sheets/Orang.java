package org.persekutuankarlsruhe.webapp.sheets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

public class Orang implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7695428936472603878L;

	String nama;
	String email;
	List<String> aliases = new ArrayList<String>();

	public Orang(String nama, String email) {
		this.nama = nama;
		this.email = email;
	}

	public String getNama() {
		return nama;
	}

	public String getEmail() {
		return email;
	}

	public String toString() {
		return nama + (StringUtils.isEmpty(email) ? "" : (" <" + email + ">"));
	}

	public void addAlias(String alias) {
		aliases.add(alias);
	}

	public List<String> getAliases() {
		return Collections.unmodifiableList(aliases);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			String nama = (String) obj;
			if (nama.toLowerCase().equals(this.nama.toLowerCase())) {
				return true;
			} else {
				boolean found = false;
				for (String alias : aliases) {
					if (nama.toLowerCase().equals(alias.toLowerCase())) {
						found = true;
						break;
					}
				}
				return found;
			}
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		String aliasString = "";
		for (String alias : aliases) {
			aliasString += alias;
		}
		return (toString() + "Alias:" + aliasString).hashCode();
	}
}
