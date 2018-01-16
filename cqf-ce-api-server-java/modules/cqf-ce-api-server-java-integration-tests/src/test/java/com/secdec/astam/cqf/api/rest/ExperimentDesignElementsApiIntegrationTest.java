package com.secdec.astam.cqf.api.rest;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-integration-tests
 * %%
 * Copyright (C) 2016 - 2017 Applied Visions, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.secdec.astam.cqf.api.models.ExperimentDesignElement;
import com.secdec.astam.cqf.api.rest.io.ApiException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author taylorj
 * @author srogers
 */

public class ExperimentDesignElementsApiIntegrationTest extends CQFApplicationIntegrationTestBase {

	@Before
	public void setup() throws Exception {

		this.api = new ExperimentDesignElementsApi();
	}

	@After
	public void teardown() throws Exception {

		this.api = null;
	}

	private ExperimentDesignElementsApi api;

	private final String name_that_exists_y = "com.siegetechnologies.cqf.design.item.util.workspace";
	private final String name_that_exists_n = "com.siegetechnologies.cqf.design.item.missing_category.foo";
	private final String name_that_is_bad = null;

	private final String subtypeRegexp_that_matches_y_simple  = "^unknown_subtype$"; // FIXME: srogers: implement subtype support
	private final String subtypeRegexp_that_matches_y_complex = "^(unknown_subtype|does_not_exist)$"; // FIXME: srogers: implement subtype support
	private final String subtypeRegexp_that_matches_n_complex = "does_not_exist";
	private final String subtypeRegexp_that_is_bad = "subtype**bad";

	private final String nameRegexp_that_matches_y_simple  = "^com[.]siegetechnologies[.]cqf[.]design[.]item[.]util[.]workspace$";
	private final String nameRegexp_that_matches_y_complex = "^.*[.]cqf[.]design[.]item[.][^.]+[.][^.]+$";
	private final String nameRegexp_that_matches_n_complex = "^.*[.]cqf[.]design[.]item[.]does_not_exist[.][^.]+$";
	private final String nameRegexp_that_is_bad = "name**bad";

	private final String subtype_that_exists_y = "unknown_subtype"; // FIXME: srogers: implement subtype support

	@Test
	public void testGetExperimentDesignElement_byName() throws Exception {

		ExperimentDesignElement de = api.getExperimentDesignElement(name_that_exists_y);
		assertPostconditionsForExpectedName(name_that_exists_y, de);
	}

	private void assertPostconditionsForExpectedName(String expectedName, ExperimentDesignElement de) {
		assertNotNull(de);

		assertEquals(expectedName, de.getName());
	}

	@Test
	public void testGetExperimentDesignElement_byName_notFound() throws Exception {
		try {
			ExperimentDesignElement de = api.getExperimentDesignElement(name_that_exists_n);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
			assertTrue(xx.getMessage().contains("Not Found"));
		}
	}

	@Test
	public void testGetExperimentDesignElement_byName_badValue() throws Exception {
		try {
			ExperimentDesignElement de = api.getExperimentDesignElement(name_that_is_bad);
			fail("expected a MISSING_PARAMETER exception");
		}
		catch (ApiException xx) {
			assertEquals(400, xx.getCode());
			assertTrue(xx.getMessage().startsWith("Missing the required parameter"));
		}
	}

	@Test
	public void testGetExperimentDesignElements_all() throws Exception {

		List<ExperimentDesignElement> del = api.getExperimentDesignElements(null, null);
		assertNotNull(del);

		assertTrue(! del.isEmpty());
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern() throws Exception {

		List<ExperimentDesignElement> del01 = api.getExperimentDesignElements(subtypeRegexp_that_matches_y_simple, null);
		assertPostconditionsForExpectedSubtype(subtype_that_exists_y, del01);

		List<ExperimentDesignElement> del02 = api.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, null);
		assertPostconditionsForExpectedSubtype(subtype_that_exists_y, del02);
	}

	private void assertPostconditionsForExpectedSubtype(String expectedSubtype, List<ExperimentDesignElement> del) {
		assertNotNull(del);

		assertTrue(1 <= del.stream()
				.filter(de -> expectedSubtype.equals(de.getSubtype()))
				                
				.count()
		);
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_notFound() throws Exception {
		try {
			List<ExperimentDesignElement> del = api.getExperimentDesignElements(subtypeRegexp_that_matches_n_complex, null);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
		}
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_badValue() throws Exception {
		try {
			List<ExperimentDesignElement> del = api.getExperimentDesignElements(subtypeRegexp_that_is_bad, null);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}
	}

	@Test
	public void testGetExperimentDesignElements_byNamePattern() throws Exception {

		List<ExperimentDesignElement> del01 = api.getExperimentDesignElements(null, nameRegexp_that_matches_y_simple);
		assertPostconditionsForExpectedName(name_that_exists_y, del01);

		List<ExperimentDesignElement> del02 = api.getExperimentDesignElements(null, nameRegexp_that_matches_y_complex);
		assertPostconditionsForExpectedName(name_that_exists_y, del02);
	}

	private void assertPostconditionsForExpectedName(String expectedName, List<ExperimentDesignElement> del) {
		assertNotNull(del);

		assertEquals(1, del.stream()
				.filter(de -> expectedName.equals(de.getName()))
				
				.count()
		);
	}

	@Test
	public void testGetExperimentDesignElements_byNamePattern_notFound() throws Exception {
		try {
			List<ExperimentDesignElement> del = api.getExperimentDesignElements(null, nameRegexp_that_matches_n_complex);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
		}
	}

	@Test
	public void testGetExperimentDesignElements_byNamePattern_badValue() throws Exception {
		try {
			List<ExperimentDesignElement> del = api.getExperimentDesignElements(null, nameRegexp_that_is_bad);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_byNamePattern() throws Exception {

		List<ExperimentDesignElement> del01 = api.getExperimentDesignElements(subtypeRegexp_that_matches_y_simple, nameRegexp_that_matches_y_simple);
		assertPostconditionsForExpectedSubtypeAndName(subtype_that_exists_y, name_that_exists_y, del01);

		List<ExperimentDesignElement> del02 = api.getExperimentDesignElements(subtypeRegexp_that_matches_y_simple, nameRegexp_that_matches_y_complex);
		assertPostconditionsForExpectedSubtypeAndName(subtype_that_exists_y, name_that_exists_y, del02);

		List<ExperimentDesignElement> del03 = api.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, nameRegexp_that_matches_y_simple);
		assertPostconditionsForExpectedSubtypeAndName(subtype_that_exists_y, name_that_exists_y, del03);

		List<ExperimentDesignElement> del04 = api.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, nameRegexp_that_matches_y_complex);
		assertPostconditionsForExpectedSubtypeAndName(subtype_that_exists_y, name_that_exists_y, del04);
	}

	private void assertPostconditionsForExpectedSubtypeAndName(String expectedSubtype, String expectedName, List<ExperimentDesignElement> del) {
		assertNotNull(del);

		assertEquals(1, del.stream()
				.filter(de -> expectedSubtype.equals(de.getSubtype()) && expectedName.equals(de.getName()))
				                
				.count()
		);
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_byNamePattern_notFound() throws Exception {
		try {
			List<ExperimentDesignElement> del01 = api.getExperimentDesignElements(subtypeRegexp_that_matches_n_complex, nameRegexp_that_matches_n_complex);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
		}

		try {
			List<ExperimentDesignElement> del02 = api.getExperimentDesignElements(subtypeRegexp_that_matches_n_complex, nameRegexp_that_matches_y_complex);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
		}

		try {
			List<ExperimentDesignElement> del03 = api.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, nameRegexp_that_matches_n_complex);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
		}
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_byNamePattern_badValue() throws Exception {
		try {
			List<ExperimentDesignElement> del01 = api.getExperimentDesignElements(subtypeRegexp_that_is_bad, nameRegexp_that_is_bad);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}

		try {
			List<ExperimentDesignElement> del02 = api.getExperimentDesignElements(subtypeRegexp_that_is_bad, nameRegexp_that_matches_n_complex);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}

		try {
			List<ExperimentDesignElement> del03 = api.getExperimentDesignElements(subtypeRegexp_that_is_bad, nameRegexp_that_matches_y_complex);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}

		try {
			List<ExperimentDesignElement> del04 = api.getExperimentDesignElements(subtypeRegexp_that_matches_n_complex, nameRegexp_that_is_bad);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}

		try {
			List<ExperimentDesignElement> del05 = api.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, nameRegexp_that_is_bad);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}
	}

}
