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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;

import java.util.Arrays;
import java.util.List;

/**
 * ExperimentDesignElement for creating a port group on a virtual switch.
 *
 * @author taylorj
 */
public class InterimPortGroupDesignElement extends ExperimentDesignElementImpl {
	private static final String NAME = "Port Group"; // FIXME: STRING: srogers
	private static final String CATEGORY = "Hardware"; // FIXME: STRING: srogers
	private static final List<ParameterImpl> PARAMETERS = createParameters();
	private static final DocumentationImpl DOCUMENTATION = createDocumentation();

	public InterimPortGroupDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		super(resolver);
	}

	@Override
	public DocumentationImpl getDocumentation() {
		return DOCUMENTATION;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getCategory() {
		return CATEGORY;
	}

	private static DocumentationImpl createDocumentation() {
		return DocumentationImpl.Builder.info("Port Groups are VMware's networks. Each port group is identified by a name and runs on a virtual switch.")
				.param("Name", "Name of the port group") // FIXME: STRING: srogers
				.param("Allow Promiscuous Mode", "Allow the portgroups to see all packets") // FIXME: STRING: srogers
				.param("VLAN ID", "ID of the virtual LAN of the portgroup") // FIXME: STRING: srogers
				.build();
	}

	private static List<ParameterImpl> createParameters() {
		ParameterImpl name = new ParameterImpl.Builder()
				.setLabel("Name") // FIXME: STRING: srogers
				.setName("name") // FIXME: STRING: srogers
				.setType("string")
				.setRequired(true)
				.build();

		ParameterImpl vlanId = new ParameterImpl.Builder()
				.setLabel("VLAN ID") // FIXME: STRING: srogers
				.setName("vlanId") // FIXME: STRING: srogers
				.setType("integer")
				.setDefaultValue("0")
				.setRequired(false)
				.build();

		ParameterImpl allowPromiscuous = new ParameterImpl.Builder()
				.setLabel("Allow Promiscuous") // FIXME: STRING: srogers
				.setName("allowPromiscuous")   // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("false")
				.setRequired(false)
				.build();

		ParameterImpl nameFormat = new ParameterImpl.Builder()
				.setLabel("Name Format")    // FIXME: STRING: srogers
				.setName("nameFormat")      // FIXME: STRING: srogers
				.setType("string")
				.setDefaultValue("${name}") // FIXME: STRING: srogers
				.setRequired(false)
				.build();

		return Arrays.asList(name, vlanId, allowPromiscuous, nameFormat);
	}

	@Override
	public List<ParameterImpl> getOwnParameters() {
		return PARAMETERS;
	}
}
