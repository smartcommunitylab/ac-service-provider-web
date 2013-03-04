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
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Repository of access codes for the two-step authentication
 * 
 * @author mirko perillo
 * 
 */
@Component
public class AccessCodeRepository {

	private final static int EXP_TIME = 60000;

	private static Map<String, CodeDescriptor> map = new HashMap<String, CodeDescriptor>();

	/**
	 * Generation of a short-living access code
	 * 
	 * @param authToken
	 *            token to bind with generated access code to
	 * @return the access code generated
	 */
	public synchronized String generateAccessCode(String authToken) {
		String code = UUID.randomUUID().toString();
		CodeDescriptor d = new CodeDescriptor(authToken,
				System.currentTimeMillis() + EXP_TIME);
		map.put(code, d);
		return code;
	}

	/**
	 * Validation of the access code
	 * 
	 * @param code
	 *            access code to validate
	 * @return if code is valid then the binded authentication token is
	 *         retrieved otherwise null
	 */

	public synchronized String validateAccessCode(String code) {
		if (map.get(code) != null) {
			CodeDescriptor d = map.remove(code);
			if (System.currentTimeMillis() < d.expTime) {
				return d.authToken;
			}
		}
		return null;
	}

	private static class CodeDescriptor {
		String authToken;
		long expTime;

		public CodeDescriptor(String authToken, long expTime) {
			super();
			this.authToken = authToken;
			this.expTime = expTime;
		}
	}

}
