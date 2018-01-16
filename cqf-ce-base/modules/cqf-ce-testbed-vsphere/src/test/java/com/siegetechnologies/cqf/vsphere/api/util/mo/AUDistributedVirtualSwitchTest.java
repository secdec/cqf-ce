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

import static com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualSwitch.underlying;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author srogers
 */
public class AUDistributedVirtualSwitchTest
{
	AUObjectManager mom;

	AUDistributedVirtualSwitch vs01;

	/**/

	@Before
	public void setUp()
			throws Exception
	{
		mom = new AUObjectManagerTestSubject("mom");

		vs01 = new AUDistributedVirtualSwitchTestSubject(mom, null, "vs01",
				null, null
		);
	}

	/**/

	@Test
	public void testSetUpTearDown()
			throws Exception
	{
		/**/
	}

	/**/

	@Test
	public void testUnderlyingAndGetDelegate()
			throws Exception
	{
		assertSame(underlying(vs01), vs01.getDelegate());
	}

	@Test
	@Ignore
	public void testGetDelegateMOR()
			throws Exception
	{
		// FIXME: srogers: implement this unit test
	}

	@Test
	@Ignore
	public void testGetMessageDigestOfKeyProperties()
			throws Exception
	{
		// FIXME: srogers: implement this unit test
	}

	/**/

	@Test
	public void testGetName()
			throws Exception
	{
		assertEquals("vs01", vs01.getName());
	}

	@Test
	public void testGetName_none_expected()
			throws Exception
	{
		AUDistributedVirtualSwitch vs02 = new AUDistributedVirtualSwitchTestSubject(
				mom, null, null, null, null
		);
		try {

			String n = vs02.getName();

			fail("exception expected");
		}
		catch (Throwable xx) {

			assertTrue(xx instanceof NullPointerException);
		}
	}

	/**/

	@Test
	@Ignore
	public void testGetAttachedHostList()
			throws Exception
	{
		// TODO: srogers: implement this unit test
	}

	@Test
	public void testGetAttachedHostList_none_expected()
			throws Exception
	{
		List<AUHost> hl01 = vs01.getAttachedHostList();

		assertNull(hl01);
	}

	/**/

	@Test
	@Ignore
	public void testGetAttachedVMList()
			throws Exception
	{
		// TODO: srogers: implement this unit test
	}

	@Test
	public void testGetAttachedVMList_none_expected()
			throws Exception
	{
		List<AUVirtualMachine> vml01 = vs01.getAttachedVMList();

		assertNull(vml01);
	}

	/**/

}
