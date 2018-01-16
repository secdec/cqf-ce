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

import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImporter;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;

import java.util.zip.ZipFile;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * Experiment result importer for ZIP result files. This importer dispatches to
 * version-specific ZIP result file processors.
 * 
 * @author taylorj
 */
public class ZippedExecutionResultImporter implements ExecutionResultImporter<ZipFile> {

	private final ZipProber prober;
	private final ImporterResolver resolver;

	/**
	 * Creates a new importer with provided ZIP result file format version prober and
	 * importer resolver.
	 * 
	 * @param prober the prober
	 * @param resolver the resolver
	 */
	ZippedExecutionResultImporter(ZipProber prober, ImporterResolver resolver) {
		this.prober = prober;
		this.resolver = resolver;
	}

	/**
	 * Creates a new importer. ZIP result file format versions are determined using
	 * {@link #probeVersion(ZipFile)} and importers are selected using
	 * {@link #getImporter(ZippedExecutionResultVersion)}.
	 */
	public ZippedExecutionResultImporter() {
		this(ZippedExecutionResultImporter::probeVersion, ZippedExecutionResultImporter::getImporter);
	}

	/**
	 * Imports an execution result from a ZIP file. The input is probed for a file format version, then
	 * an importer is selected based on the file format version, and then the importer is
	 * applied to the input, and the imported execution result returned.
	 */
	@Override
	public ExecutionResultImpl importResult(ZipFile input) {
		ZippedExecutionResultVersion version = prober.probe(input);
		ExecutionResultImporter<ZipFile> importer = resolver.resolve(version);
		return importer.importResult(input);
	}

	/**
	 * Returns the file format version of the ZIP result file.
	 * 
	 * @return the file format version
	 *
	 * @param zipFile
	 */
	static ZippedExecutionResultVersion probeVersion(ZipFile zipFile) {
		return ZippedExecutionResultVersion.VERSION_0;
	}

	/**
	 * Returns an importer for a file version.
	 * 
	 * @param version the version
	 * @return the importer
	 */
	static ExecutionResultImporter<ZipFile> getImporter(ZippedExecutionResultVersion version) {
		isTrue(version == ZippedExecutionResultVersion.VERSION_0, "Cannot export to version: " + version + ".");
		return new ZippedExecutionResultVersion0Importer();
	}

	/**
	 * Determines file format version for a ZIP result file.
	 * 
	 * @author taylorj
	 */
	@FunctionalInterface
	interface ZipProber {
		/**
		 * Returns the file format version for a ZIP result file.
		 * 
		 * @return the version
		 */
		ZippedExecutionResultVersion probe(ZipFile file);
	}

	/**
	 * Selects a ZIP file importer for a particular file format version.
	 * 
	 * @author taylorj
	 */
	@FunctionalInterface
	interface ImporterResolver {
		/**
		 * Returns an importer for a specified ZIP result file version.
		 * 
		 * @param version the ZIP result file format version
		 * @return the execution result importer
		 */
		ExecutionResultImporter<ZipFile> resolve(ZippedExecutionResultVersion version);
	}
}
