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

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ParameterImplTest {
	
	@Test
	public void testConstructor() {
		ParameterImpl temporary = new ParameterImpl();
		assertFalse(temporary.isRequired());
		temporary.setRequired(true);
		assertTrue(temporary.isRequired());
	}
	
	@Test
	public void testUpdate() {
		ParameterImpl empty_param = new ParameterImpl();
		ParameterImpl filled_param = new ParameterImpl();
		filled_param.setDefaultValue("test");
		
		assertTrue(filled_param.hasDefaultValue());
		
		ParameterImpl test_param = empty_param.copyAndUpdateWithDefaultsFrom(filled_param);
		
		assertTrue(test_param.equals(filled_param));
	}
}
