package com.secdec.astam.cqf.api.rest.responders;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-impl
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

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.secdec.astam.cqf.api.models.ExperimentElement;
import com.secdec.astam.cqf.api.models.util.ExperimentValidator;
import com.secdec.astam.cqf.api.rest.app.CQFApplication;
import com.secdec.astam.cqf.api.rest.app.CQFApplicationTestBase;
import com.secdec.astam.cqf.api.rest.app.CQFApplicationTestSubjectWithFullCatalog;
import com.secdec.astam.cqf.api.rest.app.CQFResourceManager;
import com.secdec.astam.cqf.api.rest.app.CQFResourceManagerTestSubject;
import com.secdec.astam.cqf.api.rest.app.aor.impl.e2e.ExperimentManagerForE2ETesting;
import com.secdec.astam.cqf.api.rest.responders.util.ResponderToolkit;
import com.secdec.astam.cqf.api.util.MockExperimentFactory;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author srogers
 */
public class ExperimentsApiResponderTest extends CQFApplicationTestBase {

	private ExperimentsApiResponder responder;

	private final String id_that_exists_y = "FIXME";
	private final String id_that_exists_n = "does_not_exist";
	private final String id_that_is_bad = null;

	private final String executionPhaseRegexp_that_matches_y_simple = "^FIXME$";
	private final String executionPhaseRegexp_that_matches_y_complex = "^(FXIME|does_not_exist)";
	private final String executionPhaseRegexp_that_matches_n_complex = "^(does_not_exist|does_not_exist02)$";
	private final String executionPhaseRegexp_that_is_bad = "id**bad";

	private final static ResponderToolkit toolkit = new ResponderToolkit();

	/**/

	@Override
	public void setUp() throws Exception {

		super.setUp();

		assertTrue(CQFResourceManager.theInstance().getExperimentManager()
				instanceof ExperimentManagerForE2ETesting
		);
		this.responder = new ExperimentsApiResponder();
		assertSame(this.rm01, this.responder.resourceManager);
	}

	@Override
	protected void initTheApplicationInstance() {

		this.app01 = new CQFApplicationTestSubjectWithFullCatalog();
	}

	@Override
	public void tearDown() throws Exception {

		this.responder = null;

		super.tearDown();
	}

	/**/

	@Test
	public void testSetUpTearDown() throws Exception {

		/**/
	}

	@Test
	public void testConstructor_default() throws Exception {

		ExperimentsApiResponder responder02 = new ExperimentsApiResponder();

		assertSame("a responder for experiments uses the singleton resource manager by default",
				rm01, responder02.resourceManager
		);
	}

	@Test
	public void testConstructor_resourceManager() throws Exception {

		CQFResourceManager rm02 = new CQFResourceManagerTestSubject();

		ExperimentsApiResponder responder02 = new ExperimentsApiResponder(rm02);

		assertSame("a responder for experiments uses the resource manager provided",
				rm02, responder02.resourceManager
		);
		assertNotSame("a responder for experiments can use a specialized resource manager",
				rm01, responder02.resourceManager
		);
	}

	@Test
	public void testCreateExperiment_forEachItemInMainDesignCatalog() {

		int i = 0;

		for (ExperimentDesignElementId designExperimentDesignElementId : getMainItemCatalog().getItems()) {

			assertNotNull(designExperimentDesignElementId.value());

			ExperimentElement experiment = MockExperimentFactory
					.newExperimentWithDesignItem(designExperimentDesignElementId.value());

			assertTrue(ExperimentValidator.accept(experiment, true, true));

			++ i;
		}

		assertTrue(15 <= i);
	}

	@Test
	public void testCreateExperiment_withEveryItemInMainDesignCatalog() {

		List<String> namesOfEveryNonRootDesignItemInCatalog = (getMainItemCatalog().getItems().stream()

				.map(id -> id.value())

				.peek(name -> assertNotNull(name))

				.filter(name -> ! name.matches(this.workspaceDesignItemNameRegex))

				.filter(name -> ! name.matches(this.archetypeDesignItemNameRegex))

				.collect(toList())
		);

		ExperimentElement experiment = MockExperimentFactory
				.newExperimentWithDesignItems(namesOfEveryNonRootDesignItemInCatalog);

		assertTrue(ExperimentValidator.accept(experiment, true, true));

		assertTrue(15 <= namesOfEveryNonRootDesignItemInCatalog.size());
	}

	@Test
	@Ignore // FIXME: srogers: convert this scenario to MultinodeArchetypeDesignElement
	public void testCreateExperiment_DotCMSScenario() throws Exception {

		CQFResourceManager rm  = CQFApplication.theInstance().getResourceManager();

		ExperimentElement ee01 = MockExperimentFactory.newExperiment_DotCMSScenario();
		assertTrue(ExperimentValidator.accept(ee01, true, false));

		ExperimentElementImpl ee01_impl = toolkit.newExperimentElementImplFromModel(ee01, null, rm);

		ExperimentElement ee02 = toolkit.newExperimentElementFromDelegate(ee01_impl, rm);
		assertTrue(ExperimentValidator.accept(ee02, true, true));
	}

	@Test
	public void testCreateExperiment_HelloWorldScenario() throws Exception {

		CQFResourceManager rm  = CQFApplication.theInstance().getResourceManager();

		ExperimentElement ee01 = MockExperimentFactory.newExperiment_HelloWorldScenario();
		assertTrue(ExperimentValidator.accept(ee01, true, false));

		ExperimentElementImpl ee01_impl = toolkit.newExperimentElementImplFromModel(ee01, null, rm);

		ExperimentElement ee02 = toolkit.newExperimentElementFromDelegate(ee01_impl, rm);
		assertTrue(ExperimentValidator.accept(ee02, true, true));
	}

	@Test
	public void testCreateExperiment_SQLInjectionAttack() throws Exception {

		CQFResourceManager rm  = CQFApplication.theInstance().getResourceManager();

		ExperimentElement ee01 = MockExperimentFactory.newExperiment_SQLInjectionAttack();
		assertTrue(ExperimentValidator.accept(ee01, true, false));

		ExperimentElementImpl ee01_impl = toolkit.newExperimentElementImplFromModel(ee01, null, rm);

		ExperimentElement ee02 = toolkit.newExperimentElementFromDelegate(ee01_impl, rm);
		assertTrue(ExperimentValidator.accept(ee02, true, true));
	}

	@Test
	@Ignore // TODO: srogers: finish implementing this unit test
	public void testCreateAndExecuteExperiment_forEachItemInMainDesignCatalog() {

		/**/
	}

	@Test
	@Ignore // TODO: srogers: finish implementing this unit test
	public void testCreateAndExecuteExperiment_withEveryItemInMainDesignCatalog() {

		/**/
	}

	@Test
	@Ignore // FIXME: srogers: convert this scenario to MultinodeArchetypeDesignElement
	public void testCreateAndExecuteExperiment_DotCMSScenario() throws Exception {

		ExperimentElement ee00 = MockExperimentFactory.newExperiment_DotCMSScenario();
		assertTrue(ExperimentValidator.accept(ee00, true, false));

		Response r01 = responder.createAndExecuteExperiment(ee00);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r01.getStatus()
		);

		ExperimentElement ee01 = (ExperimentElement) r01.getEntity();
		assertTrue(ExperimentValidator.accept(ee01, true, true));

		Response r02 = responder.getExperiment(ee01.getId());

		ExperimentElement ee02 = (ExperimentElement) r02.getEntity();
		assertTrue(ExperimentValidator.accept(ee02, true, true));

		assertEquals(ee02.getExecution(), ee01.getExecution());
		assertEquals("complete", ee02.getExecution().getPhase());
	}

	@Test
	public void testCreateAndExecuteExperiment_SQLInjectionAttack() throws Exception {

		ExperimentElement ee00 = MockExperimentFactory.newExperiment_SQLInjectionAttack();
		assertTrue(ExperimentValidator.accept(ee00, true, false));

		Response r01 = responder.createAndExecuteExperiment(ee00);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r01.getStatus()
		);

		ExperimentElement ee01 = (ExperimentElement) r01.getEntity();
		assertTrue(ExperimentValidator.accept(ee01, true, true));

		Response r02 = responder.getExperiment(ee01.getId());

		ExperimentElement ee02 = (ExperimentElement) r02.getEntity();
		assertTrue(ExperimentValidator.accept(ee02, true, true));

		assertEquals(ee02.getExecution(), ee01.getExecution());
		assertEquals("complete", ee02.getExecution().getPhase());
	}

	@Test
	@Ignore // TODO: srogers: finish implementing this unit test
	public void testGetExperiment_byId() throws Exception {

		Response r = responder.getExperiment(id_that_exists_y);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperiment_byId_notFound() throws Exception {

		Response r = responder.getExperiment(id_that_exists_n);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperiment_byId_badValue() throws Exception {

		Response r = responder.getExperiment(id_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus()
		);
	}

	@Test
	@Ignore // TODO: srogers: finish implementing this unit test
	public void testGetExperiments_all() throws Exception {

		Response r = responder.getExperiments(null);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r.getStatus()
		);
	}

	@Test
	@Ignore // TODO: srogers: finish implementing this unit test
	public void testGetExperiments_byExecutionPhasePattern() throws Exception {

		Response r01 = responder.getExperiments(executionPhaseRegexp_that_matches_y_simple);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r01.getStatus()
		);

		Response r02 = responder.getExperiments(executionPhaseRegexp_that_matches_y_complex);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r02.getStatus()
		);
	}

	@Test
	public void testGetExperiments_byExecutionPhasePattern_notFound() throws Exception {

		Response r = responder.getExperiments(executionPhaseRegexp_that_matches_n_complex);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperiments_byExecutionPhasePattern_badValue() throws Exception {

		Response r = responder.getExperiments(executionPhaseRegexp_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus()
		);
	}

}
