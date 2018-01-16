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

import static com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtilCutpoint.runRemoteAction;

import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHost;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHostNetworkSystem;
import com.vmware.vim25.HostPortGroup;
import com.vmware.vim25.HostPortGroupConfig;
import com.vmware.vim25.HostPortGroupSpec;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.HostVirtualSwitchConfig;
import com.vmware.vim25.HostVirtualSwitchSpec;
import com.vmware.vim25.mo.HostNetworkSystem;

/**
 * @author srogers
 */
public class AUHostNetworkSystemProxy extends AUHostNetworkSystem
{
	/**
	 * Creates an object of this type.
	 *
	 * @param mom
	 * @param system
	 * @param name_expected
	 */
	protected AUHostNetworkSystemProxy(
			Manager/*             */ mom,
			HostNetworkSystem/*   */ system,
			String/*              */ name_expected,
			AUHost/*              */ owner_expected
	)
	{
		super(mom, system, name_expected, owner_expected);
	}

	/**/

	@Override
	protected String delegate_rpc_getName() {

		/*
		 * A host network system does not actually have a name on the server side.
		 * It has only the name we assign it locally, for convenience.
		 */
		return null;
	}

	@Override
	protected AUHost delegate_rpc_getOwner() {

		/*
		 * This is a back pointer.
		 * If we don't know it already at construction time: punt; too expensive.
		 */
		return null;
	}

	/**/

	@Override
	public HostPortGroupConfig[] getPortGroupConfigs()
	{
		return runRemoteAction(
				() -> this.delegate.getNetworkConfig().getPortgroup(),
				() -> "while getting port group configs"
		);
	}

	@Override
	public HostPortGroup[] getPortGroups()
	{
		return runRemoteAction(
				() -> this.delegate.getNetworkInfo().getPortgroup(),
				() -> "while getting port groups"
		);
	}

	@Override
	public void addPortGroup(HostPortGroupSpec spec)
	{
		runRemoteAction(
				() -> this.delegate.addPortGroup(spec),
				() -> "while adding port group: " + spec.getName()
		);
	}

	@Override
	public void removePortGroup(String name)
	{
		runRemoteAction(
				() -> this.delegate.removePortGroup(name),
				() -> "while removing port group: " + name
		);
	}

	@Override
	public void updatePortGroup(String name, HostPortGroupSpec spec)
	{
		runRemoteAction(
				() -> this.delegate.updatePortGroup(name, spec),
				() -> "while updating port group: " + name
		);
	}

	/**/

	@Override
	public HostVirtualSwitchConfig[] getVirtualSwitchConfigs()
	{
		return runRemoteAction(
				() -> this.delegate.getNetworkConfig().getVswitch(),
				() -> "while getting virtual switch configs"
		);
	}

	@Override
	public HostVirtualSwitch[] getVirtualSwitches()
	{
		return runRemoteAction(
				() -> this.delegate.getNetworkInfo().getVswitch(),
				() -> "while getting virtual switches"
		);
	}

	@Override
	public void addVirtualSwitch(String name, HostVirtualSwitchSpec spec)
	{
		runRemoteAction(
				() -> this.delegate.addVirtualSwitch(name, spec),
				() -> "while adding virtual switch: " + name
		);
	}

	@Override
	public void removeVirtualSwitch(String name)
	{
		runRemoteAction(
				() -> this.delegate.removeVirtualSwitch(name),
				() -> "while removing virtual switch: " + name
		);
	}

	@Override
	public void updateVirtualSwitch(String name, HostVirtualSwitchSpec spec)
	{
		runRemoteAction(
				() -> this.delegate.updateVirtualSwitch(name, spec),
				() -> "while updating virtual switch: " + name
		);
	}

	/**/

	public static class Manager implements AUHostNetworkSystem.Manager
	{
		AUHostNetworkSystem.Manager parent;

		public Manager(AUHostNetworkSystem.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		public AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/* */ system
		)
		{
			return new AUHostNetworkSystemProxy(this, system, null, null);
		}

		public AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/* */ system,
				String/*            */ name_expected,
				AUHost/*            */ owner_expected
		)
		{
			return new AUHostNetworkSystemProxy(this, system, name_expected, owner_expected);
		}

	}

	/**/

}
