package com.siegetechnologies.cqf.core.experiment.design.util;

/*-
 * #%L
 * astam-cqf-ce-core
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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A parameter binding for an experiment element.
 * A parameter binding assigns a value to a particular parameter.
 */
public class ParameterBindingImpl extends ParameterBase {

	/*
	 * TODO: These should really go somewhere else, or be taken out of here
	 * completely. But for now they are the best solution to a global CQF_TAGS
	 * default.
	 */
	public static final String TAG_PARAMETER_NAME = "CQF_TAGS";
	public static final String TAG_PARAMETER_DEFAULT = "${cqf.name}";

	/**/

	protected String value;

	/**
	 * Creates a new object of this type.
	 */
	protected ParameterBindingImpl() {
		this(null, null, null, null);
	}

	/**
	 * Creates a new object of this type.
	 *
	 * @param label the display label for this parameter
	 * @param name the name of this parameter
	 * @param type the type of this parameter's value
	 * @param value the value of this parameter
	 */
	@JsonCreator
	protected ParameterBindingImpl
	(
			@JsonProperty("label") String label,
			@JsonProperty("name") String name,
			@JsonProperty("type") String type,
			@JsonProperty("value") String value
	) {
		super(label, name, type);

		this.value = value;
	}

	@Override
	protected ParameterBindingImpl createInstance() {
		return new ParameterBindingImpl();
	}

	@Override
	public String toString() {
		return String.format("{name: %s; value: %s}", name, value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ParameterBindingImpl that = (ParameterBindingImpl) o;
		return super.equals(that) &&
				Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() +
				Objects.hash(value);
	}

	/**/

	/**
	 * Returns the value of this parameter.
	 *
	 * @return the value of this parameter
	 */
	public String getValue() { return value; }

	public void setValue(String value) { this.value = value; }


	/**/

	public static class Builder extends BuilderBase<ParameterBindingImpl, Builder> {

		public Builder() {
			super(new ParameterBindingImpl());
		}

		/**/

		public Builder setValue(String value) {
			result.setValue(value);
			return this;
		}

	}

	/**/

	/**
	 * Updates this object with non-null property values from each successive other object.
	 *
	 * @param others other object(s)
	 * @return this object
	 */
	public ParameterBindingImpl updateFrom(ParameterBindingImpl... others) {
		super.updateFrom(others);

		for (ParameterBindingImpl other : others) {
			if (other == null) {
				continue;
			}
			if (other.value != null) {
				this.value = other.value;
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
	public ParameterBindingImpl copyAndUpdateWithDefaultsFrom(ParameterBindingImpl other) {
		ParameterBindingImpl result = createInstance();
		return result.updateFrom(other, this);
	}

}
