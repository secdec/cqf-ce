package com.secdec.astam.cqf.api.rest.app.aor.impl.vsphere;

import static com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase.COMPLETE;
import static com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase.INITIALIZE;
import static com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase.QUANTIFY;

import com.secdec.astam.cqf.api.rest.app.aor.ExecutionPlatformManager;
import com.secdec.astam.cqf.api.rest.app.aor.impl.ExperimentManagerBase;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutor;
import com.siegetechnologies.cqf.core._v01.experiment.execution.impl.DefaultExperimentExecutor;
import com.siegetechnologies.cqf.core._v01.experiment.execution.mixin.AsTaskMixin;
import com.siegetechnologies.cqf.core._v01.experiment.execution.mixin.ExperimentResultsMixin;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTask;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTaskManager;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.impl.DefaultExecutionTask;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.VSphereExperimentExecutor;
import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtil;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Manages the execution of experiments and their retrieval by ID.
 */
public class ExperimentManagerForVSphere extends ExperimentManagerBase
{
	protected final Map<Integer, ExecutionTask>/* */ executionTaskMap/*     */ = new HashMap<>(); // key: ID
	protected final ExecutionTaskManager/*        */ executionTaskManager/* */ = new ExecutionTaskManager();

	/**/

	/**
	 * Creates an object of this type.
	 *
	 * @param parentExecutionPlatformManager
	 */
	public ExperimentManagerForVSphere(ExecutionPlatformManager parentExecutionPlatformManager)
	{
		super(parentExecutionPlatformManager);
	}

	/**/

	@Override
	public void execute(ExperimentImpl experiment)
	{
		super.execute(experiment);

		ExperimentExecutor executor = newExecutorForExperiment(experiment);
		new ExperimentResultsMixin().apply(executor);

		String executionTaskName = String.format("[%s] %s", QUANTIFY, experiment.getName());
		Consumer<ExecutionTask> executionTaskRunner = (x -> runExecutionTask(x, experiment, executor));

		ExecutionTask executionTask = newExecutionTask(executionTaskName, null, executionTaskRunner);
		executionTaskMap.put(executionTask.getId(), executionTask);

		/**/

		this.changePhaseOfExecutingExperiment(experiment, INITIALIZE, null);

		CompletableFuture<Void> executionFuture = (executionTaskManager.submit(executionTask)
				.whenComplete((value, exception) -> {

					ExecutionResultImpl executionResult = (executor
							.getResult(experiment, ExperimentResultsMixin.RESULT_KEY)
							.filter(ExecutionResultImpl.class::isInstance)
							.map(ExecutionResultImpl.class::cast)
							.orElse(null)
					);
					this.changePhaseOfExecutingExperiment(experiment, COMPLETE, null);

					executionTask.logEvent("complete", true);
					//^-- FIXME: srogers: refer to a public constant instead

					experiment.setResult(executionResult);

					executionTask.shutdown();
				})
		);
		executionTask.setFuture(executionFuture);
	}

	protected ExperimentExecutor newExecutorForExperiment(ExperimentImpl experiment)
	{
		return newVSphereExecutorForExperiment(experiment);
	}

	protected ExperimentExecutor newDefaultExecutorForExperiment(ExperimentImpl experiment)
	{
		return new DefaultExperimentExecutor();
	}

	protected ExperimentExecutor newVSphereExecutorForExperiment(ExperimentImpl experiment)
	{
		VSphereAPIUtil apiUtil = this.parentExecutionPlatformManager
				.getSessionManagerAs(SessionManagerForVSphere.class).getAPIUtil();

		String executionLogger_name = "Experiment-" + experiment.getName();
		Logger executionLogger = LoggerFactory.getLogger(executionLogger_name);

		return new VSphereExperimentExecutor(apiUtil, null, executionLogger);
	}

	protected void runExecutionTask(ExecutionTask executionTask, ExperimentImpl experiment, ExperimentExecutor executor)
	{

		new AsTaskMixin(executionTask).apply(executor);
		try {
			executor.execute(experiment, QUANTIFY);

		}
		catch (InterruptedException | IOException | ExecutionException e) {

			this.changePhaseOfExecutingExperiment(experiment, COMPLETE, e);

			throw new RuntimeException("Execution task failed", e);
		}
	}

	protected ExecutionTask newExecutionTask
			(
					String executionTaskName,
					Duration duration,
					Consumer<ExecutionTask> executionTaskRunner
			)
	{
		return new DefaultExecutionTask(executionTaskName, duration, executionTaskRunner);
	}

	/**/

	@Override
	public Collection<ExperimentImpl> getExperiments()
	{
		return experimentMap.values();
	}

	/**/

}
