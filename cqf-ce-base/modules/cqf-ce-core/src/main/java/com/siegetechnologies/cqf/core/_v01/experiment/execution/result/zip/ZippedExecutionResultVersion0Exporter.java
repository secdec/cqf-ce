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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.siegetechnologies.cqf.core._v01.experiment.ExperimentElementSpec;
import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultExporter;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Exports a result in the version 0 format.
 * 
 * @author taylorj
 */
public class ZippedExecutionResultVersion0Exporter implements ExecutionResultExporter<Path> {
	private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	/**
	 * Name of the main experiment configuration file.
	 */
	static final String CONFIGURATION_FILE_NAME = "data.json"; // FIXME: STRING: srogers

	/**
	 * Format string for the prefix for a result entry. The format string
	 * expects three arguments: (i) the item category; (ii) the item name; and
	 * (iii) the instance UUID.
	 */
	static final String PREFIX_FORMAT = "0000/%s-%s-%s/"; // FIXME: STRING: srogers

	@Override
	public Path exportResult(ExecutionResultImpl result) throws IOException {
		Path path = Files.createTempFile("result-", ".zip"); // FIXME: STRING: srogers

		try (FileOutputStream fOut = new FileOutputStream(path.toFile());
				ZipOutputStream zip = new ZipOutputStream(fOut)) {

			// Write the top level configuration, and then write
			// the entries for each of the files.
			writeConfiguration(result, zip);
			result.getEntries()
					.forEach(entry -> writeEntry(entry, zip));
		}
		catch (UncheckedIOException e) {
			throw e.getCause();
		}

		return path;
	}

	/**
	 * Write the configuration for an experiment to an ZIP output stream.
	 * 
	 * @param result
	 * @param out the output stream
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	void writeConfiguration(ExecutionResultImpl result, ZipOutputStream out) throws IOException {
		// The result entries are produced in pre-order traversal, so the first
		// entry has to be the workspace item, and it's within there that the
		// main configuration file should be placed.
		String prefix = result.getEntries()
				.limit(1)
				.findFirst()
				.map(this::getEntryPrefix)
				.orElseThrow(() -> new IllegalArgumentException("Could not get prefix for main configuration file."));

		ExperimentElementSpec spec = result.getConfiguration();
		byte[] bSpec = mapper.writeValueAsBytes(spec);
		out.putNextEntry(new ZipEntry(prefix + CONFIGURATION_FILE_NAME));
		out.write(bSpec);
	}

	/**
	 * Returns the directory prefix for the files produced by the instance
	 * within an entry.
	 * 
	 * @param entry the entry
	 * @return the directory prefix
	 */
	String getEntryPrefix(ExecutionResultImpl.Entry entry) {
		ExecutionResultImpl.Entry curr = entry;
		StringBuilder sb = new StringBuilder(180);
		while (curr != null) {
			ExperimentElementSpec spec = curr.getExperimentElementSpec();
			ExperimentDesignElementSpec item = spec.getItem();
			String uuid = spec.getId();
			String category = item.getCategory();
			String name = item.getName();
			String part = String.format(PREFIX_FORMAT, category, name, uuid);
			sb.insert(0, part);
			curr = curr.getParent()
					.orElse(null);
		}
		return sb.toString();
	}

	/**
	 * Write the result files from an entry to a ZIP output stream
	 * 
	 * @param entry the entry
	 * @param out the output stream
	 */
	void writeEntry(ExecutionResultImpl.Entry entry, ZipOutputStream out) {
		String entryPrefix = getEntryPrefix(entry);
		entry.getFiles()
				.forEach(file -> writeFile(file, out, entryPrefix));
	}

	/**
	 * Write an individual file from an entry to a ZIP output stream.
	 * 
	 * @param file the file
	 * @param out the output stream
	 * @param entryPrefix the prefix
	 */
	void writeFile(ResultFileImpl file, ZipOutputStream out, String entryPrefix) {
		String name = file.getResultName();
		String path = entryPrefix + name;
		try {
			out.putNextEntry(new ZipEntry(path));
			IOUtils.copy(file.getContentInputStream(), out);
		}
		catch (IOException e) {
			throw new UncheckedIOException("Unable to write entry: " + path + ".", e);
		}
	}

}
