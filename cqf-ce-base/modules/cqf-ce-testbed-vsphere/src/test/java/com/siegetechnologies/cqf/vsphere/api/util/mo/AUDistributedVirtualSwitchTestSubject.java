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

import java.util.List;

/**
 * @author srogers
 */
public class AUDistributedVirtualSwitchTestSubject extends AUDistributedVirtualSwitch implements AUTestSubject
{
	protected AUDistributedVirtualSwitchTestSubject(
			AUDistributedVirtualSwitch.Manager/*  */ mom,
			AUDistributedVirtualSwitch.Delegate/* */ virtualSwitch,
			String/*                              */ name_expected,
			List<AUHost>/*                        */ attachedHostList_expected,
			List<AUVirtualMachine>/*              */ attachedVMList_expected
	)
	{
		super(mom, virtualSwitch, name_expected, attachedHostList_expected, attachedVMList_expected);
	}

	/**/

	public static class Manager implements AUDistributedVirtualSwitch.Manager
	{
		AUDistributedVirtualSwitch.Manager parent;

		public Manager(AUDistributedVirtualSwitch.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(Delegate virtualSwitch)
		{
			return new AUDistributedVirtualSwitchTestSubject(this, virtualSwitch,
					null,null, null
			);
		}

		@Override
		public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(Delegate virtualSwitch, String name_expected,
				List<AUHost> attachedHostList_expected, List<AUVirtualMachine> attachedVMList_expected)
		{
			return new AUDistributedVirtualSwitchTestSubject(this, virtualSwitch,
					name_expected, attachedHostList_expected, attachedVMList_expected
			);
		}
	}
}


