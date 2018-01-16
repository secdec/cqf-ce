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
import org.junit.Test;

/**
 * @author srogers
 */
public class ResourceCollectionProviderTest extends CQFApplicationTestBase
{
	private ResourceCollectionProviderTestSubject rcp01;
	
	@Override
	public void setUp() throws Exception {
		
		super.setUp();
		
		this.rcp01 = new ResourceCollectionProviderTestSubject();
	}
	
	@Override
	public void tearDown() throws Exception {
		
		this.rcp01 = null;
		
		super.tearDown();
	}

	/**/
	
	@Test
	public void testSetUpTearDown() {

		/**/
	}
	
	@Test
	public void testLoadUnload() throws Exception {
		
		ImmutableConfiguration c01 = new CompositeConfiguration();
		ImmutableConfiguration c02 = new CompositeConfiguration();
		
		assertFalse(rcp01.isAvailable());
		
		rcp01.load(c01);
		assertTrue(rcp01.isAvailable());
		
		rcp01.load(c01);
		assertTrue(rcp01.isAvailable());
		//^-- idempotent
		
		try {
			rcp01.load(c02);
			assertTrue(rcp01.isAvailable());
			//^-- idempotent (enforced)
			
			fail("exception expected");
		}
		catch (IllegalStateException xx) {
			/**/
		}
		
		rcp01.unload();
		assertFalse(rcp01.isAvailable());
		
		rcp01.unload();
		assertFalse(rcp01.isAvailable());
		//^-- idempotent
	}
	
	@Test
	public void testGetConfiguration() throws Exception {
		
		ImmutableConfiguration c01 = new CompositeConfiguration();
		
		rcp01.load(c01);
		assertTrue(rcp01.isAvailable());
		
		assertSame(c01, rcp01.getConfiguration());
		
		rcp01.unload();
		assertFalse(rcp01.isAvailable());
	}
	
	@Test
	public void testGetConfiguration_notAvailable() throws Exception {
		
		try {
			ImmutableConfiguration x = rcp01.getConfiguration();
			
			fail("exception expected");
		}
		catch (IllegalStateException xx) {
			/**/
		}
	}
	
}
