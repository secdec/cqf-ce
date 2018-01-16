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

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

public class MapsTest {
	
	@Test
	public void testToUniqueMap() {
		Map<Integer,String> treeMap = new TreeMap<>();
		Map<Integer,String> map = Arrays.asList(1, 2, 3)
				.stream()
				.collect(Maps.toUniqueMap(Function.identity(), Objects::toString, () -> treeMap));
		assertSame(treeMap, map);
		Map<Integer, String> expected = Arrays.asList(1, 2, 3)
				.stream()
				.collect(toMap(Function.identity(), Objects::toString));
		assertEquals(expected, map);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testToUniqueMap_throws() {
		Arrays.asList(1, 2, 1)
				.stream()
				.collect(Maps.toUniqueMap(x -> x, Objects::toString, TreeMap::new));
		
	}

	@Test
	public void testGet() {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(0, 1);
		Assert.assertTrue(Maps.get(map, 0).isPresent());
		Assert.assertEquals(1, (int)Maps.get(map, 0).get());
		
		Assert.assertFalse(Maps.get(map, 2).isPresent());
	}
	
	@Test
	public void testInvert() {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(0,1);
		map.put(2,3);
		map.put(4,5);
		Map<Integer,Integer> imap = Maps.invert(map);
		
		Assert.assertEquals(new HashSet<>(map.values()), imap.keySet());
		Assert.assertEquals(map.keySet(), new HashSet<>(imap.values()));
		Assert.assertEquals(2, (int)imap.get(3));
	}
	
	@Test(expected=IllegalStateException.class)
	public void testInvertNonInvertible() {
		Map<Integer,Integer> map = new HashMap<Integer, Integer>();
		map.put(0, 1);
		map.put(1, 1);
		Maps.invert(map);
	}
}
