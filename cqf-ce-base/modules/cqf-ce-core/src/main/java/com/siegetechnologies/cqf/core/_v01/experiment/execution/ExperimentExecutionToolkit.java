package com.siegetechnologies.cqf.core._v01.experiment.execution;

/*-
 * #%L
 * astam-cqf-ce-core
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

import com.siegetechnologies.cqf.core._v01.experiment.execution.impl.e2e.ExperimentExecutionToolkitForE2ETesting;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPlatform;

/**
 * @author srogers
 */
public interface ExperimentExecutionToolkit
{
	public static ExperimentExecutionToolkit getDefault() {

		return new ExperimentExecutionToolkitForE2ETesting();
	}

	public static ExperimentExecutionToolkit get(ExecutionPlatform executionPlatform) {

		switch (executionPlatform) {
		case Default:
			return getDefault();
		case Test:
			return new ExperimentExecutionToolkitForE2ETesting();
		default:
		case Nil:
			throw new UnsupportedOperationException("no toolkit for execution platform: " + executionPlatform);
		}
	}

	/**/

}

//^-- FIXME: DESIGN: REVIEW: srogers: finish implementing ExperimentExecutionToolkit as service provider framework
//^-- FIXME: srogers: add support in get() for vSphere once service provider framework is in place

