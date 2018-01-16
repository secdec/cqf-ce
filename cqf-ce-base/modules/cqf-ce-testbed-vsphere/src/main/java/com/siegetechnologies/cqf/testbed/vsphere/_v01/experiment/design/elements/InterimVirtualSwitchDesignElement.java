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

import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.HOST_LIST_SPEC;

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import java.util.Arrays;
import java.util.List;

/**
 * ExperimentDesignElement for creating a virtual switch.
 *
 * @author taylorj
 */
public class InterimVirtualSwitchDesignElement extends ExperimentDesignElementImpl {

	private static final DocumentationImpl DOCUMENTATION = createDocumentation();

	private static final List<ParameterImpl> PARAMETERS = createParameters();

	public InterimVirtualSwitchDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		super(resolver);
	}

	/**
	 * Returns {@link ExperimentDesignElementImpl#DEFAULT_EXECUTION_INDEX} minus 10.
	 */
	@Override
	public int getDefaultExecutionIndex() {
	  return ExperimentDesignElementImpl.DEFAULT_EXECUTION_INDEX - 10;
	}

	@Override
	public DocumentationImpl getDocumentation() {
		return DOCUMENTATION;
	}

	@Override
	public String getName() {
		return "Virtual Switch"; // FIXME: STRING: srogers
	}

	@Override
	public String getCategory() {
		return "Hardware"; // FIXME: STRING: srogers
	}

	@Override
	public List<ParameterImpl> getOwnParameters() {
		return PARAMETERS;
	}

	private static final List<ParameterImpl> createParameters() {

		ParameterImpl hosts = new ParameterImpl.Builder()
				.setLabel("Hosts") // FIXME: STRING: srogers
				.setName(HOST_LIST_SPEC) // FIXME: STRING: srogers
				.setType("string")
				.setDefaultValue("*")
				.setRequired(true)
				.build();

		ParameterImpl name = new ParameterImpl.Builder()
				.setLabel("Name") // FIXME: STRING: srogers
				.setName("name") // FIXME: STRING: srogers
				.setType("string")
				.setRequired(true)
				.build();

		ParameterImpl numberOfPorts = new ParameterImpl.Builder()
				.setLabel("Number of Ports") // FIXME: STRING: srogers
				.setName("numPorts") // FIXME: STRING: srogers
				.setType("number")
				.setDefaultValue("120")
				.setRequired(false)
				.build();

		ParameterImpl allowPromiscuous = new ParameterImpl.Builder()
				.setLabel("Allow Promiscuous Mode") // FIXME: STRING: srogers
				.setName("allowPromiscuous") // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("false")
				.setRequired(false)
				.build();

		ParameterImpl nic = new ParameterImpl.Builder()
				.setLabel("NIC") // FIXME: STRING: srogers
				.setName("nic") // FIXME: STRING: srogers
				.setType("string")
				.setDefaultValue("")
				.setRequired(false)
				.build();

		ParameterImpl nameFormat = new ParameterImpl.Builder()
				.setLabel("Name Format") // FIXME: STRING: srogers
				.setName("nameFormat") // FIXME: STRING: srogers
				.setType("string")
				.setDefaultValue("${name}") // FIXME: STRING: srogers
				.setRequired(true)
				.build();

		return Arrays.asList(hosts, name, numberOfPorts, allowPromiscuous, nic, nameFormat);
	}

	private static DocumentationImpl createDocumentation() {
		return DocumentationImpl.Builder
				.info("A Virtual Switch.  Virtual Switches are the (virtual) hardware on which port "
						+ "groups (networks) are placed.")
				.param("Name", "The name for this virtual switch") // FIXME: STRING: srogers
				.param("Hosts",
						"A comma separated list of names of hosts on which to create switched; "
								+ "blank for first host, '*' for all hosts.") // FIXME: STRING: srogers
				.param("Number of Ports", "Number of ports on this switch.") // FIXME: STRING: srogers
				.param("Allow Promiscuous Mode", "Set the ports to see all packets. (Hub mode)") // FIXME: STRING: srogers
				.param("NIC", "A comma separated list of physical NIC IDs to connect to the switch (optional)") // FIXME: STRING: srogers
				.build();

	}

}
