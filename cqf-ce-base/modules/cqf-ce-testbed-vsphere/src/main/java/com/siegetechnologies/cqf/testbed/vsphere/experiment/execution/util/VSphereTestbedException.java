package com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util;

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
 * Exception thrown when errors occur while working with vSphere.
 * 
 * @author taylorj
 */
public class VSphereTestbedException extends RuntimeException {
	private static final long serialVersionUID = -8076005088942309644L;

	/**
	 * Creates a a new instance.
	 * 
	 * @param cause the cause
	 * 
	 * @see Exception#Exception(Throwable)
	 */
	public VSphereTestbedException(Throwable cause) {
		super(cause);
	}
	/**
	 * Creates a new instance with provided fields.
	 * 
	 * @param message the message
	 * @param cause the cause
	 * 
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public VSphereTestbedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new instance with provided fields.
	 * 
	 * @param message the message
	 * 
	 * @see RuntimeException#RuntimeException(String)
	 */
	public VSphereTestbedException(String message) {
		super(message);
	}
}
