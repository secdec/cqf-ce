package com.siegetechnologies.cqf.core.experiment.design.catalog.providers;

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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class FileBackedExperimentDesignElementListProvider_ElementTest
{

	private FileSystem fs;
	private Path directory;
	private ExperimentDesignElementImpl fbi;

	@Before
	public void init() throws IOException {
		fs = Jimfs.newFileSystem(Configuration.unix());
		directory = fs.getPath("/Subject/WatchMe/");
		Files.createDirectory(fs.getPath("/Subject"));
		Files.createDirectory(fs.getPath("/Subject/WatchMe"));
		ExperimentDesignElementIdResolver<ExperimentDesignElementImpl> resolver = null;
		fbi = new FileBackedExperimentDesignElementListProvider.Element(directory, resolver);
	}

	private static <T> Set<T> makeSet(@SuppressWarnings("unchecked") T... ts) {
		return new HashSet<>(Arrays.asList(ts));
	}

	@Test(expected = UncheckedIOException.class)
	public void testGetConfig_missing() {
		fbi.getConfig();
	}

	@Test
	public void testGetConfig() throws IOException {
		Path cfg = directory.resolve("config.json");

		// Create the config.json file and just put trivial content
		// into it. It's an "empty" JSON object, but that's enough
		// to parse and create a configuration from, even if it's
		// not a great one.
		Files.createFile(cfg);
		try (OutputStream out = Files.newOutputStream(cfg)) {
			out.write("{}".getBytes());
		}
		Optional<FileBackedExperimentDesignElementListProvider.ElementConfig> config = fbi.getConfig();
		assertTrue("A trivial configuration should have been read from the file.", config.isPresent());

	}

	@Test(expected = UncheckedIOException.class)
	public void testGetDocumentation_missing() {
		fbi.getDocumentation();
	}

	@Test
	public void testGetDocumentation() throws IOException {
		Path doc = directory.resolve("documentation.txt");
		Files.createFile(doc);
		try (OutputStream out = Files.newOutputStream(doc); PrintStream p = new PrintStream(out)) {
			p.println("@info some item documentation");
			p.println("@param a - parameter 1");
			p.println("@param b - parameter 2");
		}
		DocumentationImpl itemDoc = fbi.getDocumentation();
		assertNotNull(itemDoc);
		assertEquals("some item documentation", itemDoc.getInfo());
		assertEquals("parameter 1", itemDoc.getParams()
				.get("a"));
		assertEquals("parameter 2", itemDoc.getParams()
				.get("b"));
	}

	@Test
	public void testRequiredFiles() throws IOException {
		List<String> reqs = fbi.getRequiredFiles();
		assertTrue(reqs.isEmpty());

		Path requiredFiles = directory.resolve("RequiredFiles");
		Files.createDirectories(requiredFiles);

		reqs = fbi.getRequiredFiles();
		assertTrue(reqs.isEmpty());

		Files.createFile(requiredFiles.resolve("req1"));
		Files.createFile(requiredFiles.resolve("req2"));
		Files.createFile(requiredFiles.resolve("req3"));
		Files.createDirectory(requiredFiles.resolve("Windows"));
		Files.createFile(requiredFiles.resolve("Windows/req4"));
		reqs = fbi.getRequiredFiles();
		assertEquals(4, reqs.size());

		assertEquals(makeSet("req1", "req2", "req3", "Windows/req4"), new HashSet<>(reqs));
	}

	@Test
	public void testScriptFiles() throws IOException {
		List<String> scripts = fbi.getScriptFiles();
		assertTrue(scripts.isEmpty());

		Path scriptFiles = directory.resolve("Scripts");
		Files.createDirectories(scriptFiles);

		scripts = fbi.getScriptFiles();
		assertTrue(scripts.isEmpty());

		Files.createFile(scriptFiles.resolve("req1"));
		Files.createFile(scriptFiles.resolve("req2"));
		Files.createFile(scriptFiles.resolve("req3"));
		Files.createDirectory(scriptFiles.resolve("Windows"));
		Files.createFile(scriptFiles.resolve("Windows/req4"));
		scripts = fbi.getScriptFiles();
		assertEquals(4, scripts.size());

		assertEquals(makeSet("req1", "req2", "req3", "Windows/req4"), new HashSet<>(scripts));
	}

	@Test
	public void testGetFiles() throws IOException {
		List<String> files = fbi.getFiles();
		assertTrue(files.isEmpty());

		Files.createFile(directory.resolve("config.json"));
		Files.createFile(directory.resolve("documentation.txt"));
		files = fbi.getFiles();
		assertEquals(2, files.size());
	}

	@Test(expected = IOException.class)
	public void getGetFile_missing() throws IOException {
		fbi.getFile("foobar.txt");
	}

	@Test
	public void testGetFile() throws IOException {
		Files.createFile(directory.resolve("config.json"));
		fbi.getFile("config.json");
	}

	/**
	 * sTest some of the "easy" methods that can be tested. Many of the
	 * Element methods actually depend on some filesystem interaction. It
	 * might be worth stubbing out a filesystem API wrapper, but that's not
	 * something that we've done yet.
	 */
	@Test
	public void test() {
		Path directory = fs.getPath("foo/bar");
		ExperimentDesignElementIdResolver<ExperimentDesignElementImpl> resolver = null;
		FileBackedExperimentDesignElementListProvider.Element item = new FileBackedExperimentDesignElementListProvider.Element(directory, resolver);

		assertEquals("bar", item.getName());
		assertEquals("foo", item.getCategory());

		assertSame(directory, item.getDirectory()
				.get());

		String repr = item.toString();
		assertTrue(repr.contains("foo/bar"));
		assertTrue(repr.contains("name=bar"));
		assertTrue(repr.contains("category=foo"));

		assertTrue(item.hasConfig());
	}

	// FIXME: srogers: add unit tests for properties backed by "config.js"
}
