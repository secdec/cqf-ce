package com.siegetechnologies.cqf.vsphere.api;

/*-
 * #%L
 * cqf-ce-testbed-vsphere
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

/**
 * Properties used for recording clone metadata in VM configurations.
 * 
 * @author taylorj
 */
public enum CloneProperty {

	/**
	 * Property indicating the VMID of the donor from which a clone was created.
	 */
	PARENT("parent"), // FIXME: STRING: srogers

	/**
	 * Property indicating the datastore path where a clone's files are stored.
	 */
	DIRECTORY("directory"), // FIXME: STRING: srogers

	/**
	 * Property indicating the UUID for which the clone was created.
	 */
	CREATOR_UUID("creatorUuid"); // FIXME: STRING: srogers

	/**
	 * Prefix of clone properties.
	 */
	public static final String PREFIX = "com.siegetechnologies.cqf."; // FIXME: STRING: srogers
	
	/**
	 * The individual value
	 */
	private final String value;

	/**
	 * Creates an instance whose {@link #getValue() value} is the result of
	 * concatenating {@code suffix} to the end of the {@link #PREFIX}.
	 * 
	 * @param suffix the suffix
	 */
	CloneProperty(String suffix) {
		this.value = PREFIX + suffix;
	}

	/**
	 * Returns the full value of the property.
	 * 
	 * @return the value of the property
	 */
	public String getValue() {
		return this.value;
	}

}
