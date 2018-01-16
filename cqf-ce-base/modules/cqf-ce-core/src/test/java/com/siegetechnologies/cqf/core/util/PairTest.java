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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

public class PairTest {
	
	@Test
	public void testOf() {
		Pair<Integer,String> p = Pair.of(42, "the answer");
		Assert.assertEquals(42, (int) p.getLeft());
		Assert.assertEquals("the answer", p.getRight());
		Assert.assertEquals("Pair.of(42,the answer)", p.toString());
	}
	
	@Test
	public void testSwap() {
		Assert.assertEquals(Pair.of("answer", 42), Pair.of(42, "answer").swap());
	}
	
	@Test
	public void testSupplierOf() {
		Assert.assertEquals(Pair.of(42, "x"), Pair.supplierOf(42, "x").get());
	}
	
	@Test
	public void testMapLeft() {
		Assert.assertEquals(Pair.of(43,"x"), Pair.of(42,"x").mapLeft(n -> n+1));
	}

	@Test
	public void testMapRight() {
		Assert.assertEquals(Pair.of(42,"xy"), Pair.of(42,"x").mapRight(x -> x+"y"));
	}
	
	@Test
	public void testMap() {
		Assert.assertEquals(Pair.of(43, "xy"), Pair.of(42,"x").map(n -> n+1, x -> x+"y"));
	}
	
	@Test
	public void testToMap() {
		Map<Integer,String> actual = Arrays.asList(Pair.of(2,"two"), Pair.of(1,"one")).stream().collect(Pair.toMap());
		Map<Integer,String> expected = new HashMap<>();
		expected.put(2, "two");
		expected.put(1, "one");
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testCall() {
		Assert.assertEquals("42x42", Pair.of(42, "x").call((x,y) -> x+y+x));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(Objects.hash(42, "x"), Pair.of(42,"x").hashCode());
	}
	
	@Test
	public void testEquals() {
		Assert.assertTrue(Pair.of(42,"x").equals(Pair.of(42,"x")));
		Assert.assertFalse(Pair.of(42,"x").equals(null));
		Assert.assertFalse(Pair.of(42,"x").equals(Pair.of(42,"y")));
		Assert.assertFalse(Pair.of(42,"x").equals(Pair.of(43,"x")));
	}

}
