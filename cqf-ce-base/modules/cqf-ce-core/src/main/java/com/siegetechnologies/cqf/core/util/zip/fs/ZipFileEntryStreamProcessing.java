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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author srogers
 */
public class ZipFileEntryStreamProcessing {

	private ZipFileEntryStreamProcessing() {}

	public static void scan
	(
			String      zipFileResourceName,
			ClassLoader zipFileResourceLoader,
			Processor   processor

	) throws IOException {

		InputStream rawInputStream = zipFileResourceLoader.getResourceAsStream(zipFileResourceName);
		if (rawInputStream == null) {
			throw new IOException("missing resource: " + zipFileResourceName);
		}

		try (ZipInputStream zipInputStream = new ZipInputStream(rawInputStream)) {

			scan(zipInputStream, processor);
		}
	}

	public static void scan
	(
			ZipInputStream zipInputStream,
			Processor      processor

	) throws IOException {

		Scanner scanner = new Scanner(zipInputStream, processor);
		scanner.execute();
	}

	public interface Processor {

		default void stream_begin(ZipInputStream zipInputStream) throws IOException {}
		default void stream_end(ZipInputStream zipInputStream, Throwable xx) throws IOException {}

		default void entry(ZipEntry e) throws IOException {};

		/**/
	}

	public static class Scanner {

		private ZipInputStream zipInputStream;
		private Processor processor;

		public Scanner(ZipInputStream zipInputStream, Processor processor) {

			this.zipInputStream = zipInputStream;
			this.processor = processor;
		}

		public void execute() throws IOException {

			try {
				processor.stream_begin(zipInputStream);

				scanStream();
			}
			catch (Throwable xx) {

				processor.stream_end(zipInputStream, xx);
			}
			finally {

				processor.stream_end(zipInputStream, null);
			}
		}

		protected void scanStream() throws IOException {

			ZipEntry e;

			while (null != (e = zipInputStream.getNextEntry())) {
				try {
					processor.entry(e);
				}
				finally {

					zipInputStream.closeEntry();
				}
			}
		}

	}

}
