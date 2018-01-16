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

import com.secdec.astam.cqf.api.models.ExperimentDesignCatalog;
import com.secdec.astam.cqf.api.rest.io.ApiException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author srogers
 */

public class ExperimentDesignCatalogsApiIntegrationTest extends CQFApplicationIntegrationTestBase {

	@Before
	public void setup() throws Exception {

		this.api = new ExperimentDesignCatalogsApi();
	}

	@After
	public void teardown() throws Exception {

		this.api = null;
	}

	private ExperimentDesignCatalogsApi api;

	private final String name_that_exists_y = "main";
	private final String name_that_exists_n = "does_not_exist";
	private final String name_that_is_bad = null;

	private final String nameRegexp_that_matches_y_simple = "^main$";
	private final String nameRegexp_that_matches_y_complex = "^(m..n|does_not_exist)";
	private final String nameRegexp_that_matches_n_complex = "^(does_not_exist|does_not_exist02)$";
	private final String nameRegexp_that_is_bad = "name**bad";

	@Test
	public void testGetExperimentDesignCatalog_byName() throws Exception {

		ExperimentDesignCatalog dc = api.getExperimentDesignCatalog(name_that_exists_y);
		assertPostconditionsForExpectedName(name_that_exists_y, dc);
	}

	private void assertPostconditionsForExpectedName(String expectedName, ExperimentDesignCatalog dc) {
		assertNotNull(dc);

		assertEquals(expectedName, dc.getName());
	}

	@Test
	public void testGetExperimentDesignCatalog_byName_notFound() throws Exception {
		try {
			ExperimentDesignCatalog dc = api.getExperimentDesignCatalog(name_that_exists_n);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
			assertTrue(xx.getMessage().contains("Not Found"));
		}
	}

	@Test
	public void testGetExperimentDesignCatalog_byName_badValue() throws Exception {
		try {
			ExperimentDesignCatalog dc = api.getExperimentDesignCatalog(name_that_is_bad);
			fail("expected a MISSING_PARAMETER exception");
		}
		catch (ApiException xx) {
			assertEquals(400, xx.getCode());
			assertTrue(xx.getMessage().startsWith("Missing the required parameter"));
		}
	}

	@Test
	public void testGetExperimentDesignCatalogs_all() throws Exception {

		List<ExperimentDesignCatalog> dcl = api.getExperimentDesignCatalogs(null);
		assertNotNull(dcl);

		assertTrue(! dcl.isEmpty());
	}

	@Test
	public void testGetExperimentDesignCatalogs_byNamePattern() throws Exception {

		List<ExperimentDesignCatalog> dcl01 = api.getExperimentDesignCatalogs(nameRegexp_that_matches_y_simple);
		assertPostconditionsForExpectedName(name_that_exists_y, dcl01);

		List<ExperimentDesignCatalog> dcl02 = api.getExperimentDesignCatalogs(nameRegexp_that_matches_y_complex);
		assertPostconditionsForExpectedName(name_that_exists_y, dcl02);
	}

	private void assertPostconditionsForExpectedName(String expectedName, List<ExperimentDesignCatalog> dcl) {
		assertNotNull(dcl);

		assertEquals(1, dcl.stream()
                .filter(dc -> expectedName.equals(dc.getName()))

                .count()
		);
	}

	@Test
	public void testGetExperimentDesignCatalogs_byNamePattern_notFound() throws Exception {
		try {
			List<ExperimentDesignCatalog> dcl = api.getExperimentDesignCatalogs(nameRegexp_that_matches_n_complex);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
		}
	}

	@Test
	public void testGetExperimentDesignCatalogs_byNamePattern_badValue() throws Exception {
		try {
			List<ExperimentDesignCatalog> dcl = api.getExperimentDesignCatalogs(nameRegexp_that_is_bad);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}
	}

}
