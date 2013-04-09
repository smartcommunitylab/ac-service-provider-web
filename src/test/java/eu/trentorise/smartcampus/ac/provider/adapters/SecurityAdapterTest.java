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
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SecurityAdapterTest {

	private static SecurityAdapter security;
	private static AttributesAdapter attrs;

	@BeforeClass
	public static void setup() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"spring/applicationContext.xml");
		security = ctx.getBean(SecurityAdapter.class);
		security.init();
		attrs = ctx.getBean(AttributesAdapter.class);
		attrs.init();
	}

	@Test
	public void accessOK() {
		Map<String, String> userAttrs = new HashMap<String, String>();
		userAttrs.put("eppn", "sc-user");

		userAttrs.put("eu.trentorise.smartcampus.givenname", "sc");
		userAttrs.put("eu.trentorise.smartcampus.surname", "user");
		Assert.assertTrue(security.access("fbk", Arrays.asList("eppn"),
				userAttrs));
	}

	@Test
	public void accessKO() {
		Map<String, String> userAttrs = new HashMap<String, String>();
		userAttrs.put("epnn", "fake");

		userAttrs.put("eu.trentorise.smartcampus.givenname", "fake");
		userAttrs.put("eu.trentorise.smartcampus.surname", "user");
		Assert.assertFalse(security.access("fbk", Arrays.asList("epnn"),
				userAttrs));
	}

	@Test
	public void accessWrongIdAttr() {
		Map<String, String> userAttrs = new HashMap<String, String>();
		userAttrs.put("eppn", "sc-user");

		userAttrs.put("eu.trentorise.smartcampus.givenname", "sc");
		userAttrs.put("eu.trentorise.smartcampus.surname", "user");
		Assert.assertFalse(security.access("fbk", Arrays.asList("epnn-fake"),
				userAttrs));
	}

	@Test
	public void accessNoWhitelist() {
		Map<String, String> userAttrs = new HashMap<String, String>();
		userAttrs.put("eppn", "sc-user");

		userAttrs.put("eu.trentorise.smartcampus.givenname", "sc");
		userAttrs.put("eu.trentorise.smartcampus.surname", "user");
		Assert.assertTrue(security.access("fbk-fake", Arrays.asList("eppn"),
				userAttrs));
	}

	@Test
	public void accessNoNameAttr() {
		Map<String, String> userAttrs = new HashMap<String, String>();
		userAttrs.put("eppn", "sc-user");

		Assert.assertTrue(security.access("fbk", Arrays.asList("eppn"),
				userAttrs));
	}

	@Test
	public void testFull() throws IOException {
		BufferedReader bis = null;
		try {
			bis = new BufferedReader(new FileReader("src/test/resources/whitelist"));
			String line = null;
			while ((line=bis.readLine()) != null) {
				if (line.trim().length()==0 || line.startsWith("#")) continue;
				Map<String, String> userAttrs = new HashMap<String, String>();
				String[] elems = line.split(",");
				String auth = elems[0].trim();
				userAttrs.put("eu.trentorise.smartcampus.givenname",elems[1].trim());
				userAttrs.put("eu.trentorise.smartcampus.surname", elems[2].trim());
				if (elems.length > 3) {
					userAttrs.put(elems[3].trim(), elems[4].trim());
				}
				Assert.assertTrue(security.access(auth, attrs.getIdentifyingAttributes(auth),userAttrs));
			}
		} finally {
			if (bis != null) bis.close();
		}
	}
}
