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
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.VirtualMachine;
import java.net.PasswordAuthentication;

/**
 * @author srogers
 */
public class AUVirtualMachineTestSubject extends AUVirtualMachine implements AUTestSubject
{
	protected AUVirtualMachineTestSubject(
			AUVirtualMachine.Manager mom, VirtualMachine machine, String name_expected,
			GuestFamily family_expected, PasswordAuthentication adminCredentials_expected,
			AUVirtualMachine donor_expected, AUResourcePool resourcePool_expected
	)
	{
		super(
				mom, machine, name_expected, family_expected, adminCredentials_expected,
				donor_expected, resourcePool_expected
		);
	}

	/**/

	@Override
	protected String delegate_rpc_getName()
	{
		return null;
	}

	@Override
	protected GuestFamily delegate_rpc_getFamily()
	{
		return null;
	}

	@Override
	protected PasswordAuthentication delegate_rpc_getAdminCredentials()
	{
		return null;
	}

	@Override
	protected AUVirtualMachine delegate_rpc_getDonor()
	{
		return null;
	}

	@Override
	protected AUResourcePool delegate_rpc_getResourcePool()
	{
		return null;
	}

	@Override
	public VirtualDevice[] getHardwareDevices()
	{
		return new VirtualDevice[0];
	}

	/**/

	public static class Manager implements AUVirtualMachine.Manager
	{
		AUVirtualMachine.Manager parent;

		public Manager(AUVirtualMachine.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUVirtualMachine AUVirtualMachine_from(VirtualMachine machine)
		{
			return new AUVirtualMachineTestSubject(
					this, machine, null, null, null,
					null, null
			);
		}

		@Override
		public AUVirtualMachine AUVirtualMachine_from(
				VirtualMachine machine, String name_expected,
				GuestFamily family_expected, PasswordAuthentication adminCredentials_expected,
				AUVirtualMachine donor_expected, AUResourcePool resourcePool_expected
		)
		{
			return new AUVirtualMachineTestSubject(
					this, machine, name_expected, family_expected, adminCredentials_expected,
					donor_expected, resourcePool_expected
			);
		}

		@Override
		public AUResourcePool AUResourcePool_from(ResourcePool pool)
		{
			return parent.AUResourcePool_from(pool);
		}

		@Override
		public AUResourcePool AUResourcePool_from(ResourcePool pool, String name_expected, AUResourcePool parent_expected)
		{
			return parent.AUResourcePool_from(pool, name_expected, parent_expected);
		}
	}
}
