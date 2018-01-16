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

import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;

/**
 * @author srogers
 */
public class AUResourcePoolProxy extends AUResourcePool
{
	/**
	 * Creates an object of this type.
	 *
	 * @param mom
	 * @param pool
	 * @param parent_expected
	 * @param name_expected
	 */
	protected AUResourcePoolProxy(
			Manager/*        */ mom,
			ResourcePool/*   */ pool,
			String/*         */ name_expected,
			AUResourcePool/* */ parent_expected
	)
	{
		super(mom, pool, name_expected, parent_expected);
	}

	/**/

	protected String delegate_rpc_getName() {

		String result = runRemoteAction(
				() -> this.delegate.getName(),
				() -> "while getting name of resource pool"
		);
		return result;
	}

	protected AUResourcePool delegate_rpc_getParent() {

		ManagedEntity parent = runRemoteAction(
				() -> this.delegate.getParent(),
				() -> "while getting parent of resource pool"
		);

		if (parent == null) {

			return null;
		}
		else if (parent instanceof ResourcePool) {

			ResourcePool parent_actual = (ResourcePool) parent;

			return mom.AUResourcePool_from(parent_actual);
		}

		return null; // this resource pool has no parent (resource pool)
	}

	/**/

	public static class Manager implements AUResourcePool.Manager
	{
		protected AUResourcePool.Manager parent;

		public Manager(AUResourcePool.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUResourcePool AUResourcePool_from(
				ResourcePool/*   */ pool
		)
		{
			return new AUResourcePoolProxy(this, pool, null, null);
		}

		@Override
		public AUResourcePool AUResourcePool_from(
				ResourcePool/*   */ pool,
				String/*         */ name_expected,
				AUResourcePool/* */ parent_expected
		)
		{
			return new AUResourcePoolProxy(this, pool, name_expected, parent_expected);
		}

	}

}
