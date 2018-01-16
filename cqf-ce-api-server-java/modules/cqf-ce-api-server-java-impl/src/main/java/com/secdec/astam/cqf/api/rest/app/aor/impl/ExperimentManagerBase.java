package com.secdec.astam.cqf.api.rest.app.aor.impl;

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

import static com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase.COMPLETE;
import static com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase.INITIALIZE;

import com.secdec.astam.cqf.api.rest.app.aor.ExecutionPlatformManager;
import com.secdec.astam.cqf.api.rest.app.aor.ExperimentManager;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementId;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentId;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author srogers
 */
public abstract class ExperimentManagerBase extends
		ExecutionPlatformManagerBase implements ExperimentManager
{
	private static final Logger logger = LoggerFactory.getLogger(ExperimentManagerBase.class);

	protected final Map<ExperimentElementId, ExperimentImpl> experimentMap = new HashMap<>(); // key: root ID

	protected final Listener monitor = new Monitor();

	/**/

	/**
	 * Creates an object of this type.
	 */
	public ExperimentManagerBase(ExecutionPlatformManager parentExecutionPlatformManager) {

		super(parentExecutionPlatformManager);
	}

	/**/

	@Override
	public void execute(ExperimentImpl experiment) {

		this.registerExperiment(experiment);

		this.prepareExperiment(experiment);
	}

	@Override
	public Collection<ExperimentImpl> getExperiments() {

		return this.experimentMap.values();
	}

	/**
	 * Registers an experiment with this experiment manager.
	 *
	 * @param experiment
	 */
	protected void registerExperiment(ExperimentImpl experiment) {

		this.experimentMap.put(experiment.getRoot().getId(), experiment);

		this.fireHasAddedExperiment(experiment);
	}

	/**
	 * Withdraws an experiment from this experiment manager.
	 *
	 * @param experiment
	 */
	protected void withdrawExperiment(ExperimentImpl experiment) {

		this.experimentMap.remove(experiment.getRoot().getId());

		this.fireHasDroppedExperiment(experiment);
	}

	/**
	 * Prepares a registered experiment for execution.
	 *
	 * @param experiment
	 */
	protected void prepareExperiment(ExperimentImpl experiment) {

		assert resolve(experiment.getId()).isPresent();

		experiment.walk(e -> {

			e.getDesign().prepareExperimentElementForExecution(e);

			logger.info(String.format("[%s] prepared for execution; final bindings: %s", this, e.getParameterValueMap()));
		});

		this.fireHasPreparedExperiment(experiment);
	}

	protected synchronized void changePhaseOfExecutingExperiment(
			ExperimentImpl/* */ experiment,
			ExecutionPhase/* */ executionPhase,
			Throwable/*      */ causeOfTransition
	) {
		if (executionPhase.equals(INITIALIZE)) {
			this.fireHasStartedExecutingExperiment(experiment);
		}

		this.fireHasChangedPhaseOfExecutingExperiment(experiment, executionPhase, causeOfTransition);

		if (executionPhase.equals(COMPLETE)) {
			this.fireHasFinishedExecutingExperiment(experiment);
		}
	}

	/**/

	@Override
	public Optional<ExperimentImpl> resolve(ExperimentId experimentId) {

		ExperimentElementId experimentRootId = experimentId;

		ExperimentImpl experiment = this.experimentMap.get(experimentRootId);
		if (experiment == null) { return Optional.empty(); }

		ExperimentElementImpl experimentRoot = experiment.getRoot();
		assert experiment == null || experimentRoot.getId().equals(experimentRootId);

		return Optional.ofNullable(experiment);
	}

	@Override
	public Optional<ExperimentElementImpl> resolve(ExperimentElementId experimentElementId) {

		throw new UnsupportedOperationException("no support yet for looking up arbitrary experiment elements by ID");
		//^-- FIXME: srogers: implement ExperimentManager.resolve(ExperimentElementId)
	}

	/**/

	private void fireHasAddedExperiment(
			ExperimentImpl/* */ experiment
	) {
		monitor.hasAddedExperiment(experiment);
	}

	private void fireHasPreparedExperiment(
			ExperimentImpl/* */ experiment
	) {
		monitor.hasPreparedExperiment(experiment);
	}

	private void fireHasStartedExecutingExperiment(
			ExperimentImpl/* */ experiment
	) {
		monitor.hasStartedExecutingExperiment(experiment);
	}

	private void fireHasChangedPhaseOfExecutingExperiment(
			ExperimentImpl/* */ experiment,
			ExecutionPhase/* */ executionPhase,
			Throwable/*      */ causeOfTransition
	) {
		monitor.hasChangedPhaseOfExecutingExperiment(experiment, executionPhase, causeOfTransition);
	}

	private void fireHasFinishedExecutingExperiment(
			ExperimentImpl/* */ experiment
	) {
		monitor.hasFinishedExecutingExperiment(experiment);
	}

	private void fireHasDroppedExperiment(
			ExperimentImpl/* */ experiment
	) {
		monitor.hasDroppedExperiment(experiment);
	}

	/**/

	protected class Monitor implements Listener
	{
		@Override
		public void hasAddedExperiment(
				ExperimentImpl/* */ experiment
		) {}

		@Override
		public void hasStartedExecutingExperiment(
				ExperimentImpl/* */ experiment
		) {}

		@Override
		public void hasChangedPhaseOfExecutingExperiment(
				ExperimentImpl/* */ experiment,
				ExecutionPhase/* */ executionPhase,
				Throwable/*      */ causeOfTransition
		) {
			String detail = executionTraceDetailForTransitionToPhase(executionPhase, causeOfTransition);

			experiment.addToExecutionTrace(executionPhase, experiment.getRoot(), detail);
			//^-- side effect: changes the phase of the experiment
		}

		@Override
		public void hasFinishedExecutingExperiment(
				ExperimentImpl/* */ experiment
		) {}

		@Override
		public void hasDroppedExperiment(
				ExperimentImpl/* */ experiment
		) {}

	}

	/**/

	private static String executionTraceDetailForTransitionToPhase(
			ExecutionPhase/* */ executionPhase,
			Throwable/*      */ causeOfTransition
	) {
		String result;
		switch (executionPhase) {
		case INITIALIZE:
			result = "Setting up";
			break;
		case RUN:
			result = "Running";
			break;
		case RETRIEVE_DATA:
			result = "Retrieving data";
			break;
		case CLEANUP:
			result = "Cleaning up";
			break;
		case COMPLETE:
			result = "Completed";
			break;
		default:
			throw new IllegalArgumentException("unsupport execution phase: " + executionPhase);
		}

		if (causeOfTransition != null) {
			result += " [cause: " + causeOfTransition.getMessage() + "]";
		}

		return result;
	}

	/**/

}

//^-- TODO: DESIGN: REVIEW: srogers: add support for multiple ExperimentExecutionListeners

//^-- FIXME: DESIGN: REVIEW: srogers: stop tracking experiments that have been (both) completed and retrieved

//^-- TODO: srogers: extract executionTraceDetailForTransitionToPhase into a utility class
