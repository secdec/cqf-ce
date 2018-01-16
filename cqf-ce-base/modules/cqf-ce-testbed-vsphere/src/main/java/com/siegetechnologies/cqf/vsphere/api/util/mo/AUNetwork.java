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

import com.vmware.vim25.mo.Network;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Models a network accessible by hosts and virtual machines. This can be a physical network or a logical network, such as a
 * VLAN.
 *
 * @author srogers
 */
public abstract class AUNetwork extends AUManagedObject
{
	protected Manager/*                          */ mom;
	protected Network/*                          */ delegate;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	protected Optional<String>/*                 */ name;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	protected Optional<List<AUHost>>/*           */ attachedHostList;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	protected Optional<List<AUVirtualMachine>>/* */ attachedVMList;

	private static final boolean OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT = true;

	protected static final String/*  */ this_concept/*             */ = "network";

	protected static final String/*  */ name_concept/*             */ = this_concept+" name";
	protected static final String/*  */ attachedHostList_concept/* */ = this_concept+" attached-host list";
	protected static final String/*  */ attachedVMList_concept/*   */ = this_concept+" attached-VM list";

	protected static final boolean/* */ name_actual_is_optional_or_has_no_rpc/*             */ = false;
	protected static final boolean/* */ attachedHostList_actual_is_optional_or_has_no_rpc/* */ = false;
	protected static final boolean/* */ attachedVMList_actual_is_optional_or_has_no_rpc/*   */ = false;

	/**/

	/**
	 * Creates an object of this type.
	 */
	protected AUNetwork(
			Manager/*                */ mom,
			Network/*                */ network,
			String/*                 */ name_expected,
			List<AUHost>/*           */ attachedHostList_expected,
			List<AUVirtualMachine>/* */ attachedVMList_expected
	)
	{
		this.mom/*              */ = requireNonNull(mom, "mom must not be null");
		this.delegate/*         */ = requireNonNullUnlessTesting(mom, network, "network must not be null");
		this.name/*             */ = initial_value_of_cached_property(name_expected);
		this.attachedHostList/* */ = initial_value_of_cached_property(attachedHostList_expected);
		this.attachedVMList/*   */ = initial_value_of_cached_property(attachedVMList_expected);

		check(name_expected, attachedHostList_expected, attachedVMList_expected);
	}

	public void check(
			String/*                 */ name_expected,
			List<AUHost>/*           */ attachedHostList_expected,
			List<AUVirtualMachine>/* */ attachedVMList_expected
	)
	{
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, name_concept,
				name_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getName,
				() -> name_expected
		);
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, attachedHostList_concept,
				attachedHostList_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getAttachedHostList,
				() -> attachedHostList_expected
		);
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, attachedVMList_concept,
				attachedVMList_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getAttachedVMList,
				() -> attachedVMList_expected
		);
	}

	/**/

	public static Network underlying(AUNetwork network)
	{

		if (network == null) {
			return null;
		}

		return network.getDelegate();
	}

	@Override
	public/*for_mocking_otherwise_protected*/ Network getDelegate()
	{

		return this.delegate;
	}

	/**/

	abstract
	protected String delegate_rpc_getName();

	abstract
	protected List<AUHost> delegate_rpc_getAttachedHostList();

	abstract
	protected List<AUVirtualMachine> delegate_rpc_getAttachedVMList();

	/**/

	/**
	 * Returns the name of this network.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public String getName()
	{

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, name_concept,
				name_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getName,
				() -> this.name, (v) -> this.name = v
		);
	}

	/**/

	/**
	 * Returns the hosts attached to this network.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public List<AUHost> getAttachedHostList()
	{

		return Collections.unmodifiableList(get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, attachedHostList_concept,
				attachedHostList_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getAttachedHostList,
				() -> this.attachedHostList, (v) -> this.attachedHostList = v
		));
	}

	/**/

	/**
	 * Returns the virtual machines attached to this network.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public List<AUVirtualMachine> getAttachedVMList()
	{

		return Collections.unmodifiableList(get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, attachedVMList_concept,
				attachedVMList_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getAttachedVMList,
				() -> this.attachedVMList, (v) -> this.attachedVMList = v
		));
	}

	/**/

	public interface Factory extends AUHost.Factory, AUVirtualMachine.Factory
	{
		AUNetwork AUNetwork_from(
				Network/*                */ network
		);

		AUNetwork AUNetwork_from(
				Network/*                */ network,
				String/*                 */ name_expected,
				List<AUHost>/*           */ attachedHostList_expected,
				List<AUVirtualMachine>/* */ attachedVMList_expected
		);

	}

	public interface Manager extends Factory
	{ }

	/**/

}
