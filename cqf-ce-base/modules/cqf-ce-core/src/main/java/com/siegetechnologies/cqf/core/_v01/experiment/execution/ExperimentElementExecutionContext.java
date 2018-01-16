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

import static org.apache.commons.lang3.Validate.isTrue;

import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTaskId;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementId;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionMode;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context for running instances.  When running an experimentElement context, {@link
 * ExperimentElementExecutionHandler}s have access to the experimentElement, the the result from the
 * parent experimentElement, the context under which the parent was run, and the index of
 * the current experimentElement within the parent.
 * <p>
 * Context for experimentElement running.
 */
public class ExperimentElementExecutionContext
{
	private static final Logger logger = LoggerFactory.getLogger(ExperimentElementExecutionContext.class);

	private final ExecutionTaskId executionTaskId;
	private final Logger instanceLogger;
	private final ExperimentElementImpl experimentElement;
	private final ExperimentElementExecutionHandlerRegistry registry;
	private final Optional<Object> parentResult;
	private final Optional<ExperimentElementExecutionContext> parentContext;
	private final int indexInParent;
	private final ExecutionMode executionMode;
	private final Optional<Function<String, InputStream>> resourceResolver;
	private final String registryContextName;
	private final ExecutionPhase executionPhase;
	private final LocalDateTime currentTime;
	private final ExecutionTraceRecorder executionTraceRecorder;
	private final ExperimentExecutionToolkit executionToolkit;
	private final Map<String, Object> attributes;

	/**
	 * Format string for network names. Four parameters are expected: the name
	 * parameter of the virtual switch item, the ID of the virtual switch
	 * item, the name parameter of the portgroup item, and the ID of the
	 * portgroup item. A portgroup name might appear as
	 * <code>sName-sId_pgName-pgId</code>.
	 */
	private static final String NETWORK_NAME_FORMAT = "%s-%s_%s-%s";

	@FunctionalInterface
	public interface ExecutionTraceRecorder
	{
		void record(ExecutionPhase executionPhase, ExperimentElementImpl experimentElement, Object record);
	}

	/**
	 * Creates a new experimentElement context for running an experimentElement.
	 *
	 * @param experimentElement the experimentElement
	 * @param indexInParent     the experimentElement's index in its parent
	 * @param result            the result from the parent
	 * @param resourceResolver  the resource resolver
	 * @param context           the context of the parent
	 * @param registry          the experimentElement context handler registry
	 * @param executionMode     the run mode
	 * @param executionPhase    the test phase
	 * @param attributes        attributes of this experimentElement
	 * @param logger            a logger for execution logging
	 */
	ExperimentElementExecutionContext(
			ExperimentElementImpl experimentElement,
			int indexInParent,
			Object result,
			Function<String, InputStream> resourceResolver,
			String registryContextName,
			ExperimentElementExecutionContext context,
			ExperimentElementExecutionHandlerRegistry registry,
			ExecutionMode executionMode,
			ExecutionPhase executionPhase,
			Map<String, Object> attributes,
			Logger logger,
			ExecutionTraceRecorder executionTraceRecorder,
			ExecutionTaskId executionTaskId,
			ExperimentExecutionToolkit executionToolkit
	) {
		this(
				Objects.requireNonNull(experimentElement, "experimentElement must not be null"),
				indexInParent,
				Optional.ofNullable(result),
				Optional.ofNullable(resourceResolver),
				registryContextName,
				Optional.ofNullable(context),
				Objects.requireNonNull(registry, "item registry must not be null"),
				Objects.requireNonNull(executionMode, "run mode must not be null"),
				Objects.requireNonNull(executionPhase, "test phase must not be null"),
				Objects.requireNonNull(attributes, "attributes map must not be null"),
				Objects.requireNonNull(logger, "logger must not be null"),
				executionTraceRecorder,
				LocalDateTime.now(),
				Objects.requireNonNull(executionTaskId, "executionTaskId must not be null"),
				executionToolkit
		);
	}

	private ExperimentElementExecutionContext(
			ExperimentElementImpl experimentElement,
			int indexInParent,
			Optional<Object> result,
			Optional<Function<String, InputStream>> resourceResolver,
			String registryContextName,
			Optional<ExperimentElementExecutionContext> context,
			ExperimentElementExecutionHandlerRegistry registry,
			ExecutionMode executionMode,
			ExecutionPhase executionPhase,
			Map<String, Object> attributes,
			Logger logger,
			ExecutionTraceRecorder executionTraceRecorder,
			LocalDateTime currentTime,
			ExecutionTaskId executionTaskId,
			ExperimentExecutionToolkit executionToolkit
	) {
		this.experimentElement = Objects.requireNonNull(experimentElement, "experimentElement must not be null");
		this.indexInParent = indexInParent;
		this.parentResult = result;
		this.resourceResolver = resourceResolver;
		this.parentContext = context;
		this.registry = Objects.requireNonNull(registry, "item registry must not be null");
		this.executionMode = Objects.requireNonNull(executionMode, "run mode must not be null");
		this.executionPhase = Objects.requireNonNull(executionPhase, "test phase must not be null");
		this.attributes = Objects.requireNonNull(attributes, "attributes map must not be null");
		this.instanceLogger = logger;
		this.currentTime = currentTime;
		this.registryContextName = registryContextName;
		this.executionTaskId = executionTaskId;
		this.executionTraceRecorder = executionTraceRecorder;
		this.executionToolkit = executionToolkit;
	}

	@Override
	public String toString() {
		return String.format(
				"{%s/%s, taskid=%s, indexInParent=%s, executionPhase=%s, executionMode=%s, time=%s, parameters=%s}",
				experimentElement.getDesign().getCategory(),
				experimentElement.getDesign().getName(),
				executionTaskId,
				indexInParent,
				executionPhase,
				executionMode,
				currentTime,
				experimentElement.getParameterValueMap()
		);
	}

	/**
	 * Returns a context for a child experimentElement with a given index, and a result
	 * from the evaluation of this context.
	 *
	 * @param experimentElement the child experimentElement
	 * @param indexInParent     the index of the child experimentElement within the parent
	 * @param result            the result produced by the parent
	 *
	 * @return the new child context
	 */
	public ExperimentElementExecutionContext createChildContext(ExperimentElementImpl experimentElement, int indexInParent,
			Object result) {
		return new ExperimentElementExecutionContextBuilder(this)
				.instance(experimentElement)
				.parent(result, this, indexInParent)
				.build();
	}

	/**
	 * Returns a context like this one, but with the specified test phase,
	 * or this context, if the test phase is already this one's.
	 *
	 * @param executionPhase the test phase
	 *
	 * @return an experimentElement context
	 */
	public ExperimentElementExecutionContext withExecutionPhase(ExecutionPhase executionPhase) {
		return executionPhase.equals(getExecutionPhase())
				? this
				: new ExperimentElementExecutionContextBuilder(this).executionPhase(executionPhase).build();
	}

	/**
	 * Returns the execution-trace recorder.
	 *
	 * @return the execution-trace recorder
	 */
	public Optional<ExecutionTraceRecorder> getExecutionTraceRecorder() {
		return Optional.ofNullable(executionTraceRecorder);
	}

	/**
	 * Returns the task id.
	 *
	 * @return the task id
	 */
	public ExecutionTaskId getExecutionTaskId() {
		return executionTaskId;
	}

	/**
	 * Returns the execution toolkit (if any).
	 *
	 * @return the execution toolkit (if any)
	 */
	public ExperimentExecutionToolkit getExecutionToolkit() {
		return this.executionToolkit;
	}

	public final <U extends ExperimentExecutionToolkit> U getExecutionToolkitAs(
			Class<U> experimentExecutionToolkitSubclass
	) {
		U result = experimentExecutionToolkitSubclass.cast(this.getExecutionToolkit());
		return result;
	}

	/**
	 * @return a calendar with the creation time of the experimentElement context
	 */
	public LocalDateTime getCurrentTime() {
		return currentTime;
	}

	/**
	 * @return the test phase
	 */
	public ExecutionPhase getExecutionPhase() {
		return executionPhase;
	}

	/**
	 * @return the resource resolver
	 */
	public Optional<Function<String, InputStream>> getResourceResolver() {
		return resourceResolver;
	}

	public Optional<String> getRegistryContextName() {
		return Optional.ofNullable(registryContextName);
	}

	/**
	 * @return the index of this experimentElement within its parent
	 */
	public int getIndexInParent() {
		return indexInParent;
	}

	/**
	 * @return the experimentElement being run
	 */
	public ExperimentElementImpl getExperimentElement() {
		return experimentElement;
	}

	/**
	 * Returns the object bound with the specified name in this experimentElement
	 * context, cast as the particular class.
	 *
	 * @param name  the attribute name
	 * @param klass the type of the result
	 * @param <T>   optional result type
	 *
	 * @return the attribute value
	 */
	public <T> Optional<T> getAttribute(String name, Class<T> klass) {
		return Optional.ofNullable(this.attributes.get(name)).map(klass::cast);
	}

	/**
	 * @return an unmodifiable view of the attributes map of this context
	 */
	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(this.attributes);
	}

	/**
	 * @return the result from the parent, or null
	 */
	public Optional<Object> getResult() {
		return parentResult;
	}

	/**
	 * Returns the result, cast to a specific class.
	 *
	 * @param klass the type of the result
	 * @param <T>   the class
	 *
	 * @return the result
	 */
	public <T> T getResult(Class<T> klass) {
		return getResult().map(klass::cast).get();
	}

	/**
	 * @return the context of the parent
	 */
	public Optional<ExperimentElementExecutionContext> getContext() {
		return parentContext;
	}

	/**
	 * @return the registry
	 */
	public ExperimentElementExecutionHandlerRegistry getRegistry() {
		return registry;
	}

	/**
	 * @return the run mode
	 */
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	/**
	 * @return the root context of this experimentElement context
	 */
	public ExperimentElementExecutionContext getRootContext() {
		ExperimentElementExecutionContext context = this;
		while (context.getContext().isPresent()) {
			context = context.getContext().get();
		}
		return context;
	}

	/*
	 * I'm not sure exactly what this is computing.  It seems to be an approximation of the number
	 * of nodes that will be created. But counting only happens until the point where the current
	 * item appears.  So we're counting the number of nodes before this one I think. We're counting some
	 * kind of unique index for the current node (assuming it's a node).
	 *
	 * This is only used as an auxiliary function for ExperimentElementExecutionContextQueryEngine#handleTerminalQuery
	 * in processing the genclassb substitution.  It's used as a component of an IP address.  That means
	 * that the starting at 2 is probably to account for gateway's IP address or something.
	 */

	/**
	 * Returns a "unique integer" for this experimentElement context.  The "unique
	 * integer" is essentially the index of the experimentElement in a depth first
	 * traversal of the nodes starting from the root context of this experimentElement
	 * context, accounting for multiplicity in nodes and hosts.
	 *
	 * @return a "unique integer"
	 */
	public int getUnique() {
		// Save the ID of the experimentElement's parent's experimentElement.
		String thisId = getContext().map(ExperimentElementExecutionContext::getExperimentElement)
				.map(ExperimentElementImpl::getId)
				.map(ExperimentElementId::value)
				.orElse("NoParentID");

		// TODO why does this start at 2? Probably to compensate for the gateway's IP address
		int numNodes = 2;

		for (ExperimentElementImpl child : getRootContext().getExperimentElement().getChildren()) {
			if (Objects.equals(child.getId(), thisId)) {
				return numNodes;
			}

			numNodes += 1;

			int hostCount = getHostMultiplier(child);

			for (ExperimentElementImpl grandchild : child.getChildren()) {
				if ("Node".equals(grandchild.getDesign().getCategory())) { // FIXME: STRING: srogers
					if (Objects.equals(grandchild.getId(), thisId)) {
						return numNodes;
					}
					numNodes += hostCount;
				}
			}
		}
		return numNodes;
	}

	/**
	 * Returns the number of hosts for the current
	 * experimentElement.  If the experimentElement is a Host item,
	 * then this returns the number of hosts it specifies.
	 * Otherwise, returns one so that multiplication for children
	 * is unaffected.
	 *
	 * @param experimentElement the experimentElement
	 *
	 * @return the number of hosts
	 */
	private int getHostMultiplier(ExperimentElementImpl experimentElement) {
		if ("Host".equals(experimentElement.getDesign().getName())) { // FIXME: STRING: srogers
			return experimentElement.getParameterValueMap().get("name").split(",").length; // FIXME: STRING: srogers
		}
		else {
			return 1;
		}
	}

	/**
	 * Returns the value of a named parameter on an experimentElement with a specified
	 * ID.
	 *
	 * @param id    the id
	 * @param param the name of the parameter
	 *
	 * @return the value
	 */
	public String getIDvar(String id, String param) {
		List<ExperimentElementImpl> children = getRootContext().getExperimentElement()
				.getChildren();
		return searchForIDandParam(id, param, "", children);
	}

	/**
	 * Returns the name of the network specified by a portgroup item with
	 * a specified ID.
	 *
	 * @param portgroupID the ID of the portgroup experimentElement
	 *
	 * @return the name of the network
	 */
	public String getNetwork(String portgroupID) {
		String result = "ID Not Found";
		ExperimentElementExecutionContext context = this;
		while (context.getContext().isPresent()) {
			context = context.getContext().get();
		}
		List<ExperimentElementImpl> children = context.getExperimentElement().getChildren();
		Iterator<ExperimentElementImpl> itr = children.iterator();
		while (itr.hasNext()) {
			ExperimentElementImpl child = itr.next();
			List<ExperimentElementImpl> subchildren = child.getChildren();
			Iterator<ExperimentElementImpl> subitr = subchildren.iterator();
			while (subitr.hasNext()) {
				ExperimentElementImpl subchild = subitr.next();
				if (Objects.equals(subchild.getId(), portgroupID)) {
					return String.format(
							NETWORK_NAME_FORMAT,
							child.getParameterValueMap().get("name"), // FIXME: STRING: srogers // BUG: srogers: it is named something else now
							child.getId(),
							subchild.getParameterValueMap().get("name"), // FIXME: STRING: srogers // BUG: srogers: it is named something else now
							subchild.getId()
					); // FIXME: extract string construction into a dedicated method
				}
			}
		}
		return result;
	}

	private String searchForIDandParam(String id, String param, String initialValue, List<ExperimentElementImpl> children) {
		String result = initialValue;
		Iterator<ExperimentElementImpl> itr = children.iterator();
		while (itr.hasNext()) {
			ExperimentElementImpl child = itr.next();
			if (Objects.equals(child.getId(), id)) {
				result = child.getParameterValueMap().get(param);
			}
			else if (! child.getChildren().isEmpty()) {
				result = searchForIDandParam(id, param, result, child.getChildren());
			}
		}
		return result;
	}

	/**
	 * Returns a StrSubstitutor that can replace values based on the values
	 * present within an experimentElement context.   The format of the strings that can
	 * be substituted is:
	 * <p>
	 * <pre>queryString [ ';' formatString ]</pre>
	 *
	 * The query string is used to retrieve a value (via {@link
	 * ExperimentElementExecutionContextQueryEngine#query(ExperimentElementExecutionContext, String)}). The value
	 * is then formatted according to the format string, or <code>%s</code> if
	 * no format string is provided.
	 *
	 * @return a string substitutor for the context
	 */
	public StrSubstitutor getContextSubstitutor() {
		return new StrSubstitutor(new StrLookup<Object>()
		{
			@Override
			public String lookup(String key) {
				List<String> parts = Arrays.asList(key.split(";"));
				int size = parts.size();
				isTrue(
						1 <= size && size <= 2, "Lookup string should have at most two part separated by ';', but was %s.",
						key
				);
				String query = parts.get(0);
				String format = size == 2 ? parts.get(1) : "%s";
				try {
					Object value = ExperimentElementExecutionContextQueryEngine
							.query(ExperimentElementExecutionContext.this, query);
					return String.format(format, value);
				}
				catch (IllegalArgumentException e) {
					logger.error("could not execute experimentElement context query", e);
					return null;
				}
			}
		});
	}

	/**
	 * Return the result of replacing parameters within
	 * text with contextual values.  Permissible variables
	 * are described in {@link #getContextSubstitutor()}.
	 *
	 * @param text the text on which to perform substitution
	 *
	 * @return a string like text, but with substitutions performed
	 */
	public String substitute(String text) {
		StrSubstitutor ss = getContextSubstitutor();
		return ss.replace(text);
	}

	/**
	 * Returns an optional containing the value of the experimentElement parameter (that
	 * is, with substitution applied) with the specified name.
	 *
	 * @param name the name of the parameter
	 *
	 * @return an optional containing the value of the parameter
	 */
	public Optional<String> getOptionalInstanceParameter(String name) {
		return getExperimentElement().getParameter(name).map(this::substitute);
	}

	/**
	 * Returns an optional containing a mapping function applied to the the
	 * value of an experimentElement parameter (that is, with substitution applied).
	 *
	 * @param name   the name of the experimentElement parameter
	 * @param mapper the mapping function
	 * @param <T>    optional result type
	 *
	 * @return an optional containing the result
	 *
	 * @deprecated Use {@link #getOptionalInstanceParameter(String)} and {@link Optional#map(Function)} instead.
	 */
	@Deprecated
	public <T> Optional<T> getOptionalInstanceParameter(String name, Function<String, T> mapper) {
		return getOptionalInstanceParameter(name).map(mapper);
	}

	/**
	 * Return the value of this experimentElement's parameter, but with
	 * parameter substitution applied, or null if the experimentElement
	 * has no such parameter.
	 *
	 * @param name the name of the parameter
	 *
	 * @return the value of the parameter, after substitution is applied.
	 */
	public String getInstanceParameter(String name) {
		return getOptionalInstanceParameter(name).orElse(null);
	}

	/**
	 * Returns the value of the named parameter, throwing an
	 * exception if it is not present.
	 *
	 * @param name the name of the parameter
	 *
	 * @return the value of the parameter
	 */
	public String getRequiredInstanceParameter(String name) {
		return getOptionalInstanceParameter(name)
				.orElseThrow(() -> new NoSuchElementException("no parameter named '" + name + "'."));
	}

	/**
	 * Returns the value of applying a mapping function to
	 * this context's experimentElement's parameter (as returned by
	 * {@link #getInstanceParameter(String)}).
	 *
	 * @param name   the name of the parameter
	 * @param mapper the mapping function
	 * @param <T>    result type
	 *
	 * @return the result of the mapper applied to the value
	 */
	public <T> T getInstanceParameter(String name, Function<String, T> mapper) {
		return getOptionalInstanceParameter(name).map(mapper).orElse(null);
	}

	/**
	 * Gets the string value of a parameter, or "NoValueFound" if
	 * the parameter has no value.
	 *
	 * @param param the name of the a parameter.
	 *
	 * @return the value of the parameter, or "NoValueFound"
	 */
	public String getStringValue(String param) {
		return getOptionalInstanceParameter(param).orElse("NoValueFound");
	}

	/**
	 * Returns a directory path based on the proeprties of this experimentElement
	 * context.  The directory is a relative directory of the form
	 * <code>0000/category-name-id</code> the category-name-id is built from
	 * the category and name of experimentElement's item, and the id of the experimentElement.
	 * <p>
	 * <p>Note: the 0000 directory is a remnant of the defunct multiplexing.
	 *
	 * @return a directory path
	 */
	private Path getLocalDirectory() {
		String id = experimentElement.getId().value();
		String name = experimentElement.getDesign().getName();
		String category = experimentElement.getDesign().getCategory();
		String nodePortion = String.format("%s-%s-%s", category, name, id); // FIXME: STRING: srogers
		return Paths.get("0000", nodePortion); // FIXME: STRING: srogers
	}

	/**
	 * Returns a directory path based on this experimentElement context
	 * and its ancestors.
	 *
	 * @return the directory path
	 */
	public Path getDirectory() {
		Path p = Paths.get("");
		Optional<ExperimentElementExecutionContext> context = Optional.of(this);
		while (context.isPresent()) {
			p = context.get().getLocalDirectory().resolve(p);
			context = context.get().getContext();
		}
		logger.trace("getDirectory() => {}", p);
		return p;
	}

	/**
	 * Returns a logger that can log the progress of the execution of this
	 * context.
	 *
	 * @return a logger
	 */
	public Logger getLogger() {
		return instanceLogger;
	}
}
