package com.siegetechnologies.cqf.vsphere.api.util.mo.proxies;

/*-
 * #%L
 * cqf-ce-testbed-vsphere
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

import com.siegetechnologies.cqf.vsphere.api.util.GuestFamily;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.VirtualMachine;
import java.net.PasswordAuthentication;

public class AUVirtualMachineProxy extends AUVirtualMachine
{
	/**
	 * Creates an object of this type.
	 *
	 * @param mom                       managed-object manager
	 * @param machine                   the virtual machine
	 * @param name_expected             name of this machine
	 * @param family_expected           operating system family of this machine
	 * @param adminCredentials_expected admin credentials for interacting w/ this machine
	 * @param donor_expected            donor for this machine (if any)
	 * @param resourcePool_expected     resource pool for this machine
	 */
	protected AUVirtualMachineProxy(
			Manager/*                */ mom,
			VirtualMachine/*         */ machine,
			String/*                 */ name_expected,
			GuestFamily/*            */ family_expected,
			PasswordAuthentication/* */ adminCredentials_expected,
			AUVirtualMachine/*       */ donor_expected,
			AUResourcePool/*         */ resourcePool_expected
	)
	{
		super(mom, machine, name_expected, family_expected, adminCredentials_expected,
				donor_expected, resourcePool_expected
		);
	}

	/**/

	@Override
	protected String delegate_rpc_getName() {

		String result = runRemoteAction(
				() -> this.delegate.getName(),
				() -> "while getting name of virtual machine"
		);
		return result;
	}

	@Override
	protected GuestFamily delegate_rpc_getFamily() {

		GuestFamily result = GuestFamily.valueOf(this.delegate);
		//^-- potential RPC to vSphere (expensive)

		return result;
	}

	@Override
	protected PasswordAuthentication delegate_rpc_getAdminCredentials() {

		/*
		 * We only maintain the (proxy) virtual machine's admin credentials locally.
		 */
		return null;
	}

	@Override
	protected AUVirtualMachine delegate_rpc_getDonor() {

		return null; // TODO: REVIEW: srogers: implement this stub(??)
	}

	@Override
	protected AUResourcePool delegate_rpc_getResourcePool() {

		ResourcePool result_delegate = runRemoteAction(
				() -> delegate.getResourcePool(),
				() -> "while getting resource pool of virtual machine"
		);
		AUResourcePool result = mom.AUResourcePool_from(
				result_delegate, null, null
		);
		return result;
	}

	/**/

	@Override
	public VirtualDevice[] getHardwareDevices()
	{
		return runRemoteAction(
				() -> this.delegate.getConfig().getHardware().getDevice(),
				() -> "while getting hardware devices"
		);
	}

	/**/

	public static class Manager implements AUVirtualMachine.Manager
	{
		protected AUVirtualMachine.Manager parent;

		public Manager(AUVirtualMachine.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUVirtualMachine AUVirtualMachine_from(
				VirtualMachine/*         */ machine
		)
		{
			return new AUVirtualMachineProxy(this, machine,
					null, null, null,
					null, null
			);
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
			return new AUVirtualMachineProxy(this, machine,
					name_expected, family_expected, adminCredentials_expected, donor_expected,
					resourcePool_expected
			);
		}

		@Override
		public AUResourcePool AUResourcePool_from(
				ResourcePool/*   */ pool
		)
		{
			return parent.AUResourcePool_from(pool);
		}

		@Override
		public AUResourcePool AUResourcePool_from(
				ResourcePool/*   */ pool,
				String/*         */ name_expected,
				AUResourcePool/* */ parent_expected
		)
		{
			return parent.AUResourcePool_from(pool, name_expected, parent_expected);
		}

	}

	/**/

}
