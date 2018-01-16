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
import com.vmware.vim25.mo.HostNetworkSystem;
import com.vmware.vim25.mo.HostSystem;

/**
 * @author srogers
 */
public class AUHostProxy extends AUHost
{
	/**
	 * Creates an object of this type.
	 *
	 * @param system
	 * @param name_expected
	 */
	public AUHostProxy(
			Manager/*      */ mom,
			HostSystem/*   */ system,
			String/*       */ name_expected
	)
	{
		super(mom, system, name_expected);
	}

	/**/

	@Override
	protected String delegate_rpc_getName() {

		String result = runRemoteAction(
				() -> this.delegate.getName(),
				() -> "while getting name of host"
		);
		return result;
	}

	@Override
	protected AUHostNetworkSystem delegate_rpc_getNetworkSystem() {

		HostNetworkSystem result_delegate = runRemoteAction(
				() -> this.delegate.getHostNetworkSystem(),
				() -> "while getting network system of host"
		);
		String result_name = this.getName();
		//^-- by design: same as owning host

		AUHostNetworkSystem result = mom.AUHostNetworkSystem_from(
				result_delegate, result_name, this
		);
		return result;
	}

	/**/

	public static class Manager implements AUHost.Manager
	{
		AUHost.Manager parent;

		public Manager(AUHost.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		public AUHost AUHost_from(
				HostSystem/* */ system
		)
		{
			return new AUHostProxy(this, system, null);
		}

		public AUHost AUHost_from(
				HostSystem/* */ system,
				String/*     */ name_expected
		)
		{
			return new AUHostProxy(this, system, name_expected);
		}

		@Override
		public AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/* */ system
		)
		{
			return parent.AUHostNetworkSystem_from(system);
		}

		@Override
		public AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/* */ system,
				String/*            */ name_expected,
				AUHost/*            */ owner_expected
		)
		{
			return parent.AUHostNetworkSystem_from(system, name_expected, owner_expected);
		}
	}

	/**/

}
