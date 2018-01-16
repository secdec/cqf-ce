package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers;

/*-
 * #%L
 * cqf-ce-testbed-vsphere
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
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;

/**
 * Handler for creating a resource pool.
 *
 * @param <R_main>
 * 		the main result type
 *
 * @author taylorj
 */
public class InterimResourcePoolExecutionHandler<R_main>
		extends BasicExecutionHandlerForTestbedVSphere<AUResourcePool, R_main>
{
	@Override
	public String getDesignName() {
		return "Resource Pool"; // FIXME: STRING: srogers
	}

	@Override
	public String getDesignCategory() {
		return "Hardware"; // FIXME: STRING: srogers
	}

	/**/

	@Override
	public AUResourcePool initialize_beforeHook(ExperimentElementExecutionContext context) {
		try {
			return vSphere_toolkit_of(context).createInterimResourcePoolInExecutionContext(context);
		}
		finally {
			super.initialize_beforeHook(context);
		}
	}

	/**/

	@Override
	public Object cleanup_afterHook(ExperimentElementExecutionContext context, R_main result_main) {
		try {
			return super.cleanup_afterHook(context, result_main);
		}
		finally {
			vSphere_toolkit_of(context).deleteInterimResourcePoolInExecutionContext(context);
		}
	}

	/**/

}
