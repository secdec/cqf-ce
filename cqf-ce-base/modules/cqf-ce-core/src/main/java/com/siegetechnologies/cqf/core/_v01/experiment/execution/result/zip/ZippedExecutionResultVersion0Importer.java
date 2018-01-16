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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImporter;
import com.siegetechnologies.cqf.core._v01.experiment.ExperimentElementSpec;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

/**
 * Importer for "Version0" ZIP files.
 * 
 * @author taylorj
 */
class ZippedExecutionResultVersion0Importer implements ExecutionResultImporter<ZipFile> {

	private final JsonFactory factory;
	private final ObjectMapper mapper;

	/**
	 * Creates a new instance with a provided object mapper.
	 * 
	 * @param mapper the object mapper
	 */
	ZippedExecutionResultVersion0Importer(ObjectMapper mapper) {
		this.mapper = mapper;
		this.factory = new JsonFactory().configure(Feature.AUTO_CLOSE_SOURCE, false);
	}

	/**
	 * Creates a new instance of the importer.
	 */
	public ZippedExecutionResultVersion0Importer() {
		this(new ObjectMapper());
	}

	/**
	 * Imports an ExecutionResultImpl object from a zip file. The version of this
	 * file is the old style CQF implementation. This version contains a
	 * directory structure as follows:
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
	 * 
	 * @throws UncheckedIOException if an I/O error occurs while importing the result
	 */
	@Override
	public ExecutionResultImpl importResult(ZipFile input) {
		ExperimentElementSpec experimentConfig;
		try {
			experimentConfig = getConfiguration(input);
		}
		catch (IOException e) {
			throw new UncheckedIOException("Unable to read configuration from ZIP file.", e);
		}
		Map<String, List<ZippedExecutionResultEntry>> dataFiles = getDataFiles(input);
		return new ZippedExecutionResultVersion0(experimentConfig, dataFiles);
	}

	/**
	 * Returns a stream of ZipResultsEntries read from a ZIP file.
	 * 
	 * @param zipFile the ZIP file
	 * @return the stream
	 */
	Stream<ZippedExecutionResultEntry> getZipResultsEntries(ZipFile zipFile) {
		return zipFile.stream()
				.map(zfe -> ZippedExecutionResultEntry.of(zipFile, zfe));
	}

	/**
	 * Returns the experiment configuration from a ZIP file. The configuration
	 * is stored in the top level.
	 * 
	 * @param zipFile the ZIP file
	 * @return the experiment configuration
	 * 
	 * @throws IOException if an I/O error occurs, for instance, while reading
	 *             from the ZIP file, or if the data.json is not found, of if
	 *             there is an error parsing the JSON
	 */
	ExperimentElementSpec getConfiguration(ZipFile zipFile) throws IOException {
		ZippedExecutionResultEntry entry = getZipResultsEntries(zipFile).filter(this::isTopLevelDataJson)
				.findFirst()
				.orElseThrow(() -> new FileNotFoundException("No toplevel configuration file found in archive"));
		InputStream inputStream = entry.getInputStream();
		JsonParser parser = factory.createParser(inputStream);
		return mapper.readValue(parser, ExperimentElementSpec.class);
	}

	/**
	 * Returns true if the result-file entry is a top-level "data.json" file.
	 * The top-level "data.json" file is the specification of the entire
	 * experiment.
	 * 
	 * @param entry the entry
	 * @return whether the entry is the top level "data.json" file
	 */
	boolean isTopLevelDataJson(ZippedExecutionResultEntry entry) {
		// TODO: Update this to use PathHelper.
		return entry.getPath()
				.split("/").length == 3 && "data.json".equals(entry.getName()); // FIXME: STRING: srogers
	}

	/**
	 * Returns a map from file names to result file entries for the content
	 * within a ZIP file. Result file entries whose names are "data.json" or
	 * "parameters.json" are excluded, as these are not result files proper, but
	 * metadata about the items in the experiment configuration. Additionally,
	 * result files are only taken from items that are software, that is, where
	 * {@link ZippedExecutionResultPathHelper#getSoftware()} returns a non-null value.
	 * 
	 * @param zipFile the ZIP file
	 * @return the map of result entries
	 */
	Map<String, List<ZippedExecutionResultEntry>> getDataFiles(ZipFile zipFile) {
		return getZipResultsEntries(zipFile)
				.filter(rfe -> !("data.json".equals(rfe.getName()) || "parameters.json".equals(rfe.getName())))
				.filter(rfe -> ZippedExecutionResultPathHelper.parse(rfe.getPath())
						.getSoftware() != null)
				.collect(Collectors.groupingBy(rfe -> ZippedExecutionResultPathHelper.parse(rfe.getPath())
						.getSoftwareUuid()));
	}
}
