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

package eu.trentorise.smartcampus.ac.provider.utils;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.stereotype.Service;

/**
 * Utility class
 * 
 * @author mirko perillo
 * 
 */
@Service
public class Utils {

	private static final String POLICY_FILE = "antisamy-total.xml";

	private Encoder encoder;
	private AntiSamy sanitizer;

	@PostConstruct
	@SuppressWarnings("unused")
	private void init() throws PolicyException {
		encoder = ESAPI.encoder();
		sanitizer = new AntiSamy(Policy.getInstance(getClass().getResource(
				POLICY_FILE)));

	}

	/**
	 * Sanitizes a string from html entities
	 * 
	 * @param s
	 *            the string to sanitize
	 * @return the sanitized string
	 * @throws ValidationException
	 * @throws IntrusionException
	 * @throws ScanException
	 * @throws PolicyException
	 */
	public String sanitize(String s) throws ValidationException,
			IntrusionException, ScanException, PolicyException {

		s = encoder.canonicalize(s);
		s = sanitizer.scan(s).getCleanHTML();
		return s;
	}

	public String retrieveDomain(String url) throws MalformedURLException {
		return retrieveDomain(new URL(url));
	}

	public String retrieveDomain(URL url) {
		return url.getHost();
	}
}
