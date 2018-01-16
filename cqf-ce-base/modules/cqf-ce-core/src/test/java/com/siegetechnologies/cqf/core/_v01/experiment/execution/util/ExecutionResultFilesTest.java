package com.siegetechnologies.cqf.core._v01.experiment.execution.util;

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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ExecutionResultFilesTest {

	private ExecutionResultFiles executionResultFiles;

	@Before
	public void init() {
		/*
		 * Create a ExecutionResultFiles instance for testing that uses a custom provided
		 * path creator. The default path creator does not actually create the
		 * files on disk, but only generates the paths. Even so, using one that
		 * does not depend on the actual file system or the current time, etc.,
		 * may be a bit more robust for testing.
		 */
		executionResultFiles = new ExecutionResultFiles(new HashMap<>(), (ctx, name) -> Paths.get(ctx + "_" + name));
	}

	@Test
	public void testDefaultConstructor() {
		ExecutionResultFiles rf = new ExecutionResultFiles();
		assertNotNull(rf);
	}
	
	@Test
	public void testContextAndNameEquals() {
		ExecutionResultFiles.ContextAndName can = new ExecutionResultFiles.ContextAndName("ctx", "name");
		assertTrue(can.equals(can));
		assertFalse(can.equals(null));
		assertFalse(can.equals(new ExecutionResultFiles.ContextAndName("ctx", "otherName")));
		assertFalse(can.equals(new ExecutionResultFiles.ContextAndName("otherContext", "name")));
		assertFalse(can.equals("aDifferentTypeObject"));
	}

	/**
	 * Checks that the default context is not visible when there are no
	 * result files, but that it is visible once something has been added.
	 */
	@Test
	public void testDefaultContextIsVisible() {
		assertFalse(executionResultFiles.contexts()
				.contains(ExecutionResultFiles.DEFAULT_CONTEXT));
		executionResultFiles.save("name", mock(Path.class));
		assertTrue(executionResultFiles.contexts()
				.contains(ExecutionResultFiles.DEFAULT_CONTEXT));
	}

	@Test
	public void testTimestampedTemporaryFileCreator() throws IOException {
		Path p = ExecutionResultFiles.createTimestampedTemporaryPath("theContext", "theName");
		assertTrue(Files.notExists(p));
		String fileName = p.getFileName()
				.toString();
		assertTrue(fileName.contains("theContext"));
		assertTrue(fileName.contains("theName"));
	}

	@Test
	public void testSaveAndGetWithDefaultContext() {
		Path expected = Paths.get("abc");
		executionResultFiles.save("name", expected);
		Path actual = executionResultFiles.get("name")
				.get();
		assertSame(expected, actual);
	}

	@Test
	public void testSaveAndGetWithContext() {
		String ctx = "context";
		String name = "name";
		Path expected = Paths.get("abc");
		executionResultFiles.save(ctx, name, expected);
		Path actual = executionResultFiles.get(ctx, name)
				.get();
		assertSame(expected, actual);
	}

	@Test
	public void testGetEmpty() {
		assertFalse(executionResultFiles.get("ctx", "name")
				.isPresent());
	}

	@Test
	public void testRegisterWithContext() {
		assertFalse(executionResultFiles.get("c", "n")
				.isPresent());
		Path path = executionResultFiles.register("c", "n");
		assertNotNull(path);
		assertSame(path, executionResultFiles.get("c", "n")
				.get());
	}

	@Test
	public void testRegisterWithDefaultContext() {
		assertFalse(executionResultFiles.get("n")
				.isPresent());
		Path path = executionResultFiles.register("n");
		assertNotNull(path);
		assertSame(path, executionResultFiles.get("n")
				.get());
	}

	@Test
	public void listWithDefaultContext() {
		Path p1 = mock(Path.class);
		Path p2 = mock(Path.class);
		Path p3 = mock(Path.class);

		assertEquals(Collections.emptyList(), executionResultFiles.list());

		executionResultFiles.save("name1", p1);
		assertEquals(new HashSet<>(Arrays.asList("name1")), new HashSet<>(executionResultFiles.list()));

		executionResultFiles.save("name2", p2);
		assertEquals(new HashSet<>(Arrays.asList("name1", "name2")), new HashSet<>(executionResultFiles.list()));

		executionResultFiles.save("name3", p3);
		assertEquals(new HashSet<>(Arrays.asList("name1", "name2", "name3")), new HashSet<>(executionResultFiles.list()));
	}

	@Test
	public void testContexts() {
		assertEquals(Collections.emptyList(), executionResultFiles.contexts());

		executionResultFiles.save("c1", "n1", mock(Path.class));
		assertEquals(new HashSet<>(Arrays.asList("c1")), new HashSet<>(executionResultFiles.contexts()));

		executionResultFiles.save("c2", "n1", mock(Path.class));
		assertEquals(new HashSet<>(Arrays.asList("c1", "c2")), new HashSet<>(executionResultFiles.contexts()));
	}

	@Test
	public void testToString() {
		executionResultFiles.save("c1", "n1", Paths.get("p1"));
		executionResultFiles.save("c1", "n2", Paths.get("p2"));
		executionResultFiles.save("c2", "n1", Paths.get("p3"));
		executionResultFiles.save("c2", "n2", Paths.get("p4"));
		String s = executionResultFiles.toString();
		assertNotNull(s);
		assertTrue(s.contains("c1"));
		assertTrue(s.contains("c2"));
		assertTrue(s.contains("n1"));
		assertTrue(s.contains("n2"));
		assertTrue(s.contains("p1"));
		assertTrue(s.contains("p2"));
		assertTrue(s.contains("p3"));
		assertTrue(s.contains("p4"));
	}

	@Test
	public void listWithContext() {
		Path p1 = mock(Path.class);
		Path p2 = mock(Path.class);
		Path p3 = mock(Path.class);
		Path p4 = mock(Path.class);

		executionResultFiles.save("c1", "name1", p1);
		executionResultFiles.save("c1", "name2", p2);
		executionResultFiles.save("c2", "name3", p3);
		executionResultFiles.save("c3", "name4", p4);

		Set<String> actual = executionResultFiles.list("c1")
				.stream()
				.collect(toSet());
		Set<String> expected = new HashSet<>(Arrays.asList("name1", "name2"));
		assertEquals(expected, actual);

		assertEquals(Collections.emptyList(), executionResultFiles.list("cNotExists"));
	}

	@Test
	public void testAll() {
		Path p1 = mock(Path.class);
		Path p2 = mock(Path.class);
		Path p3 = mock(Path.class);
		Path p4 = mock(Path.class);

		executionResultFiles.save("c1", "name1", p1);
		executionResultFiles.save("c1", "name2", p2);
		executionResultFiles.save("c2", "name3", p3);
		executionResultFiles.save("c3", "name4", p4);

		Set<Path> actual = new HashSet<>(executionResultFiles.all("c1"));
		Set<Path> expected = new HashSet<>(Arrays.asList(p1, p2));
		assertEquals(actual, expected);

		assertEquals(Collections.emptyList(), executionResultFiles.all("notExists"));
	}

}
