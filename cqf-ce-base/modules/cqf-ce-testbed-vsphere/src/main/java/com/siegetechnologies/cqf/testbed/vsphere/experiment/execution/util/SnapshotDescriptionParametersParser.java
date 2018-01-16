package com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util;

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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * Utility to extract login credentials from a string.
 * 
 * @author taylorj
 */
public class SnapshotDescriptionParametersParser {

	/**
	 * Beginning marker for CQF parameters string.
	 */
	private static final String BEGIN_DESCRIPTION = "CQF[";

	/**
	 * Ending marker for CQF parameters string.
	 */
	private static final String END_DESCRIPTION = "]CQF";

	/**
	 * Simple representation of a variant specification.
	 * 
	 * @author taylorj
	 */
	public interface Variant {
		/**
		 * Returns the name of the variant.
		 * 
		 * @return the name
		 */
		String getName();

		/**
		 * Returns the default values for the variant parameters.
		 * 
		 * @return the default values
		 */
		Map<String, String> getParameters();

		/**
		 * Returns a new instance with provided name and parameters.
		 * 
		 * @param name the name
		 * @param parameters the parameters
		 * @return the instance
		 */
		static Variant of(String name, Map<String, String> parameters) {
			return new Variant() {
				@Override
				public Map<String, String> getParameters() {
					return parameters;
				}

				@Override
				public String getName() {
					return name;
				}

				@Override
				public String toString() {
					return String.format("Variant.of(name=%s, parameters=%s)", getName(), getParameters());
				}
			};
		}

		/**
		 * Parses a variant from an input string. The input string should be
		 * key/value pairs separated by commas. Exactly one key value pair
		 * should have "name" as the key, indicating the name of the variant.
		 * The remaining pairs should have distinct names, and each pair value
		 * is the default value of the parameter for the pair's name.
		 * 
		 * @param input the input
		 * @return the variant
		 */
		static Optional<Variant> parse(String input) {
			String[] kvs = input.split(",");
			Map<Boolean, List<KeyValue>> isName = Stream.of(kvs)
					.map(KeyValue::parse)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(partitioningBy(kv -> "name".equals(kv.getKey())));
			List<KeyValue> names = isName.get(true);
			if (names.size() != 1) {
				return Optional.empty();
			}

			final String name = names.get(0)
					.getValue();

			final Map<String, String> parameters = isName.get(false)
					.stream()
					.collect(toMap(KeyValue::getKey, KeyValue::getValue));

			return Optional.of(Variant.of(name, parameters));
		}
	}

	/**
	 * A description from a snapshot.
	 * 
	 * @author taylorj
	 */
	public interface Description {
		/**
		 * Returns a map from parameter names to default parameter values.
		 * 
		 * @return the map of default values
		 */
		Map<String, String> getParameters();

		/**
		 * Returns a list of variants.
		 * 
		 * @return the list of variants
		 */
		List<Variant> getVariants();

		/**
		 * Returns a map constructed from key-value pairs in an input string.
		 * Within the input string, key-value pairs are separated by semicolons,
		 * and each key-value pair separates the key and value with an equal
		 * sign. Whitespace is trimmed from the key and value in each pair.
		 * 
		 * @param input the input string
		 * @return
		 */
		public static Description parse(String input) {
			Map<Boolean, List<KeyValue>> kvs = Stream.of(input.split(";"))
					.map(KeyValue::parse)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(partitioningBy(kv -> "variant".equals(kv.getKey())));

			Map<String, String> parameters = kvs.get(false)
					.stream()
					.collect(toMap(KeyValue::getKey, KeyValue::getValue));

			List<Variant> variants = kvs.get(true)
					.stream()
					.map(KeyValue::getValue)
					.map(Variant::parse)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(toList());

			return Description.of(parameters, variants);
		}

		/**
		 * Returns a new description with provided default parameters and
		 * variants.
		 * 
		 * @param parameters the default values
		 * @param variants the variants
		 * @return the description
		 */
		static Description of(Map<String, String> parameters, List<Variant> variants) {
			return new Description() {
				@Override
				public String toString() {
					return String.format("Description.of(parameters=%s, variants=%s)", getParameters(), getVariants());
				}

				@Override
				public List<Variant> getVariants() {
					return variants;
				}

				@Override
				public Map<String, String> getParameters() {
					return parameters;
				}
			};
		}
	}

	/**
	 * Simple key-value representation with support for parsing.
	 * 
	 * @author taylorj
	 */
	private interface KeyValue {
		/**
		 * Returns the key.
		 * 
		 * @return the key
		 */
		String getKey();

		/**
		 * Returns the value.
		 * 
		 * @return the value
		 */
		String getValue();

		static KeyValue of(String key, String value) {
			return new KeyValue() {
				@Override
				public String getKey() {
					return key;
				}

				@Override
				public String getValue() {
					return value;
				}
			};
		}

		/**
		 * Attempts to parse a key value pair from an input string. The input
		 * string be of the form {@code key = value}. The whitespace trimmed
		 * text before the first {@code =} is the key, and the whitespace
		 * trimmed text after it is the value.
		 * 
		 * @param input the input string
		 * @return the parsed key value pair
		 */
		static Optional<KeyValue> parse(String input) {
			final int split = input.indexOf('=');
			if (split == -1) {
				return Optional.empty();
			}
			else {
				final String key = input.substring(0, split)
						.trim();
				final String value = input.substring(split + 1)
						.trim();
				return Optional.of(KeyValue.of(key, value));
			}
		}
	}

	/**
	 * Returns a map of the parameters parsed from an input string. The result
	 * map is an empty optional if the string does not contain begin and end
	 * markers.
	 * 
	 * @param string the input string
	 * @return the parameters
	 */
	public Optional<Description> parseDescription(String string) {
		return getParametersSubstring(string).map(Description::parse);
	}

	/**
	 * Returns the substring of an input string bounded by the begin and end
	 * markers, if there is one. For instance, applied to
	 * {@code "hello CQF[a=b; c=d]CQF goodbye"}, the result is
	 * {@code "a=b; c=d"}.
	 * 
	 * @param input the input string
	 * @return the substring
	 */
	private Optional<String> getParametersSubstring(String input) {
		final int rawBegin = input.indexOf(BEGIN_DESCRIPTION);
		final int end = input.indexOf(END_DESCRIPTION);

		if (rawBegin == -1 || end == -1) {
			return Optional.empty();
		}

		final int begin = rawBegin + BEGIN_DESCRIPTION.length();

		if (end <= begin) {
			return Optional.empty();
		}

		return Optional.of(input.substring(begin, end));
	}
}
