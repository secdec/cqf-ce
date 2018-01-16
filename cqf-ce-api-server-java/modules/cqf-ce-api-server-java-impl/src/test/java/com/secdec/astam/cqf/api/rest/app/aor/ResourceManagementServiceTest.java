package com.secdec.astam.cqf.api.rest.app.aor;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.secdec.astam.cqf.api.rest.app.CQFApplicationTestBase;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author srogers
 */
public class ResourceManagementServiceTest extends CQFApplicationTestBase
{
	protected ResourceManagementService rms01; // might be running
	protected ResourceManagementService rms02; // definitely not running
	
	protected CompositeConfiguration rms_configuration01a;
	protected CompositeConfiguration rms_configuration01b;
	
	@Before
	public void setUp() throws Exception {

		super.setUp();
		
		this.setUpResourceManagementServiceTestSubjects();
		this.setUpResourceManagementServiceTestSubjectConfigurations();
	}
	
	protected void setUpResourceManagementServiceTestSubjects() throws Exception {
		
		this.rms01 = new ResourceManagementServiceTestSubject();
		this.rms02 = new ResourceManagementServiceTestSubject();
	}
	
	protected void setUpResourceManagementServiceTestSubjectConfigurations() throws Exception {
		
		this.rms_configuration01a = new CompositeConfiguration();
		this.rms_configuration01b = new CompositeConfiguration();
	}
	
	@After
	public void tearDown() throws Exception {
	
		this.rms02 = null;
		this.rms01 = null;
		
		super.tearDown();
	}
	
	/**/
	
	@Test
	public void testSetUpTearDown() {

		/**/
	}
	
	@Test
	public void testStartupShutdown() throws Exception {
		
		assertFalse(rms02.isRunning());
		
		rms02.startup(rms_configuration01a);
		assertTrue(rms02.isRunning());
		
		rms02.startup(rms_configuration01a);
		assertTrue(rms02.isRunning());
		//^-- idempotent

		try {
			rms02.startup(rms_configuration01b);
			assertTrue(rms02.isRunning());
			//^-- idempotent (enforced)
			
			fail("exception expected");
		}
		catch (IllegalStateException xx) {
			/**/
		}
		
		rms02.shutdown();
		assertFalse(rms02.isRunning());
		
		rms02.shutdown();
		assertFalse(rms02.isRunning());
		//^-- idempotent
	}
	
	@Test
	public void testGetConfiguration() throws Exception {

		rms02.startup(rms_configuration01a);
		assertTrue(rms02.isRunning());

		if (rms_configuration01a != null) {
			assertSame(rms_configuration01a, rms02.getConfiguration());
		}

		rms02.shutdown();
		assertFalse(rms02.isRunning());
	}

	@Test
	public void testGetConfiguration_notRunning() throws Exception {
		
		assertFalse(rms02.isRunning());
		
		try {
			ImmutableConfiguration x = rms02.getConfiguration();
			
			fail("exception expected");
		}
		catch (IllegalStateException xx) {
			/**/
		}
	}
	
}
