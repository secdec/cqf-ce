package com.siegetechnologies.cqf.core._v01.experiment.execution.result.zip;

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

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Encapsulates a ZipFileEntry with some extra information.
 */
public interface ZippedExecutionResultEntry {
	/**
	 * Returns the name of the result file.
	 *
	 * @return the name of the result file
	 */
	String getName();

	/**
	 * Returns the name of the ZIP entry.
	 *
	 * @return the entry name
	 */
	String getEntryName();

	/**
	 * Returns the path of the result file in the entry.
	 *
	 * @return the path of the entry
	 */
	String getPath();

	/**
	 * Returns the zip entry for the result file.
	 *
	 * @return an input stream
	 */
	ZipEntry getEntry();

	/**
	 * Returns the ZIP file that this is an entry from.
	 * 
	 * @return the ZIP file
	 */
	ZipFile getFile();

	/**
	 * Returns an input stream for the entry content.
	 * 
	 * @return an input stream
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Creates a new instance from a ZipEntry.
	 * 

	 * @param zipFile ZIP file the ZipEntry belongs to
	 * @param zipEntry ZipEntry to convert
	 *
	 * @return converted ZipEntry
	 */
	/**
	 * Creates a new instance from a ZIP entry in a ZIP file.
	 * 
	 * <p>
	 * Note: the ZIP entry must be from the ZIP file, but the ZIP API methods
	 * provide no mechanism to actually enforce this constraint. If the entry is
	 * not from the file, then the result's
	 * {@link ZippedExecutionResultEntry#getInputStream()} is likely to fail.
	 * 
	 * @param zipFile the ZIP file
	 * @param zipEntry the ZIP entry
	 * @return the instance
	 */
	static ZippedExecutionResultEntry of(ZipFile zipFile, ZipEntry zipEntry) {
		String entryName = zipEntry.getName();
		String path = FilenameUtils.separatorsToUnix(zipEntry.getName());
		String name = FilenameUtils.getName(path);
		return new ZippedExecutionResultEntryImpl(name, entryName, path, zipEntry, zipFile);
	}
}
