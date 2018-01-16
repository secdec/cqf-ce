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

import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.Test;

public class FunctionsTest {
	
	@Test(expected=UncheckedIOException.class)
	public void testUncheckedFunction_throw() {
		Functions.uncheck(x -> { throw new IOException(); }).apply(null);
	}

	@Test
	public void testUncheckedFunction_noThrow() {
		assertNull(Functions.uncheck(x -> null).apply(null));
	}

	@Test(expected=UncheckedIOException.class)
	public void testUncheckedSupplier_throw() {
		Functions.uncheck(() -> { throw new IOException(); }).get();
	}

	@Test
	public void testUncheckedSupplier_noThrow() {
		assertNull(Functions.uncheck(() -> null).get());
	}




}
