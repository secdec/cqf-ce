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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * An implementation base class for parameters and parameter bindings.
 */
public class ParameterBase {

	protected String label;

	@JsonProperty(required=true)
	protected String name;

	protected String type;

	/**
	 * Creates an object of this type.
	 */
	protected ParameterBase() {
		this(null, null, null);
	}

	/**
	 * Creates an object of this type.
	 *
	 * @param label the display label for this parameter
	 * @param name the name of this parameter
	 * @param type the type of this parameter's value
	 */
	protected ParameterBase
	(
			@JsonProperty("label") String label,
			@JsonProperty("name") String name,
			@JsonProperty("type") String type
	) {
		this.label = label;
		this.name = name;
		this.type = type;
	}

	/**
	 * Creates a new instance of this type using the default constructor.
	 *
	 * @return the new instance
	 */
	protected ParameterBase createInstance() {
		return new ParameterBase();
	}

	@Override
	public String toString() {
		return String.format("{name: %s}", name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ParameterBase that = (ParameterBase) o;
		return Objects.equals(label, that.label) &&
				Objects.equals(name, that.name) &&
				Objects.equals(type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(label, name, type);
	}

	/**/

	/**
	 * Returns the display label for this parameter.
	 * This should be a value suitable for an HTML input form.
	 *
	 * @return the display label for this parameter
	 */
	public String getLabel() { return label; }

	public void setLabel(String value) { this.label = value; }

	/**
	 * Returns the name of this parameter.
	 *
	 * @return the name of this parameter
	 */
	public String getName() { return name; }

	public void setName(String value) { this.name = value; }

	/**
	 * Returns the type of this parameter.
	 * This should be a value suitable for an HTML input form.
	 *
	 * @return the type of this parameter
	 */
	public String getType() { return type; }

	public void setType(String value) {
		checkValidType(value);
		this.type = value;
	}

	protected void checkValidType(String value) throws IllegalArgumentException {
		if (! isValidType(value)) {
			String m = String.format("invalid parameter type: %s", value);
			throw new IllegalArgumentException(m);
		}
	}

	protected boolean isValidType(String value) {

		return (value == null || validTypes.contains(value));
	}

	private final static Set<String> validTypes = new HashSet<>(Arrays.asList(
			"checkbox", "integer", "number", "string")
	);

	public static class Builder extends BuilderBase<ParameterBase, Builder> {

		public Builder() {
			super(new ParameterBase());
		}

	}

	/**/

	/**
	 * An implementation base class for builders of parameters and parameter bindings.
	 *
	 * @param <R> type of the object to be built
	 * @param <B> type of the builder itself
	 */
	public static class BuilderBase<R extends ParameterBase, B extends BuilderBase> {

		protected final R result;

		public BuilderBase(R result) {
			this.result = result;
		}

		public R build() {
			return result;
		}

		/**/

		@SuppressWarnings("unchecked")
		public B setLabel(String value) {
			result.setLabel(value);
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B setName(String value) {
			result.setName(value);
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B setType(String value) {
			result.setType(value);
			return (B) this;
		}

	}

	/**/

	/**
	 * Updates this object with non-null property values from each successive other object.
	 *
	 * @param others other object(s)
	 * @return this object
	 */
	protected ParameterBase updateFrom(ParameterBase... others) {
		for (ParameterBase other : others) {
			if (other == null) {
				continue;
			}
			if (other.label != null) {
				this.label = other.label;
			}
			if (other.name != null) {
				this.name = other.name;
			}
			if (other.type != null) {
				this.type = other.type;
			}
		}
		return this;
	}

}
