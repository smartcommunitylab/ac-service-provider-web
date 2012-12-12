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

import org.apache.log4j.Logger;
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

import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.adapters.AcServiceAdapter;
import eu.trentorise.smartcampus.ac.provider.adapters.AccessCodeRepository;
import eu.trentorise.smartcampus.ac.provider.adapters.AttributesAdapter;
import eu.trentorise.smartcampus.ac.provider.utils.Utils;

/**
 * 
 * @author Viktor Pravdin
 */
@Controller
public class AcSpWeb {

	private static final Logger logger = Logger.getLogger(AcSpWeb.class);

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

	@SuppressWarnings("unused")
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
	 * @param browserRequest
	 *            flag to identify a browser request (actually not used)
	 * @param codeRequest
	 *            flag to identify a two steps token retrieving
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
			@RequestParam(value = "browser", required = false) String browserRequest,
			@RequestParam(value = "code", required = false) String codeRequest)
			throws ValidationException, IntrusionException, ScanException,
			PolicyException {
		// FOR TESTING PURPOSES
		if (request.getParameter("TESTING") != null) {
			request.getSession().setAttribute("TESTING", true);
		}
		// used to attach browser parameter to getToken urls
		if (browserRequest != null) {
			model.addAttribute("browser", "");
		}
		// used to attach two-phase code parameter to getToken urls
		if (codeRequest != null) {
			model.addAttribute("code", "");
		}
		Map<String, String> authorities = attrAdapter.getAuthorityUrls();
		model.addAttribute("authorities", authorities);

		String redirect = request.getParameter("redirect");
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
	 * @param browserRequest
	 *            flag to identify a browser request (actually not used)
	 * @param codeRequest
	 *            flag to identify a two steps token retrieving
	 * @return
	 * @throws AcServiceException
	 * @throws IOException
	 */

	@RequestMapping(method = RequestMethod.GET, value = "/getToken/{authorityUrl}")
	public String getToken(
			@PathVariable("authorityUrl") String authorityUrl,
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "browser", required = false) String browserRequest,
			@RequestParam(value = "code", required = false) String codeRequest)
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

		String redirect = request.getParameter("redirect");

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
		} catch (SecurityException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			target = "/ac/denied";
		}

		if (codeRequest != null) {
			String code = codeRepository.generateAcessCode(token);
			return "redirect:" + target + "#" + code;
		} else {
			return "redirect:" + target + "#" + token;
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
	 * @return the authentication token relative to the code
	 * @throws AcServiceException
	 * @throws IOException
	 */

	@RequestMapping(method = RequestMethod.POST, value = "/validateCode/{code}")
	public String validateCode(Model model, HttpServletRequest request,
			HttpServletResponse response, @PathVariable String code)
			throws AcServiceException, IOException {
		String token = codeRepository.validateAccessCode(code);
		if (token != null) {
			model.addAttribute("token", token);
			return "validated";
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
