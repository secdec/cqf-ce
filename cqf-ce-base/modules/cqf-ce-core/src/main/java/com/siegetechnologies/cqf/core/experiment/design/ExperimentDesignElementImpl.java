package com.siegetechnologies.cqf.core.experiment.design;

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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.FileBackedExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;
import com.siegetechnologies.cqf.core.experiment.design.variant.VariantSpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExperimentDesignElementImpl implements Comparable<ExperimentDesignElementImpl> {

	private static final Logger logger = LoggerFactory.getLogger(ExperimentDesignElementImpl.class);

	/**
	 * Default execution index
	 */
	public static final int DEFAULT_EXECUTION_INDEX = 100;

	/**
	 * Unique ID of this design element.
	 * <p/>
	 * <em>Implementation note:</em><br>
	 * Computed on demand via @{link #getId()}.
	 * That way subclasses can initialize the design element's
	 * identifying properties first.
	 *
	 * @see @{link ExperimentElementId#of(ExperimentDesignElementImpl)}
	 */
	private ExperimentDesignElementId id_memoized;

	/**
	 * Design element resolver used by this design element.
	 */
	protected final ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver;

	/**
	 * A map of the children of this design element, keyed by role name.
	 * Iteration over the children occurs in insertion order.
	 */
	protected final Map<String,ExperimentDesignElementImpl> children = new LinkedHashMap<>();

	/**
	 * Creates an object of this type.
	 */
	protected ExperimentDesignElementImpl() {
		this(null);
	}

	/**
	 * Creates an object of this type.
	 *
	 * @param resolver the design element resolver used by the new design element.
	 */
	public ExperimentDesignElementImpl(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		this.resolver = resolver;
	}

	/**/

	@Override
	public int hashCode() {

		return this.getId().hashCode();
	}

	@Override
	public int compareTo(ExperimentDesignElementImpl that) {

		int result = this.getId().compareTo(that.getId());
		assert result != 0 || this == that;
		//^-- each experiment design element is unique
		return result;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (o instanceof ExperimentDesignElementImpl) {

			ExperimentDesignElementImpl that = (ExperimentDesignElementImpl) o;

			boolean result = this.getId().equals(that.getId());
			assert result != true || this == that;
			//^-- each experiment design element is unique
			return result;
		}
		return false;
	}

	@Override
	public String toString() {

		return this.getId().toString();
	}

	/**/

	/**
	 * Returns this design element's resolver.
	 */
	@Deprecated
	public ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> getResolver() {
		return this.resolver;
	}

	/**
	 * Returns a unique ID for this design element.
	 *
	 * @return a unique ID for this design element
	 *
	 * @see ExperimentDesignElementId#of(ExperimentDesignElementImpl)
	 */
	@JsonUnwrapped
	public final ExperimentDesignElementId getId() {
		if (this.id_memoized == null) {
			this.id_memoized = ExperimentDesignElementId.of(this);
		}
		if (this.id_memoized == null) {
			throw new IllegalStateException("ID has not been set");
		}
		return this.id_memoized;
	}

	/**
	 * Returns a (mutable) map of the children of this item, keyed by role name.
	 * Iteration over the children occurs in insertion order.
	 *
	 * @return a (mutable) map of the children of this item, keyed by role name
	 */
	public Map<String,ExperimentDesignElementImpl> getChildren() {
		return children;
	}

	/**
	 * Returns the real path of the item, if the item is backed by files on disk.
	 * <p>
	 * The default implementation returns an empty Optional.
	 *
	 * @return the real path of the item
	 */
	public Optional<Path> getDirectory() {
		return Optional.empty();
	}

	/**
	 * Returns the default execution index for instances of an item.  The default implementation
	 * returns {@link #DEFAULT_EXECUTION_INDEX}.
	 */
	public int getDefaultExecutionIndex() {
	  return DEFAULT_EXECUTION_INDEX;
	}

	/**
	 * Returns the 'superdesign' of this design element (if any).
	 *
	 * @return the 'superdesign' of this design element (if any)
	 */
	protected Optional<? extends ExperimentDesignElementImpl> getSuperDesignElement() {
		return Optional.empty();
	}

	/**
	 * Sets the variant configuration of the item.
	 *
	 * @param spec a variant specification
	 */
	public void setVariantConfiguration(VariantSpec spec) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Helper function used to create an variation of a given ExperimentDesignElement.
	 *
	 * When inheriting from the given designElement, the following actions occur:
	 * 1. The parents parameters are inherited
	 * 2. A new set of documentation is inherited from the parent
	 * 3. The parent's ancestry is used for this designElement.
	 * 4. The parent's config is used for this designElement.
	 * 5. The parent's files are used for this designElement
	 * 6. The parent's result files are used for this designElement.
	 * @param designElement ExperimentDesignElement to inherit from
	 */
	public void inheritFrom(ExperimentDesignElementImpl designElement) {
		throw new UnsupportedOperationException();
	}


	/**
	 * Sets the item configuration of this item.
	 *
	 * @param config the configuration
	 */
	public void setConfig(FileBackedExperimentDesignElementListProvider.ElementConfig config) {
		throw new UnsupportedOperationException();
	}
	//^-- FIXME: srogers: push setConfig() down to FileBackExperimentDesignCatalogProvider.Element
	//^-- FIXME: srogers: delete FileBackExperimentDesignCatalogProvider.Element.setConfig()

	/**
	 * Returns true if the item has a non-null configuration.
	 *
	 * @return true if the item has a non-null configuration
	 *
	 * @see #setConfig(FileBackedExperimentDesignElementListProvider.ElementConfig)
	 */
	public boolean hasConfig() {
		return false;
	}
	//^-- FIXME: srogers: push hasConfig() down to FileBackExperimentDesignCatalogProvider.Element
	//^-- FIXME: srogers: delete FileBackExperimentDesignCatalogProvider.Element.hasConfig()

	/**
	 * Returns item configuration.
	 *
	 * @return item configuration
	 */
	public Optional<FileBackedExperimentDesignElementListProvider.ElementConfig> getConfig() {
		return Optional.empty();
	}
	//^-- FIXME: srogers: push getConfig() down to FileBackExperimentDesignCatalogProvider.Element
	//^-- FIXME: srogers: make FileBackExperimentDesignCatalogProvider.Element.getConfig() private

	/**
	 * Set the item's documentation.
	 *
	 * @param documentation the new documentation
	 */
	public void setDocumentation(DocumentationImpl documentation) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns item documentation.
	 *
	 * @return item documentation
	 */
	public abstract DocumentationImpl getDocumentation();

	/**
	 * Returns a list of the parameters for the item. These are a combination of
	 * the item's {@link #getOwnParameters() own parameters} along with the
	 * parameters inherited from the ancestry.
	 *
	 * @return the parameters of this item
	 */
	public List<ParameterImpl> getParameters() {
		List<ParameterImpl> ownParameters = getOwnParameters();
		List<ParameterImpl> superParameters = getSuperDesignElement()
				.map(ExperimentDesignElementImpl::getParameters)
				.orElseGet(Collections::emptyList);
		return merge(superParameters, ownParameters);
	}

	static List<ParameterImpl> merge(List<ParameterImpl> superParameters, List<ParameterImpl> ownParameters) {
		Map<String, ParameterImpl> ownParametersByName = ownParameters.stream()
				.collect(toMap(ParameterImpl::getName, Function.identity()));

		Set<String> superParameterNames = superParameters.stream()
				.map(ParameterImpl::getName).collect(toSet());

		Stream<ParameterImpl> updatedSuperParameters = superParameters.stream()
				.map(superParameter -> merge(superParameter, ownParametersByName.get(superParameter.getName())));

		Stream<ParameterImpl> newOwnParameters = ownParameters.stream()
				.filter(p -> !superParameterNames.contains(p.getName()));

		return Stream.concat(updatedSuperParameters, newOwnParameters).collect(toList());
	}

	static ParameterImpl merge(ParameterImpl superParameter, ParameterImpl ownParameter) {
		return ownParameter == null ? superParameter : ownParameter.copyAndUpdateWithDefaultsFrom(superParameter);
	}

	/**
	 * Returns list of specified parameters.
	 *
	 * @return list of specified parameters
	 */
	public abstract List<ParameterImpl> getOwnParameters();

	/**
	 * Returns description of the item.
	 *
	 * <p>The default implementation returns {@code getDocumentation().getInfo()}.
	 *
	 * @return description of the item
	 *
	 * @deprecated Use {@link #getDocumentation()} and {@link DocumentationImpl#getInfo()} instead.
	 */
	@Deprecated
	public String getDescription() {
		return getDocumentation().getInfo();
	}

	/**
	 * Returns the ancestry of this design element, as a list.
	 * This is a list of this design element, its parent, its grandparent, and so on.
	 *
	 * @return the ancestry of this design element
	 */
	public List<ExperimentDesignElementImpl> getAncestry() {
		List<ExperimentDesignElementImpl> ancestry = new ArrayList<>();
		ExperimentDesignElementImpl x = this;
		while (x != null) {
			ancestry.add(x);
			x = x.getSuperDesignElement().orElse(null);
		}
		return ancestry;
	}

	/**
	 * Returns a list of required files that are needed for item deployment.
	 * These are provided as relative paths within the RequiredFiles directory
	 * within the item directory.
	 *
	 * @return a list of required file paths
	 * @throws IOException if an I/O error occurs
	 */
	public List<String> getRequiredFiles() throws IOException {
		return Collections.emptyList();
	}

	/**
	 * Returns a list of required scripts that are needed for item deployment.
	 * These are provided as relative paths within the Scripts directory within
	 * the item directory.
	 *
	 * @return a list of script paths
	 * @throws IOException if an I/O error occurs
	 */
	public List<String> getScriptFiles() throws IOException {
		return Collections.emptyList();
	}

	/**
	 * Returns files associated with this item.
	 *
	 * @return files associated with this item
	 */
	public List<String> getFiles() {
		return Collections.emptyList();
	}

	/**
	 * Returns the simple name of this item.
	 *
	 * @return the simple name of this item
	 */
	public abstract String getName();

	/**
	 * Returns the category of this item.
	 *
	 * @return the category of this item
	 */
	public abstract String getCategory();

	/**
	 * Returns the status of the item, which is either a string containing some
	 * human-readable status message or diagnostic, or null.
	 *
	 * @return the status of the item
	 */
	public String getStatus() {
		return null;
	}

	/**
	 * Set the status of this item.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the result files specified in the configuration.  These
	 * are the files that should be retrieved from a host at the completion
	 * of an experiment; they capture the result of this type of element.
	 *
	 * @return a set of result files
	 */
	public Set<ResultFileImpl> getResultFiles() {
		return Collections.emptySet();
	}

	/**
	 * Returns a FileInputStream for the named file in this item's directory.
	 *
	 * @param filename the name of the file
	 * @return a FileInputStream for the named file
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public InputStream getFile(String filename) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a list of items compatible with this item.
	 *
	 * @return a list of items compatible with this item
	 */
	public List<String> getCompatibleWith() {
		return Collections.emptyList();
	}

	/**
	 * Gets the name of this item variant, if any.
	 *
	 * @return name of the variant, or null
	 */
	public String getVariantName() {
		return null;
	}
	//^-- TODO: make optional

	/**
	 * Returns the item of which this is a variant, or null.
	 *
	 * @return the item of which this is a variant, or null
	 */
	public ExperimentDesignElementImpl getVariantOf() {
		return null;
	}
	//^-- TODO: make optional

	/**
	 * Returns variants defined in the configuration of this item.
	 *
	 * @return variants defined in the configuration of this item
	 */
	@JsonIgnore
	public List<VariantSpec> getVariants() {
		return Collections.emptyList();
	}

	/**
	 * Prepares an experiment element for execution.
	 * The experiment element must be directly associated with this design element.
	 *
	 * @param experimentElement
	 */
	public void prepareExperimentElementForExecution(ExperimentElementImpl experimentElement) {

		if (this != experimentElement.getDesign()) {
			throw new IllegalArgumentException("mismatched design for experiment element: " + experimentElement);
		}

		logger.info(String.format("[%s] preparing for execution: %s", this, experimentElement));
	}

}

