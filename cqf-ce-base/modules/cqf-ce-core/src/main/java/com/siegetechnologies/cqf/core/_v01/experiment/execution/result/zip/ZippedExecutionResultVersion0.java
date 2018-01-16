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
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.AbstractExecutionResultImpl;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A ExecutionResultImpl backed by zipped result file entries.
 * 
 * @author taylorj
 */
class ZippedExecutionResultVersion0 extends AbstractExecutionResultImpl implements ExecutionResultImpl
{
	private final ExperimentElementSpec experimentConfig;
	private final Map<String, List<ZippedExecutionResultEntry>> dataFiles;

	ZippedExecutionResultVersion0(ExperimentElementSpec experimentConfig, Map<String, List<ZippedExecutionResultEntry>> dataFiles) {
		this.experimentConfig = experimentConfig;
		this.dataFiles = dataFiles;
	}

	@Override
	public ExperimentElementSpec getConfiguration() {
		return experimentConfig;
	}

	@Override
	public Stream<ResultFileImpl> getFilesForElement(String elementId) {
		return dataFiles.getOrDefault(elementId, Collections.emptyList()).stream()
				.map(this::getExperimentResultFile);
	}

	/**
	 * Returns an execution result file backed by a ZIP result entry. This is
	 * a utility method used in implementing {@link #getFilesForElement(String)}.
	 * 
	 * @param zipResultEntry
	 * @return the experiment result file
	 */
	ResultFileImpl getExperimentResultFile(ZippedExecutionResultEntry zipResultEntry) {
		return new ZippedResultFile(zipResultEntry);
	}

	private static class ZippedResultFile extends ResultFileImpl
	{
		private final ZippedExecutionResultEntry zipResultEntry;

		public ZippedResultFile(ZippedExecutionResultEntry zipResultEntry) {
			super(null, zipResultEntry.getName(), zipResultEntry.getName());
			this.zipResultEntry = zipResultEntry;
		}

		@Override
		public InputStream getContentInputStream() throws IOException {
			return this.zipResultEntry.getInputStream();
		}
	}

}
