package com.secdec.astam.cqf.api.rest.app;

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

import static junit.framework.TestCase.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.secdec.astam.cqf.api.rest.app.aor.ResourceManagementServiceTest;
import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionHandlerRegistry;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionHandlerSpec;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import java.util.Collections;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author srogers
 */
public class CQFResourceManagerTest extends ResourceManagementServiceTest
{
	CQFResourceManager rm02; // definitely not running
	
	@Override
	public void setUp() throws Exception {
		
		super.setUp();
	}
	
	@Override
	protected void setUpResourceManagementServiceTestSubjects() throws Exception {
		
		this.rm02 = new CQFResourceManagerTestSubject();
		assertNotSame(CQFResourceManager.theInstance(), this.rm02);
		assertFalse(this.rm02.isRunning());
		
		this.rms01 = this.rm01;
		this.rms02 = this.rm02;
	}
	
	@Override
	protected void setUpResourceManagementServiceTestSubjectConfigurations() throws Exception {
		
		this.rms_configuration01a = null; // for now
		this.rms_configuration01b = new CompositeConfiguration();
	}
	
	/**/
	
	@Test
	public void testSetUpTearDown() {
		/**/
	}
	
	@Test
	public void testTheInstance() throws Exception {
		
		CQFResourceManager rm03 = CQFResourceManager.theInstance();
		
		assertTrue(rm01 != rm02);
		assertTrue(rm01 == rm03);
	}
	
	@Test
	public void testLoadUnloadDesignCatalogs() throws Exception {
		
		assertFalse(rm02.isRunning());
		
		assertNotNull(rm02.getMainDesignCatalog());
		assertEquals(0, rm02.getMainDesignCatalog().getItems().size());
		
		rm02.startup(null);
		assertTrue(rm02.isRunning());
		
		rm02.loadDesignCatalogs();
		
		assertNotNull(rm02.getMainDesignCatalog());
		assertTrue(0 < rm02.getMainDesignCatalog().getItems().size());
		
		assertEquals(1, rm02.getDesignCatalogs().size());
		assertTrue(rm02.getMainDesignCatalog() == rm02.getDesignCatalogs().get("main"));
		
		rm02.unloadDesignCatalogs();
		
		assertNotNull(rm02.getMainDesignCatalog());
		assertEquals(0, rm02.getMainDesignCatalog().getItems().size());
		
		rm02.shutdown();
		assertFalse(rm02.isRunning());
	}
	
	@Test
	public void testRegisterExecutionHandlers() throws Exception {
		
		assertTrue(rm01.isRunning());
		
		ExperimentDesignElementSpec experimentDesignElementSpec = new ExperimentDesignElementSpec("Clone VM", "Node");
		
		ExperimentElementExecutionHandlerSpec experimentElementExecutionHandlerSpec = new ExperimentElementExecutionHandlerSpec(
				"Clone VM", "Node", ExecutionPhase.QUANTIFY, Collections.singletonList(experimentDesignElementSpec)
		);
		assertNotNull(
				ExperimentElementExecutionHandlerRegistry.findHandler("astam", experimentElementExecutionHandlerSpec));
		
	}
	
	@Test
	@Ignore // FIXME: cbancroft: reimplement within integration test suite
	public void testBadLoginFailsGracefully() throws Exception {
		
		/**/
	}
	
	@Test
	@Ignore // FIXME: cbancroft: reimplement within integration test suite
	public void testLoginFailsGracefullyWithNoConfig() throws Exception {
		
		/**/
	}
	
}
