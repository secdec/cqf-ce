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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AbstractExecutionResultImplTest
{
	
	@Test
	public void testToString() {
		ExperimentElementSpec configuration = mock(ExperimentElementSpec.class, RETURNS_DEEP_STUBS);
		
		when(configuration.getItem().getName()).thenReturn("name");
		when(configuration.getItem().getCategory()).thenReturn("category");
		
		ExecutionResultFormatter formatter = mock(ExecutionResultFormatter.class);
		
		AbstractExecutionResultImpl result = new AbstractExecutionResultImpl(formatter) {
			@Override
			public ExperimentElementSpec getConfiguration() {
				return null;
			}
			
			@Override
			protected Stream<ResultFileImpl> getFilesForElement(String elementId) {
				return null;
			}
		};
		
		result.toString();
		verify(formatter).format(result);
	}
	
	@Test
	public void test() {
		ExperimentElementSpec child0 = mock(ExperimentElementSpec.class,RETURNS_DEEP_STUBS);
		when(child0.getId()).thenReturn("x");
		ExperimentElementSpec child1 = mock(ExperimentElementSpec.class, RETURNS_DEEP_STUBS);
		when(child1.getId()).thenReturn("y");
		
		ExperimentElementSpec configuration = mock(ExperimentElementSpec.class, RETURNS_DEEP_STUBS);
		List<ExperimentElementSpec> children = Arrays.asList(child0, child1);
		when(configuration.getChildren()).thenReturn(children);
		when(configuration.getId()).thenReturn("z");
		
		int[] count = new int[] { 0 };
		
		ExecutionResultImpl result = new AbstractExecutionResultImpl() {
			@Override
			public ExperimentElementSpec getConfiguration() {
				return configuration;
			}
			
			@Override
			protected Stream<ResultFileImpl> getFilesForElement(String elementId) {
				count[0]++;
				return Stream.empty();
			}
		};
		
		assertEquals(0, count[0]);
		result.getEntries().forEach(ExecutionResultImpl.Entry::getFiles);
		assertEquals("Getting files for three instances should call getFilesForElement(String) three times.", 3, count[0]);
		
	}

}
