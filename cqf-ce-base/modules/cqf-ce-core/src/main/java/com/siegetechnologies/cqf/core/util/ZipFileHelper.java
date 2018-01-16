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

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Provides access to the result file for an ECM run.
 */
public class ZipFileHelper {

	private static final Logger logger = LoggerFactory.getLogger(ZipFileHelper.class);
	private File file;
	private ZipFile zipFile;
	private String name;

	/**
	 * An entry in a ZIP file.
	 * 
	 * @author taylorj
	 */
	public class ZipFileEntry {
		private final String name;
		private final String entryName;
		private final String path;
		private final InputStream stream;

		/**
		 * Creates a new entry with provided fields.
		 * 
		 * @param name the name 
		 * @param entryName the entry name
		 * @param path the path
		 * @param stream the input stream
		 */
		public ZipFileEntry(String name, String entryName, String path, InputStream stream) {
			this.name = name;
			this.entryName = entryName;
			this.path = path;
			this.stream = stream;
		}

		/**
		 * Returns the raw filename.
		 * 
		 * @return the raw filename
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the name of the entry.
		 * 
		 * @return the name of the entry
		 */
		public String getEntryName() {
			return entryName;
		}

		/**
		 * Returns the path of the file in the ZIP.
		 * 
		 * @return the path of the file in the ZIP
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Returns the input stream of the file
		 * 
		 * @return the input stream of the file
		 */
		public InputStream getStream() {
			return stream;
		}

	}

	/**
	 * Creates a new ZipFileHelper with provided fields.
	 * 
	 * @param name the name of the helper
	 * @param fileBytes the file content
	 * 
	 * @throws IOException if an IO exception occurs
	 */
	public ZipFileHelper(String name, byte[] fileBytes) throws IOException {
		Path p = Files.createTempFile(name, ".zip"); // FIXME: STRING: srogers
		this.file = p.toFile();
		Files.write(p, fileBytes);
		this.file.deleteOnExit();

		logger.info("Created result file {}", file.getName());

		this.zipFile = new ZipFile(file);

		this.name = name;
	}

	/**
	 * Returns the name of the helper.
	 * 
	 * @return the name of the helper
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a new ZipFileEntry for the entry in the ZipFile with a specified
	 * name.
	 * 
	 * @param name the name of entry in the ZIP file
	 * @return the ZIP file entry
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public ZipFileEntry getEntry(String name) throws IOException {
		ZipEntry zipEntry = Objects.requireNonNull(zipFile.getEntry(name), "no zip entry for name, " + name + ".");
		return entryFromZipEntry(zipEntry);
	}

	/**
	 * Returns a new ZipFileEntry for a provided ZipEntry.
	 * 
	 * @param zipEntry the zip entry
	 * @return the ZipFileEntry
	 * @throws IOException if an I/O error occurs
	 */
	private ZipFileEntry entryFromZipEntry(ZipEntry zipEntry) throws IOException {
		InputStream stream = this.zipFile.getInputStream(zipEntry);
		String entryName = zipEntry.getName();
		String pathPath = FilenameUtils.separatorsToUnix(zipEntry.getName());
		String pathName = FilenameUtils.getName(pathPath);
		return new ZipFileEntry(pathName, entryName, pathPath, stream);
	}

	/**
	 * Returns a stream of ZIP file entries for the entries in the
	 * ZIP file. 
	 * 
	 * @return the stream
	 */
	public Stream<ZipFileEntry> stream() {
		return this.zipFile.stream()
				.map(zfe -> {
					try {
						return entryFromZipEntry(zfe);
					}
					catch (IOException e) {
						logger.error("Could not get entry for {}", zfe, e);
						return null;
					}
				});
	}
}
