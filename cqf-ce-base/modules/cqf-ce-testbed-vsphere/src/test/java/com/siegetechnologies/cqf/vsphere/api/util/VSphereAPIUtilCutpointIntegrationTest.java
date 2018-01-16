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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.siegetechnologies.cqf.core.util.Pair;
import com.siegetechnologies.cqf.testbed.base.util.PasswordBasedCredentialsResourceUtil;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualPortGroup;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualSwitch;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHost;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUNetwork;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import com.vmware.vim25.HostPortGroup;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author srogers
 */
public class VSphereAPIUtilCutpointIntegrationTest extends VSphereAPIUtilCutpointIntegrationTestBase
{
	static final String standard_testbed_vm_template_name = "ASTAM CQF WP2 Ubuntu 64-bit";

	static final int maximum_time_per_test_in_milliseconds = Integer.MAX_VALUE; // 15000

	VSphereAPIUtilCutpoint/* */ cp01;

	@Before
	public void setUp()
			throws Exception
	{
		cp01 = new VSphereAPIUtil(loginManager01);
	}

	@After
	public void tearDown()
			throws Exception
	{
		cp01 = null;
	}

	/**/

	@Test
	public void testSetUpTearDown()
			throws Exception
	{
		/**/
	}

	@Test
	public void testGetLoginInfo()
			throws Exception
	{
		assertSame(loginInfo01, cp01.getLoginInfo());
	}

	@Test
	public void testFindHost()
			throws Exception
	{
		List<AUHost> hosts01 =
				cp01.findMatchingHosts(null);

		assertTrue(! cp01.findHost("does_not_exist").isPresent());

		if (hosts01.size() > 0) {


			assertTrue(hosts01.stream().findFirst().isPresent());

			String hosts01a_name = hosts01.stream().findFirst().get().getName();

			Optional<AUHost> host01a1 =
					cp01.findHost(null);

			assertTrue(host01a1.isPresent());
			assertEquals(hosts01a_name, host01a1.get().getName());

			Optional<AUHost> host01a2 =
					cp01.findHost(hosts01a_name);

			assertTrue(host01a2.isPresent());
			assertEquals(hosts01a_name, host01a2.get().getName());

			Optional<AUHost> host01b =
					cp01.findHost(hosts01a_name + "_" + "does_not_exist");

			assertTrue(! host01b.isPresent());
		}
	}

	@Test
	public void testFindMatchingHosts()
			throws Exception
	{
		List<AUHost> hosts01 = cp01.findMatchingHosts(null);

		List<AUHost> hosts02 = cp01.findMatchingHosts(".*");

		List<AUHost> hosts03 = cp01.findMatchingHosts("does_not_exist");

		assertEquals(hosts01.size(), hosts02.size());
		assertEquals(0, hosts03.size());

		if (hosts01.size() > 0) {

			/**/
		}
	}

	@Test
	public void testFindNetwork()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 35000) {
			return;
		}

		List<AUNetwork> networks01 =
				cp01.findMatchingNetworks(null);

		assertTrue(! cp01.findNetwork("does_not_exist").isPresent());

		if (networks01.size() > 0) {

			assertTrue(networks01.stream().findFirst().isPresent());

			String networks01a_name = networks01.stream().findFirst().get().getName();

			Optional<AUNetwork> network01a1 =
					cp01.findNetwork(null);

			assertTrue(network01a1.isPresent());
			assertEquals(networks01a_name, network01a1.get().getName());

			Optional<AUNetwork> network01a2 =
					cp01.findNetwork(networks01a_name);

			assertTrue(network01a2.isPresent());
			assertEquals(networks01a_name, network01a2.get().getName());

			Optional<AUNetwork> network01b =
					cp01.findNetwork(networks01a_name + "_" + "does_not_exist");

			assertTrue(! network01b.isPresent());
		}
	}

	@Test
	public void testFindMatchingNetworks()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 45000) {
			return;
		}

		List<AUNetwork> networks01 = cp01.findMatchingNetworks(null);

		List<AUNetwork> networks02 = cp01.findMatchingNetworks(".*");

		List<AUNetwork> networks03 = cp01.findMatchingNetworks("does_not_exist");

		assertEquals(networks01.size(), networks02.size());
		assertEquals(0, networks03.size());

		if (networks01.size() > 0) {

			/**/
		}
	}

	@Test
	public void testFindDistributedVirtualSwitch()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 5000) {
			return;
		}

		List<AUDistributedVirtualSwitch> virtualSwitches01 =
				cp01.findMatchingDistributedVirtualSwitches(null);

		assertTrue(! cp01.findDistributedVirtualSwitch("does_not_exist").isPresent());

		if (virtualSwitches01.size() > 0) {

			assertTrue(virtualSwitches01.stream().findFirst().isPresent());

			String virtualSwitches01a_name = virtualSwitches01.stream().findFirst().get().getName();

			Optional<AUDistributedVirtualSwitch> virtualSwitch01a1 =
					cp01.findDistributedVirtualSwitch(null);

			assertTrue(virtualSwitch01a1.isPresent());
			assertEquals(virtualSwitches01a_name, virtualSwitch01a1.get().getName());

			Optional<AUDistributedVirtualSwitch> virtualSwitch01a2 =
					cp01.findDistributedVirtualSwitch(virtualSwitches01a_name);

			assertTrue(virtualSwitch01a2.isPresent());
			assertEquals(virtualSwitches01a_name, virtualSwitch01a2.get().getName());

			Optional<AUDistributedVirtualSwitch> virtualSwitch01b =
					cp01.findDistributedVirtualSwitch(virtualSwitches01a_name + "_" + "does_not_exist");

			assertTrue(! virtualSwitch01b.isPresent());
		}
	}

	@Test
	public void testFindMatchingDistributedVirtualSwitches()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 00000) {
			return;
		}

		List<AUDistributedVirtualSwitch> virtualSwitches01 = cp01.findMatchingDistributedVirtualSwitches(null);

		List<AUDistributedVirtualSwitch> virtualSwitches02 = cp01.findMatchingDistributedVirtualSwitches(".*");

		List<AUDistributedVirtualSwitch> virtualSwitches03 = cp01.findMatchingDistributedVirtualSwitches("does_not_exist");

		assertEquals(virtualSwitches01.size(), virtualSwitches02.size());
		assertEquals(0, virtualSwitches03.size());

		if (virtualSwitches01.size() > 0) {

			/**/
		}
	}

	@Test
	public void testCreateDeleteDistributedVirtualSwitch()
			throws Exception
	{

		if (maximum_time_per_test_in_milliseconds < 10000) {
			return;
		}

		String virtualSwitch01_name =
				newDistributedVirtualSwitchName("vs01");

		List<AUHost> attachedHosts =
				cp01.findMatchingHosts(null);

		String[] nicList =
				null;

		boolean allowPromiscuous =
				false;

		AUDistributedVirtualSwitch virtualSwitch01a =
				cp01.createDistributedVirtualSwitch(virtualSwitch01_name, attachedHosts, nicList, allowPromiscuous);

		assertNotNull(virtualSwitch01a);
		assertEquals(virtualSwitch01_name, virtualSwitch01a.getName());
		assertEquals(attachedHosts, virtualSwitch01a.getAttachedHostList());

		AUDistributedVirtualSwitch virtualSwitch01b =
				cp01.findDistributedVirtualSwitch(virtualSwitch01_name).orElse(null);

		assertEquals(virtualSwitch01a, virtualSwitch01b);

		cp01.deleteDistributedVirtualSwitch(virtualSwitch01a);

		assertTrue(! cp01.findDistributedVirtualSwitch(virtualSwitch01_name).isPresent());
	}

	@Test
	public void testCreateDeleteDistributedVirtualPortGroup()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 20000) {
			return;
		}

		String vs01_name =
				newDistributedVirtualSwitchName("vs01");

		List<AUHost> vs01_attachedHosts =
				cp01.findMatchingHosts(null);

		AUDistributedVirtualSwitch vs01 = cp01.createDistributedVirtualSwitch(
				vs01_name, vs01_attachedHosts, null, false
		);
		assertNotNull(vs01);

		AUHost vs01_attachedHost01 = vs01_attachedHosts.get(0);
		assertNotNull(vs01_attachedHost01);

		/**/

		String vs01_pg01_name =
				newDistributedVirtualPortGroupName("vs01_pg01");

		String vs01_pg02_name =
				newDistributedVirtualPortGroupName("vs01_pg02");

		List<String> vs01_portGroupNames01a = namesOfPortGroupsOnHost(vs01_attachedHost01);
		assertTrue(! vs01_portGroupNames01a.contains(vs01_pg01_name));
		assertTrue(! vs01_portGroupNames01a.contains(vs01_pg01_name));

		AUDistributedVirtualPortGroup vs01_pg01 = cp01.createDistributedVirtualPortGroup(
				vs01_pg01_name, vs01, 0, false
		);
		assertNotNull(vs01_pg01);

		List<String> vs01_portGroupNames01b = namesOfPortGroupsOnHost(vs01_attachedHost01);
		assertTrue(vs01_portGroupNames01b.contains(vs01_pg01_name));
		assertTrue(! vs01_portGroupNames01b.contains(vs01_pg02_name));

		AUDistributedVirtualPortGroup vs01_pg02 = cp01.createDistributedVirtualPortGroup(
				vs01_pg02_name, vs01, 0, false
		);
		assertNotNull(vs01_pg02);

		List<String> vs01_portGroupNames01c = namesOfPortGroupsOnHost(vs01_attachedHost01);
		assertTrue(vs01_portGroupNames01c.contains(vs01_pg01_name));
		assertTrue(vs01_portGroupNames01c.contains(vs01_pg02_name));

		cp01.deleteDistributedVirtualPortGroup(vs01_pg02);

		List<String> vs01_portGroupNames01d = namesOfPortGroupsOnHost(vs01_attachedHost01);
		assertTrue(vs01_portGroupNames01d.contains(vs01_pg01_name));
		assertTrue(! vs01_portGroupNames01d.contains(vs01_pg02_name));

		cp01.deleteDistributedVirtualPortGroup(vs01_pg01);

		List<String> vs01_portGroupNames01e = namesOfPortGroupsOnHost(vs01_attachedHost01);
		assertTrue(! vs01_portGroupNames01e.contains(vs01_pg01_name));
		assertTrue(! vs01_portGroupNames01e.contains(vs01_pg02_name));

		cp01.deleteDistributedVirtualSwitch(vs01);
	}

	protected List<String> namesOfPortGroupsOnHost(AUHost host)
	{
		HostPortGroup[] portGroups = host.getNetworkSystem().getPortGroups();

		return (Stream.of(portGroups)

				.map(x -> x.getSpec().getName())

				.collect(Collectors.toCollection(() -> new LinkedList<>()))
				//^-- collect results in encounter order
		);
	}

	@Test
	public void testGetRootResourcePool()
			throws Exception
	{
		AUResourcePool root = cp01.getRootResourcePool();

		assertNotNull(root);
		assertNull(root.getParent());
	}

	@Test
	public void testFindResourcePool()
			throws Exception
	{
		List<AUResourcePool> resourcePools01 =
				cp01.findMatchingResourcePools(null);

		assertTrue(! cp01.findResourcePool("does_not_exist").isPresent());

		if (resourcePools01.size() > 0) {

			assertTrue(resourcePools01.stream().findFirst().isPresent());

			String resourcePools01a_name = resourcePools01.stream().findFirst().get().getName();

			Optional<AUResourcePool> resourcePool01a1 =
					cp01.findResourcePool(null);

			assertTrue(resourcePool01a1.isPresent());
			assertEquals(resourcePools01a_name, resourcePool01a1.get().getName());

			Optional<AUResourcePool> resourcePool01a2 =
					cp01.findResourcePool(resourcePools01a_name);

			assertTrue(resourcePool01a2.isPresent());
			assertEquals(resourcePools01a_name, resourcePool01a2.get().getName());

			Optional<AUResourcePool> resourcePool01b =
					cp01.findResourcePool(resourcePools01a_name + "_" + "does_not_exist");

			assertTrue(! resourcePool01b.isPresent());
		}
	}

	@Test
	public void testFindMatchingResourcePools()
			throws Exception
	{
		List<AUResourcePool> resourcePools01 = cp01.findMatchingResourcePools(null);

		List<AUResourcePool> resourcePools02 = cp01.findMatchingResourcePools(".*");

		List<AUResourcePool> resourcePools03 = cp01.findMatchingResourcePools("does_not_exist");

		assertEquals(resourcePools01.size(), resourcePools02.size());
		assertEquals(0, resourcePools03.size());

		if (resourcePools01.size() > 0) {

			/**/
		}
	}

	@Test
	public void testCreateDeleteResourcePool()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 15000) {
			return;
		}

		String p01_name = newResourcePoolName("p01");
		String p02_name = newResourcePoolName("p02");
		AUResourcePool.AllocationInfo[] p01_allocationInfo = newResourcePoolAllocationInfoArray();
		AUResourcePool.AllocationInfo[] p02_allocationInfo = newResourcePoolAllocationInfoArray();

		String p02_01_name = newResourcePoolName("p02_01");
		String p02_02_name = newResourcePoolName("p02_02");
		AUResourcePool.AllocationInfo[] p02_01_allocationInfo = newResourcePoolAllocationInfoArray();
		AUResourcePool.AllocationInfo[] p02_02_allocationInfo = newResourcePoolAllocationInfoArray();

		long resourcePoolCount_initially =
				cp01.findMatchingResourcePools(null).size();

		assertTrue(resourcePoolCount_initially > 1);

		AUResourcePool p00 = cp01.getRootResourcePool();

		AUResourcePool p01 = cp01.createResourcePool(
				p01_name, p00, p01_allocationInfo[0], p01_allocationInfo[1]
		);
		AUResourcePool p02 = cp01.createResourcePool(
				p02_name, p00, p02_allocationInfo[0], p02_allocationInfo[1]
		);

		AUResourcePool p02_01 = cp01.createResourcePool(
				p02_01_name, p02, p02_01_allocationInfo[0], p02_01_allocationInfo[1]
		);
		AUResourcePool p02_02 = cp01.createResourcePool(
				p02_02_name, p02, p02_02_allocationInfo[0], p02_02_allocationInfo[1]
		);

		long resourcePoolCount_after_creating_all =
				cp01.findMatchingResourcePools(null).size();

		assertTrue(resourcePoolCount_after_creating_all >= 1 + 2 + 2);

		Optional<AUResourcePool> p01_maybe = cp01.findResourcePool(p01_name);
		assertTrue(p01_maybe.isPresent());
		assertEquals(p01.getName(), p01_maybe.get().getName());

		Optional<AUResourcePool> p02_02_maybe = cp01.findResourcePool(p02_02_name);
		assertTrue(p02_02_maybe.isPresent());
		assertEquals(p02_02.getName(), p02_02_maybe.get().getName());

		cp01.deleteResourcePool(p02);

		long resourcePoolCount_after_deleting_p02 =
				cp01.findMatchingResourcePools(null).size();

		assertEquals(3, resourcePoolCount_after_creating_all - resourcePoolCount_after_deleting_p02);

		assertTrue(cp01.findResourcePool(p01_name).isPresent());
		assertTrue(! cp01.findResourcePool(p02_name).isPresent());
		assertTrue(! cp01.findResourcePool(p02_01_name).isPresent());
		assertTrue(! cp01.findResourcePool(p02_02_name).isPresent());

		cp01.deleteResourcePool(p01);

		long resourcePoolCount_after_deleting_all =
				cp01.findMatchingResourcePools(null).size();

		assertEquals(1, resourcePoolCount_after_deleting_p02 - resourcePoolCount_after_deleting_all);

		assertTrue(! cp01.findResourcePool(p01_name).isPresent());
		assertTrue(! cp01.findResourcePool(p02_name).isPresent());
		assertTrue(! cp01.findResourcePool(p02_01_name).isPresent());
		assertTrue(! cp01.findResourcePool(p02_02_name).isPresent());

		assertEquals(resourcePoolCount_initially, resourcePoolCount_after_deleting_all);
	}

	@Test
	public void testFindVirtualMachine()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 20000) {
			return;
		}

		List<AUVirtualMachine> virtualMachines01 =
				cp01.findMatchingVirtualMachines(null);

		assertTrue(! cp01.findVirtualMachine("does_not_exist").isPresent());

		if (virtualMachines01.size() > 0) {

			assertTrue(virtualMachines01.stream().findFirst().isPresent());

			String virtualMachines01a_name = virtualMachines01.stream().findFirst().get().getName();
			assertNotNull(virtualMachines01a_name);
			assertTrue(virtualMachines01a_name.trim().length() > 0);

			Optional<AUVirtualMachine> virtualMachine01a1 =
					cp01.findVirtualMachine(null);

			assertTrue(virtualMachine01a1.isPresent());
			assertEquals(virtualMachines01a_name, virtualMachine01a1.get().getName());

			Optional<AUVirtualMachine> virtualMachine01a2 =
					cp01.findVirtualMachine(virtualMachines01a_name);

			assertTrue(virtualMachine01a2.isPresent());
			assertEquals(virtualMachines01a_name, virtualMachine01a2.get().getName());

			Optional<AUVirtualMachine> virtualMachine01b =
					cp01.findVirtualMachine(virtualMachines01a_name + "_" + "does_not_exist");

			assertTrue(! virtualMachine01b.isPresent());

			assertTrue(cp01.findVirtualMachine(standard_testbed_vm_template_name).isPresent());
		}
	}

	@Test
	public void testFindMatchingVirtualMachines()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 20000) {
			return;
		}

		List<AUVirtualMachine> virtualMachines01 = cp01.findMatchingVirtualMachines(null);

		List<AUVirtualMachine> virtualMachines02 = cp01.findMatchingVirtualMachines(".*");

		List<AUVirtualMachine> virtualMachines03 = cp01.findMatchingVirtualMachines("does_not_exist");

		assertEquals(virtualMachines01.size(), virtualMachines02.size());
		assertEquals(0, virtualMachines03.size());

		if (virtualMachines01.size() > 0) {

			/**/
		}
	}

	@Test
	public void testConfigureNetworkAdapterForVirtualMachine()
			throws Exception
	{
	}

	@Test
	public void testEnsurePowerStateOfVirtualMachine()
			throws Exception
	{
	}

	@Test
	public void testCreateDeleteLinkedClone()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 20000) {
			return;
		}

		String donorName = standard_testbed_vm_template_name;

		Optional<AUVirtualMachine> donor_maybe = cp01.findVirtualMachine(donorName);
		assertTrue(donor_maybe.isPresent());

		AUVirtualMachine donor = donor_maybe.get();
		assertEquals(standard_testbed_vm_template_name, donor.getName());

		String clone01_name =
				newVirtualMachineName("clone01");

		String creatorUUID =
				UUID.randomUUID().toString();

		AUResourcePool resourcePool =
				cp01.getRootResourcePool();

		PasswordAuthentication cloneAdminCredentials = (PasswordBasedCredentialsResourceUtil
				.findPasswordBasedCredentials("testbedVM.credentials.root", null)
		);
		assertNotNull(cloneAdminCredentials);
		assertEquals("root", cloneAdminCredentials.getUserName());

		AUVirtualMachine clone01a = cp01.createLinkedClone(
				donor, clone01_name, creatorUUID, resourcePool, cloneAdminCredentials
		);
		assertNotNull(clone01a);
		assertEquals(donor, clone01a.getDonor());
		assertEquals(clone01_name, clone01a.getName());
		assertEquals(resourcePool, clone01a.getResourcePool());
		assertEquals(cloneAdminCredentials.getUserName(),
				clone01a.getAdminCredentials_in_vSphere_form().getUsername());

		AUVirtualMachine clone01b = cp01.findVirtualMachine(clone01_name).orElse(null);
		assertEquals(clone01a, clone01b);

		cp01.deleteLinkedClone(clone01a);

		assertTrue(! cp01.findVirtualMachine(clone01_name).isPresent());
	}

	@Test
	public void testCreateDeleteLinkedClones()
			throws Exception
	{
		if (maximum_time_per_test_in_milliseconds < 30000) {
			return;
		}

		String donorName = standard_testbed_vm_template_name;

		Optional<AUVirtualMachine> donor_maybe = cp01.findVirtualMachine(donorName);
		assertTrue(donor_maybe.isPresent());

		AUVirtualMachine donor = donor_maybe.get();
		assertEquals(standard_testbed_vm_template_name, donor.getName());

		List<String> cloneNames = Arrays.asList(
				newVirtualMachineName("clone01"),
				newVirtualMachineName("clone02"),
				newVirtualMachineName("clone03")
		);
		String creatorUUID =
				UUID.randomUUID().toString();

		AUResourcePool resourcePool =
				cp01.getRootResourcePool();

		PasswordAuthentication cloneAdminCredentials = (PasswordBasedCredentialsResourceUtil
				.findPasswordBasedCredentials("testbedVM.credentials.root", null)
		);
		assertNotNull(cloneAdminCredentials);
		assertEquals("root", cloneAdminCredentials.getUserName());

		Map<String,AUVirtualMachine> cloneMap = (cp01
				.createLinkedClones(donor, cloneNames, creatorUUID, resourcePool, cloneAdminCredentials)

				.map(nx_supplier -> nx_supplier.get())

				.collect(Pair.toMap())
		);
		assertEquals(3, cloneMap.size());
		assertEquals(3, cloneMap.keySet().stream().filter(x -> x != null).count());
		assertEquals(3, cloneMap.values().stream().filter(x -> x != null).count());

		for (String cloneName : cloneNames) {

			AUVirtualMachine clone = cloneMap.get(cloneName);
			assertNotNull(clone);
			assertEquals(donor, clone.getDonor());
			assertEquals(cloneName, clone.getName());
			assertEquals(resourcePool, clone.getResourcePool());
			assertEquals(
					cloneAdminCredentials.getUserName(),
					clone.getAdminCredentials_in_vSphere_form().getUsername()
			);
			assertEquals(clone, cp01.findVirtualMachine(cloneName).orElse(null));

		}

		cp01.deleteLinkedClones(cloneMap.values().stream());

		for (String cloneName : cloneNames) {

			assertTrue(! cp01.findVirtualMachine(cloneName).isPresent());
		}
	}

	@Test
	@Ignore // FIXME: srogers: implement this unit test
	public void testRunRemoteAction()
			throws Exception
	{
	}

	@Test
	public void testConnectionDidReset()
			throws Exception
	{
		Throwable t01 = new Throwable("");
		Throwable t02 = new Throwable("foo");
		Throwable t03 = new Throwable((String) null);

		Throwable t04 = new Throwable("connection reset");
		Throwable t05 = new Throwable("recv failed");

		assertTrue(! VSphereAPIUtilCutpoint.connectionDidReset((t01)));
		assertTrue(! VSphereAPIUtilCutpoint.connectionDidReset((t02)));
		assertTrue(! VSphereAPIUtilCutpoint.connectionDidReset((t03)));

		assertTrue(VSphereAPIUtilCutpoint.connectionDidReset(t04));
		assertTrue(VSphereAPIUtilCutpoint.connectionDidReset(t05));
	}

	/**/

}
