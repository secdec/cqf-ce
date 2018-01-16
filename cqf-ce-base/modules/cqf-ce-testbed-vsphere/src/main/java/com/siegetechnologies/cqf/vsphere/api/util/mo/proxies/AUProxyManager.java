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

import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHost;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHostNetworkSystem;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUNetwork;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUObjectManager;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;

/**
 * Manages instances of object types in this package.
 *
 * @author srogers
 */
public class AUProxyManager extends AUObjectManager
{
	public AUProxyManager()
	{
		super();
	}

	public AUProxyManager(String name)
	{
		super(name);
	}

	/**/

	@Override
	protected AUHost.Manager
	newHostManager(AUHost.Manager parent)
	{
		return new AUHostProxy.Manager(parent);
	}

	@Override
	protected AUHostNetworkSystem.Manager
	newHostNetworkSystemManager(AUHostNetworkSystem.Manager parent)
	{
		return new AUHostNetworkSystemProxy.Manager(parent);
	}

	@Override
	protected AUNetwork.Manager
	newNetworkManager(AUNetwork.Manager parent)
	{
		return new AUNetworkProxy.Manager(parent);
	}

	@Override
	protected AUResourcePool.Manager
	newResourcePoolManager(AUResourcePool.Manager parent)
	{
		return new AUResourcePoolProxy.Manager(parent);
	}

	@Override
	protected AUVirtualMachine.Manager
	newVirtualMachineManager(AUVirtualMachine.Manager parent)
	{
		return new AUVirtualMachineProxy.Manager(parent);
	}

	/**/

}
