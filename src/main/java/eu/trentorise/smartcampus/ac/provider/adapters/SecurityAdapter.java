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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This class manages the operations on security white-lists, that contains the
 * set of users, for every authority, qualified to access the system
 * 
 * @author mirko perillo
 * 
 */
@Component
public class SecurityAdapter {

	private static final Logger logger = Logger
			.getLogger(SecurityAdapter.class);
	private static final String NAME_ATTR = "eu.trentorise.smartcampus.givenname";
	private static final String SURNAME_ATTR = "eu.trentorise.smartcampus.surname";
	@Autowired
	AttributesAdapter attrAdapter;
	private static Map<String, List<SecurityEntry>> securityMap;

	/**
	 * The methods reads at scheduled time authorities white-list files
	 * 
	 * @throws IOException
	 */

	// fixedRate is in ms
	@Scheduled(fixedRate = 30000)
	public void refreshSecurityList() throws IOException {
		if (securityMap == null) {
			securityMap = new HashMap<String, List<SecurityEntry>>();
		} else {
			securityMap.clear();
		}
		Set<String> authNames = attrAdapter.getAuthorityUrls().keySet();
		for (String authName : authNames) {
			List<SecurityEntry> securityEntries = new ArrayList<SecurityEntry>();
			URL fileUrl = getClass().getClassLoader().getResource(
					authName + "-whitelist.txt");
			if (fileUrl != null) {
				BufferedReader bin = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileUrl.getFile())));
				String line = null;
				while ((line = bin.readLine()) != null) {
					line = line.trim();
					// bypass comment lines or empty lines
					if (line.length() == 0 || line.startsWith("#")) {
						continue;
					}
					String[] token = line.split(",");
					SecurityEntry se = new SecurityEntry();
					se.setNameValue(token[0].trim());
					se.setSurnameValue(token[1].trim());
					if (token.length > 2) {
						String[] idNames = token[2].split("\\|");
						String[] idValues = token[3].split("\\|");
						for (int i = 0; i < idNames.length; i++) {
							se.addIdSecurityEntry(idNames[i].trim(),
									idValues[i].trim());
						}
					}
					securityEntries.add(se);
				}
				securityMap.put(authName, securityEntries);
				bin.close();
			}
		}
		logger.info("Security list refreshed");
	}

	protected void init() throws IOException {
		refreshSecurityList();
	}

	/**
	 * Method checks if a user (defined by its list of attributes) is authorized
	 * to access the system.
	 * 
	 * First it's checked the list of attributes passed as checkAttrs, then, if
	 * this check fails, name and surname of the user
	 * 
	 * @param authName
	 *            authority name to get list of attributes
	 * @param checkAttrs
	 *            list of attributes to check
	 * @param attrs
	 *            user attributes
	 * @return true if user attribute is present in authority white-list, false
	 *         otherwise
	 */
	public boolean access(String authName, List<String> checkAttrs,
			Map<String, String> attrs) {
		List<SecurityEntry> securityList = securityMap.get(authName);

		boolean idAttrCheck = false;
		boolean nameCheck = false;
		if (securityList != null) {
			for (SecurityEntry se : securityList) {
				// check for name and surname
				if (!nameCheck) {
					try {
						nameCheck |= attrs.get(NAME_ATTR).equals(
								se.getNameValue())
								&& attrs.get(SURNAME_ATTR).equals(
										se.getSurnameValue());
					} catch (NullPointerException e) {
						nameCheck = false;
					}
				}

				// check id attrs

				// security entry MUST contain only valid key attribute
				boolean access = !Collections.disjoint(checkAttrs, se
						.getIdSecurityEntries().keySet());
				if (!idAttrCheck && !se.getIdSecurityEntries().isEmpty()) {
					for (String idAttr : checkAttrs) {
						try {
							if (se.getIdSecurityEntries().get(idAttr) != null) {
								access &= attrs.get(idAttr).equals(
										se.getIdSecurityEntries().get(idAttr));
							}
						} catch (NullPointerException e) {
							access = false;
						}
						if (!access) {
							break;
						}
					}
					idAttrCheck = access;
				}

				if (idAttrCheck) {
					break;
				}
			}
		} else {
			return true;
		}
		if (!(idAttrCheck || nameCheck)) {
			String attrValues = "";
			for (String attrKey : checkAttrs) {
				attrValues += attrKey + " -> " + attrs.get(attrKey) + " ";
			}
			logger.warn("Authentication failed. User: givenname -> "
					+ attrs.get(NAME_ATTR) + " surname -> "
					+ attrs.get(SURNAME_ATTR) + " " + attrValues);
		}
		return idAttrCheck || nameCheck;
	}
}
