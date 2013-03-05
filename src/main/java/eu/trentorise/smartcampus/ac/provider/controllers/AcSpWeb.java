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
package eu.trentorise.smartcampus.ac.provider.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.adapters.AcServiceAdapter;
import eu.trentorise.smartcampus.ac.provider.adapters.AccessCodeRepository;
import eu.trentorise.smartcampus.ac.provider.adapters.AttributesAdapter;
import eu.trentorise.smartcampus.ac.provider.jaxbmodel.UserData;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.ac.provider.utils.Utils;

/**
 * Access provider REST controller
 * 
 * @author Viktor Pravdin
 */
@Controller
public class AcSpWeb {

	/**
	 * 
	 */
	private static final String AT_OFFLINE = "offline";

	/**
	 * 
	 */
	private static final String AT_ONLINE = "online";

	@Autowired
	private AcServiceAdapter service;
	@Autowired
	private AttributesAdapter attrAdapter;
	private List<String> redirectHosts;
	private static String defaultHosts = null;

	@Autowired
	private AccessCodeRepository codeRepository;

	@Autowired
	private Utils utility;

	@PostConstruct
	private void init() throws IOException {
		Properties configurations = PropertiesLoaderUtils
				.loadAllProperties("acproviderweb.properties");
		redirectHosts = new ArrayList<String>();
		for (int i = 1; i <= 100; i++) {
			String host = configurations.getProperty("ac.redirect.hosts_" + i);
			if (host != null) {
				redirectHosts.add(host.trim());
			} else {
				break;
			}
		}
	}

	/**
	 * The method generate an html page presenting the authorities available for
	 * authentication
	 * 
	 * @param model
	 * @param request
	 *            the http request
	 * @param redirect
	 *            URL to redirect the request the result with the validation code
	 * @return page to forward to
	 * @throws ValidationException
	 * @throws IntrusionException
	 * @throws ScanException
	 * @throws PolicyException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getToken")
	public String showAuthorities(
			Model model,
			HttpServletRequest request,
			@RequestParam(value = "redirect", required = false) String redirect)
			throws ValidationException, IntrusionException, ScanException,
			PolicyException {
		// FOR TESTING PURPOSES
		if (request.getParameter("TESTING") != null) {
			request.getSession().setAttribute("TESTING", true);
		}
		Map<String, String> authorities = attrAdapter.getAuthorityUrls();
		model.addAttribute("authorities", authorities);

		if (redirect != null && !redirect.isEmpty()) {
			if (!checkRedirect(redirect, redirectHosts, getDefaultHost(request))) {
				throw new IllegalArgumentException("Incorrect redirect URL: "
						+ redirect);
			}
			model.addAttribute("redirect", utility.sanitize(redirect));
		} else {
			model.addAttribute("redirect", "");
		}
		return "authorities";
	}

	private static String getDefaultHost(HttpServletRequest request) {
		if (defaultHosts == null) {
			String result = request.getServerPort() == 80 ? (request
					.getServerName() + ",") : "";
			defaultHosts = "(https|http)://(" + result
					+ request.getServerName() + ":" + request.getServerPort()
					+ ")/(.)*";
		}
		return defaultHosts;
	}

	private static boolean checkRedirect(String redirect,
			List<String> redirectHosts, String _default) {
		if (redirectHosts.isEmpty()) {
			redirectHosts.add(_default);
		}
		for (String s : redirectHosts) {
			if (redirect.matches(s))
				return true;
		}
		return false;
	}

	/**
	 * The method redirect to provided url appending to the end the
	 * authentication token for given authority. If it is present codeRequest
	 * flag, methods appends a short-living code to redirect url, the code is
	 * used in the two steps authentication to retrieve the token for
	 * authentication
	 * 
	 * @param authorityUrl
	 *            URL of authority used for authentication
	 * @param request
	 *            the http request
	 * @param response
	 *            the http response
	 * @param redirect
	 *            URL to redirect the request the result with the validation code
	 * @return
	 * @throws AcServiceException
	 * @throws IOException
	 */

	@RequestMapping(method = RequestMethod.GET, value = "/getToken/{authorityUrl}")
	public String getToken(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("authorityUrl") String authorityUrl,
			@RequestParam(value = "redirect", required = false) String redirect)
			throws AcServiceException, IOException {
		// FOR TESTING PURPOSES
		if (request.getParameter("TESTING") != null
				|| request.getSession().getAttribute("TESTING") != null) {
			request.getSession().setAttribute("TESTING", true);
			Map<String, String> authorities = attrAdapter.getAuthorityUrls();
			for (String name : authorities.keySet()) {
				if (authorityUrl.equals(authorities.get(name))) {
					List<String> attrs = attrAdapter
							.getIdentifyingAttributes(name);
					for (String a : attrs) {
						request.setAttribute(a, "sc-user");
					}
					// set some values to test alias

					// attribute with alias : see authorities.xml FBK authority
					request.setAttribute("givenName", "sc");

					request.setAttribute("Shib-Application-ID", "dummyvalue");

				}
			}
		}
		String target = "/ac/success";
		if (redirect != null && !redirect.isEmpty()) {
			if (!checkRedirect(redirect, redirectHosts, getDefaultHost(request))) {
				throw new IllegalArgumentException("Incorrect redirect URL: "
						+ redirect);
			}
			target = redirect;
		}
		
		String token = "";
		try {
			token = service.updateUser(authorityUrl, request);
			String code = codeRepository.generateAccessCode(token);
			return "redirect:" + target + "#" + code;
		} catch (SecurityException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return "redirect:/ac/denied";
		}
	}

	/**
	 * The method invalidates the token
	 * 
	 * @param token
	 *            the token to invalidate
	 * @throws AcServiceException
	 */

	@RequestMapping(method = RequestMethod.DELETE, value = "/invalidateToken/{token}")
	public void deleteToken(@RequestParam("token") String token)
			throws AcServiceException {
		service.deleteToken(token);
	}

	/**
	 * Validation of short-living code.
	 * 
	 * @param model
	 * @param request
	 *            the http request
	 * @param response
	 *            the http response
	 * @param code
	 *            code to validate
	 * @param accessType
	 *            type of access: offline (to retrieve long-living token) or online (short time token)
	 * @return the authentication token relative to the code
	 * @throws AcServiceException
	 * @throws IOException
	 */

	@RequestMapping(method = RequestMethod.POST, value = "/validateCode/{code}")
	public @ResponseBody UserData validateCode(Model model, HttpServletRequest request,
			HttpServletResponse response, 
			@PathVariable String code,
			@RequestParam(value = "accessType", required = false, defaultValue=AT_OFFLINE) String accessType)
			throws AcServiceException, IOException {
		String token = codeRepository.validateAccessCode(code);
		if (token != null) {
			UserData data = new UserData();
			User user = null; 
			
			if (AT_ONLINE.equals(accessType)) {
				user = service.getUserForSession(token); 
			} else {
				user = service.getUser(token);
			}
			
			data.setToken(token);
			data.setExpires(user.getExpTime());
			data.setSocialId(user.getSocialId());
			data.setUserId(user.getId()+"");
			data.setIdentityAttributes(new ArrayList<Attribute>());
			for (Attribute a : user.getAttributes()) {
				if (attrAdapter.isIdentityAttr(a)) {
					data.getIdentityAttributes().add(a);
				}
			}
			
			return data;
		} else {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
	}

	@RequestMapping("/success")
	public void success() {
	}

	@RequestMapping("/denied")
	public void denied() {
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public void badRequest(IllegalArgumentException ex, HttpServletResponse resp)
			throws IOException {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
	}

	@RequestMapping("/test")
	public String test() {
		return "test";
	}

}
