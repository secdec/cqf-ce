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

import com.vmware.vim25.ResourceAllocationInfo;
import com.vmware.vim25.mo.ResourcePool;
import java.util.Optional;

/**
 * @author srogers
 */
@SuppressWarnings("ALL")
public abstract class AUResourcePool extends AUManagedObject
{
	protected Manager/*                  */ mom;
	protected ResourcePool/*             */ delegate;
	protected Optional<String>/*         */ name;
	protected Optional<AUResourcePool>/* */ parent;

	private static final boolean OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT = true;

	protected static final String/*  */ this_concept/*   */ = "resource pool";

	protected static final String/*  */ name_concept/*   */ = this_concept+" name";
	protected static final String/*  */ parent_concept/* */ = this_concept+" parent";

	protected static final boolean/* */ name_actual_is_optional_or_has_no_rpc/*   */ = false;
	protected static final boolean/* */ parent_actual_is_optional_or_has_no_rpc/* */ = true;

	/**/

	/**
	 * Creates an object of this type.
	 */
	protected AUResourcePool(
			Manager/*        */ mom,
			ResourcePool/*   */ pool,
			String/*         */ name_expected,
			AUResourcePool/* */ parent_expected
	) {
		this.mom/*      */ = requireNonNull(mom, "mom must not be null");
		this.delegate/* */ = requireNonNullUnlessTesting(mom, pool, "pool must not be null");
		this.name/*     */ = initial_value_of_cached_property(name_expected);
		this.parent/*   */ = initial_value_of_cached_property(parent_expected);

		check(name_expected, parent_expected);
	}

	public void check(
			String/*         */ name_expected,
			AUResourcePool/* */ parent_expected
	) {
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, name_concept,
				name_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getName,
				() -> name_expected
		);
		check_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, parent_concept,
				parent_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getParent,
				() -> parent_expected
		);
	}

	/**/

	public static ResourcePool underlying(AUResourcePool pool) {

		if (pool == null) {
			return null;
		}

		return pool.getDelegate();
	}

	@Override
	public/*for_mocking_otherwise_protected*/ ResourcePool getDelegate() {

		return this.delegate;
	}

	/**/

	abstract
	protected String delegate_rpc_getName();

	abstract
	protected AUResourcePool delegate_rpc_getParent();

	/**/

	/**
	 * Returns the name of this resource pool.
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
	 * Returns the parent of this resource pool.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public AUResourcePool getParent() {

		return get_cached_property(
				OPTIMIZE_CORRECTNESS_OVER_RPC_COUNT, parent_concept,
				parent_actual_is_optional_or_has_no_rpc, this::delegate_rpc_getParent,
				() -> this.parent, (v) -> this.parent = v
		);
	}

	/**/

	public interface Factory
	{
		AUResourcePool AUResourcePool_from(
				ResourcePool/*   */ pool
		);

		AUResourcePool AUResourcePool_from(
				ResourcePool/*   */ pool,
				String/*         */ name_expected,
				AUResourcePool/* */ parent_expected
		);

	}

	public interface Manager extends Factory
	{}

	/**/

	public static class AllocationInfo
	{
		public Long/*    */ limit;
		public Long/*    */ reservation;
		public Boolean/* */ reservationExpandable;

		/**/

		public Long getLimit()
		{
			return this.limit;
		}

		public void setLimit(Long value)
		{
			this.limit = value;
		}

		/**/

		public ResourceAllocationInfo toVSphereResourceAllocationInfo()
		{
			ResourceAllocationInfo result = new ResourceAllocationInfo();

			result.setReservation(this.reservation);
			result.setExpandableReservation(this.reservationExpandable);
			result.setLimit(this.limit);

			return result;
		}

		/**/

		public Long getReservation()
		{
			return this.reservation;
		}

		public void setReservation(Long value)
		{
			this.reservation = value;
		}

		/**/

		public Boolean isReservationExpandable()
		{
			return this.reservationExpandable;
		}

		public void setReservationExpandable(Boolean value)
		{
			this.reservationExpandable = value;
		}

		/**/

	}

	/**/

}
