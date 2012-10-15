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
