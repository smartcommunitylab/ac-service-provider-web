package eu.trentorise.smartcampus.ac.provider.adapters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecurityAdapter {

	private static final Logger logger = Logger
			.getLogger(SecurityAdapter.class);
	private static final String NAME_ATTR = "eu.trentorise.smartcampus.givenname";
	private static final String SURNAME_ATTR = "eu.trentorise.smartcampus.surname";
	@Autowired
	AttributesAdapter attrAdapter;
	Map<String, List<SecurityEntry>> securityMap;

	protected void init() throws IOException {
		securityMap = new HashMap<String, List<SecurityEntry>>();
		Set<String> authNames = attrAdapter.getAuthorityUrls().keySet();
		for (String authName : authNames) {
			List<SecurityEntry> securityEntries = new ArrayList<SecurityEntry>();
			InputStream in = getClass().getClassLoader().getResourceAsStream(
					authName + "-whitelist.txt");
			if (in != null) {
				BufferedReader bin = new BufferedReader(new InputStreamReader(
						in));
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
				in.close();
			}

		}
	}

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

				// security entry MUST only valid key attribute
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
