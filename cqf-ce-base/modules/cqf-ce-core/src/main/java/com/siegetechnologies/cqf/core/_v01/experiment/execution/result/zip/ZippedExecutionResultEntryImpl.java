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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A "standard" implemenation of ZipFileResultEntry.
 * 
 * @author taylorj
 */
class ZippedExecutionResultEntryImpl implements ZippedExecutionResultEntry {

	private final String name;
	private final String entryName;
	private final String path;
	private final ZipEntry zipEntry;
	private final ZipFile zipFile;

	/**
	 * Creates a new instance with provided fields.
	 * 
	 * @param name the name
	 * @param entryName the entry name
	 * @param path the path
	 * @param zipEntry the entry
	 * @param zipFile the ZIP file
	 */
	public ZippedExecutionResultEntryImpl(String name, String entryName, String path, ZipEntry zipEntry, ZipFile zipFile) {
		this.name = name;
		this.entryName = entryName;
		this.path = path;
		this.zipEntry = zipEntry;
		this.zipFile = zipFile;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getEntryName() {
		return entryName;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public ZipEntry getEntry() {
		return zipEntry;
	}

	@Override
	public ZipFile getFile() {
		return zipFile;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getFile().getInputStream(getEntry());
	}

	@Override
	public String toString() {
		return String.format("RFE:<%s:%s>", getPath(), getName());
	}
}
