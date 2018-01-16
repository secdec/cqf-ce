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
import java.util.Optional;

/**
 * Models a virtual port group that is owned by (part of) a distributed virtual switch.
 *
 * @author srogers
 */
public class AUDistributedVirtualPortGroup extends AUSyntheticManagedObject
{
	protected Manager/*                              */ mom;
	protected Delegate/*                             */ delegate;
	protected Optional<String>/*                     */ name;
	protected Optional<AUDistributedVirtualSwitch>/* */ owner;

	private static final boolean OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT = true;

	protected static final String/*  */ this_concept/*  */ = "virtual port group";

	protected static final String/*  */ name_concept/*  */ = this_concept+" name";
	protected static final String/*  */ owner_concept/* */ = this_concept+" owner";

	protected static final boolean/* */ name_actual_is_optional_or_has_no_rpc/*  */ = true;
	protected static final boolean/* */ owner_actual_is_optional_or_has_no_rpc/* */ = true;

	/**/

	/**
	 * Creates an object of this type.
	 */
	protected AUDistributedVirtualPortGroup(
			Manager/*                    */ mom,
			Delegate/*                   */ portGroup,
			String/*                     */ name_expected,
			AUDistributedVirtualSwitch/* */ owner_expected
	) {
		this.mom/*      */ = requireNonNull(mom, "mom must not be null");
		this.delegate/* */ = requireNull(mom, portGroup, "portGroup must be null");
		this.name/*     */ = initial_value_of_cached_property(name_expected);
		this.owner/*    */ = initial_value_of_cached_property(owner_expected);

		check(name_expected, owner_expected);
	}

	public void check(
			String/*            */ name_expected,
			AUDistributedVirtualSwitch/*            */ owner_expected
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

	public static Delegate underlying(AUDistributedVirtualPortGroup portGroup) {

		if (portGroup == null) {
			return null;
		}

		return portGroup.getDelegate();
	}

	@Override
	public/*for_mocking_otherwise_protected*/ Delegate getDelegate() {

		return this.delegate;
	}

	@Override
	protected String getMessageDigestOfKeyProperties(String algorithm)
	{
		MessageDigest digest = getMessageDigest(algorithm);

		digest.update(this.getName().getBytes());

		AUDistributedVirtualSwitch owner = this.getOwner();
		if (owner != null) {

			String owner_name = owner.getName();
			if (owner_name != null) {

				digest.update(owner_name.getBytes());
			}
		}

		return digest.toString();
	}

	/**/

	protected String delegate_rpc_getName()
	{
		return null;
	}

	protected AUDistributedVirtualSwitch delegate_rpc_getOwner()
	{
		return null;
	}

	/**/

	/**
	 * Returns the name of this host network portGroup.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public String getName() {

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
	 * Returns the owner of this host network portGroup.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public AUDistributedVirtualSwitch getOwner() {

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, owner_concept,
				owner_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getOwner,
				() -> this.owner, (v) -> this.owner = v
		);
	}

	/**/

	public interface Factory
	{
		AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
				Delegate/*                   */ portGroup
		);

		AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
				Delegate/*                   */ portGroup,
				String/*                     */ name_expected,
				AUDistributedVirtualSwitch/* */ owner_expected
		);

	}

	public interface Manager extends Factory
	{}

	public static class ManagerImpl implements Manager
	{
		AUDistributedVirtualPortGroup.Manager parent;

		ManagerImpl(AUDistributedVirtualPortGroup.Manager parent)
		{
			this.parent = parent;
		}

		/**/

		@Override
		public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
				Delegate/*                   */ portGroup
		)
		{
			return new AUDistributedVirtualPortGroup(this, portGroup, null, null);
		}

		@Override
		public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
				Delegate/*                   */ portGroup,
				String/*                     */ name_expected,
				AUDistributedVirtualSwitch/* */ owner_expected
		)
		{
			return new AUDistributedVirtualPortGroup(this, portGroup, name_expected, owner_expected);
		}

	}

	/**/

	public static class Delegate extends ManagedObject
	{ }

	/**/

}
