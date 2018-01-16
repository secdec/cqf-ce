package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution;

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
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContextBuilder;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutionToolkit;
import com.siegetechnologies.cqf.core._v01.experiment.execution.impl.DefaultExperimentExecutor;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTaskId;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTimeSlotDurationCalculator;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementId;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.elements.WorkspaceDesignElement;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionMode;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes experiments on VSphere managed virtual machines.
 */
public class VSphereExperimentExecutor extends DefaultExperimentExecutor
{
	private static final Logger logger = LoggerFactory.getLogger(VSphereExperimentExecutor.class);

	private final VSphereAPIUtil/*                                            */ vSphereAPIUtil;
	private final ExperimentElementExecutionContextBuilder.ResourceResolver/* */ resourceResolver;
	private final Logger/*                                                    */ sessionLogger;

	/**/

	public VSphereExperimentExecutor(
			VSphereAPIUtil vSphereAPIUtil,
			ExperimentElementExecutionContextBuilder.ResourceResolver resourceResolver,
			Logger sessionLogger
	) {
		super();

		this.vSphereAPIUtil/*   */ = vSphereAPIUtil;
		this.resourceResolver/* */ = resourceResolver;
		this.sessionLogger/*    */ = sessionLogger;

		this.registerBeforeHook((executor, experiment, phase) ->
				new ExecutionTimeSlotDurationCalculator().apply(experiment)
		);
		this.registerExceptionHandler((exception, experiment, phase) ->
				logAndThenThrowException(exception, experiment, phase)
		);
	}

	private void logAndThenThrowException(Exception e, ExperimentImpl experiment, ExecutionPhase phase)
			throws ExecutionException {

		logger.error("Exception while executing experiment", e);

		sessionLogger.trace("Exception while executing experiment in phase {} and mode {}.",
				phase, ExecutionMode.RELEASE
		);
		throw new ExecutionException("Exception while executing experiment", e);
	}

	/**/

	@Override
	public boolean validate(ExperimentImpl experiment, ExecutionPhase phase) {

		ExperimentDesignElementImpl rootDesignElement = experiment.getRoot().getDesign();

		if (! WorkspaceDesignElement.matches(rootDesignElement)) {

			logger.error("Unexpected top-level design element; category: {}; name: {}",
					rootDesignElement.getCategory(), rootDesignElement.getName()
			);
			return false;
		}

		Set<String> duplicates = findElementIdDuplicatesInExperiment(experiment);

		if (! duplicates.isEmpty()) {

			throw new IllegalArgumentException("Some experiment element IDs appear more than once: " + duplicates);
		}

		return true;
	}

	protected Set<String> findElementIdDuplicatesInExperiment(ExperimentImpl experiment) {

		List<ExperimentElementImpl> experimentElements = new ArrayList<>();
		experiment.getRoot().walk(experimentElements::add);

		Map<ExperimentElementId, List<ExperimentElementImpl>> experimentElementsGroupedById =
				experimentElements.stream().collect(Collectors.groupingBy(ExperimentElementImpl::getId));

		return (experimentElementsGroupedById.entrySet().stream()

				.filter(groupingEntry -> groupingEntry.getValue().size() > 1)

				.map(groupingEntry -> groupingEntry.getKey().value())

				.collect(Collectors.toSet()) // => set of experiment IDs
		);
	}

	/**/

	@Override
	public Object execute_internal(ExperimentImpl experiment, ExecutionPhase phase) throws ExecutionException {

		sessionLogger.trace("Started executing experiment in phase {} and mode {}",
				phase, ExecutionMode.RELEASE
		);
		Object result = super.execute_internal(experiment, phase);

		sessionLogger.trace("Finished executing experiment in phase {} and mode {}.",
				phase, ExecutionMode.RELEASE
		);
		return result;
	}

	@Override
	protected ExperimentElementExecutionContext buildContext(ExperimentImpl experiment, ExecutionPhase phase) {

		ExperimentExecutionToolkit executionToolkit =
				new ExperimentExecutionToolkitForTestbedVSphere(this.vSphereAPIUtil);

		return new ExperimentElementExecutionContextBuilder()
				.instance(experiment.getRoot())
				.executionPhase(phase)
				.logger(this.sessionLogger)
				.runMode(ExecutionMode.RELEASE)
				.registryContext(experiment.getContextName())
				.taskId(ExecutionTaskId.of(experiment.getRoot().getId().value()))
				.executionTraceRecorder(experiment::addToExecutionTrace)
				.executionToolkit(executionToolkit)
				.build();
	}

	/**/

}
