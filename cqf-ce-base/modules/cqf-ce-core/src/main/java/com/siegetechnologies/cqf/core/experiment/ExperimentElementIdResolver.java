package com.siegetechnologies.cqf.core.experiment.design;

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

import com.siegetechnologies.cqf.core.experiment.ExperimentElementId;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import java.util.Optional;

/**
 * Resolves elements by ID.
 */
@FunctionalInterface
public interface ExperimentElementIdResolver<T extends ExperimentElementImpl> {
	
	/**
	 * Returns the experiment element that corresponds to the specified ID (if any).
	 *
	 * @param elementId
	 * @return the corresponding experiment element
	 */
	Optional<T> resolve(ExperimentElementId elementId);
	
}
