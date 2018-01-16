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

import org.apache.commons.io.FilenameUtils;

/**
 * Symbolic operating system families, along with a few utilities for
 * interoperability and conversions.
 * 
 * @author taylorj
 *
 */
public enum OperatingSystemFamily {
	/**
	 * Windows platforms
	 */
	WINDOWS,

	/**
	 * Unix platforms
	 */
	UNIX;

	/**
	 * Returns the pathname with separators converted for the operating system.
	 * This is a thin wrapper around
	 * {@link FilenameUtils#separatorsToUnix(String)} and
	 * {@link FilenameUtils#separatorsToWindows(String)}.
	 * 
	 * @param path the pathname
	 * @return the converted pathname
	 */
	public String separatorsToFamily(String path) {
		switch (this) {
		case UNIX:
			return FilenameUtils.separatorsToUnix(path);
		case WINDOWS:
			return FilenameUtils.separatorsToWindows(path);
		default:
			throw new UnrecognizedOperatingSystemFamily(this);
		}
	}

	/**
	 * Returns a command to create a directory. For {@link #UNIX} systems,
	 * returns "mkdir [directory]". On {@link #WINDOWS} systems, returns "cmd /C
	 * mkdir [directory]". In both cases, the "[directory]" is converted with
	 * {@link #separatorsToFamily(String)} and quoted.
	 * 
	 * @param directory the directory
	 * @return the command
	 */
	public ProgramExecutionSpec getCreateDirectoryCommand(String directory) {
		String familyDirectory = "\""+separatorsToFamily(directory)+"\"";
		switch (this) {
		case UNIX:
			return ProgramExecutionSpec.of("mkdir", familyDirectory);
		case WINDOWS:
			return ProgramExecutionSpec.of("cmd", "/C mkdir "+familyDirectory);
		default:
			throw new UnrecognizedOperatingSystemFamily(this);
		}
	}

	/**
	 * Exception used to indicate that an operating system family was not
	 * recognized for a given context. This is expected to occur only if new
	 * operating system families are added and switch statements are not
	 * updated.
	 * 
	 * @author taylorj
	 */
	static class UnrecognizedOperatingSystemFamily extends AssertionError {
		private static final long serialVersionUID = -490773984732992106L;

		private final OperatingSystemFamily family;

		public UnrecognizedOperatingSystemFamily(OperatingSystemFamily family) {
			super("Unrecognized OperatingSystemFamily, " + family + ".");
			this.family = family;
		}

		public OperatingSystemFamily getOperatingSystemFamily() {
			return this.family;
		}
	}
}
