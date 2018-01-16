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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import com.secdec.astam.cqf.api.rest.app.CQFApplicationTestBase;
import com.secdec.astam.cqf.api.rest.app.CQFApplicationTestSubjectWithFullCatalog;
import com.secdec.astam.cqf.api.rest.app.CQFResourceManager;
import com.secdec.astam.cqf.api.rest.app.CQFResourceManagerTestSubject;
import javax.ws.rs.core.Response;
import org.junit.Test;

/**
 * @author srogers
 */
public class ExperimentDesignCatalogsApiResponderTest extends CQFApplicationTestBase {
	
	private ExperimentDesignCatalogsApiResponder responder;
	
	private final String name_that_exists_y = "main";
	private final String name_that_exists_n = "does_not_exist";
	private final String name_that_is_bad = null;
	
	private final String nameRegexp_that_matches_y_simple = "^main$";
	private final String nameRegexp_that_matches_y_complex = "^(m..n|does_not_exist)";
	private final String nameRegexp_that_matches_n_complex = "^(does_not_exist|does_not_exist02)$";
	private final String nameRegexp_that_is_bad = "name**bad";
	
	/**/
	
	@Override
	public void setUp() throws Exception {

		super.setUp();

		this.responder = new ExperimentDesignCatalogsApiResponder();
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

	@Test
	public void testSetUpTearDown() throws Exception {

		/**/
	}

	@Test
	public void testConstructor_default() throws Exception {

		ExperimentDesignCatalogsApiResponder responder02 =
				new ExperimentDesignCatalogsApiResponder();

		assertSame("a responder for experiment design catalogs uses the singleton resource manager by default",
				rm01, responder02.resourceManager
		);
	}

	@Test
	public void testConstructor_resourceManager() throws Exception {

		CQFResourceManager rm02 = new CQFResourceManagerTestSubject();

		ExperimentDesignCatalogsApiResponder responder02 = new ExperimentDesignCatalogsApiResponder(rm02);

		assertSame("a responder for experiment design catalogs uses the resource manager provided",
				rm02, responder02.resourceManager
		);
		assertNotSame("a responder for experiment design catalogs can use a specialized resource manager",
				rm01, responder02.resourceManager
		);
	}

	@Test
	public void testGetExperimentDesignCatalog_byName() throws Exception {

		Response r = responder.getExperimentDesignCatalog(name_that_exists_y);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignCatalog_byName_notFound() throws Exception {

		Response r = responder.getExperimentDesignCatalog(name_that_exists_n);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignCatalog_byName_badValue() throws Exception {

		Response r = responder.getExperimentDesignCatalog(name_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignCatalogs_all() throws Exception {

		Response r = responder.getExperimentDesignCatalogs(null);
		
		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignCatalogs_byNamePattern() throws Exception {

		Response r01 = responder.getExperimentDesignCatalogs(nameRegexp_that_matches_y_simple);
		
		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r01.getStatus()
		);

		Response r02 = responder.getExperimentDesignCatalogs(nameRegexp_that_matches_y_complex);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r02.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignCatalogs_byNamePattern_notFound() throws Exception {

		Response r = responder.getExperimentDesignCatalogs(nameRegexp_that_matches_n_complex);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignCatalogs_byNamePattern_badValue() throws Exception {

		Response r = responder.getExperimentDesignCatalogs(nameRegexp_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus()
		);
	}

}
