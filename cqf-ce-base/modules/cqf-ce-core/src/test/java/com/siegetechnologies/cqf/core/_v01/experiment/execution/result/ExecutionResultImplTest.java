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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

import com.siegetechnologies.cqf.core._v01.experiment.ExperimentElementSpec;

public class ExecutionResultImplTest
{
	
	@Test
	public void testResults() {
		
		ExecutionResultImpl.Entry entry = mock(ExecutionResultImpl.Entry.class, RETURNS_DEEP_STUBS);
		when(entry.getExperimentElementSpec().getId()).thenReturn("y");

		List<ExecutionResultImpl.Entry> entries = Arrays.asList(entry);
		
		ExecutionResultImpl r = new ExecutionResultImpl() {
			@Override
			public Stream<Entry> getEntries() {
				return entries.stream();
			}
			
			@Override
			public ExperimentElementSpec getConfiguration() {
				return null;
			}
		};
		
		assertFalse(r.getEntryForElement("x").isPresent());
		assertSame(entry, r.getEntryForElement("y").get());
	}

}
