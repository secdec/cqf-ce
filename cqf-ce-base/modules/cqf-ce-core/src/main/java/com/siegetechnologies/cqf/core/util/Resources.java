package com.siegetechnologies.cqf.core.util;

/*-
 * #%L
 * astam-cqf-ce-core
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

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author srogers
 */
public abstract class Resources
{
	public static String[] selectors_default = {null, "default", "sample"};

	/**/

	public static String nameFrom(
			String stem, String suffix
	)
	{
		return nameFrom(stem, suffix, null);
	}

	public static String nameFrom(
			String stem, String suffix, String selector
	)
	{
		StringBuilder result = new StringBuilder();

		result.append(stem);

		selector = (selector != null) ? selector.trim() : "";
		if (selector.length() > 0) {

			result.append("_");
			result.append(selector);
		}

		result.append(suffix);

		return result.toString();
	}

	public static Stream<String> nameStreamFrom(
			String stem, String suffix, String... selectors
	)
	{
		if (selectors.length == 0) {
			selectors = selectors_default;
		}

		return (Arrays.stream(selectors)

				.map(s -> nameFrom(stem, suffix, s))
		);
	}

	/**/

	public static Pair<String,URL> firstMatching(
			ClassLoader loader, Supplier<Stream<String>> nameCandidateStreamSupplier
	)
	{
		Stream<String> nameCandidates = nameCandidateStreamSupplier.get();

		Optional<Pair<String, URL>> match_maybe = (nameCandidates

				.map(n -> Pair.of(n, loader.getResource(n)))

				.filter(nx -> nx.getValue() != null)

				.findFirst()
		);

		return match_maybe.orElse(null);
	}

	public static Pair<String,InputStream> firstMatchingAsInputStream(
			ClassLoader loader, Supplier<Stream<String>> nameCandidateStreamSupplier
	)
	{
		Stream<String> nameCandidates = nameCandidateStreamSupplier.get();

		Optional<Pair<String, InputStream>> match_maybe = (nameCandidates

				.map(n -> Pair.of(n, loader.getResourceAsStream(n)))

				.filter(nx -> nx.getValue() != null)

				.findFirst()
		);

		return match_maybe.orElse(null);
	}

	/**/

}
