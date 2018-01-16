package com.secdec.astam.cqf.api.rest.app;

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

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertTrue;

import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignCatalogImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author srogers
 */
public class CQFApplicationTestUtil
{
	public static final String archetypeDesignItemNameRegex =
			"^(.+)[.]cqf[.]design[.]item[.]archetype[.](.+)$";

	public static final String workspaceDesignItemNameRegex =
			"^(.+)[.]cqf[.]design[.]item[.](.+)[.]workspace$";

	public ExperimentDesignCatalogImpl getMainItemCatalog() {

		ExperimentDesignCatalogImpl result = CQFApplication.theInstance().getResourceManager().getMainDesignCatalog();
		return result;
	}

	public static Set<String> getAllDesignItemCategoryNamesIn(
			ExperimentDesignCatalogImpl experimentDesignCatalog,
			Set<String> expectedItemCategoryNames
	) {
		if (expectedItemCategoryNames == null) {
			expectedItemCategoryNames = allEssentialItemCategoryNamesInMainDesignCatalog;
		}

		Set<String> result = (experimentDesignCatalog.getItems().stream()

				.map(id -> experimentDesignCatalog.resolve(id))

				.filter(Optional::isPresent)

				.map(optionalItem -> optionalItem.get().getCategory())

				.collect(toSet())
		);
		assertTrue(! result.isEmpty());
		assertTrue(result.containsAll(expectedItemCategoryNames));

		return result;
	}

	public static final HashSet<String> allEssentialItemCategoryNamesInMainDesignCatalog = new HashSet<>(Arrays.asList(
			"Archetype",
			"Attackee",
			"Attacker",
			"CQF",
			"Database",
			"Node",
			"Package",
			"Sensor",
			"System",
			"Util"
	));

	public static Set<String> getAllDesignItemNamesIn(
			ExperimentDesignCatalogImpl experimentDesignCatalog,
			Set<String> expectedItemNames
	) {
		if (expectedItemNames == null) {
			expectedItemNames = allEssentialItemNamesInMainDesignCatalog;
		}

		Set<String> result = (experimentDesignCatalog.getItems().stream()

				.map(id -> id.value())

				.collect(toSet())
		);
		assertTrue(! result.isEmpty());
		assertTrue(result.containsAll(expectedItemNames));

		return result;
	}

	public static HashSet<String> allEssentialItemNamesInMainDesignCatalog = new HashSet<>(Arrays.asList(
			"com.siegetechnologies.cqf.design.item.archetype.sql_injection_attack",
			"com.siegetechnologies.cqf.design.item.attackee.dotcms",
			"com.siegetechnologies.cqf.design.item.attacker.esm_7",
			"com.siegetechnologies.cqf.design.item.cqf.scheduler",
			"com.siegetechnologies.cqf.design.item.database.mysql",
			"com.siegetechnologies.cqf.design.item.node.astam_ubuntu",
			"com.siegetechnologies.cqf.design.item.system.command",
			"com.siegetechnologies.cqf.design.item.system.reboot",
			"com.siegetechnologies.cqf.design.item.system.shutdown",
			"com.siegetechnologies.cqf.design.item.util.workspace"
	));

}
