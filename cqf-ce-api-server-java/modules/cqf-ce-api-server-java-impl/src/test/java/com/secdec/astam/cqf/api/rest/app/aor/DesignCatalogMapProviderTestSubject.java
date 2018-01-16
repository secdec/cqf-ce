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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.CoreBuiltinExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.catalog.providers.VSphereBuiltinExperimentDesignElementListProvider;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author srogers
 */
public class DesignCatalogMapProviderTestSubject extends DesignCatalogMapProvider
{
	@Override
	protected ArrayList<ExperimentDesignElementListProvider<? extends ExperimentDesignElementImpl>>
	newMainDesignElementListProvider() throws IOException {

		ArrayList<ExperimentDesignElementListProvider<? extends ExperimentDesignElementImpl>> result = new ArrayList<>();

		result.add(new CoreBuiltinExperimentDesignElementListProvider());
		result.add(new VSphereBuiltinExperimentDesignElementListProvider());

		return result;
	}

}
