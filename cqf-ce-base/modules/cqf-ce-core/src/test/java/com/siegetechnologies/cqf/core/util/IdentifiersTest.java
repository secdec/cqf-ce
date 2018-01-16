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

import org.junit.Assert;
import org.junit.Test;

public class IdentifiersTest {
	@Test
	public void testIdentifiers() {
		String x = Identifiers.randomIdentifier(10);
		String y = Identifiers.randomIdentifier(10);
		String z = Identifiers.randomIdentifier(8);

		Assert.assertEquals(16, x.length());
		Assert.assertEquals(16, y.length());
		Assert.assertFalse(x.equals(y));
		Assert.assertEquals(12, z.length());
	}
}
