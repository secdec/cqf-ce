package com.siegetechnologies.cqf.core.experiment.design.util;

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

import java.util.HashMap;
import java.util.Map;

public class DocumentationImplTest {
	
	@Test
	public void testOf() {
		String info = "info";
		Map<String,String> params = new HashMap<>();
		Map<String,String> unknowns = new HashMap<>();
		DocumentationImpl id = new DocumentationImpl(info, params, unknowns);
		Assert.assertSame(info, id.getInfo());
		Assert.assertSame(params, id.getParams());
		Assert.assertSame(unknowns, id.getUnknowns());
	}

}
