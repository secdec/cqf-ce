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

import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;

import java.util.concurrent.TimeUnit;

/**
 * A {@link ExecutionQuantifierConfiguration} provides the configuration
 * information for a {@link ExecutionQuantifier} that is used while executing
 * the {@link ExecutionPhase#QUANTIFY} phase.  See the documentation for
 * {@link ExecutionQuantifier} to understand how a {@link ExecutionQuantifierConfiguration}
 * is used.
 *
 * @author taylorj
 */
public interface ExecutionQuantifierConfiguration {
	/**
	 * Returns the number of times that execution of a phase
	 * may be retried.
	 *
	 * @param phase the phase
	 * @return the number of times to retry
	 */
	int getNumberOfTries(ExecutionPhase phase);

	/**
	 * Returns the {@link TimeUnit} of the delay between
	 * attempts for quantifying a phase.
	 *
	 * @param phase the phase
	 * @return the time unit of the delay for the phae
	 */
	TimeUnit getDelayTimeUnit(ExecutionPhase phase);

	/**
	 * Returns the measure of the delay between
	 * attempts for quantifying a phase.
	 *
	 * @param phase the phase
	 * @return the measure of the delay for the phase
	 */
	long getDelayMeasure(ExecutionPhase phase);
}
