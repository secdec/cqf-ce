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

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZippedExecutionResultEntryTest {
	
	@Test
	public void test() throws IOException {
		ZipFile zipFile = mock(ZipFile.class);
		ZipEntry zipEntry = mock(ZipEntry.class);
		when(zipEntry.getName()).thenReturn("x/y/z");
		InputStream in = mock(InputStream.class);
		when(zipFile.getInputStream(zipEntry)).thenReturn(in);
		ZippedExecutionResultEntry e = ZippedExecutionResultEntry.of(zipFile, zipEntry);
		
		assertEquals("x/y/z", e.getEntryName());
		assertEquals("z", e.getName());
		assertSame(zipEntry, e.getEntry());
		assertSame(zipFile, e.getFile());
		assertEquals("x/y/z", e.getPath());
		assertSame(in, e.getInputStream());
		assertEquals("RFE:<x/y/z:z>", e.toString());
	}

}
