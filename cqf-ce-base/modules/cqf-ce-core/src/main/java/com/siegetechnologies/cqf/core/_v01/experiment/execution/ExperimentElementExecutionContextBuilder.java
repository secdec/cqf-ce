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

import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionMode;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTaskId;
import org.slf4j.Logger;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A builder for {@link ExperimentElementExecutionContext}.  {@link ExperimentElementExecutionContext} has a public
 * constructor which is perfectly usable, but it has a large number of arguments,
 * and the builder may be more convenient.
 */
public class ExperimentElementExecutionContextBuilder
{
	private ExperimentElementImpl experimentElement;
	private ExperimentElementExecutionHandlerRegistry registry;
	private Optional<String> registryContextName;
	private Optional<Object> parentResult;
	private Optional<ExperimentElementExecutionContext> parentContext;
	private Logger instanceLogger;
	private Integer indexInParent;
	private ExecutionMode executionMode;
	private Optional<Function<String, InputStream>> resourceResolver;
	private ExecutionPhase executionPhase;
	private Map<String, Object> attributes;
	private ExperimentElementExecutionContext.ExecutionTraceRecorder executionTraceRecorder;
	private ExecutionTaskId executionTaskId;
	private ExperimentExecutionToolkit executionToolkit;

	/**
	 * Returns an new builder with default values.
	 */
	public ExperimentElementExecutionContextBuilder() {
		experimentElement = null;
		registry = new ExperimentElementExecutionHandlerRegistry();
		parentResult = Optional.empty();
		parentContext = Optional.empty();
		indexInParent = 0;
		instanceLogger = null;
		executionMode = ExecutionMode.DEBUG;
		resourceResolver = Optional.empty();
		registryContextName = Optional.empty();
		executionPhase = ExecutionPhase.INITIALIZE;
		attributes = new HashMap<>();
		executionTaskId = null;
		executionTraceRecorder = null;
	}

	/**
	 * Returns a new builder initialized with values from an experimentElement context.
	 *
	 * @param defaults the experimentElement context
	 */
	public ExperimentElementExecutionContextBuilder(ExperimentElementExecutionContext defaults) {
		experimentElement = defaults.getExperimentElement();
		registry = defaults.getRegistry();
		parentResult = defaults.getResult();
		parentContext = defaults.getContext();
		indexInParent = defaults.getIndexInParent();
		instanceLogger = defaults.getLogger();
		executionMode = defaults.getExecutionMode();
		registryContextName = defaults.getRegistryContextName();
		resourceResolver = defaults.getResourceResolver();
		executionPhase = defaults.getExecutionPhase();
		attributes = new HashMap<>(defaults.getAttributes());
		executionTaskId = defaults.getExecutionTaskId();
		executionTraceRecorder = defaults.getExecutionTraceRecorder().get();
	}

	/**
	 * Set the logger for the context.
	 *
	 * @param logger the logger
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder logger(Logger logger) {
		this.instanceLogger = logger;
		return this;
	}

	/**
	 * Sets the experimentElement for the context.
	 *
	 * @param experimentElement the experimentElement
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder instance(ExperimentElementImpl experimentElement) {
		this.experimentElement = experimentElement;
		return this;
	}

	/**
	 * Sets the registry for the context.
	 *
	 * @param registry the registry
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder registry(ExperimentElementExecutionHandlerRegistry registry) {
		this.registry = registry;
		return this;
	}

	/**
	 * Sets the result from the parent, the parent context, and the index within the parent
	 * of the context.
	 *
	 * @param parentResult  the parent result
	 * @param parentContext the parent context
	 * @param indexInParent the index in the parent
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder parent(Object parentResult,
			ExperimentElementExecutionContext parentContext, int indexInParent) {
		this.parentResult = Optional.ofNullable(parentResult);
		this.parentContext = Optional.of(parentContext);
		this.indexInParent = indexInParent;
		return this;
	}

	/**
	 * Sets the run mode of the context.
	 *
	 * @param executionMode the run mode
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder runMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
		return this;
	}

	/**
	 * Sets the task ID of the context.
	 *
	 * @param executionTaskId the task id
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder taskId(ExecutionTaskId executionTaskId) {
		this.executionTaskId = executionTaskId;
		return this;
	}

	public ExperimentElementExecutionContextBuilder executionToolkit(ExperimentExecutionToolkit executionToolkit) {
		this.executionToolkit = executionToolkit;
		return this;
	}

	/**
	 * Functional interface for mapping a string to a input stream.
	 *
	 * @author taylorj
	 */
	@FunctionalInterface
	public interface ResourceResolver extends Function<String, InputStream>
	{

	}

	/**
	 * Sets the resource resolver of the context.
	 *
	 * @param resourceResolver the resource resolver
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder resourceResolver(ResourceResolver resourceResolver) {
		this.resourceResolver = Optional.of(resourceResolver);
		return this;
	}

	/**
	 * Sets the registry context
	 *
	 * @param registryContext
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder registryContext(String registryContext ) {
		this.registryContextName = Optional.ofNullable(registryContext);
		return this;
	}

	/**
	 * Sets the test phase of the context.
	 *
	 * @param executionPhase the test phase
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder executionPhase(ExecutionPhase executionPhase) {
		this.executionPhase = executionPhase;
		return this;
	}

	/**
	 * Adds an attribute for the context.
	 *
	 * @param name  the attribute name
	 * @param value the attribute value
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder attribute(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

	/**
	 * Adds multiple attributes for the context.
	 *
	 * @param attrs the attribute names and values
	 *
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder attributes(Map<String, Object> attrs) {
		attributes.putAll(attrs);
		return this;
	}

	/**
	 * Sets and returns current context with trace recorder.
	 *
	 * @param executionTraceRecorder
	 * @return the builder
	 */
	public ExperimentElementExecutionContextBuilder executionTraceRecorder(ExperimentElementExecutionContext.ExecutionTraceRecorder executionTraceRecorder)
	{
		this.executionTraceRecorder = executionTraceRecorder;
		return this;
	}

	/**
	 * Builds and returns an experimentElement context.
	 *
	 * @return the experimentElement context
	 */
	public ExperimentElementExecutionContext build() {
		Objects.requireNonNull(indexInParent, "index in parent must not be null");
		return new ExperimentElementExecutionContext(
				experimentElement,
				indexInParent,
				parentResult.orElse(null),
				resourceResolver.orElse(null),
				registryContextName.orElse(null),
				parentContext.orElse(null),
				registry,
				executionMode,
				executionPhase,
				attributes,
				instanceLogger,
				executionTraceRecorder,
				executionTaskId,
				executionToolkit
		);
	}
}
