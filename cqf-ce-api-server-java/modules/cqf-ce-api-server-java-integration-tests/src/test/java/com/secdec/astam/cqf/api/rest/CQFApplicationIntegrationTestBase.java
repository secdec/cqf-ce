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

import com.secdec.astam.cqf.api.rest.io.ApiClient;
import com.secdec.astam.cqf.api.rest.io.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author srogers
 */
public class CQFApplicationIntegrationTestBase {

	@BeforeClass
	public static void setUpHarness() throws Exception {

		ApiClient c = new ApiClient().setBasePath("http://localhost:9080/cqf/api/v1");

		Configuration.setDefaultApiClient(c);
	}

	@Before
	public void setUp() throws Exception {

		/**/
	}

	@After
	public void tearDown() throws Exception {

		/**/
	}

	@AfterClass
	public static void tearDownHarness() throws Exception {

		Configuration.setDefaultApiClient(null);
	}

}
