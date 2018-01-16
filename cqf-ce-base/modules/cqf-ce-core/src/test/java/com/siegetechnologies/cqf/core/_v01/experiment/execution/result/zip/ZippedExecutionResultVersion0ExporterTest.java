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

import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.*;

public class ZippedExecutionResultVersion0ExporterTest {
	

	@Test
	public void testWriteFile_normal() throws IOException {
		ZippedExecutionResultVersion0Exporter z = new ZippedExecutionResultVersion0Exporter();

		try (ByteArrayInputStream in = new ByteArrayInputStream("hello".getBytes())) {
			ResultFileImpl file = mock(ResultFileImpl.class);
			when(file.getResultName()).thenReturn("name");
			when(file.getContentInputStream()).thenReturn(in);
			
			ZipOutputStream out = mock(ZipOutputStream.class);
			String entryPrefix = "prefix";
			z.writeFile(file, out, entryPrefix);
			
			verify(out).putNextEntry(Mockito.any());
		}
	}
	
	@Test(expected=UncheckedIOException.class)
	public void testWriteFile_throws() throws IOException {
		ZippedExecutionResultVersion0Exporter z = new ZippedExecutionResultVersion0Exporter();

		try (ByteArrayInputStream in = new ByteArrayInputStream("hello".getBytes())) {
			ResultFileImpl file = mock(ResultFileImpl.class);
			when(file.getResultName()).thenReturn("name");
			when(file.getContentInputStream()).thenThrow(new IOException("nope"));
			
			ZipOutputStream out = mock(ZipOutputStream.class);
			String entryPrefix = "prefix";
			z.writeFile(file, out, entryPrefix);
		}
	}
	
	@Test
	public void testWriteEntry() throws IOException {
		ZippedExecutionResultVersion0Exporter z = new ZippedExecutionResultVersion0Exporter();
		ExecutionResultImpl.Entry entry = mock(ExecutionResultImpl.Entry.class, RETURNS_DEEP_STUBS);
		when(entry.getParent()).thenReturn(Optional.empty());
		ResultFileImpl file = mock(ResultFileImpl.class);
		when(file.getContentInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {}));
		when(entry.getFiles()).thenReturn(Stream.of(file, file));
		
		ZipOutputStream out = mock(ZipOutputStream.class);
		
		z.writeEntry(entry, out);
		
		verify(out, times(2)).putNextEntry(Mockito.any());
	}
}
