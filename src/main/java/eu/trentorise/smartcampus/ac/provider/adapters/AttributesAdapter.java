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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.adapters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.jaxbmodel.Attributes;
import eu.trentorise.smartcampus.ac.provider.jaxbmodel.Authorities;
import eu.trentorise.smartcampus.ac.provider.jaxbmodel.AuthorityMapping;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.utils.Utils;

/**
 * This class manages all operations on attributes
 * 
 * @author Viktor Pravdin
 */
@Component
public class AttributesAdapter {

	@Autowired
	private AcServiceAdapter service;
	private Map<String, AuthorityMapping> authorities;
	private Map<String, Set<String>> identityAttributes;

	// Called from the AcServiceAdapter's init
	protected void init() throws JAXBException, AcServiceException {
		JAXBContext jaxb = JAXBContext.newInstance(AuthorityMapping.class,
				Authorities.class);
		Unmarshaller unm = jaxb.createUnmarshaller();
		JAXBElement<Authorities> element = (JAXBElement<Authorities>) unm
				.unmarshal(
						new StreamSource(getClass().getResourceAsStream(
								"authorities.xml")), Authorities.class);
		Authorities auths = element.getValue();
		authorities = new HashMap<String, AuthorityMapping>();
		identityAttributes = new HashMap<String, Set<String>>();
		for (AuthorityMapping mapping : auths.getAuthorityMapping()) {
			Authority auth = service.getAuthorityByName(mapping.getName());
			if (auth == null) {
				auth = new Authority();
				auth.setName(mapping.getName());
				auth.setRedirectUrl(mapping.getUrl());
				service.createAuthority(auth);
			}
			authorities.put(mapping.getName(), mapping);
			Set<String> identities = new HashSet<String>();
			if (mapping.getIdentifyingAttributes() != null) {
				identities.addAll(mapping.getIdentifyingAttributes());
			}
			identityAttributes.put(auth.getName(), identities);
		}
	}

	/**
	 * Retrieve from http request the attribute of a specified authority
	 * 
	 * @param authority
	 *            the authority specified
	 * @param request
	 *            the http request to process
	 * @return a map of user attributes
	 */
	public Map<String, String> getAttributes(String authority,
			HttpServletRequest request) {
		AuthorityMapping mapping = authorities.get(authority);
		if (mapping == null) {
			throw new IllegalArgumentException("Unsupported authority: "
					+ authority);
		}
		Map<String, String> attrs = new HashMap<String, String>();
		if ("google".equals(authority) && request.getParameter("token") != null) {
			String token = request.getParameter("token");
			Map<String,Object> map = Utils.getUserFromGoogle(token);
			if (map != null) {
				String value = (String)map.get("given_name");
				if (value != null) attrs.put("eu.trentorise.smartcampus.givenname", value);
				value = (String)map.get("family_name");
				if (value != null) attrs.put("eu.trentorise.smartcampus.surname", value);
				if (map.containsKey("email")) attrs.put("openid.ext1.value.email", (String)map.get("email"));
				else attrs.put("openid.ext1.value.email", (String)map.get("sub"));
				if (attrs.get("openid.ext1.value.email") == null) attrs.clear();
			}
			
		} else {
			for (String key : mapping.getIdentifyingAttributes()) {
				Object value = readAttribute(request, key, mapping.isUseParams());
				if (value != null) {
					attrs.put(key, value.toString());
				}
			}
			for (Attributes attribute : mapping.getAttributes()) {
				// used alias if present to set attribute in map
				String key = (attribute.getAlias() != null && !attribute.getAlias()
						.isEmpty()) ? attribute.getAlias() : attribute.getValue();
				Object value = readAttribute(request,attribute.getValue(), mapping.isUseParams());
				if (value != null) {
					attrs.put(key, value.toString());
				}
			}
		}
		
		return attrs;
	}

	/**
	 * @param request
	 * @param key
	 * @return
	 */
	private Object readAttribute(HttpServletRequest request, String key, boolean useParams) {
		if (useParams) return request.getParameter(key);
		return request.getAttribute(key);
	}

	/**
	 * Returns of the identifying attributes of an authority
	 * 
	 * @param authority
	 *            the authority
	 * @return the list of identifying attributes for the given authority
	 */
	public List<String> getIdentifyingAttributes(String authority) {
		AuthorityMapping mapping = authorities.get(authority);
		if (mapping == null) {
			throw new IllegalArgumentException("Unsupported authority: "
					+ authority);
		}
		return mapping.getIdentifyingAttributes();
	}

	/**
	 * Returns of the authorities available
	 * 
	 * @return the map of authorities, the key is authority name and the value
	 *         is its url
	 */

	public Map<String, String> getAuthorityUrls() {
		Map<String, String> map = new HashMap<String, String>();
		for (AuthorityMapping mapping : authorities.values()) {
			map.put(mapping.getName(), mapping.getUrl());
		}
		return map;
	}

	/**
	 * @param a
	 * @return true if the specified attribute is the identity attribute for the authority
	 */
	public boolean isIdentityAttr(Attribute a) {
		return identityAttributes.containsKey(a.getAuthority().getName()) && 
				identityAttributes.get(a.getAuthority().getName()).contains(a.getKey());
	}
}
