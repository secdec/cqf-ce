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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;
import org.junit.Test;

/**
 * @author srogers
 */
public class CQFApplicationTest extends CQFApplicationTestBase
{
	@Test
	public void testSetUpTearDown() throws Exception {
		super.testSetUpTearDown();
	}
	
	@Test
	public void testTheInstance() throws Exception {
		
		CQFApplication app02 = new CQFApplicationTestSubject();
		assertNotNull(app02);
		
		CQFApplication app03 = CQFApplication.theInstance();
		assertNotNull(app03);
		
		assertTrue(app01 != app02);
		assertTrue(app01 == app03);
		
		/**/
		
		CQFResourceManager rm02 = app02.getResourceManager();
		assertNotNull(rm02);
		
		CQFResourceManager rm03 = app03.getResourceManager();
		assertNotNull(rm03);
		
		CQFResourceManager rm04 = CQFResourceManager.theInstance();
		assertNotNull(rm04);
		
		assertTrue(rm01 != rm02);
		assertTrue(rm01 == rm03);
		assertTrue(rm01 == rm04);
	}
	
	@Test
	public void testAutomaticStartup() throws Exception {
		
		CQFApplication app = new CQFApplicationTestSubject();
		assertTrue(app instanceof Application);
		
		assertNotNull(app.getResourceManager());
		assertTrue(app.getResourceManager().isRunning());
	}
	
}
