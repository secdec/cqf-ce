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

import com.siegetechnologies.cqf.vsphere.api.util.GuestFamily;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualSwitch.Delegate;
import com.vmware.vim25.mo.HostNetworkSystem;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.VirtualMachine;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Objects;

/**
 * Manages instances of object types in this package.
 *
 * @author srogers
 */
public abstract class AUObjectManager implements
		AUObjectFactory,
		AUDistributedVirtualPortGroup.Manager,
		AUDistributedVirtualSwitch.Manager,
		AUHost.Manager,
		AUHostNetworkSystem.Manager,
		AUNetwork.Manager,
		AUResourcePool.Manager,
		AUVirtualMachine.Manager
{
	protected String name;

	protected AUDistributedVirtualPortGroup.Manager/* */distributedVirtualPortGroupManager;
	protected AUDistributedVirtualSwitch.Manager/*    */distributedVirtualSwitchManager;
	protected AUHost.Manager/*                        */hostManager;
	protected AUHostNetworkSystem.Manager/*           */hostNetworkSystemManager;
	protected AUNetwork.Manager/*                     */networkManager;
	protected AUResourcePool.Manager/*                */resourcePoolManager;
	protected AUVirtualMachine.Manager/*              */virtualMachineManager;

	/**/

	public AUObjectManager()
	{

		this("");
	}

	public AUObjectManager(String name)
	{
		this.name = Objects.requireNonNull(name, "name must not be null");

		this.distributedVirtualPortGroupManager/* */ = newDistributedVirtualPortGroupManager(this);
		this.distributedVirtualSwitchManager/*    */ = newDistributedVirtualSwitchManager(this);
		this.hostManager/*                        */ = newHostManager(this);
		this.hostNetworkSystemManager/*           */ = newHostNetworkSystemManager(this);
		this.networkManager/*                     */ = newNetworkManager(this);
		this.resourcePoolManager/*                */ = newResourcePoolManager(this);
		this.virtualMachineManager/*              */ = newVirtualMachineManager(this);
	}

	/**/

	public String getName()
	{
		return this.name;
	}

	/**/

	protected AUDistributedVirtualPortGroup.Manager
	newDistributedVirtualPortGroupManager(
			AUDistributedVirtualPortGroup.Manager/*  */ parent
	)
	{
		return new AUDistributedVirtualPortGroup.ManagerImpl(parent);
	}

	protected AUDistributedVirtualSwitch.Manager
	newDistributedVirtualSwitchManager(
			AUDistributedVirtualSwitch.Manager/*     */ parent
	)
	{
		return new AUDistributedVirtualSwitch.ManagerImpl(parent);
	}

	abstract
	protected AUHost.Manager
	newHostManager(
			AUHost.Manager/*                         */ parent
	);

	abstract
	protected AUHostNetworkSystem.Manager
	newHostNetworkSystemManager(
			AUHostNetworkSystem.Manager/*            */ parent
	);

	abstract
	protected AUNetwork.Manager
	newNetworkManager(
			AUNetwork.Manager/*                      */ parent
	);

	abstract
	protected AUVirtualMachine.Manager
	newVirtualMachineManager(
			AUVirtualMachine.Manager/*               */ parent
	);

	abstract
	protected AUResourcePool.Manager
	newResourcePoolManager(
			AUResourcePool.Manager/*                 */ parent
	);

	/**/

	protected <T> T prepare(T newManagedObject)
	{
		return newManagedObject;
	}

	/**/

	@Override
	public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
			AUDistributedVirtualPortGroup.Delegate/* */ portGroup
	)
	{
		return prepare(distributedVirtualPortGroupManager.AUDistributedVirtualPortGroup_from(portGroup));
	}

	@Override
	public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
			AUDistributedVirtualPortGroup.Delegate/* */ portGroup,
			String/*                                 */ name_expected,
			AUDistributedVirtualSwitch/*             */ owner_expected
	)
	{
		return prepare(distributedVirtualPortGroupManager.AUDistributedVirtualPortGroup_from(
				portGroup, name_expected, owner_expected
		));
	}

	/**/

	@Override
	public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(
			Delegate/*                               */ virtualSwitch
	)
	{
		return prepare(distributedVirtualSwitchManager.AUDistributedVirtualSwitch_from(virtualSwitch));
	}

	@Override
	public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(
			Delegate/*                               */ virtualSwitch,
			String/*                                 */ name_expected,
			List<AUHost>/*                           */ attachedHostList_expected,
			List<AUVirtualMachine>/*                 */ attachedVMList_expected
	)
	{
		return prepare(distributedVirtualSwitchManager.AUDistributedVirtualSwitch_from(
				virtualSwitch, name_expected, attachedHostList_expected, attachedVMList_expected
		));
	}

	/**/

	@Override
	public AUHost AUHost_from(
			HostSystem/*                             */ system
	)
	{
		return prepare(hostManager.AUHost_from(system));
	}

	@Override
	public AUHost AUHost_from(
			HostSystem/*                             */ system,
			String/*                                 */ name_expected
	)
	{
		return prepare(hostManager.AUHost_from(system, name_expected));
	}

	/**/

	@Override
	public AUHostNetworkSystem AUHostNetworkSystem_from(
			HostNetworkSystem/*                      */ system
	)
	{
		return prepare(this.hostNetworkSystemManager.AUHostNetworkSystem_from(system));
	}

	@Override
	public AUHostNetworkSystem AUHostNetworkSystem_from(
			HostNetworkSystem/*                      */ system,
			String/*                                 */ name_expected,
			AUHost/*                                 */ owner_expected
	)
	{
		return prepare(this.hostNetworkSystemManager.AUHostNetworkSystem_from(system, name_expected, owner_expected));
	}

	/**/

	@Override
	public AUNetwork AUNetwork_from(
			Network/*                                */ network
	)
	{
		return prepare(networkManager.AUNetwork_from(network));
	}

	@Override
	public AUNetwork AUNetwork_from(
			Network/*                                */ network,
			String/*                                 */ name_expected,
			List<AUHost>/*                           */ attachedHostList_expected,
			List<AUVirtualMachine>/*                 */ attachedVMList_expected
	)
	{
		return prepare(networkManager.AUNetwork_from(network, name_expected,
				attachedHostList_expected, attachedVMList_expected
		));
	}

	/**/

	@Override
	public AUResourcePool AUResourcePool_from(
			ResourcePool/*                           */ pool
	)
	{
		return prepare(resourcePoolManager.AUResourcePool_from(pool));
	}

	@Override
	public AUResourcePool AUResourcePool_from(
			ResourcePool/*                           */ pool,
			String/*                                 */ name_expected,
			AUResourcePool/*                         */ parent_expected
	)
	{
		return prepare(resourcePoolManager.AUResourcePool_from(pool, name_expected, parent_expected));
	}

	/**/

	@Override
	public AUVirtualMachine AUVirtualMachine_from(
			VirtualMachine/*                         */ machine
	)
	{
		return prepare(virtualMachineManager.AUVirtualMachine_from(machine));
	}

	@Override
	public AUVirtualMachine AUVirtualMachine_from(
			VirtualMachine/*                         */ machine,
			String/*                                 */ name_expected,
			GuestFamily/*                            */ family_expected,
			PasswordAuthentication/*                 */ adminCredentials_expected,
			AUVirtualMachine/*                       */ donor_expected,
			AUResourcePool/*                         */ resourcePool_expected
	)
	{
		return prepare(virtualMachineManager.AUVirtualMachine_from(machine, name_expected,
				family_expected, adminCredentials_expected, donor_expected, resourcePool_expected
		));
	}

	/**/

}
