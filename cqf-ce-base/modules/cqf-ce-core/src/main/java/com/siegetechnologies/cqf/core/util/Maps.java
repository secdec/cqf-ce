/**
 *  Copyright (c) 2016 Siege Technologies.
 */
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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Utility methods for working with {@link Map}s.
 * 
 * @author taylorj
 */
public class Maps {

    private Maps() {}
    
    /**
	 * Throws an exception with a message that the arguments should not be
	 * merged.
	 * 
	 * @param a the first value
	 * @param b the second value
	 * @return none
	 * 
	 * @throws UnsupportedOperationException always
	 */
    static <U> U failToMerge(U a, U b) {
    	throw new UnsupportedOperationException("Should not attempt to merge "+a+" and "+b+".");
    }
    
    /**
	 * Returns a collector like the one returned by
	 * {@link Collectors#toMap(Function, Function, java.util.function.BinaryOperator, Supplier)},
	 * but with a binary operator that throws an unsupported operation exception
	 * if an attempt is made to merge any values.
	 * 
	 * @param keyMapper the key mapping function
	 * @param valueMapper the value mapping function
	 * @param mapSupplier the map supplier
	 * @return the collector
	 */
	public static <T, K, U, M extends Map<K, U>> Collector<T,?,M> toUniqueMap(
			Function<? super T, ? extends K> keyMapper,
			Function<? super T, ? extends U> valueMapper,
			Supplier<M> mapSupplier) {
		return Collectors.toMap(keyMapper, valueMapper, Maps::failToMerge, mapSupplier);
	}
	
    /**
     * Returns the value for the key in the map.  If the
     * value is present and non-null, then the result
     * is an optional containing the value.  Otherwise, the
     * result is an empty optional.  The result of this
     * method does not provide a method a way to distinguish
     * between no value and a null value in the map.
     * 
     * @param map the map 
     * @param key the key
     * 
     * @param <K> the key type of the map
     * @param <T> the value type of the map
     *  
     * @return an optional containing an element from the map
     */
    public static final <K,T> Optional<T> get(Map<K,T> map, K key) {
    	return Optional.ofNullable(map.get(key));
    }

    /**
     * Returns a partial inverse of the provided map.  Each value in 
     * the provided map is a key in the inverse map, and is mapped to 
     * the key in the original map that mapped to it.
     * 
     * @param map the original map
     * @return the inverse map
     *
     * @param <K> the key type
     * @param <V> the value type
     * 
     * @throws IllegalStateException if the map is not invertible
     */
    public static <K,V> Map<V,K> invert(Map<K,V> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
}
