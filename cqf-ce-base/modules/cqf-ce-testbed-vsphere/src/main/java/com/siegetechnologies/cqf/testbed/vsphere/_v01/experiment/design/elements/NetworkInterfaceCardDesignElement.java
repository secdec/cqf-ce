package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements;

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

import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_NETWORK_NAME_STEM;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.NETWORK_ADAPTER_NUMBER;

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;

import java.util.Arrays;
import java.util.List;

/**
 * ExperimentDesignElement for configuring a network interface card on a virtual machine.
 *
 * @author taylorj
 */
public class NetworkInterfaceCardDesignElement extends ExperimentDesignElementImpl {

	private static final List<ParameterImpl> PARAMETERS = createParameters();

	public NetworkInterfaceCardDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		super(resolver);
	}

	@Override
	public DocumentationImpl getDocumentation() {
		return DocumentationImpl.Builder.info("A network card with one port")
				.param("Adapter Number", "The index of the network adapter being set")
				.param("Network", "Which ESXi port group to connect to")
				.build();
	}

	@Override
	public String getName() {
		return "Network Interface Card"; // FIXME: STRING: srogers
	}

	@Override
	public String getCategory() {
		return "Hardware"; // FIXME: STRING: srogers
	}

	@Override
	public List<ParameterImpl> getOwnParameters() {
		return PARAMETERS;
	}

	private static List<ParameterImpl> createParameters() {
		ParameterImpl adapterNumber = new ParameterImpl.Builder()
				.setLabel("Network Adapter Number") // FIXME: STRING: srogers
				.setName(NETWORK_ADAPTER_NUMBER)   // FIXME: STRING: srogers
				.setType("number")
				.setDefaultValue("1")
				.setRequired(true)
				.build();
		ParameterImpl network = new ParameterImpl.Builder()
				.setLabel("Network") // FIXME: STRING: srogers
				.setName(INTERIM_NETWORK_NAME_STEM)  // FIXME: STRING: srogers
				.setType("string")
				.setRequired(true)
				.build();
		return Arrays.asList(adapterNumber, network);
	}

}
