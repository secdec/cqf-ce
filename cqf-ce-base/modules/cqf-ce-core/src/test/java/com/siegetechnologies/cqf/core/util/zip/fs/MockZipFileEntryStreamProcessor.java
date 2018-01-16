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

import java.util.ArrayDeque;
import java.util.zip.ZipEntry;

/**
 * @author srogers
 */
public class MockZipFileEntryStreamProcessor implements ZipFileEntryStreamProcessing.Processor {

	public ArrayDeque<String> zipEntries = new ArrayDeque<>();

	public void entry(ZipEntry zipEntry) {

		zipEntries.add(zipEntry.getName());
	}

}
