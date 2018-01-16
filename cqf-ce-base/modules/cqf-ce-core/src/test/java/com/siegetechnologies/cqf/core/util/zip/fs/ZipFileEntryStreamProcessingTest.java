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

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author srogers
 */
public class ZipFileEntryStreamProcessingTest {

	@Test
	public void testProcessing_simpleDirectoryTree() throws Exception {

		String zipFileResourceName = "zip-files/a-b-b1.b2.b3-c-c1.c2.c3-c31-d.zip";

		ClassLoader this_classLoader = this.getClass().getClassLoader();

		MockZipFileEntryStreamProcessor processor = new MockZipFileEntryStreamProcessor();

		ZipFileEntryStreamProcessing.scan(zipFileResourceName, this_classLoader, processor);

		String result = "[a, b/, b/b1, b/b2, b/b3, c/, c/c1, c/c2, c/c3/, c/c3/c31, d/]";

		assertTrue(result.equals(processor.zipEntries.toString()));
	}

}
