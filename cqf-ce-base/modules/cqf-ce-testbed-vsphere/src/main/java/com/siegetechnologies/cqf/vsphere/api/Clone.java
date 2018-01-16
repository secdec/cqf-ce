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

import com.vmware.vim25.mo.VirtualMachine;

/**
 * Coupling of a virtual machine that is a clone and view
 * of its metadata.
 * 
 * @author taylorj
 */
public interface Clone {
	/**
	 * Returns the clone virtual machine.
	 * 
	 * @return the clone virtual machine
	 */
	VirtualMachine getVirtualMachine();
	
	/**
	 * Returns the clone metadata.
	 * 
	 * @return the clone metadata
	 */
	CloneMetadata getMetadata();

	/**
	 * Returns a clone with provided virtual machine and metadata.
	 * 
	 * @param virtualMachine the virtual machine
	 * @param cloneMetadata the metadata
	 * @return the clone
	 */
	static Clone of(VirtualMachine virtualMachine, CloneMetadata cloneMetadata) {
		return new Clone() {
			@Override
			public VirtualMachine getVirtualMachine() {
				return virtualMachine;
			}
			
			@Override
			public CloneMetadata getMetadata() {
				return cloneMetadata;
			}
			
			@Override
			public String toString() {
				return String.format("Clone.of(virtualMachine=%s, cloneMetadata=%s)",
						getVirtualMachine(),
						getMetadata());
			}
		};
	}
}
