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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import java.util.Arrays;

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignElementListProvider;
import org.junit.Test;

public class ProviderBackedExperimentDesignElementListProviderTest
{
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGet() {
		ExperimentDesignElementListProvider<ExperimentDesignElementImpl> p1 = mock(ExperimentDesignElementListProvider.class);
		ExperimentDesignElementListProvider<ExperimentDesignElementImpl> p2 = mock(ExperimentDesignElementListProvider.class);
		ProviderBackedExperimentDesignElementListProvider<ExperimentDesignElementImpl> uip = new ProviderBackedExperimentDesignElementListProvider<>(Arrays.asList(p1, p2));
		
		ExperimentDesignElementIdResolver<ExperimentDesignElementImpl> repo = null;
		
		// Getting the items from the union provider
		// should trigger getting the items from each 
		// of the sub-providers.
		uip.getDesignElements(repo);
		verify(p1).getDesignElements(repo);
		verify(p2).getDesignElements(repo);
	}

}
