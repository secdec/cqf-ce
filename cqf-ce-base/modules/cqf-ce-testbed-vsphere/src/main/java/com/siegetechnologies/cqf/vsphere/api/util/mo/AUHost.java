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

import com.vmware.vim25.mo.HostSystem;
import java.util.Optional;

/**
 * A computer that uses virtualization software, such as ESXi.
 * Hosts provide the CPU and memory resources that virtual machines use,
 * and give virtual machines access to storage and network connectivity.
 *
 * @author srogers
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class AUHost extends AUManagedObject
{
	protected Manager/*                       */ mom;
	protected HostSystem/*                    */ delegate;
	protected Optional<String>/*              */ name;
	protected Optional<AUHostNetworkSystem>/* */ networkSystem;

	private static final boolean OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT = true;

	protected static final String/*  */ this_concept/*          */ = "host";

	protected static final String/*  */ name_concept/*          */ = this_concept+" name";
	protected static final String/*  */ networkSystem_concept/* */ = this_concept+" network system";

	protected static final boolean/* */ name_actual_is_optional_or_has_no_rpc/*          */ = false;
	protected static final boolean/* */ networkSystem_actual_is_optional_or_has_no_rpc/* */ = false;

	/**/

	/**
	 * Creates an object of this type.
	 */
	protected AUHost(
			Manager/*    */ mom,
			HostSystem/* */ system,
			String/*     */ name_expected
	) {
		this.mom/*               */ = requireNonNull(mom, "mom must not be null");
		this.delegate/*          */ = requireNonNullUnlessTesting(mom, system, "system must not be null");
		this.name/*              */ = initial_value_of_cached_property(name_expected);
		this.networkSystem/*     */ = null;

		check(name_expected);
	}

	public void check(
			String/*     */ name_expected
	) {
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, name_concept,
				name_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getName,
				() -> name_expected
		);
	}

	/**/

	public static HostSystem underlying(AUHost system) {

		if (system == null) {
			return null;
		}

		return system.getDelegate();
	}

	@Override
	public/*for_mocking_otherwise_protected*/ HostSystem getDelegate() {

		return this.delegate;
	}

	/**/

	abstract
	protected String delegate_rpc_getName();

	abstract
	protected AUHostNetworkSystem delegate_rpc_getNetworkSystem();

	/**/

	/**
	 * Returns the name of this host.
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
	 * Returns the network system of this host.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public AUHostNetworkSystem getNetworkSystem() {

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, networkSystem_concept,
				networkSystem_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getNetworkSystem,
				() -> this.networkSystem, (v) -> this.networkSystem = v
		);
	}

	/**/

	public interface Factory extends AUHostNetworkSystem.Factory
	{
		AUHost AUHost_from(
				HostSystem/* */ system
		);

		AUHost AUHost_from(
				HostSystem/* */ system,
				String/*     */ name_expected
		);

	}

	public interface Manager extends Factory
	{}

	/**/

}
