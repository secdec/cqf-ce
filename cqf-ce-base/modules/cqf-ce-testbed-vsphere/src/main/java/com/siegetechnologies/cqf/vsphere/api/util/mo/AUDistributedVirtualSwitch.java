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

import com.vmware.vim25.mo.ManagedObject;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Models a virtual switch that exists on a collection of (attached) hosts and virtual machines.
 *
 * @author srogers
 */
public class AUDistributedVirtualSwitch extends AUSyntheticManagedObject
{
	protected Manager/*                          */ mom;
	protected Delegate/*                         */ delegate;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	protected Optional<String>/*                 */ name;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	protected Optional<List<AUHost>>/*           */ attachedHostList;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	protected Optional<List<AUVirtualMachine>>/* */ attachedVMList;

	private static final boolean OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT = true;

	protected static final String/*  */ this_concept/*             */ = "virtual switch";

	protected static final String/*  */ name_concept/*             */ = this_concept+" name";
	protected static final String/*  */ attachedHostList_concept/* */ = this_concept+" attached-host list";
	protected static final String/*  */ attachedVMList_concept/*   */ = this_concept+" attached-VM list";

	protected static final boolean/* */ name_actual_is_optional_or_has_no_rpc/*             */ = true;
	protected static final boolean/* */ attachedHostList_actual_is_optional_or_has_no_rpc/* */ = true;
	protected static final boolean/* */ attachedVMList_actual_is_optional_or_has_no_rpc/*   */ = true;

	/**/

	/**
	 * Creates an object of this type.
	 */
	protected AUDistributedVirtualSwitch(
			Manager/*                */ mom,
			Delegate/*               */ switch_stub,
			String/*                 */ name_expected,
			List<AUHost>/*           */ attachedHostList_expected,
			List<AUVirtualMachine>/* */ attachedVMList_expected
	)
	{
		this.mom/*              */ = requireNonNull(mom, "mom must not be null");
		this.delegate/*         */ = requireNull(mom, switch_stub, "switch must be null");
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

	public static Delegate underlying(AUDistributedVirtualSwitch virtualSwitch)
	{

		if (virtualSwitch == null) {
			return null;
		}

		return virtualSwitch.getDelegate();
	}

	@Override
	public/*for_mocking_otherwise_protected*/ Delegate getDelegate()
	{

		return this.delegate;
	}

	@Override
	protected String getMessageDigestOfKeyProperties(String algorithm)
	{
		MessageDigest digest = getMessageDigest(algorithm);

		digest.update(this.getName().getBytes());

		List<AUHost> attachedHostList = this.getAttachedHostList();
		if (attachedHostList != null) {

			TreeSet<String> attachedHostNames = new TreeSet<>(); // supplies names in natural order
			attachedHostList.stream().forEach(x -> attachedHostNames.add(x.getName()));
			for (String n : attachedHostNames) {

				digest.update(n.getBytes());
			}
		}

		List<AUVirtualMachine> attachedVMList = this.getAttachedVMList();
		if (attachedVMList != null) {

			TreeSet<String> attachedVMNames = new TreeSet<>(); // supplies names in natural order
			attachedVMList.stream().forEach(x -> attachedVMNames.add(x.getName()));
			for (String n : attachedVMNames) {

				digest.update(n.getBytes());
			}
		}

		return digest.toString();
	}

	/**/

	protected String delegate_rpc_getName()
	{
		return null;
	}

	protected List<AUHost> delegate_rpc_getAttachedHostList()
	{
		return null;
	}

	protected List<AUVirtualMachine> delegate_rpc_getAttachedVMList()
	{
		return null;
	}

	/**/

	/**
	 * Returns the name of this distributed virtual switch.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public String getName()
	{
		String result = get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, name_concept,
				name_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getName,
				() -> this.name, (v) -> this.name = v
		);

		if (result == null) {

			throw new NullPointerException("name must not be null");
		}

		return result;
	}

	/**/

	/**
	 * Returns the hosts attached to this distributed virtual switch.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public List<AUHost> getAttachedHostList()
	{

		List<AUHost> result = get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, attachedHostList_concept,
				attachedHostList_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getAttachedHostList,
				() -> this.attachedHostList, (v) -> this.attachedHostList = v
		);
		return (result == null) ? null : Collections.unmodifiableList(result);
	}

	/**/

	/**
	 * Returns the virtual machines attached to this distributed virtual switch.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public List<AUVirtualMachine> getAttachedVMList()
	{

		List<AUVirtualMachine> result = get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, attachedVMList_concept,
				attachedVMList_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getAttachedVMList,
				() -> this.attachedVMList, (v) -> this.attachedVMList = v
		);
		return (result == null) ? null : Collections.unmodifiableList(result);
	}

	/**/

	public interface Factory
	{
		AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(
				Delegate/*               */ virtualSwitch
		);

		AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(
				Delegate/*               */ virtualSwitch,
				String/*                 */ name_expected,
				List<AUHost>/*           */ attachedHostList_expected,
				List<AUVirtualMachine>/* */ attachedVMList_expected
		);

	}

	public interface Manager extends Factory
	{ }

	public static class ManagerImpl implements Manager
	{
		AUDistributedVirtualSwitch.Manager parent;

		ManagerImpl(AUDistributedVirtualSwitch.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(Delegate virtualSwitch)
		{
			return new AUDistributedVirtualSwitch(this, virtualSwitch,
					null,null, null
			);
		}

		@Override
		public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(Delegate virtualSwitch, String name_expected,
				List<AUHost> attachedHostList_expected, List<AUVirtualMachine> attachedVMList_expected)
		{
			return new AUDistributedVirtualSwitch(this, virtualSwitch,
					name_expected, attachedHostList_expected, attachedVMList_expected
			);
		}
	}

	/**/

	public static class Delegate extends ManagedObject
	{ }

}
