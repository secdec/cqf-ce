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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The (structured) result of an experiment execution.
 */
public interface ExecutionResultImpl
{
	/**
	 * Returns the experiment configuration.
	 * 
	 * @return the experiment configuration
	 */
	ExperimentElementSpec getConfiguration();

	/**
	 * Returns the entries for this (structured) experiment result as a stream.
	 * The entries are provided in pre-order-traversal order.
	 * That is, the entry for the root is first, then its first child,
	 * then the first child of the first child, and so on.
	 * 
	 * @return a stream of result entries
	 */
	Stream<Entry> getEntries();

	/**
	 * Returns the entry for an experiment element with the specified ID (if any).
	 *
	 * @return the entry for an experiment element with the specified ID (if any)
	 *
	 * @param elementId
	 */
	default Optional<Entry> getEntryForElement(String elementId) {
		return getEntries()
				.filter(e -> Objects.equals(elementId, e.getExperimentElementSpec().getId()))
				.findAny(); // FIXME: srogers: use findFirst() instead
	}

	/**
	 * An entry in a result file, along with hierarchical context.
	 *
	 * @author taylorj
	 */
	interface Entry
	{
		/**
		 * Returns the instance specification of the entry.
		 *
		 * @return the specification
		 */
		ExperimentElementSpec getExperimentElementSpec();

		/**
		 * Returns the parent of the entry. Every entry has a parent, except for the
		 * root.
		 *
		 * @return the parent of the entry
		 */
		Optional<Entry> getParent();

		/**
		 * Returns a stream of the result file(s) available for this entry.
		 *
		 * @return a stream of result file(s)
		 */
		Stream<ResultFileImpl> getFiles();

		/**
		 * Returns a result file with the specified name.
		 *
		 * @return the result file with the specified name
		 *
		 * @param name
		 */
		default Optional<ResultFileImpl> getFile(String name) {
			return getFiles().filter(f -> Objects.equals(name, f.getResultName())).findFirst();
		}

		/**
		 * Returns the depth of the entry in the result tree.
		 * The root entry has depth 0, its children have depth 1, and so on.
		 *
		 * @return the depth of the entry
		 */
		int getDepth();

		/**
		 * Returns a new result entry with provided fields.
		 *
		 * @param instance the instance
		 * @param parent the parent, or null
		 * @param depth the depth
		 * @param files a supplier of the files
		 * @return the new result entry
		 */
		static Entry of
		(
				ExperimentElementSpec instance,
				Entry parent,
				int depth,
				Supplier<Stream<ResultFileImpl>> files
		) {
			return new Entry() {
				@Override
				public Optional<Entry> getParent() {
					return Optional.ofNullable(parent);
				}

				@Override
				public ExperimentElementSpec getExperimentElementSpec() {
					return instance;
				}

				@Override
				public int getDepth() {
					return depth;
				}

				@Override
				public Stream<ResultFileImpl> getFiles() {
					return files.get();
				}
			};
		}
	}

}
