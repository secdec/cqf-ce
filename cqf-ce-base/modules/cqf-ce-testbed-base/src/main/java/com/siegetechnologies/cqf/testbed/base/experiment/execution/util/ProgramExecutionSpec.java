package com.siegetechnologies.cqf.testbed.base.experiment.execution.util;

/*-
 * #%L
 * cqf-ce-testbed-base
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
 * Specification of a command to execution on a testbed machine.
 * 
 * @author taylorj
 */
public interface ProgramExecutionSpec {

	/**
	 * Returns the path of the executable to run.
	 * 
	 * @return the path of the executable
	 */
	String getPath();

	/**
	 * Returns the arguments to pass to the executable.
	 * 
	 * @return the arguments
	 */
	String getArguments();

	/**
	 * Returns the working directory to launch the executable in.
	 * 
	 * @return the working directory, or null
	 */
	String getWorkingDirectory();

	/**
	 * Returns a string representation of a command specificiation.
	 * 
	 * @param specification the command specification
	 * @return the string representation
	 */
	static String toString(ProgramExecutionSpec specification) {
		return String.format("ProgramExecutionSpec.of(path=%s, arguments=%s, workingDirectory=%s)",
				specification.getPath(),
				specification.getArguments(),
				specification.getWorkingDirectory());
	}

	/**
	 * Returns a new command specification with provided components, and a null
	 * working directory.
	 * 
	 * @param path the path
	 * @param arguments the arguments
	 * @return the command specification
	 */
	static ProgramExecutionSpec of(String path, String arguments) {
		return of(path, arguments, null);
	}

	/**
	 * Returns a new command specification with provided components.
	 * 
	 * @param path the path
	 * @param arguments the arguments
	 * @param workingDirectory the working directory
	 * @return the command specification
	 */
	static ProgramExecutionSpec of(String path, String arguments, String workingDirectory) {
		return new ProgramExecutionSpec() {
			@Override
			public String toString() { return ProgramExecutionSpec.toString(this); }

			@Override
			public String getPath() { return path; }

			@Override
			public String getArguments() { return arguments; }

			@Override
			public String getWorkingDirectory() { return workingDirectory; }
		};
	}
}
