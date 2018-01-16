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
import java.util.List;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExceptionsTest {

	private Throwable a, b, c;
	private List<Throwable> abc;

	@Before
	public void init() {
		a = new IllegalArgumentException("a");
		b = new UnsupportedOperationException("b", a);
		c = new Throwable("c", b);
		abc = Arrays.asList(a,b,c);
	}

	@Test
	public void testGetRootCause() {
		for (Throwable x : abc) {
			Assert.assertSame(a, Exceptions.getRootCause(x));
		}
	}

	@Test
	public void testGetCauses() {
		Assert.assertEquals(Arrays.asList(a), Exceptions.getCauses(a));
		Assert.assertEquals(Arrays.asList(b,a), Exceptions.getCauses(b));
		Assert.assertEquals(Arrays.asList(c,b,a), Exceptions.getCauses(c));
	}

	@Test
	public void testHasCause_Predicate() {
		Assert.assertTrue(Exceptions.hasCause(b, Predicate.isEqual(a)));
		Assert.assertTrue(Exceptions.hasCause(b, Predicate.isEqual(b)));
		Assert.assertFalse(Exceptions.hasCause(b, Predicate.isEqual(c)));
	}

	@Test
	public void testHasCause_Class() {
		Assert.assertTrue(Exceptions.hasCause(c, IllegalArgumentException.class));
		Assert.assertTrue(Exceptions.hasCause(c, IndexOutOfBoundsException.class, IllegalArgumentException.class));
		Assert.assertTrue(Exceptions.hasCause(c, RuntimeException.class));
		Assert.assertFalse(Exceptions.hasCause(c));
		Assert.assertFalse(Exceptions.hasCause(c, Error.class));
	}

	@Test
	public void testGetSummary_Simple() {
		String actual = Exceptions.getSummary(c);
		String expected = "a [Throwable, UnsupportedOperationException, IllegalArgumentException]";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetSummary_WithIgnoredClasses() {
		String actual = Exceptions.getSummary(c, Arrays.asList(Throwable.class, UnsupportedOperationException.class));
		String expected = "a [IllegalArgumentException]";
		Assert.assertEquals(expected, actual);
	}
}
