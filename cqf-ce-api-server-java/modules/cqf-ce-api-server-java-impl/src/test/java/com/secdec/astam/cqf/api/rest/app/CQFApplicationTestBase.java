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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.siegetechnologies.cqf.core.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author srogers
 */
public class CQFApplicationTestBase extends CQFApplicationTestUtil
{
	protected CQFApplication/*     */ app01;/* */ // 'the' instance
	protected CQFResourceManager/* */ rm01;/*  */ // 'the' instance

	/**/
	
	@Before
	public void setUp() throws Exception {

		tearDown();
		
		setUpTheApplicationInstance();
	}

	protected final void setUpTheApplicationInstance() {
		
		assertNull(CQFApplication.theInstance());
		assertNull(CQFResourceManager.theInstance());
		
		initTheApplicationInstance();
		initTheResourceManagerInstance();
		
		assertSame(CQFApplication.theInstance(), this.app01);
		assertSame(CQFResourceManager.theInstance(), this.rm01);
	}
	
	protected void initTheApplicationInstance() {
		
		this.app01 = new CQFApplicationTestSubject();
	}
	
	protected final void initTheResourceManagerInstance() {
		
		this.rm01 = this.app01.getResourceManager();

		assertTrue(this.rm01.isRunning());
	}
	
	/**/
	
	@After
	public void tearDown() throws Exception {
		
		this.rm01/*  */ = null;
		this.app01/* */ = null;

		Config.resetConfiguration();

		CQFResourceManager rm = CQFResourceManager.theInstance();
		if (rm != null) rm.shutdown();
		
	    CQFResourceManager.theInstance_reset();
		CQFApplication.theInstance_reset();
	}
	
	/**/
	
	@Test
	public void testSetUpTearDown() throws Exception {
		/**/
	}
	
}
