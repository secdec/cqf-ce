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
import java.util.Optional;

/**
 * Network (sub)system of a host.
 *
 * @author srogers
 */
public abstract class AUHostNetworkSystem extends AUManagedObject
{
	protected Manager/*           */ mom;
	protected HostNetworkSystem/* */ delegate;
	protected Optional<String>/*  */ name;
	protected Optional<AUHost>/*  */ owner;

	private static final boolean OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT = true;

	protected static final String/*  */ this_concept/*  */ = "host network system";

	protected static final String/*  */ name_concept/*  */ = this_concept+" name";
	protected static final String/*  */ owner_concept/* */ = this_concept+" owner";

	protected static final boolean/* */ name_actual_is_optional_or_has_no_rpc/*  */ = true;
	protected static final boolean/* */ owner_actual_is_optional_or_has_no_rpc/* */ = true;

	/**/

	/**
	 * Creates an object of this type.
	 */
	protected AUHostNetworkSystem(
			Manager/*           */ mom,
			HostNetworkSystem/* */ system,
			String/*            */ name_expected,
			AUHost/*            */ owner_expected
	) {
		this.mom/*      */ = requireNonNull(mom, "mom must not be null");
		this.delegate/* */ = requireNonNullUnlessTesting(mom, system, "system must not be null");
		this.name/*     */ = initial_value_of_cached_property(name_expected);
		this.owner/*    */ = initial_value_of_cached_property(owner_expected);

		check(name_expected, owner_expected);
	}

	public void check(
			String/*            */ name_expected,
			AUHost/*            */ owner_expected
	) {
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT,
				name_concept, name_actual_is_optional_or_has_no_rpc,
				this::delegate_rpc_getName, () -> name_expected
		);
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT,
				owner_concept, owner_actual_is_optional_or_has_no_rpc,
				this::delegate_rpc_getOwner, () -> owner_expected
		);
	}

	/**/

	public static HostNetworkSystem underlying(AUHostNetworkSystem system) {

		if (system == null) {
			return null;
		}

		return system.getDelegate();
	}

	@Override
	public/*for_mocking_otherwise_protected*/ HostNetworkSystem getDelegate() {

		return this.delegate;
	}

	/**/

	abstract
	protected String delegate_rpc_getName();

	abstract
	protected AUHost delegate_rpc_getOwner();

	/**/

	/**
	 * Returns the name of this host network system.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public String getName() {

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, name_concept,
				name_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getName,
				() -> this.name, (v) -> this.name = v
		);
	}

	/**/

	/**
	 * Returns the owner of this host network system.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public AUHost getOwner() {

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, owner_concept,
				owner_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getOwner,
				() -> this.owner, (v) -> this.owner = v
		);
	}

	/**/

	abstract
	public HostPortGroupConfig[] getPortGroupConfigs();

	abstract
	public HostPortGroup[] getPortGroups();

	abstract
	public void addPortGroup(HostPortGroupSpec spec);

	abstract
	public void removePortGroup(String name);

	abstract
	public void updatePortGroup(String name, HostPortGroupSpec spec);

	/**/

	abstract
	public HostVirtualSwitchConfig[] getVirtualSwitchConfigs();

	abstract
	public HostVirtualSwitch[] getVirtualSwitches();

	abstract
	public void addVirtualSwitch(String name, HostVirtualSwitchSpec spec);

	abstract
	public void removeVirtualSwitch(String name);

	abstract
	public void updateVirtualSwitch(String name, HostVirtualSwitchSpec spec);

	/**/

	public interface Factory
	{
		AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/* */ system
		);

		AUHostNetworkSystem AUHostNetworkSystem_from(
				HostNetworkSystem/* */ system,
				String/*            */ name_expected,
				AUHost/*            */ owner_expected
		);

	}

	public interface Manager extends Factory
	{}

	/**/

}
