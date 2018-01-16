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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class EnumsTest {
	enum ABC {
		ay, bee, see
	}
	
	@Test
	public void test0() {
		for (String in : Arrays.asList("AY", "ay", "aY", "Ay", "Bee", "BEE", "bee")) {
			assertTrue(Enums.isValueIgnoreCase(ABC.class, in));
		}
		for (String out : Arrays.asList("a", "b", "c", null, "A", "B")) {
			assertFalse(Enums.isValueIgnoreCase(ABC.class, out));
		}
	}
}
