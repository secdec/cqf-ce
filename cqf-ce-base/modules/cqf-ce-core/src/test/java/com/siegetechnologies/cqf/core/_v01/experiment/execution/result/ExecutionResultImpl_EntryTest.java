package com.siegetechnologies.cqf.core._v01.experiment.execution.result;

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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExecutionResultImpl_EntryTest
{
	
	@Mock Stream<ResultFileImpl> files;

	@Test
	public void testOf() {
		ExperimentElementSpec instance = mock(ExperimentElementSpec.class);
		ExecutionResultImpl.Entry parent = mock(ExecutionResultImpl.Entry.class);
		int depth = 3;
		Supplier<Stream<ResultFileImpl>> sFiles = () -> files;
		ExecutionResultImpl.Entry entry = ExecutionResultImpl.Entry.of(instance, parent, depth, sFiles);
		assertSame(instance, entry.getExperimentElementSpec());
		assertSame(parent, entry.getParent().get());
		assertEquals(depth, entry.getDepth());
		assertSame(files, entry.getFiles());
	}
	
	@Test
	public void getGetFile() {
		ResultFileImpl file = mock(ResultFileImpl.class);
		when(file.getResultName()).thenReturn("y");
		List<ResultFileImpl> files = Arrays.asList(file);
		ExecutionResultImpl.Entry entry = ExecutionResultImpl.Entry.of(null, null, 0, files::stream);
		assertFalse(entry.getFile("x").isPresent());
		assertSame(file, entry.getFile("y").get());
	}

}
