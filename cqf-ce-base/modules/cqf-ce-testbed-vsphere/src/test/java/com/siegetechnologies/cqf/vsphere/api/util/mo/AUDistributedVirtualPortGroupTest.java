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

import static com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualPortGroup.underlying;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author srogers
 */
public class AUDistributedVirtualPortGroupTest
{
	AUObjectManager mom;

	AUDistributedVirtualSwitch vs01;

	AUDistributedVirtualPortGroup vs01_pg01;

	/**/

	@Before
	public void setUp()
			throws Exception
	{
		mom = new AUObjectManagerTestSubject("mom");

		vs01 = new AUDistributedVirtualSwitch(
				mom, null, "vs01", null, null
		);
		vs01_pg01 = new AUDistributedVirtualPortGroupTestSubject(
				mom, null, "vs01_pg01", vs01
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
		assertSame(underlying(vs01_pg01), vs01_pg01.getDelegate());
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
		assertEquals("vs01_pg01", vs01_pg01.getName());
	}

	@Test
	public void testGetName_none_expected()
			throws Exception
	{
		AUDistributedVirtualPortGroup vs01_pg02 = new AUDistributedVirtualPortGroupTestSubject(
				mom, null, null, vs01
		);
		try {

			String n = vs01_pg02.getName();

			fail("exception expected");
		}
		catch (Throwable xx) {

			assertEquals(NullPointerException.class, xx.getClass());
		}
	}

	/**/

	@Test
	@Ignore
	public void testGetOwner()
			throws Exception
	{
		assertSame(vs01, vs01_pg01.getOwner());
	}

	@Test
	public void testGetOwner_none_expected()
			throws Exception
	{
		// FIXME: srogers: implement this unit test
	}

	/**/

}
