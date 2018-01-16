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

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Builds an ExecutionResultImpl.
 * 
 * @author taylorj
 */
public class ExecutionResultBuilder {

	private final ExperimentElementSpec configuration;
	private final Map<String, List<ResultFileImpl>> files;

	/**
	 * Creates a new builder with a provided configuration.
	 * 
	 * @param configuration the configuration
	 */
	public ExecutionResultBuilder(ExperimentElementSpec configuration) {
		this.configuration = configuration;
		this.files = new HashMap<>();
	}

	/**
	 * Add a result file for a specified element ID.
	 * 
	 * @param elementID
	 * @param resultFile
	 */
	public void addFileForElement(String elementID, ResultFileImpl resultFile) {
		files.computeIfAbsent(elementID, x -> new ArrayList<>()).add(resultFile);
	}

	/**
	 * Returns the builder's configuration.
	 * 
	 * @return the configuration
	 */
	public ExperimentElementSpec getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the execution result.
	 * 
	 * @return the execution result.
	 */
	public ExecutionResultImpl build() {
		// Get a map like the builder's, but with the lists fixed so that
		// later changes in the builder don't show through here.
		Map<String, List<ResultFileImpl>> builtFiles = files.entrySet().stream()
				.collect(toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
		
		return new AbstractExecutionResultImpl() {
			@Override
			public ExperimentElementSpec getConfiguration() {
				return configuration;
			}

			@Override
			protected Stream<ResultFileImpl> getFilesForElement(String elementId) {
				return builtFiles.getOrDefault(elementId, Collections.emptyList()).stream();
			}
		};
	}
}
