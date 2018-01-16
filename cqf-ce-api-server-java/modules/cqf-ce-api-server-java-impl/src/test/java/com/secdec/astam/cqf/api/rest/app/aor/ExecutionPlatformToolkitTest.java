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

import static junit.framework.TestCase.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.secdec.astam.cqf.api.rest.app.CQFApplicationTestBase;
import com.secdec.astam.cqf.api.rest.app.aor.impl.e2e.ExecutionPlatformToolkitForE2ETesting;
import com.secdec.astam.cqf.api.rest.app.aor.impl.vsphere.ExecutionPlatformToolkitForVSphere;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPlatform;
import org.junit.Before;
import org.junit.Test;

/**
 * @author srogers
 */
public class ExecutionPlatformToolkitTest extends CQFApplicationTestBase
{
	ExecutionPlatformToolkit tk01;
	
	@Before
	public void setUp() throws Exception {
		
		super.setUp();
		
		tk01 = new ExecutionPlatformToolkitTestSubject();
	}
	
	/**/
	
	@Test
	public void testSetupTearDown() {
		
		/**/
	}
	
	@Test
	public void testGet_Default() throws Exception {
		
		ExecutionPlatformToolkit tk01 = ExecutionPlatformToolkit.getDefault();
		ExecutionPlatformToolkit tk02 = ExecutionPlatformToolkit.get(ExecutionPlatform.Default);
		
		assertEquals(tk01.getClass(), tk02.getClass());
		assertNotSame(tk01, tk02);
	}
	
	@Test
	public void testGet_Test() throws Exception {
		
		ExecutionPlatformToolkit tk01 = ExecutionPlatformToolkit.get(ExecutionPlatform.Test);
		ExecutionPlatformToolkit tk02 = ExecutionPlatformToolkit.get(ExecutionPlatform.Test);
		
		assertEquals(tk01.getClass(), ExecutionPlatformToolkitForE2ETesting.class);
		assertEquals(tk01.getClass(), tk02.getClass());
		assertNotSame(tk01, tk02);
	}
	
	@Test
	public void testGet_Nil() throws Exception {
		
		try {
			ExecutionPlatformToolkit tk01 = ExecutionPlatformToolkit.get(ExecutionPlatform.Nil);
			fail("expected exception");
		}
		catch (UnsupportedOperationException xx) {
			/**/
		}
		
	}
	
	@Test
	public void testGet_vSphere() throws Exception {
		
		ExecutionPlatformToolkit tk01 = ExecutionPlatformToolkit.get(ExecutionPlatform.vSphere);
		ExecutionPlatformToolkit tk02 = ExecutionPlatformToolkit.get(ExecutionPlatform.vSphere);
	
		assertEquals(tk01.getClass(), ExecutionPlatformToolkitForVSphere.class);
		assertEquals(tk01.getClass(), tk02.getClass());
		assertNotSame(tk01, tk02);
	}

}
