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

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.Validate.isTrue;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.vmware.vim25.OptionValue;
import com.vmware.vim25.VirtualMachineConfigInfo;

/**
 * Metadata about a linked clone. 
 * 
 * @author taylorj
 * 
 * @see CloneProperty
 */
public interface CloneMetadata {
	/**
	 * Returns the VMID of the donor virtual machine from which the clone was created.
	 * 
	 * @return the VMID of the donor
	 * 
	 * @see CloneProperty#PARENT
	 */
	String getParent();
	
	/**
	 * Returns the datastore path of the clone's files.
	 * 
	 * @return the datastore path of the clone's files
	 * 
	 * @see CloneProperty#DIRECTORY
	 */
	String getDirectory();
	
	/**
	 * Returns the UUID for which the clone was created. 
	 * 
	 * @return the UUID for which the clone was created
	 * 
	 * @see CloneProperty#CREATOR_UUID
	 */
	String getCreatorUuid();
	
	/**
	 * Returns clone metadata parsed from a virtual machine configuration.  The configuration's
	 * extra values are read, and the values for the {@link CloneProperty} fields are used
	 * to provide the parent, directory, and creator UUID.
	 * 
	 * @param config the configuration
	 * @return the clone metadata
	 * 
	 * @see VirtualMachineConfigInfo#getExtraConfig()
	 * 
	 * @throws IllegalArgumentException if the configuration is missing any of the properties;
	 */
	static CloneMetadata parse(VirtualMachineConfigInfo config) {
		final OptionValue[] ovs = config.getExtraConfig();
		final Map<String,Object> map = Stream.of(ovs).collect(toMap(OptionValue::getKey, OptionValue::getValue));
		isTrue(map.containsKey(CloneProperty.PARENT.getValue()), "VM has no PARENT property in: %s.", map);
		isTrue(map.containsKey(CloneProperty.DIRECTORY.getValue()), "VM has no DIRECTORY property in: %s.", map);
		isTrue(map.containsKey(CloneProperty.CREATOR_UUID.getValue()), "VM has no CREATOR_UUID property in: %s.", map);
		final String parent = map.get(CloneProperty.PARENT.getValue()).toString();
		final String directory = map.get(CloneProperty.DIRECTORY.getValue()).toString();
		final String creatorUuid = map.get(CloneProperty.CREATOR_UUID.getValue()).toString();
		return of(parent, directory, creatorUuid);
	}
	
	/**
	 * Returns a new instance with provided fields.
	 * 
	 * @param parent the VMID of the donor
	 * @param directory the datastore path of the clone's files
	 * @param creatorUuid the UUID for which the clone was created
	 * @return the clone metadata
	 */
	static CloneMetadata of(String parent, String directory, String creatorUuid) {
		Objects.requireNonNull(parent, "parent must not be null");
		Objects.requireNonNull(directory, "directory must not be null");
		Objects.requireNonNull(creatorUuid, "creatorUuid must not be null");
		return new CloneMetadata() {
			@Override
			public String getParent() {
				return parent;
			}
			
			@Override
			public String getDirectory() {
				return directory;
			}
			
			@Override
			public String getCreatorUuid() {
				return creatorUuid;
			}
			
			@Override
			public String toString() {
				return String.format("CloneMetadata.of(parent=%s, directory=%s, creatorUuid=%s)", 
						getParent(), getDirectory(), getCreatorUuid());
			}
		};
	}
}
