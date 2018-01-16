package com.siegetechnologies.cqf.core.experiment.design.catalog.providers;

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

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import org.junit.Test;

public class CoreBuiltinExperimentDesignElementListProviderTest
{
	
	@Test
	public void testCoreItemsProvider() {
		CoreBuiltinExperimentDesignElementListProvider cip = new CoreBuiltinExperimentDesignElementListProvider();
		List<? extends ExperimentDesignElementImpl> items = cip.getDesignElements(null);
		assertEquals(1, items.size());
		assertEquals("Workspace", items.get(0).getName());
		assertEquals("Util", items.get(0).getCategory());
	}
}
