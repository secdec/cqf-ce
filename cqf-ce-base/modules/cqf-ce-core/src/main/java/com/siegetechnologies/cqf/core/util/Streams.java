package com.siegetechnologies.cqf.core.util;

/*-
 * #%L
 * cqf-ce-core
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

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {
	
	/**
	 * Don't instantiate.
	 */
	private Streams() {}
	
	/**
	 * Returns a stream over the values returned by an iterator.
	 * 
	 * @param iterator the iterator
	 * @return the stream
	 */
	public static <T> Stream<T> from(Iterator<T> iterator) {
		return from(() -> iterator);
	}

	/**
	 * Returns a stream over the elements of an iterable.
	 * 
	 * @param iterable the iterable
	 * @return the stream
	 */
	public static <T> Stream<T> from(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

}
