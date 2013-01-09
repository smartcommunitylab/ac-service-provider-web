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

	@BeforeClass
	public static void setup() throws IOException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"spring/applicationContext.xml");
		security = ctx.getBean(SecurityAdapter.class);
		security.init();
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
		Assert.assertTrue(security.access("fbk", Arrays.asList("epnn-fake"),
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

}
