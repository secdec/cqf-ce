package com.secdec.astam.cqf.api.util;

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.secdec.astam.cqf.api.models.ExperimentElement;
import com.secdec.astam.cqf.api.rest.app.CQFApplication;
import com.secdec.astam.cqf.api.rest.app.CQFResourceManager;
import com.secdec.astam.cqf.api.rest.responders.util.ResponderToolkit;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignCatalogImpl;
import com.siegetechnologies.cqf.core.experiment.design.elements.WorkspaceDesignElement;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author srogers
 */
public class MockExperimentFactory extends com.secdec.astam.cqf.api.models.ExperimentElement {

	private static final ResponderToolkit toolkit = new ResponderToolkit();

	public static com.secdec.astam.cqf.api.models.ExperimentElement newExperiment_DotCMSScenario() {

		com.secdec.astam.cqf.api.models.ExperimentElement result = newExperimentFromJSONResource(
				"/experiments/3-dotcms-scenario.json"
		);
		assert result != null;

		return result;
	}

	public static com.secdec.astam.cqf.api.models.ExperimentElement newExperiment_HelloWorldScenario() {

		com.secdec.astam.cqf.api.models.ExperimentElement result = newExperimentFromJSONResource(
				"/experiments/1-machine-hello-world.cqf.experiment.json"
		);
		assert result != null;

		return result;
	}

	public static com.secdec.astam.cqf.api.models.ExperimentElement newExperiment_SQLInjectionAttack() {

		com.secdec.astam.cqf.api.models.ExperimentElement result = newExperimentFromJSONResource(
				"/experiments/sql-injection-attack.cqf.experiment.json"
		);
		assert result != null;

		return result;
	}

	public static com.secdec.astam.cqf.api.models.ExperimentElement newExperimentFromJSONResource(String jsonResourceName) {

		final Class this_class = MockExperimentFactory.class;

		final URL mockExperiment_json_url = this_class.getResource(jsonResourceName);
		assert mockExperiment_json_url != null;

		final ObjectMapper jsonObjectMapper = (new ObjectMapper()
				.configure(SerializationFeature.INDENT_OUTPUT, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
		);

		com.secdec.astam.cqf.api.models.ExperimentElement result;
		try {
			result = jsonObjectMapper.readValue(mockExperiment_json_url, com.secdec.astam.cqf.api.models.ExperimentElement.class);
		}
		catch (IOException xx) {
			result = null;
		}
		assert result != null;

		return result;
	}

	public final static com.secdec.astam.cqf.api.models.ExperimentElement newExperimentWithDesignItem(String childDesignItemName) {

		com.secdec.astam.cqf.api.models.ExperimentElement result = newExperimentWithDesignItems(childDesignItemName);
		return result;
	}

	public static com.secdec.astam.cqf.api.models.ExperimentElement newExperimentWithDesignItems(String... childDesignItemNames) {

		com.secdec.astam.cqf.api.models.ExperimentElement result = newExperimentWithDesignItems(Arrays.asList(childDesignItemNames));
		return result;
	}

	public static com.secdec.astam.cqf.api.models.ExperimentElement newExperimentWithDesignItems(List<String> childDesignItemNames) {

		CQFResourceManager resourceManager = CQFApplication.theInstance().getResourceManager();

		ExperimentDesignCatalogImpl mainExperimentDesignCatalog = resourceManager.getMainDesignCatalog();

		ExperimentDesignElementImpl designDelegate = newRootDesignDelegate();

		int i = 0;
		for (String childDesignItemName : childDesignItemNames) {

			if (designDelegate.getId().equals(childDesignItemName)) {
				continue;
			}

			Optional<ExperimentDesignElementImpl> childDesignItemDelegate = mainExperimentDesignCatalog.resolve(new ExperimentDesignElementId(childDesignItemName));
			assert childDesignItemDelegate.isPresent();

			String childDesignItemKey = Integer.toString(i);

			designDelegate.getChildren().put(childDesignItemKey, childDesignItemDelegate.get());

			++i;
		}

		ExperimentElementImpl delegate = new ExperimentElementImpl(designDelegate, null);

		ExperimentElement result = toolkit.newExperimentElementFromDelegate(delegate, resourceManager);
		return result;
	}

	protected static ExperimentDesignElementImpl newRootDesignDelegate() {

		ExperimentDesignCatalogImpl mainExperimentDesignCatalog = CQFApplication.theInstance().getResourceManager().getMainDesignCatalog();
		assert mainExperimentDesignCatalog != null;

		WorkspaceDesignElement result = new WorkspaceDesignElement(mainExperimentDesignCatalog);
		return result;
	}

}
