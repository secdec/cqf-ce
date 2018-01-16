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
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.VSphereTestbedMachine;

/**
 * Handler for referencing a pre-existing virtual machine.
 *
 * @author taylorj
 *
 * @param <R_main> the main result type
 */
public class PermanentVirtualMachineExecutionHandler<R_main>
		extends BasicExecutionHandlerForTestbedVSphere<VSphereTestbedMachine, R_main>
{

	@Override
	public String getDesignName() {
		return "Existing VM"; // FIXME: STRING: srogers
	}

	@Override
	public String getDesignCategory() {
		return "Node"; // FIXME: STRING: srogers
	}

	/**/

	@Override
	public VSphereTestbedMachine initialize_beforeHook(ExperimentElementExecutionContext context) {
		try {
			super.initialize_beforeHook(context);
		}
		finally {
			return vSphere_toolkit_of(context).getPermanentVirtualMachineInExecutionContext(context);
		}
	}

	/**/

	@Override
	public VSphereTestbedMachine run_beforeHook(ExperimentElementExecutionContext context) {
		try {
			super.run_beforeHook(context);
		}
		finally {
			return vSphere_toolkit_of(context).getPermanentVirtualMachineInExecutionContext(context);
		}
	}

	/**/

	@Override
	public VSphereTestbedMachine retrieveData_beforeHook(ExperimentElementExecutionContext context) {
		try {
			super.retrieveData_beforeHook(context);
		}
		finally {
			return vSphere_toolkit_of(context).getPermanentVirtualMachineInExecutionContext(context);
		}
	}

	/**/

}
