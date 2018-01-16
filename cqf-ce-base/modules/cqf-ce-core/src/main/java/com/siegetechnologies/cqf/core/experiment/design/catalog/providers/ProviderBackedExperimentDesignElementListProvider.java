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

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;

/**
 * An items provider that returns all the items provided by a number of
 * sub-providers.
 * 
 * @author taylorj
 */
public class ProviderBackedExperimentDesignElementListProvider<I extends ExperimentDesignElementImpl> implements ExperimentDesignElementListProvider<I>
{

	private final Collection<ExperimentDesignElementListProvider<? extends I>> providers;

	/**
	 * Creates a new instance with provided sub-providers.
	 * 
	 * @param providers the sub-providers
	 */
	public ProviderBackedExperimentDesignElementListProvider(Collection<ExperimentDesignElementListProvider<? extends I>> providers) {
		this.providers = providers;
	}

	/**
	 * Gets items from each provider and returns a list containing all the
	 * items.
	 */
	@Override
	public List<I> getDesignElements(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> repo) {
		return providers.stream()
				.map(p -> p.getDesignElements(repo))
				.flatMap(Collection::stream)
				.collect(toList());
	}
}
