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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * OperatingSystemSpecs serve as identifiers for the types of OS nodes an item
 * can be deployed onto.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperatingSystemSpec implements Comparable<OperatingSystemSpec>{
    /**
     * The name of the operating system.  For example "Windows" or "Linux"
     */
    private final String name;

    /**
     * The distribution of the operating system.  For example "XP" or "Debian"
     */
    private final String distribution;

    /**
     * The version of the operating system kernel.  For example "4.10.1"
     */
    private final String version;

    /**
     * Creates a new operating system specification with 
     * empty strings for name, distribution, and version.
     */
    public OperatingSystemSpec() {
    	this("","","");
    }

    /**
     * Creates a new item designator from provided name, 
     * distribution, and version.
     * 
     * @param name the name
     * @param distribution the distribution
     * @param version the version
     */
    @JsonCreator
    public OperatingSystemSpec(@JsonProperty(value = "name") String name,
                               @JsonProperty(value = "distribution") String distribution,
                               @JsonProperty(value = "version") String version )
    {
        this.name = name;
        this.distribution = distribution;
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format("%s(name=%s, distribution=%s, version=%s)",
                this.getClass().getSimpleName(),
                getName(),
                getDistribution(),
                getVersion()
                );
    }

    @Override
    public int compareTo( OperatingSystemSpec o ) {
        int compare = getName().compareTo(o.getName());

        if( compare != 0 ) {
            return compare;
        }

        compare = getDistribution().compareTo(o.getDistribution());
        if( compare != 0 ) {
            return compare;
        }

        return getVersion().compareTo(o.getVersion());
    }

    @Override
    public boolean equals(Object obj) {
        if( !(obj instanceof OperatingSystemSpec) )
        {
            return false;
        }

        OperatingSystemSpec other = (OperatingSystemSpec) obj;
        return Objects.equals( this.getName(), other.getName()) &&
                Objects.equals( this.getDistribution(), other.getDistribution() ) &&
                Objects.equals( this.getVersion(), other.getVersion() );
    }
    
    @Override
	public int hashCode() {
    	return Objects.hash(getName(), getDistribution(), getVersion());
	}

	/**
	 * Returns the name of the operating system spec.
	 * 
     * @return the name of the operating system spec
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the distribution of the operating system spec.
     * 
     * @return the distribution of the operating system spec
     */
    public String getDistribution() {
        return distribution;
    }

    /**
     * Returns the version of the operating system spec.
     * 
     * @return the version of the operating system spec
     */
    public String getVersion() {
        return version;
    }
}
