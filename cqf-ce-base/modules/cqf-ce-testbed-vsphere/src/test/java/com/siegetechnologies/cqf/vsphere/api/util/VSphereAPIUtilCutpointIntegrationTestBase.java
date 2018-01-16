package com.siegetechnologies.cqf.vsphere.api.util;

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

import static com.siegetechnologies.cqf.vsphere.api.util.LoginInfoResourceUtil.findLoginInfo;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.siegetechnologies.cqf.core.util.Config;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import java.util.UUID;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author srogers
 */
public class VSphereAPIUtilCutpointIntegrationTestBase
{
	static CompositeConfiguration/* */ appConfig01;

	static LoginInfo/*              */ loginInfo01;
	static LoginManager/*           */ loginManager01;
	static ServiceInstance/*        */ loginManager01_serviceInstance;

	@BeforeClass
	public static void setUpHarness()
			throws Exception
	{
		setUpAppConfiguration();
		setUpVSphereSession();
	}

	@AfterClass
	public static void tearDownHarness()
			throws Exception
	{
		tearDownVSphereSession();
		tearDownAppConfiguration();
	}

	/**/

	protected static void setUpAppConfiguration()
			throws Exception
	{
		appConfig01 = Config.getConfiguration();
	}

	protected static void tearDownAppConfiguration()
			throws Exception
	{
		Config.setConfiguration(appConfig01 = null);
	}

	/**/

	protected static void setUpVSphereSession()
			throws Exception
	{
		loginInfo01 = findLoginInfo();

		loginManager01 = new LoginManager(loginInfo01);

		assertTrue(! loginManager01.isLoggedIn());

		loginManager01_serviceInstance = loginManager01.login();
		assertSame(loginManager01_serviceInstance, loginManager01.getServiceInstance());

		assertTrue(loginManager01.isLoggedIn());
	}

	protected static void tearDownVSphereSession()
			throws Exception
	{
		assertTrue(loginManager01.isLoggedIn());

		loginManager01_serviceInstance = null;
		loginManager01.logout();

		assertTrue(! loginManager01.isLoggedIn());

		loginManager01 = null;
		loginInfo01 = null;
	}

	/**/

	protected String newDistributedVirtualSwitchName(String name_stem)
	{
		final int length_max_according_to_sdk_docs = 32;
		//^-- cf. HostNetworkSystem.AddVirtualSwitch() in vSphere Management SDK 5.5.0

		final int length_max_actual = length_max_according_to_sdk_docs - 1;

		return newUniqueName(name_stem, "switch", length_max_actual);
	}

	/**/

	protected String newDistributedVirtualPortGroupName(String name_stem)
	{
		return newUniqueName(name_stem, "port-group");
	}

	/**/

	protected String newResourcePoolName(String name_stem)
	{
		return newUniqueName(name_stem, "pool");
	}

	protected AUResourcePool.AllocationInfo[] newResourcePoolAllocationInfoArray()
	{
		AUResourcePool.AllocationInfo cpuAllocationInfo = new AUResourcePool.AllocationInfo();
		AUResourcePool.AllocationInfo ramAllocationInfo = new AUResourcePool.AllocationInfo();

		cpuAllocationInfo.setLimit(3000L/*MHz*/);
		cpuAllocationInfo.setReservation(1000L/*MHz*/);
		cpuAllocationInfo.setReservationExpandable(true);

		ramAllocationInfo.setLimit(4 * 1024L/*MB*/);
		ramAllocationInfo.setReservation(2 * 1024L/*MB*/);
		ramAllocationInfo.setReservationExpandable(true);

		AUResourcePool.AllocationInfo[] result = new AUResourcePool.AllocationInfo[2];
		result[0] = cpuAllocationInfo;
		result[1] = ramAllocationInfo;

		return result;
	}

	/**/

	protected String newVirtualMachineName(String name_stem)
	{
		return newUniqueName(name_stem, "vm");
	}

	/**/

	protected String newUniqueName(String name_stem, String name_type)
	{
		return newUniqueName(name_stem, name_type, 00);
	}

	protected String newUniqueName(String name_stem, String name_type, int total_length_max)
	{
		String uniquifier = UUID.randomUUID().toString();

		final String name_prefix = "test";

		if (total_length_max > 0) {

			int uniquifier_length_max = total_length_max;

			uniquifier_length_max -= 1 + name_prefix.length();
			uniquifier_length_max -= 1 + name_type.length();
			uniquifier_length_max -= 1 + name_stem.length();

			if (uniquifier_length_max < 1) {

				int name_stem_length_max = name_stem.length() + uniquifier_length_max - 1;

				throw new IllegalArgumentException(
						"name_stem is too long; maximum length allowed: " + name_stem_length_max
				);
			}
			else if (uniquifier_length_max < uniquifier.length()) {

				uniquifier = uniquifier.substring(0, uniquifier_length_max);
			}
		}

		StringBuilder result = new StringBuilder();

		result.append(name_prefix);
		result.append("-");
		result.append(name_type);
		result.append("-");
		result.append(name_stem);
		result.append("-");
		result.append(uniquifier);

		assert result.length() <= total_length_max || total_length_max <= 0;
		assert result.length() >= 1;

		return result.toString();
	}

	/**/

}
