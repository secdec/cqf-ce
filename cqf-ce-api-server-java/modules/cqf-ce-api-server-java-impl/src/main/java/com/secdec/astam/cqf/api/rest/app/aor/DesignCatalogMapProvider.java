package com.secdec.astam.cqf.api.rest.app.aor;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-impl
 * %%
 * Copyright (C) 2016 - 2017 Applied Visions, Inc.
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

import com.secdec.astam.cqf.api.rest.app.aor.impl.ResourceCollectionProviderBase;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignCatalogImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.CoreBuiltinExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.FileBackedExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.ZipFileBackedExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.catalog.providers.VSphereBuiltinExperimentDesignElementListProvider;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.apache.commons.configuration2.ImmutableConfiguration;

/**
 * @author srogers
 */
public class DesignCatalogMapProvider extends ResourceCollectionProviderBase
{
	private ImmutableConfiguration configuration;

	private NavigableMap<String, ExperimentDesignCatalogImpl> designCatalogMap = new TreeMap<>();

	private ExperimentDesignCatalogImpl/* */ mainDesignCatalog = null;

	private static final String/*         */ mainDesignCatalog_name = "main";

	private static final boolean/*        */ mainDesignCatalog_isLoadedFromExternalSources = false;

	public static final List<String>/*    */ mainDesignCatalog_zipFileResourceNames = Arrays.asList(
			"catalogs/astam-cqf-ce-items-1.0.0-SNAPSHOT.zip"
	);

	/**/

	public DesignCatalogMapProvider() {
	
		/**/
	}
	
	@Override
	public void load_internal() throws IOException {

		mainDesignCatalog = new ExperimentDesignCatalogImpl(newMainDesignElementListProvider());
		mainDesignCatalog.loadItems();

		designCatalogMap.put(mainDesignCatalog_name, mainDesignCatalog);
	}

	@Override
	public void unload_internal() {

		for (ExperimentDesignCatalogImpl x : designCatalogMap.values()) {
			x.unloadItems();
		}
		designCatalogMap.clear();

		assert mainDesignCatalog.getItems().isEmpty();
		mainDesignCatalog = null;
	}

	/**/

	public synchronized NavigableMap<String, ExperimentDesignCatalogImpl> getDesignCatalogMap() {
		
		assert ! this.isAvailable() || (mainDesignCatalog != null);
		
		return designCatalogMap;
	}

	public synchronized ExperimentDesignCatalogImpl getMainDesignCatalog() {
		
		assert ! this.isAvailable() || (mainDesignCatalog != null);
		
		if (mainDesignCatalog == null) {

			mainDesignCatalog = new ExperimentDesignCatalogImpl(/*empty*/);

			designCatalogMap.put(mainDesignCatalog_name, mainDesignCatalog);
		}
		return mainDesignCatalog;
	}

	/**/

	protected ArrayList<ExperimentDesignElementListProvider<? extends ExperimentDesignElementImpl>>
	newMainDesignElementListProvider() throws IOException {

		ArrayList<ExperimentDesignElementListProvider<? extends ExperimentDesignElementImpl>> result = new ArrayList<>();

		result.add(new CoreBuiltinExperimentDesignElementListProvider());

		result.add(new VSphereBuiltinExperimentDesignElementListProvider());

		for (String r : mainDesignCatalog_zipFileResourceNames) {

			result.add(new ZipFileBackedExperimentDesignElementListProvider(r));
		}

		if (mainDesignCatalog_isLoadedFromExternalSources) {

			List<?> itemDirectories = this.configuration.getList(
					"cqf.itemDirectories.directory" // FIXME: STRING: srogers
			);
			itemDirectories.stream()
					.map(String.class::cast)
					.map(Paths::get)
					.map(FileBackedExperimentDesignElementListProvider::new)
					.forEach(result::add);
		}

		return result;
	}

}
