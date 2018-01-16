package com.siegetechnologies.cqf.core.experiment.design;

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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;

import java.util.Optional;

/**
 * Unique ID of an experiment design element.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentDesignElementId implements Comparable<ExperimentDesignElementId>
{
    public static final String VALUE_PREFIX = "com.siegetechnologies.cqf.design.item"; // FIXME: STRING: srogers

    private String value;

    public ExperimentDesignElementId(String value)
    {
        initValue(value);
    }

    public ExperimentDesignElementId(String name, String category)
    {
        this(name, category, null);
    }

	@JsonCreator
	public ExperimentDesignElementId(@JsonProperty("name") String name,
	                                 @JsonProperty("category") String category,
									 @JsonProperty("variant") String variant)
	{
		initValue(valueFrom(name, category, variant));
	}

	protected void initValue(String value)
	{
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		this.value = value;
	}

	protected static String valueFrom(String name, String category, String variant)
	{
		Optional<String> canonicalName     = Optional.ofNullable(canonicalDottedNameComponent(name));
		Optional<String> canonicalCategory = Optional.ofNullable(canonicalDottedNameComponent(category));
		Optional<String> canonicalVariant  = Optional.ofNullable(canonicalDottedNameComponent(variant));

		StringBuilder resultBuilder = new StringBuilder(VALUE_PREFIX);

		resultBuilder.append(".").append(canonicalCategory.orElse("unknown_category")); // FIXME: STRING: srogers
		resultBuilder.append(".").append(canonicalName.orElse("unknown_name")); // FIXME: STRING: srogers
		canonicalVariant.ifPresent( v -> resultBuilder.append(".").append(v) );

		return resultBuilder.toString();
	}

	protected static String canonicalDottedNameComponent(String x) {

		String result = x;

		if (result != null) {
			result = result.trim();

			if (result.length() == 0) {
				result = null;
			}
			else {
				result = result.replace(' ', '_').replace('.','-').toLowerCase();

				if (result.contains(".")) {
					throw new IllegalArgumentException("malformed dotted-name component");
				}
				//^-- FIXME: srogers: add further checks/constraints on dotted-name components
			}
		}

		return result;
	}
	//^-- TODO: REVIEW: srogers: add unit tests for each constructor
	//^-- TODO: REVIEW: srogers: hoist canonicalDottedNameComponent() into a utility class

	/**/

    public static ExperimentDesignElementId of(ExperimentDesignElementImpl x)
    {
        return new ExperimentDesignElementId(x.getName(), x.getCategory(), x.getVariantName());
    }

    @Deprecated
    public static ExperimentDesignElementId of(ExperimentDesignElementSpec x)
    {
        return new ExperimentDesignElementId(x.getName(), x.getCategory());
    }

    @Deprecated
    public static ExperimentDesignElementId of(String name, String category)
    {
        return new ExperimentDesignElementId(name, category);
    }

    @Deprecated
    public static ExperimentDesignElementId of(String name, String category, String variant)
    {
        return new ExperimentDesignElementId(name, category, variant);
    }

	/**/

    @Override
    public int compareTo(ExperimentDesignElementId that) {

        return this.value.compareTo(that.value);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (o instanceof ExperimentDesignElementId) {

            ExperimentDesignElementId that = (ExperimentDesignElementId) o;

            return this.value.equals(that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {

        return this.value.hashCode();
    }

    @Override
    public String toString() {

        return this.value.toString();
    }

    /**/

	public String value()
	{
		return value;
	}

	/**/

}
