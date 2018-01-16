package com.secdec.astam.cqf.api.rest.app.aor;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-impl
 * %%
 * Copyright (C) 2016 - 2017 Applied Visions, Inc.
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

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionHandlerRegistry;
import com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.handlers.BasicExecutionHandlerForTestbedBase;
import com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.handlers.ProgramExecutionHandler;
import com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.handlers.ScheduledRunStepExecutionHandler;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers.InterimVirtualMachineExecutionHandler;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers.InterimVirtualSwitchExecutionHandler;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers.NetworkInterfaceCardExecutionHandler;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers.PermanentVirtualMachineExecutionHandler;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers.PermanentVirtualSwitchExecutionHandler;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers.InterimPortGroupExecutionHandler;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers.InterimResourcePoolExecutionHandler;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
import org.apache.commons.configuration2.ImmutableConfiguration;

/**
 * @author srogers
 */
public class ExecutionHandlerRegistrar
{
	private ImmutableConfiguration configuration;

	/**/

	public ExecutionHandlerRegistrar() {

		/**/
	}

	/**/

	public synchronized void registerExecutionHandlers(@NonNull ImmutableConfiguration configuration) {

		final String context = "astam"; // FIXME: srogers: make this a public constant

		this.configuration = configuration;

		List<Class<? extends BasicExecutionHandlerForTestbedBase>> classes = Arrays.asList(
				BasicExecutionHandlerForTestbedBase.class,

				InterimPortGroupExecutionHandler.class,
				InterimResourcePoolExecutionHandler.class,
				InterimVirtualMachineExecutionHandler.class,
				InterimVirtualSwitchExecutionHandler.class,

				PermanentVirtualMachineExecutionHandler.class,
				PermanentVirtualSwitchExecutionHandler.class,

				NetworkInterfaceCardExecutionHandler.class,

				ProgramExecutionHandler.class,
				ScheduledRunStepExecutionHandler.class
		);
		classes.forEach(c -> ExperimentElementExecutionHandlerRegistry.registerHandler(context, c));

		//^-- FIXME: srogers: avoid loading vSphere-specific execution handlers in favor of generalized archetype
	}

	public synchronized void withdrawExecutionHandlers() {

		this.configuration = null;

		// FIXME: srogers: implement ExperimentElementExecutionHandlerRegistry.withdrawAllHandlers()
	}

}
