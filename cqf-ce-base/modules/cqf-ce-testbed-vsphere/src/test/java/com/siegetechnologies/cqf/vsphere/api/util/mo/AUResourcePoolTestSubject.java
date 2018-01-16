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

import com.vmware.vim25.mo.ResourcePool;

/**
 * @author srogers
 */
public class AUResourcePoolTestSubject extends AUResourcePool implements AUTestSubject
{
	protected AUResourcePoolTestSubject(
			AUResourcePool.Manager mom, ResourcePool pool, String name_expected,
			AUResourcePool parent_expected)
	{
		super(mom, pool, name_expected, parent_expected);
	}

	@Override
	protected String delegate_rpc_getName()
	{
		return null;
	}

	@Override
	protected AUResourcePool delegate_rpc_getParent()
	{
		return null;
	}

	/**/

	public static class Manager implements AUResourcePool.Manager
	{
		AUResourcePool.Manager parent;

		public Manager(AUResourcePool.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUResourcePool AUResourcePool_from(ResourcePool pool)
		{
			return null;
		}

		@Override
		public AUResourcePool AUResourcePool_from(ResourcePool pool, String name_expected, AUResourcePool parent_expected)
		{
			return null;
		}
	}
}
