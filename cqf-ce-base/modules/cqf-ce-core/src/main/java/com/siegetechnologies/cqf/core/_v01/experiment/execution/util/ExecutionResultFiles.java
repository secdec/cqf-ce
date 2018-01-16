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

import com.siegetechnologies.cqf.core.util.TemporaryFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toList;

/**
 * Manages the storage and indexing of Result Files from executions
 */
public class ExecutionResultFiles {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ExecutionResultFiles.class);
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-kkmmss");

	/**
	 * The name of the default context used for methods that omit the context name.
	 * 
	 * @see #save(String, Path)
	 * @see #register(String)
	 */
	public static final String DEFAULT_CONTEXT = "$$DEFAULT$$";
	
	/**
	 * A map from context and name pairs to paths.
	 */
	private final Map<ContextAndName, Path> contextMap;

	/**
	 * A path creator for the result files. The "standard" implementation is
	 * {@link #createTimestampedTemporaryPath(String, String)}, but this can be
	 * injected with the package-private constructor in order to use an
	 * implementation that does not litter the file system.
	 */
	private final PathCreator pathCreator;

	/**
	 * Creates a new instance with an empty context map and that creates
	 * timestamped temporary result files.
	 */
	public ExecutionResultFiles() {
		this(new HashMap<>(), ExecutionResultFiles::createTimestampedTemporaryPath);
	}

	/**
	 * Creates a new instance with provided context map and path creator.
	 * 
	 * @param contextMapEx the context map
	 * @param pathCreator the path creator
	 */
	ExecutionResultFiles(HashMap<ContextAndName, Path> contextMapEx, PathCreator pathCreator) {
		this.contextMap = contextMapEx;
		this.pathCreator = pathCreator;
	}

	@Override
	public String toString() {
		return String.format("ExecutionResultFiles(entries=%s)", contextMap);
	}

	/**
	 * Creates a temporary file path whose name is based on a context, name, and
	 * current time. This does not actually create the file, but just generates
	 * the path.
	 * 
	 * @param context the context
	 * @param name the name
	 * @return the temporary file path
	 */
	static Path createTimestampedTemporaryPath(String context, String name) {
		LocalDateTime now = LocalDateTime.now();
		String dateString = now.format(DATE_FORMATTER);
		String filename = String.format("%s-%s-%s.zip", context, name, dateString); // FIXME: STRING: srogers
		return TemporaryFiles.create(filename);
	}

	/**
	 * Registers a new path in the given context with the given name. The path
	 * to the new path will be:
	 * {@code [TempPath]/[context]-[name]-[yyyy-MM-dd-kkmmss]}. This generated
	 * path will be stored under the given name in the index file.
	 * 
	 * @param context Context to save in
	 * @param name Name of the file. Used to generate the unique name
	 * @return Path to the new file
	 */
	public Path register(String context, String name) {
		Path newFile = pathCreator.create(context, name);
		save(context, name, newFile);
		return newFile;
	}

	/**
	 * Registers a new path in the default context.
	 * 
	 * @param name the name
	 * @return the path
	 * 
	 * @see #register(String, String)
	 */
	public Path register(String name) {
		return register(DEFAULT_CONTEXT, name);
	}

	/**
	 * Saves a path for a provided context and name.
	 * 
	 * @param context the context
	 * @param name the name
	 * @param path the path
	 * @return the previous path associated with the context and name
	 */
	public Path save(String context, String name, Path path) {
		return contextMap.put(new ContextAndName(context, name), path);
	}

	/**
	 * Saves a path for the default context and a provided name.
	 * 
	 * @param name the name
	 * @param path the path
	 * @return the previous path associated with the default context and
	 *         provided name
	 */
	public Path save(String name, Path path) {
		return save(DEFAULT_CONTEXT, name, path);
	}

	/**
	 * Returns the path associated with a provided context and name.
	 * 
	 * @param context the context
	 * @param name the name
	 * @return the path
	 */
	public Optional<Path> get(String context, String name) {
		ContextAndName key = new ContextAndName(context, name);
		if (contextMap.containsKey(key)) {
			// It's possible to store null in a map, but we really
			// shouldn't have null in here, so we use Optional.of,
			// not Optional.ofNullable.
			return Optional.of(contextMap.get(key));
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * Returns the path associated with the default context and a provided name.
	 * 
	 * @param name the name
	 * @return the path
	 */
	public Optional<Path> get(String name) {
		return get(DEFAULT_CONTEXT, name);
	}

	/**
	 * Returns a list of all names that have paths for the default context.
	 * 
	 * @return the names
	 */
	public List<String> list() {
		return list(DEFAULT_CONTEXT);
	}

	/**
	 * Returns a list of all the names that have associated paths for a provided
	 * context.
	 * 
	 * @param context the context
	 * @return the names
	 */
	public List<String> list(String context) {
		return contextMap.keySet()
				.stream()
				.filter(can -> Objects.equals(context, can.getContext()))
				.map(ContextAndName::getName)
				.collect(toList());
	}

	/**
	 * Returns a list of the all the paths associated with a provided context,
	 * under any name.
	 * 
	 * @param context the context
	 * @return the paths
	 */
	public List<Path> all(String context) {
		return contextMap.entrySet()
				.stream()
				.filter(e -> Objects.equals(context, e.getKey()
						.getContext()))
				.map(Entry::getValue)
				.collect(toList());
	}

	/**
	 * Returns a list of all contexts.
	 * 
	 * @return the contexts
	 */
	public List<String> contexts() {
		return contextMap.keySet()
				.stream()
				.map(ContextAndName::getContext)
				.collect(toList());
	}

	/**
	 * Functional interface for creating a path to store an experiment result,
	 * based on a context and a name.
	 * 
	 * @author taylorj
	 */
	@FunctionalInterface
	interface PathCreator {
		/**
		 * Creates the path for a result file.
		 * 
		 * @param context the context
		 * @param name the name
		 * @return the path
		 */
		Path create(String context, String name);
	}

	/**
	 * Key for result files. Each key is a pair of a context (a string) and a
	 * name (a string).
	 * 
	 * @author taylorj
	 */
	static class ContextAndName {
		private final String context;
		private final String name;

		/**
		 * Creates a new instance with provided context and name.
		 * 
		 * @param context the context
		 * @param name the name
		 */
		public ContextAndName(String context, String name) {
			this.context = Objects.requireNonNull(context, "context must not be null");
			this.name = Objects.requireNonNull(name, "name must not be null");
		}

		/**
		 * Returns the context.
		 * 
		 * @return the context
		 */
		public String getContext() {
			return context;
		}

		/**
		 * Returns the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return String.format("ContextAndName(context=%s, name=%s)", context, name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(context, name);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (obj instanceof ContextAndName) {
				ContextAndName can = (ContextAndName) obj;
				return Objects.equals(context, can.context) && Objects.equals(name, can.name);
			}
			return false;
		}
	}
}
