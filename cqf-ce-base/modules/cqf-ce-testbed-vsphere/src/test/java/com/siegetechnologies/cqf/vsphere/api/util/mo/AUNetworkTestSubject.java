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
import com.vmware.vim25.mo.HostNetworkSystem;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.VirtualMachine;
import java.net.PasswordAuthentication;
import java.util.List;

/**
 * @author srogers
 */
public class AUNetworkTestSubject extends AUNetwork implements AUTestSubject
{
	protected AUNetworkTestSubject(
			AUNetwork.Manager/*      */ mom,
			Network/*                */ network,
			String/*                 */ name_expected,
			List<AUHost>/*           */ attachedHostList_expected,
			List<AUVirtualMachine>/* */ attachedVMList_expected
	)
	{
		super(mom, network, name_expected, attachedHostList_expected, attachedVMList_expected);
	}

	@Override
	protected String delegate_rpc_getName()
	{
		return null;
	}

	@Override
	protected List<AUHost> delegate_rpc_getAttachedHostList()
	{
		return null;
	}

	@Override
	protected List<AUVirtualMachine> delegate_rpc_getAttachedVMList()
	{
		return null;
	}

	/**/

	public static class Manager implements AUNetwork.Manager
	{
		AUNetwork.Manager parent;

		public Manager(AUNetwork.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUNetwork AUNetwork_from(
				Network/*                */ network
		)
		{
			return new AUNetworkTestSubject(this, network, null,
					null, null
			);
		}

		@Override
		public AUNetwork AUNetwork_from(
				Network/*                */ network,
				String/*                 */ name_expected,
				List<AUHost>/*           */ attachedHostList_expected,
				List<AUVirtualMachine>/* */ attachedVMList_expected
		)
		{
			return new AUNetworkTestSubject(this, network, name_expected,
					attachedHostList_expected, attachedVMList_expected
			);
		}

		@Override
		public AUHost AUHost_from(
				HostSystem/*             */ system
		)
		{
			return parent.AUHost_from(system);
		}

		@Override
		public AUHost AUHost_from(
				HostSystem/*             */ system,
				String/*                 */ name_expected
		)
		{
			return parent.AUHost_from(system, name_expected);
		}

		@Override
		public AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/*      */ system
		)
		{
			return parent.AUHostNetworkSystem_from(system);
		}

		@Override
		public AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/*      */ system,
				String/*                 */ name_expected,
				AUHost/*                 */ owner_expected
		)
		{
			return parent.AUHostNetworkSystem_from(system, name_expected, owner_expected);
		}

		@Override
		public AUVirtualMachine AUVirtualMachine_from(
				VirtualMachine/*         */ machine
		)
		{
			return parent.AUVirtualMachine_from(machine);
		}

		@Override
		public AUVirtualMachine AUVirtualMachine_from(
				VirtualMachine/*         */ machine,
				String/*                 */ name_expected,
				GuestFamily/*            */ family_expected,
				PasswordAuthentication/* */ adminCredentials_expected,
				AUVirtualMachine/*       */ donor_expected,
				AUResourcePool/*         */ resourcePool_expected
		)
		{
			return parent.AUVirtualMachine_from(machine, name_expected,
					family_expected, adminCredentials_expected, donor_expected, resourcePool_expected
			);
		}

		@Override
		public AUResourcePool AUResourcePool_from(
				ResourcePool/*           */ pool
		)
		{
			return parent.AUResourcePool_from(pool);
		}

		@Override
		public AUResourcePool AUResourcePool_from(
				ResourcePool/*           */ pool,
				String/*                 */ name_expected,
				AUResourcePool/*         */ parent_expected
		)
		{
			return parent.AUResourcePool_from(pool, name_expected, parent_expected);
		}
	}
}


