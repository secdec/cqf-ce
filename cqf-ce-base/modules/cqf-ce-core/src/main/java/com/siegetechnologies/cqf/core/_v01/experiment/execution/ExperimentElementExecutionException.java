package com.siegetechnologies.cqf.core._v01.experiment.execution;

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

/**
 * Exception thrown when an instance context handler fails while
 * handling an instance context.
 * 
 * @author taylorj
 */
public class ExperimentElementExecutionException extends RuntimeException {
	private static final long serialVersionUID = 7129842842058587901L;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param cause the cause
	 * 
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	public ExperimentElementExecutionException(Throwable cause) {
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
	public ExperimentElementExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
