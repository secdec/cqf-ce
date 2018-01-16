package com.siegetechnologies.cqf.core._v01.experiment.execution.impl;

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

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContextBuilder;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutionToolkit;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutor;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTaskId;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionMode;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExperimentExecutor that defines most default behavior necessary to execute an experiment.
 */
public class DefaultExperimentExecutor implements ExperimentExecutor
{
	private static final Logger logger = LoggerFactory.getLogger(DefaultExperimentExecutor.class);

	private final List<BeforeHook>/*                 */ beforeHooks/*       */ = new ArrayList<>();
	private final List<AfterHook>/*                  */ afterHooks/*        */ = new ArrayList<>();
	private final List<ExceptionHandler>/*           */ exceptionHandlers/* */ = new ArrayList<>();
	private final Map<String, Map<String, Object>>/* */ savedResults/*      */ = new HashMap<>();

	/**/

	private Map<String, Object> getResultMapForExperiment(ExperimentImpl experiment) {

		return savedResults.computeIfAbsent(experiment.getRoot().getId().value(), (k) -> new HashMap<>());
	}

	/**/

	@Override
	public Object execute_internal(ExperimentImpl experiment, ExecutionPhase phase) throws ExecutionException {

		ExperimentElementExecutionContext context = buildContext(experiment, phase);

		Object result;
		try {
			result = context.getRegistry().executeUsingContext(context);
		}
		catch (Exception e) {
			throw new ExecutionException("while executing experiment", e);
		}

		return result;
	}

	@Override
	public void execute_beforeHooks(ExperimentImpl experiment, ExecutionPhase phase) {

		Objects.requireNonNull(experiment, "Experiment passed cannot be null");
		Objects.requireNonNull(phase, "ExecutionPhase passed cannot be null");

		if (! experiment.validate()) {

			throw new RuntimeException("invalid experiment (poorly formed)");
		}
		if (! validate(experiment, phase)) {

			throw new RuntimeException("invalid experiment (not executable)");
		}

		for (BeforeHook beforeHook : getRegisteredBeforeHooks()) {

			beforeHook.run(this, experiment, phase);
		}
	}

	@Override
	public Object execute_afterHooks(ExperimentImpl experiment, ExecutionPhase phase, Object result) {

		Map<String, Object> resultMap = getResultMapForExperiment(experiment);
		resultMap.clear();

		for (AfterHook afterHook : getRegisteredAfterHooks()) {
			try {
				result = afterHook.run(this, experiment, phase, result);
			}
			catch (IOException | ExecutionException e) {

				logger.error("while executing after-hook", e);
			}
		}

		return result;
	}

	@Override
	public void execute_exceptionHandlers(Exception e, ExperimentImpl experiment, ExecutionPhase phase)
			throws ExecutionException {

		for (ExceptionHandler exceptionHandler : getRegisteredExceptionHandlers()) {

			exceptionHandler.handle(e, experiment, phase);
		}
	}

    /**/

	@Override
	public List<BeforeHook> getRegisteredBeforeHooks() {

		return this.beforeHooks;
	}

	@Override
	public void registerBeforeHook(BeforeHook beforeHook) {

		getRegisteredBeforeHooks().add(beforeHook);
	}

	@Override
	public void withdrawBeforeHook(BeforeHook beforeHook) {

		getRegisteredBeforeHooks().remove(beforeHook);
	}

	/**/

	@Override
	public List<AfterHook> getRegisteredAfterHooks() {

		return this.afterHooks;
	}

	@Override
	public void registerAfterHook(AfterHook afterHook) {

		getRegisteredAfterHooks().add(afterHook);
	}

	@Override
	public void withdrawAfterHook(AfterHook afterHook) {

		getRegisteredAfterHooks().remove(afterHook);
	}

	/**/

	@Override
	public List<ExceptionHandler> getRegisteredExceptionHandlers() {

		return this.exceptionHandlers;
	}

	@Override
	public void registerExceptionHandler(ExceptionHandler exceptionHandler) {

		getRegisteredExceptionHandlers().add(exceptionHandler);
	}

	@Override
	public void withdrawExceptionHandler(ExceptionHandler exceptionHandler) {
		getRegisteredExceptionHandlers().remove(exceptionHandler);
	}

    /**/

	@Override
	public Optional<Object> getResult(ExperimentImpl experiment, String key) {

		Map<String, Object> m = getResultMapForExperiment(experiment);

		return Optional.ofNullable(m.get(key));
	}

	@Override
	public void saveResult(ExperimentImpl experiment, String key, Object result) {

		Map<String, Object> m = getResultMapForExperiment(experiment);

		m.put(key, result);
	}

	/**/

	/**
	 * Build the context that will be used during execution of the experiment.
	 *
	 * @param experiment
	 * 		experiment being executed
	 * @param phase
	 * 		phase to execute in
	 *
	 * @return {@link ExperimentElementExecutionContext} for this execution
	 */
	protected ExperimentElementExecutionContext buildContext(ExperimentImpl experiment, ExecutionPhase phase) {

		String taskId_value = "cqf-execution-task-for-root-" +
				experiment.getRoot().getId().value().replaceAll("^cqf-", "");

		return new ExperimentElementExecutionContextBuilder().instance(experiment.getRoot())
				.executionPhase(phase)
				.runMode(ExecutionMode.RELEASE)
				.logger(logger)
				.executionTraceRecorder(experiment::addToExecutionTrace)
				.registryContext(experiment.getContextName())
				.taskId(ExecutionTaskId.of(taskId_value))
				.executionToolkit(ExperimentExecutionToolkit.getDefault())
				.build();
	}

	/**/

}
