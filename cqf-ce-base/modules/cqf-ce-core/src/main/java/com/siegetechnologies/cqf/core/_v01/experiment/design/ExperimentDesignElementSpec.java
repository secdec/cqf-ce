package com.siegetechnologies.cqf.core._v01.experiment.design;

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
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;

import java.util.Objects;
import java.util.Optional;

/**
 * Items are uniquely identified by category and simple name.  ItemSpecs
 * serve as references to items without the need to have all the
 * information about an item at hand.
 */
/*
 * Ignore any unknown properties, so that anything with a 
 * simple name and a category field can serve as an item spec.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentDesignElementSpec implements Comparable<ExperimentDesignElementSpec> {
    /**
     * The simple name of the designated item.
     */
    private final String name;

    /**
     * The category of the designated item.
     */
    private final String category;
	
	/**
	 * The ID of the designated item.
	 */
	private final String itemId;

    /**
     * Creates a new item designator.
     *
     * @param name     the simple name of the designated item
     * @param category the category of the designated item
     */
    @JsonCreator
    public ExperimentDesignElementSpec(@JsonProperty("name") String name,
                                       @JsonProperty("category") String category,
                                       @JsonProperty("itemId") String itemId) {
        this.name = Objects.requireNonNull(name, "simple name must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
        String testId = Optional.ofNullable(itemId).orElse("");

        if( testId.isEmpty() )
        {
            testId = ExperimentDesignElementId.of(name, category, null).value();
        }
        this.itemId = testId;
    }
	
	/**
	 * Creates a new item designator.
	 * 
	 * @param name      the name of the designated item k
	 * @param category  the category of the designated item 
	 */
	public ExperimentDesignElementSpec(String name, String category) {
        this.name = Objects.requireNonNull(name, "simple name must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
        this.itemId = ExperimentDesignElementId.of(name, category, "").value();
    }
    
    /**
     * Returns a new ExperimentDesignElementSpec of the provided fields.
     * 
     * @param simpleName the simple name
     * @param category the category
     * @return the item spec
     */
    public static ExperimentDesignElementSpec of(String simpleName, String category) {
    	return new ExperimentDesignElementSpec(simpleName, category);
    }
    
    @Override
    public String toString() {
        return String.format("%s(%s,%s,%s)",
                this.getClass().getSimpleName(),
                getItemId(),
                getName(), getCategory());
    }

    @Override
    public int compareTo(ExperimentDesignElementSpec o) {

        //Check item ids first.  If they aren't equal, figure out the ordering
        if (getItemId().equals(o.getItemId())) {
            return 0;
        }

        int compare = getCategory().compareTo(o.getCategory());
        if (compare != 0) {
            return compare;
        }

        compare = getName().compareTo(o.getName());
        return compare;
    }
	
	/**
	 * @return the name of the designated item 
	 */
	public String getName() {
        return name;
    }
	
	/**
	 * @return the category of the designated item
	 */
	public String getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExperimentDesignElementSpec) {
            ExperimentDesignElementSpec other = (ExperimentDesignElementSpec) obj;

            //If they both have ids, then compare the ids
            if (!getItemId().isEmpty() || !other.getItemId().isEmpty()) {
                return Objects.equals(getItemId(), other.getItemId());
            }
            //Otherwise compare the other fields
            return Objects.equals(this.getCategory(), other.getCategory())
                    && Objects.equals(this.getName(), other.getName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        //If id is set, always use hash of that as the id.
        if (!getItemId().isEmpty()) {
            return Objects.hash(getItemId());
        }
        return Objects.hash(getCategory(), getName());
    }
	
	/**
	 * @return the ID of the designated item
	 */
	public String getItemId() {
        return itemId;
    }
}
