package eu.trentorise.smartcampus.ac.provider.adapters;

import java.util.HashMap;
import java.util.Map;

/**
 * Security entry of a white-list
 * 
 * @author mirko perillo
 * 
 */
public class SecurityEntry {
	private String nameValue;
	private String surnameValue;
	private Map<String, String> idSecurityEntries;

	public SecurityEntry() {
		idSecurityEntries = new HashMap<String, String>();
	}

	public String getNameValue() {
		return nameValue;
	}

	public void setNameValue(String nameValue) {
		this.nameValue = nameValue;
	}

	public String getSurnameValue() {
		return surnameValue;
	}

	public void setSurnameValue(String surnameValue) {
		this.surnameValue = surnameValue;
	}

	public void addIdSecurityEntry(String key, String value) {
		idSecurityEntries.put(key, value);
	}

	public Map<String, String> getIdSecurityEntries() {
		return idSecurityEntries;
	}

	public void setIdSecurityEntries(Map<String, String> idSecurityEntries) {
		this.idSecurityEntries = idSecurityEntries;
	}

}
