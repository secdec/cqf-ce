package com.siegetechnologies.cqf.core.util.zip.fs;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-impl
 * %%
 * Copyright (C) 2016 - 2017 Applied Visions, Inc.
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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author srogers
 */
public class FileSystemBuilderFromZipFileEntryStream implements ZipFileEntryStreamProcessing.Processor {

	public FileSystemBuilderFromZipFileEntryStream(String fileSystemHostName, String pathnamePrefix) {

		this.fileSystemHostName = fileSystemHostName;
		this.pathnamePrefix = pathnamePrefix;
	}

	private final String fileSystemHostName;
	private final String pathnamePrefix;

	@Override
	public void stream_begin(ZipInputStream zipInputStream) throws IOException {

		this.zipInputStream = zipInputStream;
		assert this.zipInputStream != null;

		this.result = Jimfs.newFileSystem(fileSystemHostName, Configuration.unix());
		assert this.result != null;

		Path root = this.result.getPath(pathnamePrefix);
		Files.createDirectories(root);
	}

	@Override
	public void stream_end(ZipInputStream zipInputStream, Throwable xx) throws IOException {

		/**/
	}

	protected ZipInputStream zipInputStream;

	@Override
	public void entry(ZipEntry zipEntry) throws IOException {

		String pn = this.pathnamePrefix + zipEntry.getName();
		Path p = this.result.getPath(pn);

		if (! zipEntry.isDirectory()) {

			Files.createDirectories(p.getParent());

			long n = Files.copy(this.zipInputStream, p);
		}

	}

	public FileSystem result() {

		assert this.zipInputStream != null;
		assert this.result != null;

		FileSystem x = this.result;

		this.zipInputStream = null;
		this.result = null;

		return x;
	}

	protected FileSystem result;

}
