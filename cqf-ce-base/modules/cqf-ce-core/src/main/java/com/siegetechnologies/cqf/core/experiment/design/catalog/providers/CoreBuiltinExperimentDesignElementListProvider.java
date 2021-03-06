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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.elements.WorkspaceDesignElement;

import java.util.Arrays;
import java.util.List;

/**
 * Provider of core items, such as the workspace item.
 *
 * @author taylorj
 */
public class CoreBuiltinExperimentDesignElementListProvider implements ExperimentDesignElementListProvider<ExperimentDesignElementImpl>
{
	/**
	 * Returns a list of the workspace item.
	 */
	@Override
	public List<ExperimentDesignElementImpl> getDesignElements(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> repo) {
		return Arrays.asList(new WorkspaceDesignElement(repo));
	}
}
