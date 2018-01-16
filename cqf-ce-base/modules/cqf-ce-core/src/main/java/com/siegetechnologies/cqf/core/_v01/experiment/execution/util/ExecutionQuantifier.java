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
import com.siegetechnologies.cqf.core.util.Concurrency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods for implementing the quantify phase
 * for an instance context handler
 *
 * @author taylorj
 */
public class ExecutionQuantifier {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionQuantifier.class);

	/**
	 * The quantifier's configuration
	 */
	private final ExecutionQuantifierConfiguration config;

	/**
	 * ExecutionPhases, ordered by priority for gathering individual result files.
	 * 
	 * @see #getMostSignificantPhaseResult(Map)
	 */
	private static final List<ExecutionPhase> TEST_PHASES_BY_PRIORITY =
			Arrays.asList(ExecutionPhase.RETRIEVE_DATA, ExecutionPhase.CLEANUP,
					ExecutionPhase.RUN, ExecutionPhase.INITIALIZE);

	/**
	 * Creates a new ExecutionQuantifier with a provided configuration.
	 *
	 * @param config the configuration
	 */
	public ExecutionQuantifier(ExecutionQuantifierConfiguration config) {
		this.config = config;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"-"+Integer.toHexString(hashCode()); // FIXME: STRING: srogers
	}

	/**
	 * Returns the result of a quantification process. This
	 * bundles a value along with the test phase from
	 * which the value was obtained.
	 * 
	 * @param <T> the type of the result value
	 *
	 * @author taylorj
	 */
	public interface Result<T> {
		/**
		 * Returns the test phase that produced the value.
		 * @return the test phase that produced the value
		 */
		ExecutionPhase getExecutionPhase();

		/**
		 * Returns the value of the result.
		 * @return the value of the result
		 */
		T getValue();

		/**
		 * Returns a {@link Result} with the provided phase and value.
		 *
		 * @param phase the phase
		 * @param value the value
		 * @return a quantification result
		 * 
		 * @param <T> the type of the result
		 */
		static <T> Result<T> of(ExecutionPhase phase, T value) {
			return new Result<T>() {
				@Override
				public ExecutionPhase getExecutionPhase() { return phase; }

				@Override
				public T getValue() { return value; }
			};
		}
	}

	/**
	 * Returns the most significant result from a phase-indexed result map.
	 * If the map contains a non-null result for
	 * {@link ExecutionPhase#RETRIEVE_DATA}, then that result is the most
	 * significant.  Otherwise, the first non-null result from the test
	 * phases, working in reverse chronological order, is the most
	 * significant.  That is, a result from {@link ExecutionPhase#CLEANUP} is
	 * more significant than a result from {@link ExecutionPhase#RUN}, which
	 * is more significant than a result from {@link ExecutionPhase#CLEANUP}.
	 *
	 * @param resultMapIndexedByPhase the phase-indexed result map
	 * @return the result
	 * 
	 * @param <T> the type of the contained result
	 */
	static <T> Optional<Result<T>> getMostSignificantPhaseResult(Map<ExecutionPhase,T> resultMapIndexedByPhase) {
		for (ExecutionPhase phase : TEST_PHASES_BY_PRIORITY) {
			if (resultMapIndexedByPhase.containsKey(phase)) {
				T value = resultMapIndexedByPhase.get(phase);
				if (value != null) {
					return Optional.of(Result.of(phase, value));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Executes the quantification process for a provided context and
	 * returns the {@link #getMostSignificantPhaseResult(Map) "most significant result"}
	 * among the individual results for the executed phases.
	 *
	 * <p>Specifically, the {@link ExecutionQuantifierContext#getEnabledPhases()} is used
	 * to determine which phases will actually be executed during the quantification
	 * process.  Then the following is repeated for each of the enabled phases.  The
	 * {@link ExecutionQuantifier}'s configuration is used to determine the number to attempts
	 * to make for the phase, and the delay between successive attempts.  Then,
	 * {@link Concurrency#retry(int, java.util.concurrent.Callable, TimeUnit, long)}
	 * is used to attempt to handle the phase and produce a result.  The result is then
	 * stored in a map indexed by {@link ExecutionPhase}s.  After handling the phase in that
	 * way, a {@link Concurrency#await(java.util.function.Supplier, long, TimeUnit, long, TimeUnit, String) wait}
	 * is injected for the duration specified by the context, using
	 * {@link ExecutionQuantifierContext#isCancelled()} as a stopping condition.  If the
	 * quantification process is cancelled in any phase, then subsequent phases
	 * will not be run, and the result will be based on the latest test phase that did
	 * run.
	 *
	 * @param context the quantifier context
	 * @return the most significant result
	 */
	public Optional<Result<Object>> quantify(ExecutionQuantifierContext context) {
		// Get the set of phases after which a delay should be injected, and the
		// set of phases that are actually to be run during quantification.
		EnumSet<ExecutionPhase> enabledPhases = context.getEnabledPhases();
		logger.info("({}) Quantification over phases: {}.", this, enabledPhases);

		// Get the phase-indexed result map that will populated and later passed
		// to #getResult to produce the return value.
		EnumMap<ExecutionPhase,Object> resultMapIndexedByPhase = new EnumMap<>(ExecutionPhase.class);

		// Go through the phases, one at a time, and execute the enabled phases.
		// Rather than iterating through the array of phases, we actually get the
		// array of phases, and iterate through it by index.  For each index, we
		// get the corresponding phases, and execute it if it's enabled.  If any
		// phase is cancelled, then we break out of the loop completely.  If a
		// phase throws an exception, then we advance the index to that of CLEANUP,
		// so that we skip any intervening phases (unless the current phase *is*
		// CLEANUP, in which case we break out of the loop).
		boolean cancelled;
		boolean isTerminated = false;
		ExecutionPhase[] phases = ExecutionPhase.values();
		int nPhases = phases.length;
		int phaseIndex = 0;

		while (phaseIndex < nPhases && !isTerminated) {
			ExecutionPhase phase = phases[phaseIndex];
			// Always skip over QUANTIFY, it's not a "real" phase. And skip over
			// phases that aren't currently enabled.
			if (phase == ExecutionPhase.QUANTIFY || !enabledPhases.contains(phase)) {
				phaseIndex++;
				continue;
			}

			// Otherwise, try to run this phase.  This results in one of four outcomes.
			// (i) The phase gets cancelled, in which we stop completely.  (ii) The
			// phase completes successfully, and we continue to the next one.  (iii) The
			// phase throws an exception and is cleanup, so we stop completely. (iv) The
			// phase throws an exception, but isn't cleanup, so we can advance to cleanup.
			logger.info("({}) Quantification beginning enabled phase {}.", this, phase);
			try {
				cancelled = handleEnabledPhase(context, phase, resultMapIndexedByPhase);
				if (cancelled) {
					logger.info("({}) Quantification cancelled at {}.", this, phase);
					isTerminated = true; // (i)
				}
				else {
					logger.info("({}) Quantification completed phase {}.", this, phase);
					phaseIndex++; // (ii)
				}
			}
			catch (Exception e) {
				if (phase == ExecutionPhase.CLEANUP) {
					logger.warn("({}) Quantification error during {}, terminating quantification.", this, phase, e);
					isTerminated = true; // (iii)
				}
				else {
					logger.warn("({}) Quantification error during {}, advancing to {}.", this, phase, ExecutionPhase.CLEANUP, e);
					phaseIndex = ExecutionPhase.CLEANUP.ordinal(); // (iv)
				}
			}
		}

		return getMostSignificantPhaseResult(resultMapIndexedByPhase);
	}
	
	/**
	 * Does the "heavy lifting" of {@link #quantify(ExecutionQuantifierContext)};
	 * a result is captured for each phase.
	 *
	 * @param context the quantifier context
	 * @param phase the test phase
	 * @param resultMapIndexedByPhase the phase-indexed result map
	 * @return whether the phase was cancelled
	 *
	 * @throws ExecutionException from {@link Concurrency#retry(int, java.util.concurrent.Callable)}
	 * @throws InterruptedException from {@link Concurrency#retry(int, java.util.concurrent.Callable)} or {@link Concurrency#await(java.util.function.Supplier, long, TimeUnit, long, TimeUnit, String)}
	 */
	private boolean handleEnabledPhase(ExecutionQuantifierContext context, ExecutionPhase phase, EnumMap<ExecutionPhase,Object> resultMapIndexedByPhase) throws ExecutionException, InterruptedException {
		TimeUnit delayUnit = config.getDelayTimeUnit(phase);
		long delayMeasure = config.getDelayMeasure(phase);
		int nTries = config.getNumberOfTries(phase);

		Object result = Concurrency.retry(nTries, () -> context.handle(phase), delayUnit, delayMeasure);
		resultMapIndexedByPhase.put(phase, result);

		TimeUnit durationUnit = context.getDurationTimeUnit(phase);
		long durationMeasure = context.getDurationMeasure(phase);

		return Concurrency.await(context::isCancelled,
				durationMeasure, durationUnit,
				delayMeasure, delayUnit,
				"("+this+") Awaiting cancellation of "+phase.name()+"."); // FIXME: srogers: extract string construction into a dedicated method
	}
}
