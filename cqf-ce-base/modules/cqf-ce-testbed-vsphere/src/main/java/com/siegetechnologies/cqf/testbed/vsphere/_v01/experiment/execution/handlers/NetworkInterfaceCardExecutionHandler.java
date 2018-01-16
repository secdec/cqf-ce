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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for creating network interface cards on a virtual machine.
 *
 * @param <R_before>
 * 		the before-hook result
 * @param <R_main>
 * 		the main result type
 *
 * @author taylorj
 */
public class NetworkInterfaceCardExecutionHandler<R_before, R_main>
		extends BasicExecutionHandlerForTestbedVSphere<R_before, R_main>
{
	@SuppressWarnings("unused")
	public final Logger logger = LoggerFactory.getLogger(NetworkInterfaceCardExecutionHandler.class);

	/**/

	@Override
	public String getDesignName() {
		return "Network Interface Card"; // FIXME: STRING: srogers
	}

	@Override
	public String getDesignCategory() {
		return "Hardware"; // FIXME: STRING: srogers
	}

	/**/

	@Override
	public R_before initialize_beforeHook(ExperimentElementExecutionContext context) {
		try {
			vSphere_toolkit_of(context).configureNetworkAdapterForVirtualMachineResultInExecutionContext(context);
		}
		finally {
			return super.initialize_beforeHook(context);
		}
	}

	/**/

}
