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
public class AUHostNetworkSystemTestSubject extends AUHostNetworkSystem implements AUTestSubject
{
	public AUHostNetworkSystemTestSubject(
			AUHostNetworkSystem.Manager mom, HostNetworkSystem system,
			String name_expected, AUHost owner_expected
	)
	{
		super(mom, system, name_expected, owner_expected);
	}

	/**/

	@Override
	protected String delegate_rpc_getName()
	{
		return null;
	}

	@Override
	protected AUHost delegate_rpc_getOwner()
	{
		return null;
	}

	@Override
	public HostPortGroupConfig[] getPortGroupConfigs()
	{
		return new HostPortGroupConfig[0];
	}

	@Override
	public HostPortGroup[] getPortGroups()
	{
		return new HostPortGroup[0];
	}

	@Override
	public void addPortGroup(HostPortGroupSpec spec)
	{

	}

	@Override
	public void removePortGroup(String name)
	{

	}

	@Override
	public void updatePortGroup(String name, HostPortGroupSpec spec)
	{

	}

	@Override
	public HostVirtualSwitchConfig[] getVirtualSwitchConfigs()
	{
		return new HostVirtualSwitchConfig[0];
	}

	@Override
	public HostVirtualSwitch[] getVirtualSwitches()
	{
		return new HostVirtualSwitch[0];
	}

	@Override
	public void addVirtualSwitch(String name, HostVirtualSwitchSpec spec)
	{

	}

	@Override
	public void removeVirtualSwitch(String name)
	{

	}

	@Override
	public void updateVirtualSwitch(String name, HostVirtualSwitchSpec spec)
	{

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
			return new AUHostNetworkSystemTestSubject(this, system, null, null);
		}

		public AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/* */ system,
				String/*            */ name_expected,
				AUHost/*            */ owner_expected
		)
		{
			return new AUHostNetworkSystemTestSubject(this, system, name_expected, owner_expected);
		}

	}

	/**/

}
