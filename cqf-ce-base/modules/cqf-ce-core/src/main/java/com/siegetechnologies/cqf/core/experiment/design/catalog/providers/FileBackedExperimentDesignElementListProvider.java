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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;
import com.siegetechnologies.cqf.core.experiment.design.variant.VariantSpec;
import com.siegetechnologies.cqf.core.util.Functions;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * An items provider that loads items from disk. The provider searches within a
 * directory for any "config.json" files and assumes that they are within a
 * directory structure of the form:
 * 
 * <pre>
 * Category/
 *     ExperimentDesignElement Name/
 *         config.json        (mandatory)
 *         documentation.txt  (mandatory)
 *         Scripts/           (optional)
 *         RequiredFiles/     (optional)
 * </pre>
 * 
 * If the root directory is not present, or is not a directory, then an empty
 * list of items is returned.
 * 
 * @author taylorj
 */
public class FileBackedExperimentDesignElementListProvider implements ExperimentDesignElementListProvider<FileBackedExperimentDesignElementListProvider.Element>
{

	private Path root;

	/**
	 * Creats a new object of this type.
	 */
	protected FileBackedExperimentDesignElementListProvider() {
		this(null);
	}
	/**
	 * Creates a new object of this type.
	 * 
	 * @param root the design item root directory
	 */
	public FileBackedExperimentDesignElementListProvider(Path root) {
		initRoot(root);
	}

	protected void initRoot(Path value) {
		assert this.root == null;

		this.root = value;
	}

	private boolean isConfigJson(Path path) {
		return "config.json".equals(path.getFileName()
				.toString());
	}

	/**
	 * Returns true if the root directory exists.
	 * 
	 * @return true if the root directory exists
	 */
	private boolean rootExists() {
		return Files.exists(root) && Files.isDirectory(root);
	}

	/**
	 * Returns a stream of the finishable items loaded from the root directory.
	 * 
	 * @return the stream of finishable items
	 */
	private Stream<FinishableElement> loadItems() {
		if (!rootExists()) {
			return Stream.empty();
		}
		return Functions.uncheck(() -> Files.walk(root)).get()
				.filter(this::isConfigJson)
				.map(Path::getParent)
				.map(p -> resolver -> new Element(p, resolver));
	}

	/*
	 * Loads finishable items from the root directory using #loadItems(),
	 * finishes each one with a provided resolver, and collects the items into
	 * a list which is then returned.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.siegetechnologies.cqf.expdesign.ExperimentDesignElementListProvider#getDesignElements(com.
	 * siegetechnologies.cqf.expdesign.ExperimentDesignElementIdResolver)
	 */
	@Override
	public List<Element> getDesignElements(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		return loadItems().map(finishableElement -> finishableElement.finish(resolver))
				.collect(toList());
	}

	/**
	 * Functional interface around an "item-to-be" that can be completed in the
	 * presence of an item repository.
	 * 
	 * @author taylorj
	 */
	@FunctionalInterface
	private interface FinishableElement {
		/**
		 * Returns the finished item.
		 * 
		 * @param resolver the items resolver
		 * @return the finished item
		 */
		Element finish(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver);
	}

	/**
	 * Implementation of ExperimentDesignElement based on an on-disk directory. The directory is should
	 * contain "config.json" and "documentation.txt" files. It may optionally
	 * contain "RequiredFiles" and "Scripts" directories. Element extends
	 * ExperimentDesignElement, which is capable of resolving super-item values dynamically.
	 *
	 * @author taylorj
	 */
	public static class Element extends ExperimentDesignElementImpl {

		private final Path directory;

		/**
		 * Creates a new instance for the specified directory and item resolver.
		 * This includes a call to the {@link ExperimentDesignElementImpl} constructor with the
		 * item resolver.
		 *
		 * @param directory the item directory
		 * @param resolver the item resolver
		 *
		 * @see ExperimentDesignElementImpl#ExperimentDesignElementImpl(ExperimentDesignElementIdResolver)
		 */
		public Element(Path directory, ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
			super(resolver);
			this.directory = Objects.requireNonNull(directory, "directory must not be null");
		}

		@Override
		public String toString() {
			return String.format("Element(name=%s, category=%s, directory=%s)",
					getName(),
					getCategory(),
					getDirectory().get());
		}

		/**
		 * A file-backed item always has a directory. That is, this optional will
		 * always have a value.
		 */
		@Override
		public Optional<Path> getDirectory() {
			return Optional.of(directory);
		}

		@Override
		public DocumentationImpl getDocumentation() {
			try {
				InputStream in = getFile("documentation.txt");
				return DocumentationImpl.fromInputStream(in);
			}
			catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		@Override
		public String getName() {
			int nameCount = directory.getNameCount();
			return directory.getName(nameCount - 1)
					.getFileName()
					.toString();
		}

		@Override
		public String getCategory() {
			int nameCount = directory.getNameCount();
			return directory.getName(nameCount - 2)
					.getFileName()
					.toString();
		}

		@Override
		public Optional<ElementConfig> getConfig() {
			try {
				ElementConfig config = ElementConfig.fromInputStream(getFile("config.json"));
				return Optional.of(config);
			}
			catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		@Override
		public List<String> getRequiredFiles() throws IOException {
			Path requiredFilesDirectory = directory.resolve("RequiredFiles");

			if (!Files.exists(requiredFilesDirectory)) {
				return Collections.emptyList();
			}

			return Files.walk(requiredFilesDirectory)
					.filter(Files::isRegularFile)
					.map(requiredFilesDirectory::relativize)
					.map(Path::toString)
					.collect(toList());
		}

		@Override
		public List<String> getScriptFiles() throws IOException {
			Path scriptFilesDir = directory.resolve("Scripts");

			if (!Files.exists(scriptFilesDir)) {
				return Collections.emptyList();
			}

			return Files.walk(scriptFilesDir)
					.filter(Files::isRegularFile)
					.map(scriptFilesDir::relativize)
					.map(Path::toString)
					.collect(toList());
		}

		@Override
		public InputStream getFile(String filename) throws IOException {
			return Files.newInputStream(directory.resolve(filename));
		}

		@Override
		public List<String> getFiles() {
			return Functions.uncheck(() -> Files.walk(directory))
					.get()
					.filter(Files::isRegularFile)
					.map(directory::relativize)
					.map(Path::toString)
					.collect(toList());
		}

		@Override
		public Optional<? extends ExperimentDesignElementImpl> getSuperDesignElement() {
		  return getConfig()
				  .flatMap(ElementConfig::getSuperDesignElementId)
				  .flatMap(id -> this.resolver.resolve(id));
		}

		@Override
		public boolean hasConfig() {
			return true;
		}

		@Override
		public List<ParameterImpl> getOwnParameters() {
			return getConfig().map(ElementConfig::getParameters)
					.orElseGet(Collections::emptyList);
		}

		@Override
		public List<String> getCompatibleWith() {
			return getConfig().map(ElementConfig::getCompatibleWith)
					.orElseGet(Collections::emptyList);
		}

		@Override
		public List<VariantSpec> getVariants() {
			return getConfig().map(ElementConfig::getVariants)
					.orElseGet(Collections::emptyList);
		}

		@Override
		public Set<ResultFileImpl> getResultFiles() {
			return getConfig().map(ElementConfig::getResultFiles)
					.map(rfs -> (Set<ResultFileImpl>) new HashSet<>(rfs))
					.orElseGet(Collections::emptySet);
		}
	}

	/**
	 * ExperimentDesignElement Configurations contain information about the item,
	 * the parameters it can accept, etc.  This file corresponds
	 * directly to the config.json files located within items,
	 * and the fields in this class are read from the config.json
	 * files.  However, properties in config.json files not that
	 * do not correspond to fields in this class are silently ignored.
	 */
	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class ElementConfig {
		/**
		 * An object mapper used by {@link #fromInputStream}.
		 */
		protected static final ObjectMapper objectMapper = new ObjectMapper();

		/**
		 * The ID of the 'superdesign' of this config's design element (if any).
		 */
		@JsonProperty("extends")
		public ExperimentDesignElementId superDesignElementId;

		/**
		 * A list of result file maps.  Each map maps "target_file"
		 * to a pathname that should be copied back during data retrieval.
		 */
		@JsonProperty(value="result_files")
		public List<ResultFileImpl> resultFiles = Collections.emptyList();

		/**
		 * The name of the item.
		 */
		@JsonProperty(required=true)
		public String name;

		/**
		 * A description of an item.
		 */
		@JsonProperty(required=true)
		public String description;


		/**
		 * An option list of items this config is compatible with
		 */
		@JsonProperty(value="compatible_with")
		public List<String> compatibleWith = Collections.emptyList();

		/**
		 * A set of specifications for services provided by this item.
		 */
		@JsonProperty(value="provides")
		public Map<String,String> provides = Collections.emptyMap();

		/**
		 * A list of parameters of the item.
		 */
		@JsonProperty(value="parameters")
		public List<ParameterImpl> parameters = Collections.emptyList();

		/**
		 * List of configuration defined variants for this item.
		 */
		public List<VariantSpec> variants = Collections.emptyList();

		/**
		 * Returns the parameters of the configuration.
		 *
		 * @return the parameters of the configuration
		 */
		public List<ParameterImpl> getParameters() {
			return parameters;
		}

		public Optional<ExperimentDesignElementId> getSuperDesignElementId() {
		  return Optional.ofNullable(superDesignElementId);
		}

		/**
		 * Returns list of specified result files.
		 *
		 * @return list of specified result files
		 */
		public List<ResultFileImpl> getResultFiles() {
			return resultFiles;
		}

		/**
		 * Returns list of item compatibilities.
		 *
		 * @return list of item compatibilities
		 */
		public List<String> getCompatibleWith() {
			return compatibleWith;
		}

		/**
		 * Returns list of variant specifications.
		 *
		 * @return list of variant specifications
		 */
		public List<VariantSpec> getVariants() {
			return variants;
		}

		/**
		 * Returns an item config corresponding to JSON read from an input stream.
		 *
		 * @param in the input stream
		 * @return the item configuration
		 *
		 * @throws IOException if an I/O error occurs
		 */
		public static ElementConfig fromInputStream(InputStream in) throws IOException {
			JsonNode node = objectMapper.readTree(in);
			return objectMapper.convertValue(node, ElementConfig.class);

		}
	}
}
