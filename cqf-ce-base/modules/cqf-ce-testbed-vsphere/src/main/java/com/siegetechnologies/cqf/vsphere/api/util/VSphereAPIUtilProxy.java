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

import com.siegetechnologies.cqf.core.util.Pair;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualPortGroup;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualSwitch;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualSwitch.Delegate;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHost;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHostNetworkSystem;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUNetwork;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import com.vmware.vim25.mo.HostNetworkSystem;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.VirtualMachine;
import java.net.PasswordAuthentication;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author srogers
 */
public class VSphereAPIUtilProxy implements VSphereAPIUtilCutpoint
{
	private VSphereAPIUtilCutpoint delegate;

	public VSphereAPIUtilProxy()
	{

		this(null);
	}

	public VSphereAPIUtilProxy(VSphereAPIUtil delegate)
	{

		this.delegate = delegate;
	}

	/**/

	@Deprecated
	public VSphereAPIUtilCutpoint getDelegate()
	{

		return delegate;
	}

	protected void setDelegate(VSphereAPIUtilCutpoint value)
	{

		if (this.delegate != null && value != null) {
			throw new IllegalStateException("delegate has already been set");
		}
		this.delegate = value;
	}

	/**/

	@Override
	public LoginInfo getLoginInfo()
	{

		return delegate.getLoginInfo();
	}

	/**/

	@Override
	public Optional<AUHost> findHost(
			String/*                   */ name
	)
	{
		return delegate.findHost(name);
	}

	@Override
	public List<AUHost> findMatchingHosts(
			String/*                   */ nameRegexp
	)
	{
		return delegate.findMatchingHosts(nameRegexp);
	}

	/**/

	@Override
	public Optional<AUNetwork> findNetwork(
			String/*                   */ name
	)
	{
		return delegate.findNetwork(name);
	}

	@Override
	public List<AUNetwork> findMatchingNetworks(
			String/*                   */ nameRegexp
	)
	{
		return delegate.findMatchingNetworks(nameRegexp);
	}

	/**/

	@Override
	public Optional<AUDistributedVirtualSwitch> findDistributedVirtualSwitch(String name)
	{
		return delegate.findDistributedVirtualSwitch(name);
	}

	@Override
	public List<AUDistributedVirtualSwitch> findMatchingDistributedVirtualSwitches(String nameRegexp)
	{
		return delegate.findMatchingDistributedVirtualSwitches(nameRegexp);
	}

	@Override
	public AUDistributedVirtualSwitch createDistributedVirtualSwitch(
			String virtualSwitchName, List<AUHost> attachedHosts, String[] nicList, boolean allowPromiscuous
	)
	{
		return delegate.createDistributedVirtualSwitch(virtualSwitchName, attachedHosts, nicList, allowPromiscuous);
	}

	@Override
	public void deleteDistributedVirtualSwitch(AUDistributedVirtualSwitch virtualSwitch)
	{
		delegate.deleteDistributedVirtualSwitch(virtualSwitch);
	}

	/**/

	@Override
	public AUDistributedVirtualPortGroup createDistributedVirtualPortGroup(
			String portGroupName, AUDistributedVirtualSwitch virtualSwitch, int vlanId, boolean allowPromiscuous
	)
	{
		return delegate.createDistributedVirtualPortGroup(portGroupName, virtualSwitch, vlanId, allowPromiscuous);
	}

	@Override
	public void deleteDistributedVirtualPortGroup(AUDistributedVirtualPortGroup portGroup)
	{
		delegate.deleteDistributedVirtualPortGroup(portGroup);
	}

	/**/

	@Override
	public AUResourcePool getRootResourcePool()
	{

		return delegate.getRootResourcePool();
	}

	@Override
	public Optional<AUResourcePool>
	findResourcePool(String name)
	{

		return delegate.findResourcePool(name);
	}

	@Override
	public List<AUResourcePool> findMatchingResourcePools(
			String/*                   */ nameRegexp
	)
	{
		return delegate.findMatchingResourcePools(nameRegexp);
	}

	@Override
	public AUResourcePool createResourcePool(
			String name,
			AUResourcePool parent,
			AUResourcePool.AllocationInfo cpuAllocationInfo,
			AUResourcePool.AllocationInfo ramAllocationInfo
	)
	{
		return delegate.createResourcePool(name, parent, cpuAllocationInfo, ramAllocationInfo);
	}

	@Override
	public void deleteResourcePool(AUResourcePool resourcePool)
	{
		delegate.deleteResourcePool(resourcePool);
	}

	/**/

	@Override
	public Optional<AUVirtualMachine> findVirtualMachine(String name)
	{
		return delegate.findVirtualMachine(name);
	}

	@Override
	public List<AUVirtualMachine> findMatchingVirtualMachines(String nameRegexp)
	{
		return delegate.findMatchingVirtualMachines(nameRegexp);
	}

	/**/

	@Override
	public void configureNetworkAdapterForVirtualMachine(
			AUVirtualMachine/*         */ virtualMachine,
			String/*                   */ networkAdapterName,
			String/*                   */ networkName
	)
	{
		delegate.configureNetworkAdapterForVirtualMachine(virtualMachine, networkAdapterName, networkName);
	}

	@Override
	public void ensurePowerStateOfVirtualMachine(
			AUVirtualMachine/*            */ virtualMachine,
			AUVirtualMachine.PowerState/* */ powerState
	)
	{
		delegate.ensurePowerStateOfVirtualMachine(virtualMachine, powerState);
	}

	/**/

	@Override
	public AUVirtualMachine createLinkedClone(
			AUVirtualMachine/*          */ donor,
			String/*                    */ cloneName,
			String/*                    */ creatorUUID,
			AUResourcePool/*            */ resourcePool,
			PasswordAuthentication/*    */ cloneAdminCredentials
	)
	{
		return delegate.createLinkedClone(donor, cloneName, creatorUUID, resourcePool, cloneAdminCredentials);
	}

	@Override
	public Stream<Supplier<Pair<String, AUVirtualMachine>>> createLinkedClones(
			AUVirtualMachine/*          */ donor,
			Collection<String>/*        */ cloneNames,
			String/*                    */ creatorUUID,
			AUResourcePool/*            */ resourcePool,
			PasswordAuthentication/*    */ cloneAdminCredentials
	)
	{
		return delegate.createLinkedClones(donor, cloneNames, creatorUUID, resourcePool, cloneAdminCredentials);
	}

	@Override
	public void deleteLinkedClone(
			AUVirtualMachine/*    */ clone)
	{

		delegate.deleteLinkedClone(clone);
	}

	@Override
	public void deleteLinkedClones(
			Stream<AUVirtualMachine> clones
	)
	{

		delegate.deleteLinkedClones(clones);
	}

	/**/

	@Override
	public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
			AUDistributedVirtualPortGroup.Delegate portGroup
	)
	{
		return delegate.AUDistributedVirtualPortGroup_from(portGroup);
	}

	@Override
	public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
			AUDistributedVirtualPortGroup.Delegate portGroup,
			String name_expected, AUDistributedVirtualSwitch owner_expected
	)
	{
		return delegate.AUDistributedVirtualPortGroup_from(portGroup, name_expected, owner_expected);
	}

	/**/

	@Override
	public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(Delegate virtualSwitch)
	{
		return delegate.AUDistributedVirtualSwitch_from(virtualSwitch);
	}

	@Override
	public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(
			Delegate virtualSwitch, String name_expected, List<AUHost> attachedHostList_expected,
			List<AUVirtualMachine> attachedVMList_expected
	)
	{
		return delegate.AUDistributedVirtualSwitch_from(
				virtualSwitch, name_expected, attachedHostList_expected, attachedVMList_expected
		);
	}

	/**/

	@Override
	public AUHost AUHost_from(HostSystem system)
	{
		return delegate.AUHost_from(system);
	}

	@Override
	public AUHost AUHost_from(HostSystem system, String name_expected)
	{
		return delegate.AUHost_from(system, name_expected);
	}

	/**/

	@Override
	public AUHostNetworkSystem AUHostNetworkSystem_from(
			HostNetworkSystem system
	)
	{
		return delegate.AUHostNetworkSystem_from(system);
	}

	@Override
	public AUHostNetworkSystem AUHostNetworkSystem_from(
			HostNetworkSystem system, String name_expected, AUHost owner_expected
	)
	{
		return delegate.AUHostNetworkSystem_from(system, name_expected, owner_expected);
	}

	/**/

	@Override
	public AUNetwork AUNetwork_from(Network network)
	{
		return delegate.AUNetwork_from(network);
	}

	@Override
	public AUNetwork AUNetwork_from(Network network, String name_expected,
			List<AUHost> attachedHostList_expected, List<AUVirtualMachine> attachedVMList_expected)
	{
		return delegate.AUNetwork_from(network, name_expected, attachedHostList_expected, attachedVMList_expected);
	}

	/**/

	@Override
	public AUVirtualMachine AUVirtualMachine_from(VirtualMachine machine)
	{
		return delegate.AUVirtualMachine_from(machine);
	}

	@Override
	public AUVirtualMachine AUVirtualMachine_from(VirtualMachine machine, String name_expected,
			GuestFamily family_expected, PasswordAuthentication adminCredentials_expected,
			AUVirtualMachine donor_expected, AUResourcePool resourcePool_expected)
	{
		return delegate.AUVirtualMachine_from(machine, name_expected,
				family_expected, adminCredentials_expected, donor_expected, resourcePool_expected
		);
	}

	/**/

	@Override
	public AUResourcePool AUResourcePool_from(ResourcePool pool)
	{
		return delegate.AUResourcePool_from(pool);
	}

	@Override
	public AUResourcePool AUResourcePool_from(ResourcePool pool, String name_expected, AUResourcePool parent_expected)
	{
		return delegate.AUResourcePool_from(pool, name_expected, parent_expected);
	}

	/**/

}
