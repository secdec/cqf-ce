/**
 * Copyright (c) 2016 Siege Technologies.
 */
package com.siegetechnologies.cqf.core.experiment;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An element of an experiment. An experiment element couples a design element
 * with a parameter map, and because elements can be structured hierarchically, an
 * optional parent and a list of children. Experiment elements are primarily immutable;
 * the design element and parent cannot be changed, but new values can be added to the
 * parameter map, and new children can be added to the children list.
 */
public class ExperimentElementImpl implements Comparable<ExperimentElementImpl>
{
	private static final Logger logger = LoggerFactory.getLogger(ExperimentElementImpl.class);

	private final ExperimentDesignElementImpl designElement;
	private final Map<String, String> parameterValueMap;
	private final Optional<ExperimentElementImpl> parent;
	private final List<ExperimentElementImpl> children;

	private boolean executable = true;
	private boolean synthetic = false;

	private ExperimentElementId id;
	private int executionIndex;

	/**
	 * Creates an instance with {@link #ExperimentElementImpl(ExperimentDesignElementImpl, ExperimentElementImpl)}, passing null
	 * as the parent argument.
	 *
	 * @param designElement the design element
	 */
	public ExperimentElementImpl(ExperimentDesignElementImpl designElement) {
		this(designElement, null);
	}

	/**
	 * Creates an instance with
	 * {@link #ExperimentElementImpl(ExperimentDesignElementImpl, ExperimentElementImpl, ExperimentElementId, Integer)} passing null as the
	 * ID argument (so that the ID is randomly generated).
	 *
	 * @param designElement the design element
	 * @param parent the parent collection
	 */
	public ExperimentElementImpl(ExperimentDesignElementImpl designElement, ExperimentElementImpl parent) {
		this(designElement, parent, null, null);
	}

	/**
	 * Creates an instance with the specified design element, uri, and id, and belonging
	 * to the parent.
	 *
	 * @param designElement the design element
	 * @param parent the instance's parent
	 * @param id the instance's id or null (in which case, randomly generate
	 *            a ID)
	 * @param executionIndex the execution index, or null, in which case the design element's execution
	 *     index will be used.
	 *
	 * @see ExperimentDesignElementImpl#getDefaultExecutionIndex()
	 */
	public ExperimentElementImpl(
			ExperimentDesignElementImpl designElement,
			ExperimentElementImpl parent,
			ExperimentElementId id,
			Integer executionIndex
	) {
		logger.trace("ExperimentElement(designElement={},parent={},id={})", designElement, parent, id);
		this.designElement = designElement;
		this.id = (id != null) ? id : new ExperimentElementId();
		this.executionIndex = executionIndex != null ? executionIndex : designElement.getDefaultExecutionIndex();
		this.parent = Optional.ofNullable(parent);
		this.parameterValueMap = new HashMap<>();
		initializeParameterValueMap();
		this.children = new ArrayList<>();
		initializeChildren();
	}

	protected void initializeParameterValueMap() {
		parameterValueMap.clear();
		parameterValueMap.putAll(createInitialParameterValueMap(getDesign().getParameters()));
	}

	protected void initializeChildren() {
		for (ExperimentDesignElementImpl childDesign : designElement.getChildren().values()) {
			ExperimentElementImpl child = new ExperimentElementImpl(childDesign, this, null, null);
			this.children.add(child);
		}
	}

	/**/

	@Override
	public int hashCode() {

		return this.id.hashCode();
	}

	@Override
	public int compareTo(ExperimentElementImpl that) {

		int result = this.id.compareTo(that.id);
		assert result != 0 || this == that;
		//^-- each experiment element is unique
		return result;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (o instanceof ExperimentElementImpl) {

			ExperimentElementImpl that = (ExperimentElementImpl) o;

			boolean result = this.id.equals(that.id);
			assert result != true || this == that;
			//^-- each experiment element is unique
			return result;
		}
		return false;
	}

	@Override
	public String toString() {

		return this.id.toString();
	}

	/**/

	/**
	 * Returns a map from parameter names to initial parameter values, where the
	 * the initial value is the (default) value of the parameter.
	 *
	 * @param parameters the parameters of the corresponding design element
	 * @return the map
	 */
	private static Map<String, String> createInitialParameterValueMap(List<ParameterImpl> parameters) {
		Map<String, String> result = new HashMap<>();

		for (ParameterImpl p : parameters) {
			String n = Objects.requireNonNull(p.getName(), "parameter name must not be null");
			String v = getValueOfParameter(p);
			result.put(n, v);
		}
		logger.trace("createInitialParameterValueMap({}) => {}", parameters, result);
		return result;
	}

	private static String getValueOfParameter(ParameterImpl p) {
		String result = null;

//      FIXME: srogers: can we safely delete this logic?
//		if (result == null || result.isEmpty()) {
//	        result = p.getValue();
//		}
		if (result == null || result.isEmpty()) {
			result = p.getDefaultValue();
		}
		return result;
	}

	public boolean isExecutable() {
		return this.executable;
	}

	public void setExecutable(boolean value) {
		this.executable = value;
	}

	public boolean isSynthetic() {
		return this.synthetic;
	}

	public void setSynthetic(boolean value) {
		this.synthetic = value;
	}

	/**
	 * Returns the list of children of this instance. Children may be added to
	 * or removed from this list.
	 *
	 * @return a list of the children of this instance
	 */
	public List<ExperimentElementImpl> getChildren() {
		return children;
	}

	/**
	 * Move the child at a position to another position. No action is performed
	 * if <code>from</code> is equal to <code>to</code>, and no error is
	 * signaled, even if the index is not a valid child index.
	 *
	 * @param from the original position of the child
	 * @param to the new position of the child
	 */
	public void reorderChild(int from, int to) {
		if (from != to) {
			ExperimentElementImpl child = getChildren().remove(from);
			getChildren().add(to, child);
		}
	}

	/**
	 * Returns the execution index of this instance.
	 *
	 * @return the execution index of this instance
	 */
	public int getExecutionIndex() {
		return executionIndex;
	}

	/**
	 * Sets the execution index of this instance.
	 *
	 * @param executionIndex the new execution order index
	 */
	protected void setExecutionIndex(int executionIndex) {
		this.executionIndex = executionIndex;
	}

	/**
	 * Returns the unique ID of this experiment element.
	 *
	 * @return the unique ID of this experiment element
	 */
	@JsonUnwrapped
	public ExperimentElementId getId() {

		if (this.id == null) {
			throw new IllegalStateException("ID has not been set");
		}
		return this.id;
	}

	/**
	 * Sets the unique ID of this experiment element. For the most part, the ID is
	 * automatically generated and should never need to be set. However,
	 * sometimes it is necessary to update an ID to match an pre-existing one.
	 *
	 * @param value the new id of this instance
	 */
	protected void setId(ExperimentElementId value) {

		if (this.id != null) {
			throw new IllegalStateException("ID has already been set");
		}
		this.id = Objects.requireNonNull(value, "ID must not be null");
	}

	/**
	 * Returns the design element of this instance.
	 *
	 * @return the design element of this instance
	 */
	public ExperimentDesignElementImpl getDesign() {
		return this.designElement;
	}

	/**
	 * Returns this designElement's instance set.
	 *
	 * @return this designElement's instance set
	 */
	@JsonIgnore
	public Optional<ExperimentElementImpl> getParent() {
		return this.parent;
	}

	/**
	 * Returns the parameter value map of this instance. Entries in the map can be
	 * added or removed from this map.
	 *
	 * @return a map of the instance's parameter values
	 */
	public Map<String, String> getParameterValueMap() {
		return this.parameterValueMap;
	}

	/**
	 * Looks up the given parameter value in the instance.  If the instance does not
	 * have a value set, then it will fall back to the value set in the design element.
	 * NOTE: This returns the string value of the parameter.  It is up to the
	 * caller to transform it into the final data type.
	 *
	 * @param name Name of the parameter to
	 * @return String value of the parameter if it exists
	 */
	public Optional<String> getParameter(String name) {
		Optional<String> ret = Optional.empty();
		Map<String, String> params = getParameterValueMap();

		if (params == null) {
			return ret;
		}

		if (params.containsKey(name)) {
			return Optional.ofNullable(params.get(name));
		}

		Optional<ParameterImpl> iParam = getDesign().getParameters()
				.stream()
				.filter(p -> Objects.equals(name, p.getName()))
				.findAny();

		return iParam.map(p -> getValueOfParameter(p));
	}

	/**
	 * Replaces the existing parameters with new parameters. This clears the
	 * existing entries from the parameters map and adds new entries to it; the
	 * actual map instance remains the same.
	 *
	 * @param newParameters the new parameters
	 */
	public void replaceParameters(Map<String, String> newParameters) {
		getParameterValueMap().clear();
		getParameterValueMap().putAll(newParameters);
	}

	/**
	 * Returns a value for the parameter.  This has complex behavior.
	 *
	 * @deprecated introduced as a workaround for retrieving script durations, should be removed
	 *
	 * @param name the name of the parameter
	 * @param mapper a mapping function to convert the string value
	 * @return the resulting value
	 */
	@Deprecated
	public <T> Optional<T> getParameter(String name, Function<String, T> mapper) {

		Map<String, String> pars = getParameterValueMap();
		if (pars.containsKey(name)) {
			String assignedValue = pars.get(name);
			try {
				T t = mapper.apply(assignedValue);
				return Optional.of(t);
			}
			catch (RuntimeException e) {
				logger.warn(
						"Error applying mapper to assigned value \"{}\" of parameter \"{}\" on instance {}.",
						assignedValue,
						name,
						getId(),
						e
				);
			}
		}

		Optional<ParameterImpl> oPar = getDesign().getParameters().stream()
				.filter(p -> Objects.equals(name, p.getName()))
				.findAny();

		if (oPar.isPresent()) {
			ParameterImpl par = oPar.get();
			String fixedValue = getValueOfParameter(par);
			if (fixedValue != null) {
				try {
					T t = mapper.apply(fixedValue);
					return Optional.of(t);
				}
				catch (RuntimeException e) {
					logger.warn(
							"Error applying mapper to fixed designElement value \"{}\" of parameter \"{}\" on instance {} of designElement {}.",
							fixedValue, name, getId(), designElement.getId(), e
					);
				}
			}

			String defaultValue = par.getDefaultValue();
			if (defaultValue != null) {
				try {
					T t = mapper.apply(defaultValue);
					return Optional.of(t);
				}
				catch (RuntimeException e) {
					logger.warn(
							"Error applying mapper to default designElement value \"{}\" of parameter \"{}\" on instance {} of designElement {}.",
							defaultValue, name, getId(), designElement.getId(), e
					);
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Clear this instance's parameter map and add all the entries from the
	 * provided map.
	 *
	 * @param parameters
	 *            the new parameters map
	 */
	public void putParameters(Map<String, String> parameters) {
		this.parameterValueMap.clear();
		this.parameterValueMap.putAll(parameters);
	}

	/**
	 * Returns this experiment element and all of its children (top to bottom), as a stream.
	 *
	 * @return the stream
	 */
	public Stream<ExperimentElementImpl> stream() {
		return Stream.concat(
				Stream.of(this),
				this.getChildren().stream()
						.flatMap(ExperimentElementImpl::stream)
		);
	}

	/**
	 * Walks this experiment element and all of its children (top to bottom),
	 * applying <code>visitor</code> to each in turn.
	 * <p/>
	 * This experiment element's children are computed *after* the element
	 * itself has been visited. Thus, an experiment element's set of children
	 * can be edited during its visit, and the edits will be visible to the
	 * ongoing traversal.
	 *
	 * @param visitor
	 */
	public void walk(Consumer<ExperimentElementImpl> visitor) {
		visitor.accept(this);
		this.getChildren().forEach(x -> x.walk(visitor));
	}

	/**
	 * Convenience method to log sizeOf details
	 */
	public void sizeOfPrint() {
		logger.debug("id: " + id.value().length());
		logger.debug("execIndex: " + Integer.SIZE / Byte.SIZE);
		logger.debug("instance params size: "
				+ parameterValueMap.entrySet().stream()
				.mapToInt(entry -> entry.getKey().length() + entry.getValue().length())
				.sum());
	}

	/**
	 * Validates an experiment element prior to execution.
	 *
	 * @author taylorj
	 */
	public static class Validator
	{

		private static final Logger logger = LoggerFactory.getLogger(Validator.class);

		static final Collection<String> FORBIDDEN_SUBSTITUTION_PARAMETERS =
				Arrays.asList("indexInMultiplex", "looprange");

		/**
		 * Checks the experimentElement and its descendants for multiplicity issues and
		 * returns a report documenting them.
		 *
		 * @param experimentElement the experimentElement
		 * @return the report
		 */
		public Report validate(ExperimentElementImpl experimentElement) {

			Report validationReport = new Report();

			experimentElement.stream()
					.peek(i -> checkSubstitutionParameters(i, validationReport))
					.forEach(i -> checkMultiplicity(i, validationReport));

			return validationReport;
		}

		/**
		 * A simple validation report with lists of warnings and errors.
		 *
		 * @author taylorj
		 */
		public static class Report
		{
			List<String> errors = new ArrayList<>();
			List<String> warnings = new ArrayList<>();

			public List<String> getErrors() { return this.errors; }

			public List<String> getWarnings() { return this.warnings; }

		}

		/**
		 * Logs a warning message.
		 *
		 * @param message
		 */
		public void warn(String message) {
			logger.warn("{}", message);
		}

		/**
		 * Logs an error message.
		 *
		 * @param message
		 */
		public void error(String message) {
			logger.error("{}", message);
		}

		/**
		 * Checks the parameters of an experimentElement for null values and non-null values
		 * containing forbidden substitution parameters.
		 *
		 * @param experimentElement the experimentElement
		 * @param report the report
		 */
		void checkSubstitutionParameters(ExperimentElementImpl experimentElement, Report report) {
			String elementId = experimentElement.getId().value();
			String elementDesignId = experimentElement.getDesign().getId().value();
			Map<String, String> params = experimentElement.getParameterValueMap();
			for (Map.Entry<String, String> e : params.entrySet()) {
				String key = e.getKey();
				String value = e.getValue();
				checkSubstitutionParameter(elementId, elementDesignId, key, value, report);
			}
		}

		/**
		 * Checks whether the value for a specified key is null or is non-null and
		 * contains forbidden substitution parameters. If a value is null, then a
		 * warning is added to the report. If a value contains a forbidden
		 * parameter, then an error is added to the report.
		 *
		 * @param elementId
		 * @param designElementName
		 * @param key
		 * @param value
		 * @param report
		 */
		void checkSubstitutionParameter(
				String elementId,
				String designElementName,
				String key,
				String value,
				Report report
		) {
			if (value == null) {
				String msg = String.format(
						"Value of parameter \"%s\" on experiment element %s (%s) is null.",
						key, elementId, designElementName
				);
				report.getWarnings().add(msg);
			}
			else {
				for (String forbidden : FORBIDDEN_SUBSTITUTION_PARAMETERS) {
					if (value.contains(forbidden)) {
						String msg = String.format(
								"Value \"%s\" of parameter \"%s\" on experiment element %s (%s) "
										+ " contains forbidden substitution parameter \"%s\".",
								value, key, elementId, designElementName, forbidden
						);
						report.getErrors().add(msg);
					}
				}
			}
		}

		/**
		 * Checks for bad conditions in the multiplicity of an experimentElement and updates
		 * a report with warnings or errors as appropriate.
		 *
		 * @param experimentElement the experimentElement
		 * @param report the report
		 */
		void checkMultiplicity(ExperimentElementImpl experimentElement, Report report) {
			String elementId = experimentElement.getId().value();
			String elementDesignId = experimentElement.getDesign().getId().value();
			Optional<String> oMultiplicity = experimentElement.getParameter("multiplicity");
			if (oMultiplicity.isPresent()) {
				String sMultiplicity = oMultiplicity.get();
				try {
					int multiplicity = Integer.parseInt(sMultiplicity);
					if (multiplicity == 1) {
						String msg = String.format(
								"Multiplicity of 1 present on experiment element %s (%s).",
								elementId, elementDesignId
						);
						report.getWarnings()
								.add(msg);
					}
					else {
						String msg = String.format(
								"Multiplicity %d present on experiment element %s (%s).",
								multiplicity, elementId, elementDesignId
						);
						report.getErrors().add(msg);
					}
				}
				catch (NumberFormatException e) {
					String msg = String.format("Malformed multiplicity \"%s\" present on experiment element %s (%s).",
							sMultiplicity, elementId, elementDesignId
					);
					report.getErrors().add(msg);
				}
			}
		}

	}
}
