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

import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultExporter;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;

import java.io.IOException;
import java.nio.file.Path;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * Exporter for exporting execution result as a ZIP file.
 * 
 * @author taylorj
 */
public class ZippedExecutionResultExporter implements ExecutionResultExporter<Path> {

	@Override
	public Path exportResult(ExecutionResultImpl result) throws IOException {
		return exportResult(result, ZippedExecutionResultVersion.VERSION_0);
	}

	/**
	 * Exports an execution result in a specified ZIP file format.
	 *
	 * @return the path of the result file
	 *
	 * @param executionResult
	 * @param version the file format version
	 *
	 * @throws IOException if an I/O error occurs
	 */
	Path exportResult(ExecutionResultImpl executionResult, ZippedExecutionResultVersion version) throws IOException {
		isTrue(version == ZippedExecutionResultVersion.VERSION_0, "Cannot export result as version: "+version+".");
		return new ZippedExecutionResultVersion0Exporter().exportResult(executionResult);
	}
	
	

}
