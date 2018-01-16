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
import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ExecutionResultFormatterTest {
	
	@Test
	public void testIndent() {
		ExecutionResultFormatter f = new ExecutionResultFormatter();
		PrintStream out = mock(PrintStream.class);
		
		f.indent(out, 5);
		verify(out, times(5)).print('\t');
	}
	
	@Test
	public void testWriteFile() {
		ExecutionResultFormatter f = new ExecutionResultFormatter();
		PrintStream out = mock(PrintStream.class);
		
		ResultFileImpl file = mock(ResultFileImpl.class);
		when(file.getResultName()).thenReturn("filename");
		
		f.writeFile(out, file, 3);
		verify(out, times(1)).print(" * ");
		verify(out, times(1)).println("filename");
	}
	
	@Test
	public void testWrite() throws IOException {
		ExecutionResultImpl result = mock(ExecutionResultImpl.class);
		ExecutionResultImpl.Entry entry = new ExecutionResultImpl.Entry() {
			@Override
			public Optional<ExecutionResultImpl.Entry> getParent() {
				return Optional.empty();
			}
			
			@Override
			public ExperimentElementSpec getExperimentElementSpec() {
				return new ExperimentElementSpec() {
					@Override
					public ExperimentDesignElementSpec getItem() {
						return new ExperimentDesignElementSpec("x", "y");
					}
				};
			}
			
			@Override
			public Stream<ResultFileImpl> getFiles() {
				ResultFileImpl rf01 = new ResultFileImpl(null, "file0", null);
				return Stream.of(rf01);
			}
			
			@Override
			public int getDepth() {
				return 3;
			}
		};
		when(result.getEntries()).thenReturn(Stream.of(entry));
		ExecutionResultFormatter f = new ExecutionResultFormatter();
		final String actual = f.format(result);
		
		assertThat(actual,containsString("null : x/y"));
		assertThat(actual,containsString("* file0"));
	}

}
