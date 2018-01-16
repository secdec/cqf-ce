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

import java.util.stream.Stream;

/**
 * Abstract experiment result implementation. This class adds an implementation
 * of {@link #getEntries()} that depends on an implementation of a new method,
 * {@link #getFilesForElement(String)}.
 * 
 * @author taylorj
 */
public abstract class AbstractExecutionResultImpl implements ExecutionResultImpl
{

	private final ExecutionResultFormatter formatter;

	/**
	 * Creates a new instance with a provided formatter.
	 * 
	 * @param formatter the formatter
	 */
	AbstractExecutionResultImpl(ExecutionResultFormatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * Creates a new instance.
	 */
	public AbstractExecutionResultImpl() {
		this(new ExecutionResultFormatter());
	}

	/**
	 * Returns the result files for the specified element, as a stream.
	 * This method is used by the implementation of
	 * {@link #getEntries()} in order to provide a result file for each entry.
	 * 
	 * @return the result files as a stream
	 *
	 * @param elementId
	 */
	protected abstract Stream<ResultFileImpl> getFilesForElement(String elementId);

	@Override
	public Stream<Entry> getEntries() {
		return getEntries(getConfiguration(), null, 0);
	}

	@Override
	public String toString() {
		return formatter.format(this);
	}

	/**
	 * Auxiliary "driver" method for {@link #getEntries()}. Returns a stream of
	 * result entries rooted at an instance, in pre-order traversal.
	 * 
	 * @param instance the instance
	 * @param parent the parent, or null
	 * @param depth the depth
	 * @return a stream of result entries
	 */
	private Stream<Entry> getEntries(ExperimentElementSpec instance, Entry parent, int depth) {
		Entry entry = Entry.of(instance, parent, depth, () -> getFilesForElement(instance.getId()));

		Stream<Entry> ofInstance = Stream.of(entry);

		Stream<Entry> ofChildren = instance.getChildren().stream()
				.flatMap(child -> getEntries(child, entry, depth + 1));

		return Stream.concat(ofInstance, ofChildren);
	}
}
