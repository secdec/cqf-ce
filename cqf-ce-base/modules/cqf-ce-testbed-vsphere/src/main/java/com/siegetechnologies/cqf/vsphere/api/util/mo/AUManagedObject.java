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

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.ManagedObject;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Locally managed object that can cache remote properties.
 *
 * @author srogers
 */
@SuppressWarnings("OptionalAssignedToNull")
public class AUManagedObject
{
	protected static <T> T requireNonNull(T o, String message) {

		return Objects.requireNonNull(o, message);
	}

	protected static <D, M> D requireNonNullUnlessTesting(M mom, D delegate, String message)
	{
		if (mom instanceof AUTestSubject) {

			return delegate;
		}

		return Objects.requireNonNull(delegate, message);
	}

	protected static <D, M> D requireNull(M mom, D delegate, String message)
	{
		if (delegate != null) {

			throw new IllegalArgumentException(message);
		}

		return delegate;
	}

	/**/

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.getDelegateMOR());
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) {
			return true;
		}

		if (o instanceof AUManagedObject) {

			AUManagedObject that = (AUManagedObject) o;

			return Objects.equals(this.getDelegateMOR(), that.getDelegateMOR());
		}

		return false;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();

		result.append(this.getClass().getSimpleName());
		result.append(" {");
		result.append(" delegateMOR: ");
		result.append(this.getDelegateMOR());
		result.append(" }");

		return result.toString();
	}

	/**/

	protected final boolean isTestSubject() {

		return this instanceof AUTestSubject;
	}

	/**/

	public/*for_mocking_otherwise_protected*/ ManagedObject getDelegate()
	{
		throw new UnsupportedOperationException("subclass responsibility");
	}

	protected ManagedObjectReference getDelegateMOR()
	{
		ManagedObject delegate = this.getDelegate();

		return (delegate != null) ? delegate.getMOR() : null;
	}

	/**/

	/**
	 * Returns the name of this managed object.
	 * <p/>
	 * NB: This value is cached locally, and might go stale.
	 */
	public String getName()
	{
		throw new UnsupportedOperationException("subclass responsibility");
	}

	/**/

	/**
	 *
	 * @param value_expected
	 * @param <T>
	 * @return
	 */
	protected <T>
	Optional<T> initial_value_of_cached_property(
			T/*                     */ value_expected
	)
	{
		return (value_expected != null) ? Optional.of(value_expected) : null;
	}

	/**
	 *
	 * @param optimize_correctness_over_rpc_count
	 * @param property_concept
	 * @param value_actual_is_optional_or_has_no_rpc
	 * @param value_actual_supplier
	 * @param value_expected_supplier
	 * @param <T>
	 *
	 * @throws IllegalStateException
	 * @throws NullPointerException
	 */
	protected <T>
	void check_cached_property(
			boolean/*               */ optimize_correctness_over_rpc_count,
			String/*                */ property_concept,
			boolean/*               */ value_actual_is_optional_or_has_no_rpc,
			Supplier<T>/*           */ value_actual_supplier,
			Supplier<T>/*           */ value_expected_supplier
	)
	{
		if (! optimize_correctness_over_rpc_count) {
			return;
		}

		T value_expected = value_expected_supplier.get();
		value_expected_supplier = null;

		if (value_expected != null) {

			T value_actual = value_actual_supplier.get();
			value_actual_supplier = null;

			if (value_actual == null && isTestSubject()) {
				value_actual = value_expected;
			}

			if (value_actual == null && ! value_actual_is_optional_or_has_no_rpc) {

				throw new NullPointerException(property_concept +
						" cross-check: unexpected null pointer for remote value");
			}

			if (value_actual != null && ! Objects.equals(value_expected, value_actual)) {

				throw new IllegalStateException(property_concept +
						" cross-check failed" + "; expected: " + value_expected + "; actual: " + value_actual
				);
			}
		}
	}

	/**
	 *
	 * @param optimize_correctness_over_rpc_count
	 * @param property_concept
	 * @param value_actual_is_optional_or_has_no_rpc
	 * @param value_actual_supplier
	 * @param property_cached_supplier
	 * @param property_cached_updater
	 * @param <T>
	 *
	 * @throws NullPointerException
	 * @return
	 */
	protected <T>
	T get_cached_property(
			boolean/*               */ optimize_correctness_over_rpc_count,
			String/*                */ property_concept,
			boolean/*               */ value_actual_is_optional_or_has_no_rpc,
			Supplier<T>/*           */ value_actual_supplier,
			Supplier<Optional<T>>/* */ property_cached_supplier,
			Consumer<Optional<T>>/* */ property_cached_updater
	)
	{
		Optional<T> property_cached = property_cached_supplier.get();
		property_cached_supplier = null;

		if (property_cached == null) {

			T value_actual = value_actual_supplier.get();
			value_actual_supplier = null;

			if (value_actual == null && ! value_actual_is_optional_or_has_no_rpc) {

				throw new NullPointerException(property_concept+" get:" +
						" unexpected null pointer for remote value");
			}

			if (value_actual_is_optional_or_has_no_rpc) {

				property_cached = Optional.ofNullable(value_actual);
			}
			else {
				property_cached = Optional.of(value_actual);
			}

			property_cached_updater.accept(property_cached);
			property_cached_updater = null;
		}

		assert property_cached != null;

		if (value_actual_is_optional_or_has_no_rpc) {
			return property_cached.orElse(null);
		}
		else {
			return property_cached.get();
		}
	}

	/**
	 *
	 * @param optimize_correctness_over_rpc_count
	 * @param property_concept
	 * @param value_actual_is_optional_or_has_no_rpc
	 * @param value_actual_supplier
	 * @param property_cached_supplier
	 * @param property_cached_updater
	 * @param value_expected_supplier
	 * @param <T>
	 *
	 * @throws IllegalStateException
	 * @throws NullPointerException
	 */
	protected <T>
	void set_cached_property(
			boolean/*               */ optimize_correctness_over_rpc_count,
			String/*                */ property_concept,
			boolean/*               */ value_actual_is_optional_or_has_no_rpc,
			Supplier<T>/*           */ value_actual_supplier,
			Supplier<Optional<T>>/* */ property_cached_supplier,
			Consumer<Optional<T>>/* */ property_cached_updater,
			Supplier<T>/*           */ value_expected_supplier
	)
	{
		T value_expected = value_expected_supplier.get();
		value_expected_supplier = null;

		if (value_expected == null && ! value_actual_is_optional_or_has_no_rpc) {

			throw new NullPointerException(property_concept+" set:" +
					" unexpected null pointer for new value");
		}

		Optional<T> property_cached = property_cached_supplier.get();
		property_cached_supplier = null;

		if (property_cached != null && ! Objects.equals(property_cached.orElse(null), value_expected)) {

			throw new IllegalStateException(property_concept+" has already been set");
		}

		check_cached_property(
				optimize_correctness_over_rpc_count, property_concept,
				value_actual_is_optional_or_has_no_rpc, value_actual_supplier,
				() -> value_expected
		);

		if (value_actual_is_optional_or_has_no_rpc) {

			property_cached = Optional.ofNullable(value_expected);
		}
		else {

			property_cached = Optional.of(value_expected);
		}

		property_cached_updater.accept(property_cached);
		property_cached_updater = null;
	}

}
