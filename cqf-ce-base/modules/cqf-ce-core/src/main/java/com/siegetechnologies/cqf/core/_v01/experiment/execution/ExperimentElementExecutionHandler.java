package com.siegetechnologies.cqf.core._v01.experiment.execution;

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

import static com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase.CLEANUP;
import static java.util.stream.Collectors.joining;

import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionQuantifier;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionQuantifierConfiguration;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionQuantifierContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.impl.DefaultExecutionQuantifierConfiguration;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.impl.ExperimentElementExecutionQuantifierContext;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.core.util.Concurrency;
import com.siegetechnologies.cqf.core.util.Config;
import com.siegetechnologies.cqf.core.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;

/**
 * Handler to execute an instance context.
 *
 * @author taylorj
 *
 * @param <R_before>  the type of the result returned by the method before-hooks.
 * @param <R_main>    the type of the result returned by the (main) methods
 */
public interface ExperimentElementExecutionHandler<R_before, R_main>
{
	/**
	 * @return the design name of elements supported by this handler.
	 */
	String getDesignName();
	
	/**
	 * @return the design category of elements supported by this handler.
	 */
	String getDesignCategory();
	
	/**/
	
	/**
	 * Handle the context. The default handle method switches on the value of
	 * the context's {@link ExperimentElementExecutionContext#getExecutionPhase() test phase}, and
	 * calls the corresponding handler method (e.g.,
	 * {@link #run(ExperimentElementExecutionContext, R_before)},
	 * {@link #initialize(ExperimentElementExecutionContext, R_before)}).
	 *
	 * @param context the context
	 * @return the result from the handler
	 *
	 * @throws InterruptedException if the handler is interrupted
	 * @throws ExecutionException if the handling process throws an exception
	 * @throws IllegalArgumentException if errors are detected in the instance
	 *             context
	 */
	default Object handle(ExperimentElementExecutionContext context) {
		
		validate(context.getExperimentElement(), context.getLogger());
		
		final R_before/* */ result00;
		final R_main/*   */ result01;
		final Object/*   */ result02;
		try {
			switch (context.getExecutionPhase()) {
			case INITIALIZE:
				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "STARTED"));
				
				result00 = initialize_beforeHook(context);
				result01 = initialize(context, result00);
				result02 = initialize_afterHook(context, result01);
				
				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "ENDED"));
				
				return result02;
				
			case RUN:
				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "STARTED"));

				result00 = run_beforeHook(context);
				result01 = run(context, result00);
				result02 = run_afterHook(context, result01);

				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "ENDED"));
				
				return result02;

			case RETRIEVE_DATA:
				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "STARTED"));

				result00 = retrieveData_beforeHook(context);
				result01 = retrieveData(context, result00);
				result02 = retrieveData_afterHook(context, result01);

				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "ENDED"));
				
				return result02;

			case CLEANUP:
				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "STARTED"));

				result00 = cleanup_beforeHook(context);
				result01 = cleanup(context, result00);
				result02 = cleanup_afterHook(context, result01);

				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "ENDED"));
				
				return result02;

			case QUANTIFY:
				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "STARTED"));

				result02 = handleQuantify(context);

				context.getExecutionTraceRecorder().ifPresent(c ->
						c.record(context.getExecutionPhase(), context.getExperimentElement(), "ENDED"));
				
				return result02;

			case COMPLETE:
				return null;

			default:
				throw new IllegalArgumentException("unsupported test phase: " + context.getExecutionPhase());
			}
		}
		catch (RuntimeException e) {
			throw new ExperimentElementExecutionException("Exception while handling instance context.", e);
		}
	}
	
	/**/
	
	/**
	 * Uses an experimentElement validator to check the experimentElement, logging warnings
	 * and errors with a provided logger.
	 *
	 * @param experimentElement the experimentElement
	 * @param logger the logger
	 *
	 * @throws IllegalArgumentException if there are errors in the experimentElement
	 */
	static void validate(ExperimentElementImpl experimentElement, Logger logger) {
		
		ExperimentElementImpl.Validator validator = new ExperimentElementImpl.Validator();
		ExperimentElementImpl.Validator.Report report = validator.validate(experimentElement);
		report.getErrors().forEach(validator::error);
		report.getWarnings().forEach(validator::warn);

		List<String> errors = report.getErrors();
		if (!errors.isEmpty()) {
			String msg = report.getErrors()
					.stream()
					.collect(Collectors.joining(" ", "Errors: ", ""));
			throw new IllegalArgumentException(msg);
		}

		List<String> warnings = report.getWarnings();
		if (!warnings.isEmpty()) {
			String msg = report.getWarnings()
					.stream()
					.collect(joining(" ", "Warnings: ", ""));
			logger.warn(msg);
		}
	}

	/**
	 * Handles a request to quantify the instance context, and returns the
	 * result from the quantification. The default implementation creates a
	 * {@link ExecutionQuantifier} based on a {@link DefaultExecutionQuantifierConfiguration}
	 * and an {@link ExperimentElementExecutionQuantifierContext} constructed from the instance
	 * context and this hander, and returns the result of the
	 * {@link ExecutionPhase#RETRIEVE_DATA} phase if it is non-null, or else the
	 * result of the latest test phase that was run.
	 *
	 * @param context the instance context
	 * @return the quantification result
	 */
	default Object handleQuantify(ExperimentElementExecutionContext context) {
		
		ExecutionQuantifierContext q_context =
				new ExperimentElementExecutionQuantifierContext(context, this);
		
		ExecutionQuantifierConfiguration q_configuration =
				new DefaultExecutionQuantifierConfiguration(Config.getConfiguration());
		
		ExecutionQuantifier q = new ExecutionQuantifier(q_configuration);
		Optional<ExecutionQuantifier.Result<Object>> result = q.quantify(q_context);
		
		return result.map(ExecutionQuantifier.Result::getValue).orElse(null);
	}
	
	/**/
	
	/**
	 * Handles the child elements of a context.
	 *
	 * @param context the context
	 * @param result_before the before-hook result from the context
	 */
	default void handleSubInstances(ExperimentElementExecutionContext context, R_before result_before) {
		
		List<ExperimentElementImpl> children = context.getExperimentElement()
				.getChildren();
		
		// If the test phase is cleanup, then we need to run in reverse order
		// from the execution index. All the other phases can use the natural
		// ordering.
		Comparator<Integer> executionIndexComparator = context.getExecutionPhase()
				.equals(CLEANUP) ? Comparator.reverseOrder() : Comparator.naturalOrder();
		
		// Pair the children up with their index in the parent to produce
		// (instance,indexInParent) pairs, then group these pairs by the
		// execution index of the instance. By grouping these into a TreeMap
		// (whose keys are naturally ordered), we can iterate over the
		// collections of instances in order of increasing execution index.
		TreeMap<Integer, List<Pair<ExperimentElementImpl, Integer>>> groupedChildren = IntStream.range(0, children.size())
				.mapToObj(indexInParent -> Pair.of(children.get(indexInParent), indexInParent))
				.collect(Collectors.groupingBy(p -> p.getLeft()
						.getExecutionIndex(), () -> new TreeMap<>(executionIndexComparator), Collectors.toList()));
		
		// The children are handled in groups by their execution order. The
		// children were paired with their index in parent earlier, and
		// grouped by their execution order into a tree map. Each handling
		// iterates through the groups of children by increasing execution
		// order, and tries to run all the children in a group in parallel.
		Callable<Object> runnable = () -> {
			for (Integer executionIndex : groupedChildren.navigableKeySet()) {
				List<Callable<Object>> moreRunnables = groupedChildren.get(executionIndex)
						.stream()
						.map(pair -> (Callable<Object>) () -> {
							int indexInParent = pair.getRight();
							ExperimentElementImpl child = pair.getLeft();
							ExperimentElementExecutionContext childContext = context.createChildContext(child, indexInParent, result_before);
							return context.getRegistry().executeUsingContext(childContext);
						})
						.collect(Collectors.toList());
				Concurrency.invokeAll(moreRunnables, ExperimentExecutorService::getExecutorService);
			}
			return null;
		};
		try {
			Concurrency.invokeAll(Collections.singleton(runnable), ExperimentExecutorService::getExecutorService);
		}
		catch (InterruptedException | ExecutionException e) {
			throw new ExperimentElementExecutionException(e);
		}
	}

	/**/

	/*
	 * Cleanup methods
	 */

	/**
	 * Perform actions before cleanup.
	 *
	 * @param context the context
	 * @return a before-hook result
	 */
	default R_before cleanup_beforeHook(ExperimentElementExecutionContext context) {
		return null;
	}

	/**
	 * Perform main actions for cleanup.
	 *
	 * @param context the context
	 * @param result_before the before-hook result
	 * @return the main result
	 */
	default R_main cleanup(ExperimentElementExecutionContext context, R_before result_before) {
		handleSubInstances(context, result_before);
		return null;
	}

	/**
	 * Perform actions after cleanup.
	 *
	 * @param context the context
	 * @param result_main the main result
	 * @return a result
	 */
	default Object cleanup_afterHook(ExperimentElementExecutionContext context, R_main result_main) {
		return null;
	}

	/**/
	
	/*
	 * Run methods
	 */

	/**
	 * Performs actions before the run.
	 *
	 * @param context the context
	 * @return the before-hook result
	 */
	default R_before run_beforeHook(ExperimentElementExecutionContext context) {
		return null;
	}

	/**
	 * Performs actions for the run.
	 *
	 * @param context the context
	 * @param result_before the before-hook result
	 * @return the main result
	 */
	default R_main run(ExperimentElementExecutionContext context, R_before result_before) {
		handleSubInstances(context, result_before);
		return null;
	}

	/**
	 * Performs actions after the run.
	 *
	 * @param context the context
	 * @param result_main the main result
	 * @return a result
	 */
	default Object run_afterHook(ExperimentElementExecutionContext context, R_main result_main) {
		return null;
	}

	/**/
	
	/*
	 * Initialize methods
	 */

	/**
	 * Performs action before initialize.
	 *
	 * @param context the context
	 * @return the before-hook result
	 */
	default R_before initialize_beforeHook(ExperimentElementExecutionContext context) {
		return null;
	}

	/**
	 * Performs main initialize action.
	 *
	 * @param context the context
	 * @param result_before the before-hook result
	 * @return the main result
	 */
	default R_main initialize(ExperimentElementExecutionContext context, R_before result_before) {
		handleSubInstances(context, result_before);
		return null;
	}

	/**
	 * Performs actions after initialize.
	 *
	 * @param context the context
	 * @param result_main the main result
	 * @return a result
	 */
	default Object initialize_afterHook(ExperimentElementExecutionContext context, R_main result_main) {
		return null;
	}

	/**/
	
	/*
	 * Retrieve Data methods
	 */

	/**
	 * Performs actions before retrieving data.
	 *
	 * @param context the context
	 * @return the before-hook result
	 */
	default R_before retrieveData_beforeHook(ExperimentElementExecutionContext context) {
		return null;
	}

	/**
	 * Performs the main retrieve data action.
	 *
	 * @param context the context
	 * @param result_before the before-hook result
	 * @return the main result
	 */
	default R_main retrieveData(ExperimentElementExecutionContext context, R_before result_before) {
		handleSubInstances(context, result_before);
		return null;
	}

	/**
	 * Performs actions after retrieving data.
	 *
	 * @param context the context
	 * @param result_main the main result
	 * @return the path of the result file
	 */
	default ExecutionResultImpl retrieveData_afterHook(ExperimentElementExecutionContext context, R_main result_main) {
		return null;
	}

	/**/
	
}
