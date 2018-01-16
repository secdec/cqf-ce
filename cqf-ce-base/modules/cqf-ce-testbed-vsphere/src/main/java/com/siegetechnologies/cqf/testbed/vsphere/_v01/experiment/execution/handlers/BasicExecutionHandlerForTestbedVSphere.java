package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers;

/*-
 * #%L
 * astam-cqf-ce-testbed-vsphere
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

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.handlers.BasicExecutionHandlerForTestbedBase;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.ExperimentExecutionToolkitForTestbedVSphere;

/**
 * @author srogers
 */
public class BasicExecutionHandlerForTestbedVSphere<R_before, R_main>
		extends BasicExecutionHandlerForTestbedBase<R_before, R_main>
{
	protected static ExperimentExecutionToolkitForTestbedVSphere vSphere_toolkit_of(
			ExperimentElementExecutionContext context
	) {
		return context.getExecutionToolkitAs(ExperimentExecutionToolkitForTestbedVSphere.class);
	}

	/**/

}
