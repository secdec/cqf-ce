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

import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImporter;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.zip.ZipFile;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ZippedExecutionResultImporterTest {
	
	@Mock
	ExecutionResultImporter<ZipFile> importer;
	
	@Test
	public void testConstructor() {
		assertNotNull(new ZippedExecutionResultImporter());
	}

	@Test
	public void testProbe() {
		ZipFile zipFile = mock(ZipFile.class);
		assertSame("probe should always return version 0 for now",
				ZippedExecutionResultVersion.VERSION_0,
				ZippedExecutionResultImporter.probeVersion(zipFile));
	}
	
	@Test
	public void testGetImporter() {
		assertTrue(ZippedExecutionResultImporter.getImporter(ZippedExecutionResultVersion.VERSION_0) instanceof ZippedExecutionResultVersion0Importer);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetImporter_badVersion() {
		ZippedExecutionResultImporter.getImporter(null);
	}
	
	@Test
	public void testImportResult() {
		ZippedExecutionResultImporter.ZipProber prober = file -> ZippedExecutionResultVersion.VERSION_0;
		ZippedExecutionResultImporter.ImporterResolver resolver = x -> importer;
		ZipFile zipFile = mock(ZipFile.class);
		ExecutionResultImpl result = mock(ExecutionResultImpl.class);
		when(importer.importResult(zipFile)).thenReturn(result);
		assertSame(result, new ZippedExecutionResultImporter(prober, resolver).importResult(zipFile));
	}
	
}
