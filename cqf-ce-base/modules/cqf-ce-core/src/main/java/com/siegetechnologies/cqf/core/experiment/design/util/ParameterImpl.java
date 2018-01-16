package com.siegetechnologies.cqf.core.experiment.design.util;

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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * A parameter of an experiment design element.
 */
@JsonInclude(value=Include.NON_NULL)
@JsonIgnoreProperties(value={"documentation"})
public class ParameterImpl extends ParameterBase {

	protected boolean required;

	protected String defaultValue;
	protected List<String> acceptableValues;

	/**
	 * Creates a new object of this type.
	 */
	ParameterImpl() {
		this(null, null, null, false, null, null);
	}

	/**
	 * Creates a new object of this type.
	 *
	 * @param label            the display label for this parameter
	 * @param name             the name of this parameter
	 * @param type             the type of this parameter's value
	 * @param required         whether this parameter is required
	 * @param defaultValue     the default value of this parameter
	 * @param acceptableValues the list of possible values for this parameter
	 */
	@JsonCreator
	protected ParameterImpl
	(
			@JsonProperty("label") String label,
			@JsonProperty("name") String name,
			@JsonProperty("type") String type,
			@JsonProperty("required") boolean required,
			@JsonProperty("defaultValue") String defaultValue,
			@JsonProperty("list"/*FIXME: srogers: change to acceptableValues*/) List<String> acceptableValues
	) {
		super(label, name, type);
		this.required = required;
		this.defaultValue = defaultValue;
		this.acceptableValues = acceptableValues;
	}

	@Override
	protected ParameterImpl createInstance() {
		return new ParameterImpl();
	}

	@Override
	public String toString() {
		return String.format("{name: %s; defaultValue: %s}", name, defaultValue);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ParameterImpl that = (ParameterImpl) o;
		return super.equals(that) &&
				Objects.equals(required, that.required) &&
				Objects.equals(defaultValue, that.defaultValue) &&
				Objects.equals(acceptableValues, that.acceptableValues);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() +
				Objects.hash(required, defaultValue, acceptableValues);
	}

	/**/

	/**
	 * Returns whether a value for this parameter must be specified.
	 *
	 * @return whether a value for this parameter must be specified
	 */
	public boolean isRequired() { return required; }

	public void setRequired(boolean value) { this.required = value; }

	/**
	 * Returns the default value of this parameter.
	 *
	 * @return the default value of this parameter
	 */
	public String getDefaultValue() { return defaultValue; }

	public void setDefaultValue(String value) { this.defaultValue = value; }

	public boolean hasDefaultValue() { return defaultValue != null; }

	/**
	 * Returns the list of possible values for this parameter.
	 *
	 * @return the list of possible values for this parameter
	 */
	public List<String> getAcceptableValues() { return acceptableValues; }

	public void setAcceptableValues(List<String> value) { this.acceptableValues = value; }

	/**/

	public static class Builder extends BuilderBase<ParameterImpl, Builder> {

		public Builder() {
			super(new ParameterImpl());
		}

		/**/

		public Builder setRequired(boolean value) {
			result.setRequired(value);
			return (Builder) this;
		}

		public Builder setDefaultValue(String value) {
			result.setDefaultValue(value);
			return (Builder) this;
		}

		public Builder setAcceptableValues(List<String> value) {
			result.setAcceptableValues(value);
			return (Builder) this;
		}

	}

	/**/

	/**
	 * Updates this object with non-null property values from each successive other object.
	 *
	 * @param others other object(s)
	 * @return this object
	 */
	public ParameterImpl updateFrom(ParameterImpl... others) {
		super.updateFrom(others);

		for (ParameterImpl other : others) {
			if (other == null) {
				continue;
			}
			this.required = other.required;

			if (other.defaultValue != null) {
				this.defaultValue = other.defaultValue;
			}
			if (other.acceptableValues != null) {
				this.acceptableValues = other.acceptableValues;
			}
		}
		return this;
	}

	/**
	 * Returns a new object like this one, but with its null property values
	 * replaced with the corresponding values of another object.
	 *
	 * @param other another object
	 * @return the new object
	 */
	public ParameterImpl copyAndUpdateWithDefaultsFrom(ParameterImpl other) {
		ParameterImpl result = createInstance();
		return result.updateFrom(other, this);
	}

}
