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

import com.siegetechnologies.cqf.core._v01.experiment.ExperimentElementSpec;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZippedExecutionResultVersion0Test {
	
	@Test
	public void testConstructorAndGetters() {
		ExperimentElementSpec configuration = mock(ExperimentElementSpec.class);
		ZippedExecutionResultVersion0 result = new ZippedExecutionResultVersion0(configuration, null);
		assertSame(configuration, result.getConfiguration());
	}
	
	@Test
	public void getExperimentResultsFile() throws IOException {
		ZippedExecutionResultVersion0 result = new ZippedExecutionResultVersion0(null, null);
		ZippedExecutionResultEntry z = mock(ZippedExecutionResultEntry.class);
		InputStream in = mock(InputStream.class);
		when(z.getInputStream()).thenReturn(in);
		when(z.getName()).thenReturn("name");
		ResultFileImpl file = result.getExperimentResultFile(z);
		assertEquals("name", file.getResultName());
		assertSame(in, file.getContentInputStream());
	}
	
	@Test
	public void testGetFiles() {
		Map<String,List<ZippedExecutionResultEntry>> dataFiles = new HashMap<>();
		ZippedExecutionResultVersion0 result = new ZippedExecutionResultVersion0(null, dataFiles);
		assertEquals(0, result.getFilesForElement("x").count());
	}


}
