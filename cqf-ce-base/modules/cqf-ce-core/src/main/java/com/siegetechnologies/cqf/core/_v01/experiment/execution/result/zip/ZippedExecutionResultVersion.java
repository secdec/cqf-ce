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

/**
 * Versions of file formats for individual execution results stored in ZIP files. The ZIP file is
 * always expected to be a correctly-formatted ZIP file; the version here refers
 * to the structure of the files within the ZIP file.
 */
enum ZippedExecutionResultVersion {
	/**
	 * Version 0.
	 * In this version, individual results in a ZIP file are structured as follows:
	 * 
	 * <pre>
	 *     0000/
	 *       <Workspace Node Name>/
	 *         data.json     <-- Top level config file
	 *         parameters.json
	 *         0000/
	 *           <Node/Switch Name>/
	 *             data.json (ignored)
	 *             parameters.json (ignored)
	 *             0000/
	 *               <ExperimentDesignElement Name>/
	 *                 data.json (ignored)
	 *                 <datafile1>
	 *                 ...
	 *                 <datafileN>
	 * </pre>
	 *
	 * @return ExecutionResultImpl file
	 */
	VERSION_0
}
