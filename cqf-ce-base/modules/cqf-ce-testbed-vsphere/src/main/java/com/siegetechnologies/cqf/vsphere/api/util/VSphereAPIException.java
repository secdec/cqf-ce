package com.siegetechnologies.cqf.vsphere.api.util;

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
 * Runtime exception class thrown by methods interacting with vSphere.
 * 
 * @author taylorj
 */
public class VSphereAPIException extends RuntimeException {

	private static final long serialVersionUID = 8764344853112192484L;

	/**
	 * Creates a new instance.
	 * 
	 * @param cause the cause
	 * 
	 * @see Throwable#Throwable(Throwable)
	 */
	public VSphereAPIException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param message the message
	 * @param cause the cause
	 * 
	 * @see Throwable#Throwable(String, Throwable)
	 */
	public VSphereAPIException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param message the message
	 * 
	 * @see Throwable#Throwable(String)
	 */
	public VSphereAPIException(String message) {
		super(message);
	}

}
