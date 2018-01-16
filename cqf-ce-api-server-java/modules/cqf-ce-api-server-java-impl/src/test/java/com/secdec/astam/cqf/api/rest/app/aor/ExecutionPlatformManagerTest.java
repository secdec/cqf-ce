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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.secdec.astam.cqf.api.rest.app.aor.impl.vsphere.ExperimentManagerForVSphere;
import com.secdec.astam.cqf.api.rest.app.aor.impl.vsphere.SessionManagerForVSphere;
import org.junit.Test;

/**
 * @author srogers
 */
public class ExecutionPlatformManagerTest extends ResourceManagementServiceTest
{
	ExecutionPlatformManager epm01;
	ExecutionPlatformManager epm02;
	
	@Override
	public void setUp() throws Exception {
		
		super.setUp();
	}
	
	protected void setUpResourceManagementServiceTestSubjects() {
		
		epm01 = new ExecutionPlatformManagerTestSubject();
		epm02 = new ExecutionPlatformManagerTestSubject();
		
		rms01 = epm01;
		rms02 = epm02;
	}
	
	/**/
	
	@Test
	public void testSetUpTearDown() {
		/**/
	}
	
	@Test
	public void testGetToolkit() throws Exception {

		assertTrue(epm01.getToolkit() instanceof ExecutionPlatformToolkitTestSubject);
	}
	
	@Test
	public void testGetSessionManager() throws Exception {
		
		assertTrue(epm01.getSessionManager() instanceof SessionManagerTestSubject);
	}
	
	@Test
	public void testGetSessionManagerAs() throws Exception {
		
		SessionManager x01a = epm01.getSessionManagerAs(SessionManagerTestSubject.class);
		assertSame(epm01.getSessionManager(), x01a);
		
		try {
			SessionManagerForVSphere x01b = epm01.getSessionManagerAs(SessionManagerForVSphere.class);
			fail("expected exception");
		}
		catch (ClassCastException xx) {
			/**/
		}
	}
	
	@Test
	public void testGetExperimentManager() throws Exception {
		
		assertTrue(epm01.getExperimentManager() instanceof ExperimentManagerTestSubject);
	}
	
	@Test
	public void testGetExperimentManagerAs() throws Exception {
		
		ExperimentManager x01a = epm01.getExperimentManagerAs(ExperimentManagerTestSubject.class);
		assertSame(epm01.getExperimentManager(), x01a);
		
		try {
			ExperimentManagerForVSphere x01b = epm01.getExperimentManagerAs(ExperimentManagerForVSphere.class);
			fail("expected exception");
		}
		catch (ClassCastException xx) {
			/**/
		}
	}
	
}
