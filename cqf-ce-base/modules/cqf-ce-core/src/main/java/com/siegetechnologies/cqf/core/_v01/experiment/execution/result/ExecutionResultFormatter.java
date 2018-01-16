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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;

/**
 * Utility for formatting an experiment result.
 * 
 * @author taylorj
 */
public class ExecutionResultFormatter {

	/**
	 * Returns a string representation of a (structured) experiment result.
	 * Implementations are encouraged to use this to implement
	 * {@link #toString()}.
	 * 
	 * @param result
	 * @return the string representation
	 */
	public String format(ExecutionResultImpl result) {
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); PrintStream out = new PrintStream(bout)) {
			out.print('[');
			write(out, result);
			out.print(']');
			return bout.toString();
		}
		catch (IOException e) {
			throw new UncheckedIOException("Failed to write experiment result to string.", e);
		}
	}

	/**
	 * Write <code>depth</code> tabs to a print stream.
	 * 
	 * @param out the print stream
	 * @param depth the number of tabs to print
	 */
	void indent(PrintStream out, int depth) {
		for (int i = 0; i < depth; i++) {
			out.print('\t');
		}
	}

	/**
	 * Write a representation of the execution result to the output stream.
	 * 
	 * @param out the output stream
	 * @param executionResult
	 */
	public void write(PrintStream out, ExecutionResultImpl executionResult) {
		executionResult.getEntries()
				.forEach(e -> {
					int depth = e.getDepth();
					ExperimentElementSpec spec = e.getExperimentElementSpec();
					ExperimentDesignElementSpec item = spec.getItem();
					indent(out, depth);
					out.print(spec.getId());
					out.print(" : ");
					out.print(item.getName());
					out.print('/');
					out.println(item.getCategory());
					e.getFiles()
							.forEach(file -> writeFile(out, file, depth));
				});
	}

	void writeFile(PrintStream out, ResultFileImpl file, int depth) {
		indent(out, depth);
		out.print(" * ");
		out.println(file.getResultName());
	}

}
