package com.siegetechnologies.cqf.core.experiment.execution.util;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Tests can be run in different modes.  The behavior of the
 * each mode is described below.
 */
@JsonFormat(shape=Shape.OBJECT)
public enum ExecutionMode {
	/**
	 * When a test is run in debug mode, each command in
	 * item scripts will be printed to the CQF log, along
	 * with its output.
	 */
	DEBUG("Debug"),

	/**
	 * When a test is run in the release mode, each commands
	 * in item scripts will be run in the background, allowing
	 * execution to continue to the next command immediately.
	 */
	RELEASE("Release"),

	/**
	 * When a test is run in this mode, commands in scripts
	 * are executed sequentially, with no special treatment
	 * of their output.
	 */
	NONE("None");

	private final String label;

	ExecutionMode(String label) {
		this.label = label;
	}

	/**
	 * Returns the label of the ExecutionMode.  This is a more human
	 * readable version of enumeration name.
	 * 
	 * @return the label of the run mode
	 */
	public String getLabel() { return label; }

	/**
	 * Returns the value, or name of the ExecutionMode.  This
	 * returns the result of the {@link #toString()} method, 
	 * which in turn is the value of the {@link #name()}
	 * method, but follows JavaBean naming conventions, which
	 * is needed JSON-based serialization.
	 * 
	 * @return the value, or name, of the run mode
	 */
	public String getValue() { return this.toString(); }
}
