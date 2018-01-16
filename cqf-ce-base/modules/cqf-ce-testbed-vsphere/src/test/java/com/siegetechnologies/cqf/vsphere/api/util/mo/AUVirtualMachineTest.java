package com.siegetechnologies.cqf.vsphere.api.util.mo;

/*-
 * #%L
 * astam-cqf-ce-testbed-vsphere
 * %%
 * Copyright (C) 2009 - 2017 Siege Technologies, LLC
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

import static com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine.underlying;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/**
 * @author srogers
 */
public class AUVirtualMachineTest
{
	AUObjectManager mom;

	AUVirtualMachine hns01;

	/**/

	@Before
	public void setUp()
			throws Exception
	{
		mom = new AUObjectManagerTestSubject("mom");

		hns01 = new AUVirtualMachineTestSubject(
				mom, null, "hns01",
				null, null, null, null
		);
	}

	/**/

	@Test
	public void testSetUpTearDown()
			throws Exception
	{
		/**/
	}

	@Test
	public void testUnderlyingAndGetDelegate()
			throws Exception
	{
		assertSame(underlying(hns01), hns01.getDelegate());
	}

	@Test
	public void testGetName()
			throws Exception
	{
		assertEquals("hns01", hns01.getName());
	}

}
