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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.secdec.astam.cqf.api.models.ExperimentElement;
import com.secdec.astam.cqf.api.models.util.ExperimentValidator;
import com.secdec.astam.cqf.api.rest.io.ApiException;
import com.secdec.astam.cqf.api.util.MockExperimentFactory;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author srogers
 */

public class ExperimentsApiIntegrationTest extends CQFApplicationIntegrationTestBase {

	@Before
	public void setUp() throws Exception {

		super.setUp();

		this.api = new ExperimentsApi();
	}

	@After
	public void tearDown() throws Exception {

		this.api = null;

		super.tearDown();
	}

	private ExperimentsApi api;

	private final String id_that_exists_y = "none";
	private final String id_that_exists_n = "does_not_exist";
	private final String id_that_is_bad = null;

	private final String idRegexp_that_matches_y_simple = "^main$";
	private final String idRegexp_that_matches_y_complex = "^(m..n|does_not_exist)";
	private final String idRegexp_that_matches_n_complex = "^(does_not_exist|does_not_exist02)$";
	private final String idRegexp_that_is_bad = "id**bad";

	@Test
	@Ignore
	public void testCreateAndExecuteExperiment_forEachItemInMainDesignCatalog() {

		/**/
	}

	@Test
	@Ignore
	public void testCreateAndExecuteExperiment_withEveryItemInMainDesignCatalog() {

		/**/
	}

	@Test
	@Ignore // FIXME: srogers: convert this scenario to MultinodeArchetypeDesignElement
	public void testCreateAndExecuteExperiment_DotCMSScenario() throws Exception {

		ExperimentElement ee00 = MockExperimentFactory.newExperiment_DotCMSScenario();
		assertTrue(ExperimentValidator.accept(ee00, true, false));

		ExperimentElement ee01 = api.createAndExecuteExperiment(ee00);
		assertTrue(ExperimentValidator.accept(ee01, true, true));

		ExperimentElement ee02 = api.getExperiment(ee01.getId());
		assertTrue(ExperimentValidator.accept(ee02, true, true));

//		assertNotEquals(ee02.getExecution(), ee01.getExecution()); // FIXME: HACK: srogers: re-enable this assertion
//		assertEquals("run", ee02.getExecution().getPhase()); // FIXME: HACK: srogers: should expect "complete"
	}

	@Test
	public void testCreateAndExecuteExperiment_SQLInjectionAttack() throws Exception {

		ExperimentElement ee00 = MockExperimentFactory.newExperiment_SQLInjectionAttack();
		assertTrue(ExperimentValidator.accept(ee00, true, false));

		ExperimentElement ee01 = api.createAndExecuteExperiment(ee00);
		assertTrue(ExperimentValidator.accept(ee01, true, true));

		ExperimentElement ee02 = api.getExperiment(ee01.getId());
		assertTrue(ExperimentValidator.accept(ee02, true, true));

//		assertNotEquals(ee02.getExecution(), ee01.getExecution()); // FIXME: HACK: srogers: re-enable this assertion
//		assertEquals("run", ee02.getExecution().getPhase()); // FIXME: HACK: srogers: should expect "complete"
	}

	@Test
	@Ignore
	public void testGetExperiment_byId() throws Exception {

		ExperimentElement ee = api.getExperiment(id_that_exists_y);
		assertPostconditionsForExpectedId(id_that_exists_y, ee);
	}

	private void assertPostconditionsForExpectedId(String expectedId, ExperimentElement ee) {
		assertNotNull(ee);

		assertEquals(expectedId, ee.getId());
	}

	@Test
	public void testGetExperiment_byId_notFound() throws Exception {
		try {
			ExperimentElement ee = api.getExperiment(id_that_exists_n);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
			assertTrue(xx.getMessage().contains("Not Found"));
		}
	}

	@Test
	public void testGetExperiment_byId_badValue() throws Exception {
		try {
			ExperimentElement ee = api.getExperiment(id_that_is_bad);
			fail("expected a MISSING_PARAMETER exception");
		}
		catch (ApiException xx) {
			assertEquals(400, xx.getCode());
			assertTrue(xx.getMessage().startsWith("Missing the required parameter"));
		}
	}

	@Test
	@Ignore
	public void testGetExperiments_all() throws Exception {

		List<ExperimentElement> eel = api.getExperiments(null);
		assertNotNull(eel);

		assertTrue(! eel.isEmpty());
	}

	@Test
	@Ignore
	public void testGetExperiments_byIdPattern() throws Exception {

		List<ExperimentElement> eel01 = api.getExperiments(idRegexp_that_matches_y_simple);
		assertPostconditionsForExpectedId(id_that_exists_y, eel01);

		List<ExperimentElement> eel02 = api.getExperiments(idRegexp_that_matches_y_complex);
		assertPostconditionsForExpectedId(id_that_exists_y, eel02);
	}

	private void assertPostconditionsForExpectedId(String expectedId, List<ExperimentElement> eel) {
		assertNotNull(eel);

		assertEquals(1, eel.stream()
                .filter(ee -> expectedId.equals(ee.getId()))

                .count()
		);
	}

	@Test
	public void testGetExperiments_byIdPattern_notFound() throws Exception {
		try {
			List<ExperimentElement> eel = api.getExperiments(idRegexp_that_matches_n_complex);
			fail("expected a NOT_FOUND exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), xx.getCode());
		}
	}

	@Test
	public void testGetExperiments_byIdPattern_badValue() throws Exception {
		try {
			List<ExperimentElement> eel = api.getExperiments(idRegexp_that_is_bad);
			fail("expected a BAD_REQUEST exception");
		}
		catch (ApiException xx) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), xx.getCode());
		}
	}

}
