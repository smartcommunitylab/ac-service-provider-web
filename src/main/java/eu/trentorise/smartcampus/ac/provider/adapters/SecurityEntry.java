/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SecurityEntry [nameValue=" + nameValue + ", surnameValue="
				+ surnameValue + ", idSecurityEntries=" + idSecurityEntries
				+ "]";
	}
}
