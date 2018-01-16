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
import java.util.Set;
import javax.ws.rs.core.Response;
import org.junit.Test;

/**
 * @author taylorj
 * @author srogers
 */
public class ExperimentDesignElementsApiResponderTest extends CQFApplicationTestBase {
	
	private ExperimentDesignElementsApiResponder responder;
	
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

	/**/
	
	@Override
	public void setUp() throws Exception {
		
		super.setUp();
		
		this.responder = new ExperimentDesignElementsApiResponder();
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

		ExperimentDesignElementsApiResponder responder02 =
				new ExperimentDesignElementsApiResponder();

		assertSame("a responder for experiment design elements uses the singleton resource manager by default",
				rm01, responder02.resourceManager
		);
	}

	@Test
	public void testConstructor_resourceManager() throws Exception {

		CQFResourceManager rm02 = new CQFResourceManagerTestSubject();

		ExperimentDesignElementsApiResponder responder02 = new ExperimentDesignElementsApiResponder(rm02);

		assertSame("a responder for experiment design elements uses the resource manager provided",
				rm02, responder02.resourceManager
		);
		assertNotSame("a responder for experiment design elements can use a specialized resource manager",
				rm01, responder02.resourceManager
		);
	}

	@Test
	public void testGetExperimentDesignElement_byName() throws Exception {

		Response r = responder.getExperimentDesignElement(name_that_exists_y);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElement_byName_notFound() throws Exception {

		Response r = responder.getExperimentDesignElement(name_that_exists_n);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElement_byName_badValue() throws Exception {

		Response r = responder.getExperimentDesignElement(name_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElement_byName_forEachItemInMainDesignCatalog() throws Exception {

		Set<String> itemNames = this.getAllDesignItemNamesIn(getMainItemCatalog(), null);

		for (String itemName : itemNames) {

			Response r01 = responder.getExperimentDesignElement(itemName);

			assertEquals("responds with status OK",
					Response.Status.OK.getStatusCode(), r01.getStatus()
			);
		}
	}

	@Test
	public void testGetExperimentDesignElements_all() throws Exception {

		Response r = responder.getExperimentDesignElements(null, null);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern() throws Exception {

		Response r01 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_y_simple, null);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r01.getStatus()
		);

		Response r02 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, null);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r02.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_notFound() throws Exception {

		Response r = responder.getExperimentDesignElements(subtypeRegexp_that_matches_n_complex, null);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_badValue() throws Exception {

		Response r = responder.getExperimentDesignElements(subtypeRegexp_that_is_bad, null);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_byNamePattern() throws Exception {

		Response r01 = responder.getExperimentDesignElements(null, nameRegexp_that_matches_y_simple);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r01.getStatus()
		);

		Response r02 = responder.getExperimentDesignElements(null, nameRegexp_that_matches_y_complex);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r02.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_byNamePattern_notFound() throws Exception {

		Response r = responder.getExperimentDesignElements(null, nameRegexp_that_matches_n_complex);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_byNamePattern_badValue() throws Exception {

		Response r = responder.getExperimentDesignElements(null, nameRegexp_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_byNamePattern_forEachItemCategoryInMainDesignCatalog() throws Exception {

		Set<String> categoryNames =
				getAllDesignItemCategoryNamesIn(getMainItemCatalog(), null);

		for (String categoryName : categoryNames) {

			String categoryNameRegexp = String.format(
					"^(.+)[.]cqf[.]design[.]item[.](%1$s)[.](.+)$",
					categoryName.toLowerCase()
			);
			Response r01 = responder.getExperimentDesignElements(null, categoryNameRegexp);

			assertEquals("responds with status OK",
					Response.Status.OK.getStatusCode(), r01.getStatus()
			);
		}
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_byNamePattern() throws Exception {

		Response r01 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_y_simple, nameRegexp_that_matches_y_simple);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r01.getStatus()
		);

		Response r02 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_y_simple, nameRegexp_that_matches_y_complex);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r02.getStatus()
		);

		Response r03 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, nameRegexp_that_matches_y_simple);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r03.getStatus()
		);

		Response r04 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, nameRegexp_that_matches_y_complex);

		assertEquals("responds with status OK",
				Response.Status.OK.getStatusCode(), r04.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_byNamePattern_notFound() throws Exception {

		Response r01 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_n_complex, nameRegexp_that_matches_n_complex);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r01.getStatus()
		);

		Response r02 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_n_complex, nameRegexp_that_matches_y_complex);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r02.getStatus()
		);

		Response r03 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, nameRegexp_that_matches_n_complex);

		assertEquals("responds with status NOT_FOUND",
				Response.Status.NOT_FOUND.getStatusCode(), r03.getStatus()
		);
	}

	@Test
	public void testGetExperimentDesignElements_bySubtypePattern_byNamePattern_badValue() throws Exception {

		Response r01 = responder.getExperimentDesignElements(subtypeRegexp_that_is_bad, nameRegexp_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r01.getStatus()
		);

		Response r02 = responder.getExperimentDesignElements(subtypeRegexp_that_is_bad, nameRegexp_that_matches_n_complex);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r02.getStatus()
		);

		Response r03 = responder.getExperimentDesignElements(subtypeRegexp_that_is_bad, nameRegexp_that_matches_y_complex);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r03.getStatus()
		);

		Response r04 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_n_complex, nameRegexp_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r04.getStatus()
		);

		Response r05 = responder.getExperimentDesignElements(subtypeRegexp_that_matches_y_complex, nameRegexp_that_is_bad);

		assertEquals("responds with status BAD_REQUEST",
				Response.Status.BAD_REQUEST.getStatusCode(), r05.getStatus()
		);
	}

}
