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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Objects;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

/**
 * @author srogers
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AUManagedObjectTest
{
	AUManagedObject mo = new AUManagedObject();

	AUManagedObject mo_testSubject = new AUManagedObjectTestSubject();

	@Before
	public void SetUp()
			throws Exception
	{
		/*
		 * This is counterintuitive: for the AUManagedObjectTest cases (only), we do not want
		 * to alter fundamental AUManagedObject behavior just because we are in a testing mode.
		 * That we can test the full spectrum of possible behavior.
		 */
		assertTrue(! mo.isTestSubject());

		assertTrue(mo_testSubject.isTestSubject());
	}

	/**/

	String value_actual;
	long value_actual_supply_count;

	String value_expected;
	long value_expected_supply_count;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	Optional<String> property_cached;
	long property_cached_supply_count;
	long property_cached_update_count;

	/**/

	String value_actual_supplier() {
		value_actual_supply_count ++;
		return value_actual;
	}

	String value_expected_supplier() {
		value_expected_supply_count ++;
		return value_expected;
	}

	Optional<String> property_cached_supplier() {
		property_cached_supply_count ++;
		return property_cached;
	}

	void property_cached_updater(Optional<String> value) {
		property_cached_update_count ++;
		property_cached = value;
	}

	/**/

	void setUp_subcase() {

		value_actual = "22";
		value_actual_supply_count = 0;

		value_expected = "4444";
		value_expected_supply_count = 0;

		property_cached = Optional.of("88888888");
		property_cached_supply_count = 0;
		property_cached_update_count = 0;
	}

	void configure_subcase(
			boolean value_actual_equals_expected,
			boolean value_actual_equals_null,
			boolean value_expected_equals_null,
			boolean property_cached_before_get_equals_null,
			boolean property_cached_value_equals_expected
	)
	{
		assertTrue( ! value_actual_equals_expected ||
				(value_actual_equals_null == value_expected_equals_null));

		if (value_actual_equals_null) {
			this.value_actual = null;
		}

		if (value_expected_equals_null) {
			this.value_expected = null;
		}

		if (value_actual_equals_expected) {
			this.value_actual = this.value_expected;
		}

		if (property_cached_before_get_equals_null) {
			//noinspection OptionalAssignedToNull
			this.property_cached = null;
		}
		else if (property_cached_value_equals_expected) {

			this.property_cached = Optional.ofNullable(this.value_expected);
		}

		String value_actual = this.value_actual;
		String value_expected = this.value_expected;

		assertTrue(! value_actual_equals_null || (value_actual == null));
		assertTrue(value_actual_equals_null || ! (value_actual == null));

		assertTrue(! value_expected_equals_null || (value_expected == null));
		assertTrue(value_expected_equals_null || ! (value_expected == null));

		assertTrue(! value_actual_equals_expected || Objects.equals(value_actual, value_expected));

		assertTrue(! property_cached_before_get_equals_null || (property_cached == null));
		assertTrue(property_cached_before_get_equals_null || ! (property_cached == null));
	}

	void tearDown_subcase() {

	}

	@Test
	public void test_initial_value_of_cached_property()
			throws Exception
	{
		for (String value_expected : new String[] {null, "foo"}) {

			Optional<String> property_cached =
					mo.initial_value_of_cached_property(value_expected);

			assertTrue( ! (value_expected == null) || (property_cached == null));
			assertTrue((value_expected == null) || ! (property_cached == null));
		}
	}

	/**/

	@Test
	public void test_check_cached_property()
			throws Exception
	{
		for (boolean optimize_correctness_over_rpc_count : new boolean[] {false, true}) {

			for (boolean value_actual_is_optional_or_has_no_rpc : new boolean[] {false, true}) {

				for (boolean value_actual_equals_expected : new boolean[] {false, true}) {

					for (boolean value_actual_equals_null : new boolean[] {false, true}) {

						for (boolean value_expected_equals_null : new boolean[] {false, true}) {

							if (value_actual_equals_expected) {

								if (value_actual_equals_null != value_expected_equals_null) {
									continue; // nonsense case
								}
							}

							setUp_subcase();

							test_check_cached_property_subcase(
									optimize_correctness_over_rpc_count,
									value_actual_is_optional_or_has_no_rpc,
									value_actual_equals_expected,
									value_actual_equals_null,
									value_expected_equals_null
							);

							tearDown_subcase();
						}
					}
				}
			}
		}
	}

	/**/

	protected void test_check_cached_property_subcase(
			boolean optimize_correctness_over_rpc_count,
			boolean value_actual_is_optional_or_has_no_rpc,
			boolean value_actual_equals_expected,
			boolean value_actual_equals_null,
			boolean value_expected_equals_null
	)
	{
		configure_subcase(
				value_actual_equals_expected,
				value_actual_equals_null,
				value_expected_equals_null,
				false,
				false
		);

		Exception caught_exception = null;
		try {
			mo.check_cached_property(
					optimize_correctness_over_rpc_count, "property",
					value_actual_is_optional_or_has_no_rpc, this::value_actual_supplier, this::value_expected_supplier
			);
		}
		catch (Exception xx) {

			caught_exception = xx;
		}

		if (optimize_correctness_over_rpc_count && value_expected != null) {

			if (value_actual == null && ! value_actual_is_optional_or_has_no_rpc) {

				assertNotNull(caught_exception);
				assertTrue(caught_exception instanceof NullPointerException);
			}
			else if (value_actual != null && ! Objects.equals(value_expected, value_actual)) {

				assertNotNull(caught_exception);
				assertTrue(caught_exception instanceof IllegalStateException);
			}
			else {
				assertTrue(caught_exception == null);
			}
		}
		else {

			assertNull(caught_exception);
		}
	}

	/**/

	@Test
	public void test_get_cached_property()
			throws Exception
	{
		for (boolean optimize_correctness_over_rpc_count : new boolean[] {false, true}) {

			for (boolean value_actual_is_optional_or_has_no_rpc : new boolean[] {false, true}) {

				for (boolean value_actual_equals_null : new boolean[] {false, true}) {

					for (boolean property_cached_before_get_equals_null : new boolean[] {false, true}) {

						for (boolean property_cached_value_equals_expected: new boolean[] {false, true}) {

							if (property_cached_before_get_equals_null) {

								if (property_cached_value_equals_expected) {
									continue; // nonsense case
								}
							}

							setUp_subcase();

							test_get_cached_property_subcase(
									optimize_correctness_over_rpc_count,
									value_actual_is_optional_or_has_no_rpc,
									value_actual_equals_null,
									property_cached_before_get_equals_null,
									property_cached_value_equals_expected
							);

							tearDown_subcase();
						}
					}
				}
			}
		}
	}

	protected void test_get_cached_property_subcase(
			boolean optimize_correctness_over_rpc_count,
			boolean value_actual_is_optional_or_has_no_rpc,
			boolean value_actual_equals_null,
			boolean property_cached_before_get_equals_null,
			boolean property_cached_value_equals_expected

	)
	{
		configure_subcase(
				false,
				value_actual_equals_null,
				false,
				property_cached_before_get_equals_null,
				property_cached_value_equals_expected
		);

		String value = null;
		Exception caught_exception = null;
		Optional<String> property_cached_before_get = this.property_cached;
		try {
			value = mo.get_cached_property(
					optimize_correctness_over_rpc_count, "property",
					value_actual_is_optional_or_has_no_rpc, this::value_actual_supplier,
					this::property_cached_supplier,
					this::property_cached_updater
			);
		}
		catch (Exception xx) {

			caught_exception = xx;
		}

		if (property_cached_before_get != null) {

			assertNull(caught_exception);

			assertEquals(0, property_cached_update_count);

			assertSame(property_cached_before_get, property_cached);

			assertNotEquals(value_actual, property_cached.get());
		}
		else if (value_actual == null && ! value_actual_is_optional_or_has_no_rpc) {

			assertNotNull(caught_exception);
			assertTrue(caught_exception instanceof NullPointerException);

			assertEquals(0, property_cached_update_count);

			assertNull(property_cached);
		}
		else {
			assertNull(caught_exception);

			assertEquals(1, property_cached_update_count);

			assertNotNull(property_cached);

			assertEquals(value_actual, property_cached.orElse(null));
		}
	}

	/**/

	@Test
	public void test_set_cached_property()
			throws Exception
	{
		for (boolean optimize_correctness_over_rpc_count : new boolean[] {false, true}) {

			for (boolean value_actual_is_optional_or_has_no_rpc : new boolean[] {false, true}) {

				for (boolean value_actual_equals_expected : new boolean[] {false, true}) {

					for (boolean value_actual_equals_null : new boolean[] {false, true}) {

						for (boolean value_expected_equals_null : new boolean[] {false, true}) {

							for (boolean property_cached_before_get_equals_null : new boolean[] {false, true}) {

								for (boolean property_cached_value_equals_expected : new boolean[] {false, true}) {

									if (value_actual_equals_expected) {

										if (value_actual_equals_null != value_expected_equals_null) {
											continue; // nonsense case
										}
									}

									if (property_cached_before_get_equals_null) {

										if (property_cached_value_equals_expected) {
											continue; // nonsense case
										}
									}

									setUp_subcase();

									test_set_cached_property_subcase(
											optimize_correctness_over_rpc_count,
											value_actual_is_optional_or_has_no_rpc,
											value_actual_equals_expected,
											value_actual_equals_null,
											value_expected_equals_null,
											property_cached_before_get_equals_null,
											property_cached_value_equals_expected
									);

									tearDown_subcase();
								}
							}
						}
					}
				}
			}
		}
	}

	protected void test_set_cached_property_subcase(
			boolean optimize_correctness_over_rpc_count,
			boolean value_actual_is_optional_or_has_no_rpc,
			boolean value_actual_equals_expected,
			boolean value_actual_equals_null,
			boolean value_expected_equals_null,
			boolean property_cached_before_get_equals_null,
			boolean property_cached_value_equals_expected
	)
	{
		configure_subcase(
				value_actual_equals_expected,
				value_actual_equals_null,
				value_expected_equals_null,
				property_cached_before_get_equals_null,
				property_cached_value_equals_expected
		);

		String value = null;
		Exception caught_exception = null;
		Optional<String> property_cached_before_get = this.property_cached;
		try {
			mo.set_cached_property(
					optimize_correctness_over_rpc_count, "property",
					value_actual_is_optional_or_has_no_rpc, this::value_actual_supplier,
					this::property_cached_supplier,
					this::property_cached_updater,
					this::value_expected_supplier
			);
		}
		catch (Exception xx) {

			caught_exception = xx;
		}

		if (value_expected == null && ! value_actual_is_optional_or_has_no_rpc) {

			assertNotNull(caught_exception);
			assertTrue(caught_exception instanceof NullPointerException);

			assertEquals(1, this.value_expected_supply_count);
			assertEquals(0, this.value_actual_supply_count);
			assertEquals(0, this.property_cached_update_count);
			assertEquals(0, this.property_cached_supply_count);
		}
		else
		if (property_cached_before_get != null && ! property_cached_value_equals_expected) {

			assertNotNull(caught_exception);
			assertTrue(caught_exception instanceof IllegalStateException);

			assertEquals(1, this.value_expected_supply_count);
			assertEquals(0, this.value_actual_supply_count);
			assertEquals(0, this.property_cached_update_count);
			assertEquals(1, this.property_cached_supply_count);
		}
		else
		if (optimize_correctness_over_rpc_count && value_expected != null) {

			if (value_actual == null && ! value_actual_is_optional_or_has_no_rpc) {

				assertNotNull(caught_exception);
				assertTrue(caught_exception instanceof NullPointerException);

				assertEquals(1, this.value_expected_supply_count);
				assertEquals(1, this.value_actual_supply_count);
				assertEquals(0, this.property_cached_update_count);
				assertEquals(1, this.property_cached_supply_count);
			}
			else if (value_actual != null && ! Objects.equals(value_expected, value_actual)) {

				assertNotNull(caught_exception);
				assertTrue(caught_exception instanceof IllegalStateException);

				assertEquals(1, this.value_expected_supply_count);
				assertEquals(1, this.value_actual_supply_count);
				assertEquals(0, this.property_cached_update_count);
				assertEquals(1, this.property_cached_supply_count);
			}
			else {

				assertNull(caught_exception);

				assertEquals(1, this.value_expected_supply_count);
				assertEquals(1, this.value_actual_supply_count);
				assertEquals(1, this.property_cached_update_count);
				assertEquals(1, this.property_cached_supply_count);
			}
		}
		else {

			assertNull(caught_exception);

			assertEquals(1, this.value_expected_supply_count);
			assertEquals(0, this.value_actual_supply_count);
			assertEquals(1, this.property_cached_update_count);
			assertEquals(1, this.property_cached_supply_count);
		}

	}

	/**/

}
