package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements;

/*-
 * #%L
 * astam-cqf-ce-testbed-vsphere
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

import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.util.DesignElementSpecEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author srogers
 */
public class MultinodeArchetypeDesignElement extends ExperimentDesignElementImpl
{
	private static final DocumentationImpl DOCUMENTATION = newDocumentation();

	private static final List<ParameterImpl> PARAMETERS = newParameters();

	private static final String NAME = "Multinode", CATEGORY = "Archetype";

	/**/

	public MultinodeArchetypeDesignElement(
			ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver
	)
	{
		super(resolver);
	}

	@Override
	public DocumentationImpl getDocumentation()
	{
		return DOCUMENTATION;
	}

	@Override
	public List<ParameterImpl> getOwnParameters()
	{
		return PARAMETERS;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getCategory()
	{
		return CATEGORY;
	}

	/**/

	private static DocumentationImpl newDocumentation()
	{
		return DocumentationImpl.Builder
				.info("A generalized archetype design element that supports multiple nodes.")
				.build();
	}

	/**/

	private static List<ParameterImpl> newParameters()
	{
		ArrayList<ParameterImpl> result = new ArrayList<>();

		result.addAll(newParameterSubset(""));

		return result;
	}

	private static List<ParameterImpl> newParameterSubset(String prefix)
	{
		ArrayList<ParameterImpl> result = new ArrayList<>();

		List<String> parameterNameStems = Arrays.asList(
		);

		for (String parameterNameStem : parameterNameStems) {

			String parameterName = prefix + parameterNameStem;

			ParameterImpl parameter = new ParameterImpl.Builder()
					.setName(parameterName)
					.setType("string")
					.build();

			result.add(parameter);
		}

		return result;
	}

	/**/

	@Override
	public void prepareExperimentElementForExecution(ExperimentElementImpl experimentElement)
	{
		super.prepareExperimentElementForExecution(experimentElement);

		/**/

		List<ExperimentElementImpl> ee_children = experimentElement.getChildren();

		List<ExperimentElementImpl> ee_children_extra = new ArrayList<>();

		ExperimentElementImpl ee_children_extra_parent = experimentElement;

		ExperimentElementImpl ee_children_extra_archetype = experimentElement;

		/**/

		ee_children_extra.add(newVirtualSwitchSubtree(
				ee_children_extra_parent, ee_children_extra_archetype));

		for (ExperimentElementImpl virtualMachinePrototype : ee_children) {

			ee_children_extra.add(newVirtualMachineSubtree(
					virtualMachinePrototype, ee_children_extra_parent, ee_children_extra_archetype));

			virtualMachinePrototype.setExecutable(false);
		}

		ee_children.addAll(ee_children_extra);
	}

	/**/

	protected ExperimentElementImpl newVirtualSwitchSubtree(
			ExperimentElementImpl parent,
			ExperimentElementImpl archetype
	)
	{
		ExperimentDesignElementImpl result_design = new InterimVirtualSwitchDesignElement(resolver);

		ExperimentElementImpl result = new ExperimentElementImpl(result_design, parent);

		result.getChildren().add(newVirtualPortGroupSubtree(result, archetype));

		copyParameterBindingsTo(result, archetype);

		result.setSynthetic(true);

		return result;
	}

	protected ExperimentElementImpl newVirtualPortGroupSubtree(
			ExperimentElementImpl parent,
			ExperimentElementImpl archetype
	)
	{
		ExperimentDesignElementImpl result_design = new InterimPortGroupDesignElement(resolver);

		ExperimentElementImpl result = new ExperimentElementImpl(result_design, parent);

		copyParameterBindingsTo(result, archetype);

		result.setSynthetic(true);

		return result;
	}

	/**/

	protected ExperimentElementImpl newVirtualMachineSubtree(
			ExperimentElementImpl virtualMachinePrototype,
			ExperimentElementImpl parent,
			ExperimentElementImpl archetype
	)
	{
		ExperimentDesignElementImpl result_design = new InterimVirtualMachineDesignElement(resolver);

		ExperimentElementImpl result = new ExperimentElementImpl(result_design, parent);

		result.getChildren().add(newStaticIPAddressSubtree(virtualMachinePrototype, result, archetype));

		result.getChildren().add(newNetworkAdapterSubtree(virtualMachinePrototype, result, archetype));

		result.getChildren().add(newOrchestratorSubtree(virtualMachinePrototype, result, archetype));

		result.getChildren().add(newSchedulerSubtree(virtualMachinePrototype, result, archetype));

		result.getChildren().add(newResultSubtree(virtualMachinePrototype, result, archetype));

		copyParameterBindingsTo(result, archetype, virtualMachinePrototype);

		result.setSynthetic(true);

		return result;
	}

	protected ExperimentElementImpl newStaticIPAddressSubtree(
			ExperimentElementImpl virtualMachinePrototype,
			ExperimentElementImpl parent,
			ExperimentElementImpl archetype
	)
	{
		ExperimentDesignElementImpl result_design = DesignElementSpecEnum.STATIC_IP.getItem(resolver);

		ExperimentElementImpl result = new ExperimentElementImpl(result_design, parent);

		copyParameterBindingsTo(result, archetype, virtualMachinePrototype);

		result.setSynthetic(true);

		return result;
	}

	protected ExperimentElementImpl newNetworkAdapterSubtree(
			ExperimentElementImpl virtualMachinePrototype,
			ExperimentElementImpl parent,
			ExperimentElementImpl archetype
	)
	{
		ExperimentDesignElementImpl result_design = DesignElementSpecEnum.NETWORK_INTERFACE_CARD.getItem(resolver);

		ExperimentElementImpl result = new ExperimentElementImpl(result_design, parent);

		copyParameterBindingsTo(result, archetype, virtualMachinePrototype);

		result.setSynthetic(true);

		return result;
	}

	protected ExperimentElementImpl newOrchestratorSubtree(
			ExperimentElementImpl virtualMachinePrototype,
			ExperimentElementImpl parent,
			ExperimentElementImpl archetype
	)
	{
		ExperimentDesignElementImpl result_design = virtualMachinePrototype.getDesign();

		ExperimentElementImpl result = new ExperimentElementImpl(result_design, parent);

		copyParameterBindingsTo(result, archetype, virtualMachinePrototype);

		result.setSynthetic(true);

		return result;
	}

	protected ExperimentElementImpl newSchedulerSubtree(
			ExperimentElementImpl virtualMachinePrototype,
			ExperimentElementImpl parent,
			ExperimentElementImpl archetype
	)
	{
		ExperimentDesignElementImpl result_design = DesignElementSpecEnum.SCHEDULER.getItem(resolver);

		ExperimentElementImpl result = new ExperimentElementImpl(result_design, parent);

		copyParameterBindingsTo(result, archetype, virtualMachinePrototype);

		result.setSynthetic(true);

		return result;
	}

	protected ExperimentElementImpl newResultSubtree(
			ExperimentElementImpl virtualMachinePrototype,
			ExperimentElementImpl parent,
			ExperimentElementImpl archetype
	)
	{
		ExperimentDesignElementImpl result_design = DesignElementSpecEnum.RESULT.getItem(resolver);

		ExperimentElementImpl result = new ExperimentElementImpl(result_design, parent);

		copyParameterBindingsTo(result, archetype, virtualMachinePrototype);

		result.setSynthetic(true);

		return result;
	}

	/**/

	protected void copyParameterBindingsTo(ExperimentElementImpl destination, ExperimentElementImpl... sources)
	{
		for (ExperimentElementImpl source : sources) {

			destination.getParameterValueMap().putAll(source.getParameterValueMap());
		}
	}

	/**/

}
