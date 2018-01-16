package com.siegetechnologies.cqf.core.experiment.design.catalog;

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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.ProviderBackedExperimentDesignElementListProvider;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * An item catalog built from a number of item root paths and
 * a collection of item providers.
 * 
 * @author taylorj
 */
public class ExperimentDesignCatalogImpl implements
		ExperimentDesignElementIdResolver<ExperimentDesignElementImpl>
{
	private final ProviderBackedExperimentDesignElementListProvider<ExperimentDesignElementImpl> provider;

	private final Map<ExperimentDesignElementId, ExperimentDesignElementImpl> items;
	private boolean items_hasBeenPopulated;

	/**
	 * Creates a new catalog that starts out empty, and stays that way.
	 */
	public ExperimentDesignCatalogImpl() {
		this(new ArrayList<ExperimentDesignElementListProvider<? extends ExperimentDesignElementImpl>>());
	}

	/**
	 * Creates a new catalog that will contain items provided by a number of sub-providers
	 * as well as items loaded from provided root directories.
	 * 
	 * @param subProviders the providers that provide other items
	 */
	public ExperimentDesignCatalogImpl(Collection<ExperimentDesignElementListProvider<? extends ExperimentDesignElementImpl>> subProviders) {
		Collection<ExperimentDesignElementListProvider<? extends ExperimentDesignElementImpl>> allProviders = new ArrayList<>();

		this.provider = new ProviderBackedExperimentDesignElementListProvider<>(subProviders);
		
		this.items = new HashMap<>();
		this.items_hasBeenPopulated = false;
	}

	/**/


	@Override
	public synchronized int hashCode() {

		this.loadItems();
		return this.items.hashCode();
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (o instanceof ExperimentDesignCatalogImpl) {

			ExperimentDesignCatalogImpl that = (ExperimentDesignCatalogImpl) o;

			synchronized(this) {
				this.loadItems();
				that.loadItems();
				return this.items.equals(that.items);
			}
		}
		return false;
	}

	@Override
	public synchronized String toString() {

		this.loadItems();
		return this.items.toString();
	}

	/**/

	/**
	 * Loads all items from the various providers. Updates the internal cache.
	 * <p/>
	 * <em>Implementation note:</em><br>
	 * By design, this operation is idempotent.
	 *
	 * @see @{link #reloadItems()}
	 */
	public final synchronized void loadItems() {
		if (items_hasBeenPopulated) {
			return;
		}

		Map<ExperimentDesignElementId, ExperimentDesignElementImpl> itemsProvided =
				provider.getDesignElements(this).stream()
						.collect(toMap(ExperimentDesignElementImpl::getId, Function.identity()));

		items.putAll(itemsProvided);
		items_hasBeenPopulated = true;
	}

	/**
	 * Unloads all items from the various providers. Flushes the internal cache.
	 */
	public final synchronized void unloadItems() {
		items_hasBeenPopulated = false;
		items.clear();
	}

	/**
	 * Same as @{link #unloadItems()} followed by @{link #loadItems()}.
	 */
	public final synchronized void reloadItems() {
		unloadItems();
		loadItems();
	}

	/**
	 * Returns the ID of each design item available in the catalog.
	 * 
	 * @return the ID of each design item available in the catalog
	 */
	public synchronized Collection<ExperimentDesignElementId> getItems() {
		return Collections.unmodifiableCollection(new TreeSet<>(items.keySet()));
	}

	@Override
	public synchronized Optional<ExperimentDesignElementImpl> resolve(ExperimentDesignElementId designElementId) {
		return Optional.ofNullable(items.get(designElementId));
	}

}
