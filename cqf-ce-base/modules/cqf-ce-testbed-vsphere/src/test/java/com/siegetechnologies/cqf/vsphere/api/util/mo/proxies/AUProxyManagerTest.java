package com.siegetechnologies.cqf.vsphere.api.util.mo.proxies;

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

import static org.junit.Assert.assertTrue;

import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHost;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHostNetworkSystem;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUNetwork;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUObjectManager;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUObjectManagerTestSubject;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUTestSubject;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author srogers
 */
public class AUProxyManagerTest
{
	AUObjectManager mom = new AUObjectManagerTestSubject("mom");

	AUProxyManager pm01 = new AUProxyManager("pm01");

	@Before
	public void setUp()
			throws Exception
	{
		assertTrue(! (pm01 instanceof AUTestSubject)); //<-- we want the real deal
	}

	@After
	public void tearDown()
			throws Exception
	{
	}

	/**/

	@Test
	public void testNewHostNetworkSystemManager()
			throws Exception
	{
		AUHostNetworkSystem.Manager sm01 = pm01.newHostNetworkSystemManager(mom);

		assertTrue(sm01 instanceof AUHostNetworkSystemProxy.Manager);
	}

	@Test
	public void testNewHostManager()
			throws Exception
	{
		AUHost.Manager sm01 = pm01.newHostManager(mom);

		assertTrue(sm01 instanceof AUHostProxy.Manager);
	}

	@Test
	public void testNewNetworkManager()
			throws Exception
	{
		AUNetwork.Manager sm01 = pm01.newNetworkManager(mom);

		assertTrue(sm01 instanceof AUNetworkProxy.Manager);
	}

	@Test
	public void testNewResourcePoolManager()
			throws Exception
	{
		AUResourcePool.Manager sm01 = pm01.newResourcePoolManager(mom);

		assertTrue(sm01 instanceof AUResourcePoolProxy.Manager);
	}

	@Test
	public void testNewVirtualMachineManager()
			throws Exception
	{
		AUVirtualMachine.Manager sm01 = pm01.newVirtualMachineManager(mom);

		assertTrue(sm01 instanceof AUVirtualMachineProxy.Manager);
	}

}
