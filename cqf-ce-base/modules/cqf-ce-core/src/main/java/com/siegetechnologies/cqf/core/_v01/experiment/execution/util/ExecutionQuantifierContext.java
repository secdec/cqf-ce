package com.siegetechnologies.cqf.core._v01.experiment.execution.util;

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

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionHandler;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;

/**
 * A quantify context provides the amount of contextual information
 * needed to run a quantify process.  This is an abstraction over
 * a {@link ExperimentElementExecutionContext} and an {@link ExperimentElementExecutionHandler}
 * that exposes just the portions needed for the quantify process.
 *
 * @author taylorj
 */
public interface ExecutionQuantifierContext {
	/**
	 * Returns the phases that are enabled for
	 * a quantification process.
	 *
	 * @return the enabled phases
	 */
	EnumSet<ExecutionPhase> getEnabledPhases();

	/**
	 * Returns the duration of the delay that should be
	 * injected after a phase that should be followed by a delay.
	 *
	 * @param phase the test phase
	 * @return duration of the delay
	 *
	 * @see #getDurationTimeUnit(ExecutionPhase)
	 */
	long getDurationMeasure(ExecutionPhase phase);

	/**
	 * Returns the unit of the delay that should
	 * be injected after a phase that should be followed by a delay.
	 *
	 * @param phase the test phase
	 * @return time unit of the delay
	 * 
	 * @see #getDurationMeasure(ExecutionPhase)
	 */
	TimeUnit getDurationTimeUnit(ExecutionPhase phase);

	/**
	 * Returns whether the quantification process
	 * has been cancelled.
	 *
	 * @return wwhether the quantification process has been cancelled
	 */
	boolean isCancelled();

	/**
	 * Handles the context in the given phase, and returns
	 * the result.
	 *
	 * @param phase the phase
	 * @return the result of handling in the phase
	 */
	Object handle(ExecutionPhase phase);
}
