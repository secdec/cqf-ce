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

import com.siegetechnologies.cqf.core.experiment.ExperimentElementId;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentId;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentIdResolver;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import java.util.Collection;
import java.util.Optional;

/**
 * @author srogers
 */
public interface ExperimentManager extends
		ResourceManagementService,
		ExperimentIdResolver<ExperimentImpl>,
		ExperimentElementIdResolver<ExperimentElementImpl>
{
	/**
	 * Executes an experiment.
	 *
	 * @param experiment
	 */
	void execute(ExperimentImpl experiment);

	/**/
	
	/**
	 * Returns the experiments that are currently executing.
	 *
	 * @return the experiments that are currently executing.
	 */
	Collection<ExperimentImpl> getExperiments();
	
	/**/
	
	/**
	 * Returns the executing experiment that corresponds to the specified ID (if any).
	 *
	 * @return the executing experiment that corresponds to the specified ID (if any)
	 *
	 * @param experimentId
	 */
	Optional<ExperimentImpl> resolve(ExperimentId experimentId);
	
	/**
	 * Returns the element of an executing experiment that corresponds to the specified ID (if any).
	 *
	 * @return the element of an executing experiment that corresponds to the specified ID (if any)
	 *
	 * @param experimentElementId
	 */
	Optional<ExperimentElementImpl> resolve(ExperimentElementId experimentElementId);
	
	/**/
	
	interface Listener
	{
		default
		void hasAddedExperiment(
				ExperimentImpl/* */ experiment
		) {}
		
		default
		void hasPreparedExperiment(
				ExperimentImpl/* */ experiment
		) {}

		default
		void hasStartedExecutingExperiment(
				ExperimentImpl/* */ experiment
		) {}
		
		default
		void hasChangedPhaseOfExecutingExperiment(
				ExperimentImpl/* */ experiment,
				ExecutionPhase/* */ executionPhase,
				Throwable/*      */ causeOfTransition
		) {};
		
		default
		void hasFinishedExecutingExperiment(
				ExperimentImpl/* */ experiment
		) {};
	
		default
		void hasDroppedExperiment(
				ExperimentImpl/* */ experiment
		) {}
		
	}
	
	/**/
	
}
