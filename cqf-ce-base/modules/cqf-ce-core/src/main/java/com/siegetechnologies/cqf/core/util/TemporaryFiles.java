package com.siegetechnologies.cqf.core.util;

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

import static org.apache.commons.lang3.Validate.isTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is used for creating paths for temporary files in the context of
 * CQF. It is similar to Files.createTempFile, except that it does not generate
 * a random string in the middle, and only provides default attributes.
 */
public class TemporaryFiles {

	private TemporaryFiles() {}

	/**
	 * Returns a path for a new temporary file. The name of the file should be a
	 * plain filename, that is, it should not contain any directory information.
	 * 
	 * @param filename the name of the file
	 * @return the path
	 * 
	 * @throws IOException if an I/O error occurs
	 * @throws IllegalArgumentException if the provided filename is not a plain
	 *             filename
	 */
	public static Path create(String filename) {
		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir")); // FIXME: STRING: srogers
		Path target = tempDir.getFileSystem().getPath(filename);
		isTrue(!target.toFile().exists(), "The file %s already exists.", target);
		isTrue(target.getParent() == null, "Path ("+target+") is not a simple path.");
		return tempDir.resolve(target);
	}
}
