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

/**
 * @author srogers
 */
public class AUDistributedVirtualPortGroupTestSubject extends AUDistributedVirtualPortGroup implements AUTestSubject
{
	protected AUDistributedVirtualPortGroupTestSubject(
			AUDistributedVirtualPortGroup.Manager/*  */ mom,
			AUDistributedVirtualPortGroup.Delegate/* */ portGroup,
			String/*                                 */ name_expected,
			AUDistributedVirtualSwitch/*             */ owner_expected
	)
	{
		super(mom, portGroup, name_expected, owner_expected);
	}

	/**/

	public static class Manager implements AUDistributedVirtualPortGroup.Manager
	{
		AUDistributedVirtualPortGroup.Manager parent;

		public Manager(AUDistributedVirtualPortGroup.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
				Delegate/*                   */ portGroup
		)
		{
			return new AUDistributedVirtualPortGroupTestSubject(this, portGroup, null, null);
		}

		@Override
		public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
				Delegate/*                   */ portGroup,
				String/*                     */ name_expected,
				AUDistributedVirtualSwitch/* */ owner_expected
		)
		{
			return new AUDistributedVirtualPortGroupTestSubject(this, portGroup, name_expected, owner_expected);
		}

	}
}


