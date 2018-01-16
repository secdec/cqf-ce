package com.siegetechnologies.cqf.vsphere.api.util.mo;

/*-
 * #%L
 * cqf-ce-testbed-vsphere
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

import com.siegetechnologies.cqf.vsphere.api.util.GuestFamily;
import com.vmware.vim25.NamePasswordAuthentication;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.VirtualMachine;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author taylorj
 * @author srogers
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class AUVirtualMachine extends AUManagedObject
{
	protected Manager/*                          */ mom;
	protected VirtualMachine/*                   */ delegate;
	protected Optional<String>/*                 */ name;
	protected Optional<GuestFamily>/*            */ family;
	protected Optional<PasswordAuthentication>/* */ adminCredentials; //<-- non-public accessors only
	protected Optional<AUVirtualMachine>/*       */ donor;
	protected Optional<AUResourcePool>/*         */ resourcePool;

	private static final boolean OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT = true;

	protected static final String/*  */ this_concept/*             */ = "virtual machine";

	protected static final String/*  */ name_concept/*             */ = this_concept+" name";
	protected static final String/*  */ family_concept/*           */ = this_concept+" family";
	protected static final String/*  */ adminCredentials_concept/* */ = this_concept+" admin credentials";
	protected static final String/*  */ donor_concept/*            */ = this_concept+" donor";
	protected static final String/*  */ resourcePool_concept/*     */ = this_concept+" resource pool";

	protected static final boolean/* */ name_actual_is_optional_or_has_no_rpc/*             */ = false;
	protected static final boolean/* */ family_actual_is_optional_or_has_no_rpc/*           */ = false;
	protected static final boolean/* */ adminCredentials_actual_is_optional_or_has_no_rpc/* */ = true;
	protected static final boolean/* */ donor_actual_is_optional_or_has_no_rpc/*            */ = true;
	protected static final boolean/* */ resourcePool_actual_is_optional_or_has_no_rpc/*     */ = true;

	/**
	 * Creates an object of this type.
	 *
	 * @param mom                       managed-object manager
	 * @param machine                   the virtual machine
	 * @param name_expected             name of this machine
	 * @param family_expected           operating system family of this machine
	 * @param adminCredentials_expected admin credentials for interacting w/ this machine
	 * @param donor_expected            donor for this machine (if any)
	 * @param resourcePool_expected     resource pool for this machine
	 */
	protected AUVirtualMachine(
			Manager/*                */ mom,
			VirtualMachine/*         */ machine,
			String/*                 */ name_expected,
			GuestFamily/*            */ family_expected,
			PasswordAuthentication/* */ adminCredentials_expected,
			AUVirtualMachine/*       */ donor_expected,
			AUResourcePool/*         */ resourcePool_expected
	) {
		this.mom/*              */ = requireNonNull(mom, "mom must not be null");
		this.delegate/*         */ = requireNonNullUnlessTesting(mom, machine, "machine must not be null");
		this.name/*             */ = initial_value_of_cached_property(name_expected);
		this.family/*           */ = initial_value_of_cached_property(family_expected);
		this.adminCredentials/* */ = initial_value_of_cached_property(adminCredentials_expected);
		this.donor/*            */ = initial_value_of_cached_property(donor_expected);
		this.resourcePool/*     */ = initial_value_of_cached_property(resourcePool_expected);

		check(name_expected, family_expected, adminCredentials_expected, donor_expected, resourcePool_expected);
	}

	public void check(
			String/*                 */ name_expected,
			GuestFamily/*            */ family_expected,
			PasswordAuthentication/* */ adminCredentials_expected,
			AUVirtualMachine/*       */ donor_expected,
			AUResourcePool/*         */ resourcePool_expected
	) {
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, name_concept,
				name_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getName,
				() -> name_expected
		);
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, family_concept,
				family_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getFamily,
				() -> family_expected
		);
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, adminCredentials_concept,
				adminCredentials_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getAdminCredentials,
				() -> adminCredentials_expected
		);
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, donor_concept,
				donor_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getDonor,
				() -> donor_expected
		);
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, resourcePool_concept,
				resourcePool_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getResourcePool,
				() -> resourcePool_expected
		);

	}

	@Override
	protected void finalize() throws Throwable {

		try {
			/*
			 * It (should be) safe to do this now *only* because we do not share
			 * our master copy of the credentials with anyone outside of this object.
			 */
			this.resetAdminCredentials();
		}
		finally {
			super.finalize();
			//^-- must always be called (last)
		}
	}

	/**/

	public static VirtualMachine underlying(AUVirtualMachine machine) {

		if (machine == null) {
			return null;
		}

		return machine.delegate;
	}

	@Override
	public/*for_mocking_otherwise_protected*/ VirtualMachine getDelegate() {

		return this.delegate;
	}

	/**/

	abstract
	protected String delegate_rpc_getName();

	abstract
	protected GuestFamily delegate_rpc_getFamily();

	abstract
	protected PasswordAuthentication delegate_rpc_getAdminCredentials();

	abstract
	protected AUVirtualMachine delegate_rpc_getDonor();

	abstract
	protected AUResourcePool delegate_rpc_getResourcePool();

	/**/

	/**
	 * Returns the name of this virtual machine.
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
	 * Returns the (guest) OS family of this virtual machine.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public GuestFamily getFamily() {

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, family_concept,
				family_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getFamily,
				() -> this.family, (v) -> this.family = v
		);
	}

	/**/

	/**
	 * Returns the admin credentials needed to interact with this virtual machine.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	private PasswordAuthentication getAdminCredentials() {

		String explanation = "unsafe to share credentials unless they: (1) are cloneable; and (2) finalize securely";

		throw new UnsupportedOperationException(explanation);
	}

	public void setAdminCredentials(PasswordAuthentication value) {

		set_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, adminCredentials_concept,
				adminCredentials_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getAdminCredentials,
				() -> this.adminCredentials, (v) -> this.adminCredentials = v, () -> value
		);
	}

	/**
	 * Makes a best effort to wipe the admin credentials from memory.
	 */
	@SuppressWarnings("OptionalAssignedToNull")
	public void resetAdminCredentials() {

		if (this.adminCredentials == null) {
			return;
		}

		Arrays.fill(this.adminCredentials.get().getPassword(), (char) -1);
		this.adminCredentials = null;
	}

	/**
	 * Returns a <em>copy</em> of this machine's admin credentials in vSphere form.
	 * <p>
	 * That is, in a form suitable for passing to the low-level APIs we use to interact with the vSphere server.
	 *
	 * @return a <em>copy</em> of this machine's admin credentials in vSphere form
	 */
	public NamePasswordAuthentication getAdminCredentials_in_vSphere_form() {

		if (this.adminCredentials == null) {
			return null;
		}

		String n = this.adminCredentials.get().getUserName();
		String p = new String(this.adminCredentials.get().getPassword());

		NamePasswordAuthentication adminCredentials_in_vSphere_form = new NamePasswordAuthentication();
		adminCredentials_in_vSphere_form.setUsername(n);
		adminCredentials_in_vSphere_form.setPassword(p);

		return adminCredentials_in_vSphere_form;
	}

	/**
	 * Returns a string describing this machine's admin credentials, without revealing the password.
	 */
	protected String adminCredentials_toString() {

		return String_from(this.adminCredentials.get());
	}

	/**
	 * Returns a string describing the authentication credentials, without revealing the password.
	 *
	 * @return a string describing the authentication credentials, without revealing the password
	 */
	protected static String String_from(PasswordAuthentication value) {

		return String.format("%s {user: %s; password: %s}",
				value.getClass().getSimpleName(),
				value.getUserName(), "********"
		);
	}

	/**/

	/**
	 * Returns the donor of this virtual machine.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public AUVirtualMachine getDonor() {

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, donor_concept,
				donor_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getDonor,
				() -> this.donor, (v) -> this.donor = v
		);
	}

	public void setDonor(AUVirtualMachine value) {

		set_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, donor_concept,
				donor_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getDonor,
				() -> this.donor, (v) -> this.donor = v, () -> value
		);
	}

	/**/

	/**
	 * Returns the resource pool of this virtual machine.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public AUResourcePool getResourcePool() {

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, resourcePool_concept,
				resourcePool_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getResourcePool,
				() -> this.resourcePool, (v) -> this.resourcePool = v
		);
	}

	/**/

	abstract
	public VirtualDevice[] getHardwareDevices();

	/**/

	public interface Factory extends AUResourcePool.Factory
	{
		AUVirtualMachine AUVirtualMachine_from(
				VirtualMachine/*         */ machine
		);

		AUVirtualMachine AUVirtualMachine_from(
				VirtualMachine/*         */ machine,
				String/*                 */ name_expected,
				GuestFamily/*            */ family_expected,
				PasswordAuthentication/* */ adminCredentials_expected,
				AUVirtualMachine/*       */ donor_expected,
				AUResourcePool/*         */ resourcePool_expected
		);

	}

	public interface Manager extends Factory
	{}

	/**/

	public static enum PowerState
	{
		ON,
		OFF,
		SUSPENDED;

		/**/

		public VirtualMachinePowerState toVSphereVirtualMachinePowerState()
		{
			switch (this) {
			case ON:
				return VirtualMachinePowerState.poweredOn;
			case OFF:
				return VirtualMachinePowerState.poweredOff;
			case SUSPENDED:
				return VirtualMachinePowerState.suspended;
			default:
				throw new IllegalStateException("unrecognized power state");
			}
		}

		/**/

		public static PowerState from(VirtualMachinePowerState value)
		{
			switch (value) {
			case poweredOn:
				return ON;
			case poweredOff:
				return OFF;
			case suspended:
				return SUSPENDED;
			default:
				throw new IllegalStateException("unrecognized vSphere power state");
			}
		}

		/**/

	}

	/**/

}
