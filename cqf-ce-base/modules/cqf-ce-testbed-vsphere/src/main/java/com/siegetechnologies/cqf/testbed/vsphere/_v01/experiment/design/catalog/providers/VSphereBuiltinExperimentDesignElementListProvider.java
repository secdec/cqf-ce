package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.catalog.providers;

/*-
 * #%L
 * cqf-ce-testbed-vsphere
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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.InterimPortGroupDesignElement;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.InterimResourcePoolDesignElement;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.InterimVirtualMachineDesignElement;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.InterimVirtualSwitchDesignElement;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.MultinodeArchetypeDesignElement;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.NetworkInterfaceCardDesignElement;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.PermanentVirtualMachineDesignElement;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.PermanentVirtualSwitchDesignElement;
import java.util.ArrayList;
import java.util.List;

/**
 * An items provided that provides static vSphere-related items.
 *
 * @author taylorj
 */
public class VSphereBuiltinExperimentDesignElementListProvider
		implements ExperimentDesignElementListProvider<ExperimentDesignElementImpl>
{
	@Override
	public List<ExperimentDesignElementImpl>
	getDesignElements(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {

		final boolean result_includes_v01_InventoryDesignElements = true;
		final boolean result_includes_v02_ArchetypeDesignElements = true;

		ArrayList<ExperimentDesignElementImpl> result = new ArrayList<>();

		if (result_includes_v01_InventoryDesignElements) {
			result.add(new InterimPortGroupDesignElement(resolver));
			result.add(new InterimResourcePoolDesignElement(resolver));
			result.add(new InterimVirtualMachineDesignElement(resolver));
			result.add(new InterimVirtualSwitchDesignElement(resolver));
			result.add(new NetworkInterfaceCardDesignElement(resolver));
			result.add(new PermanentVirtualMachineDesignElement(resolver));
			result.add(new PermanentVirtualSwitchDesignElement(resolver));
		}

		if (result_includes_v02_ArchetypeDesignElements) {
			result.add(new MultinodeArchetypeDesignElement(resolver));
		}

		return result;
	}
}
