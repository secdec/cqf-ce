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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;

import java.util.Arrays;
import java.util.List;

/**
 * ExperimentDesignElement for creating a resource pool.
 *
 * @author taylorj
 */
public class InterimResourcePoolDesignElement extends ExperimentDesignElementImpl {

	private static final List<ParameterImpl> PARAMETERS = createParameters();

	public InterimResourcePoolDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		super(resolver);
	}

	@Override
	public DocumentationImpl getDocumentation() {
		return DocumentationImpl.Builder
				.info("A resource pool specifies the virtual resources available to virtual machines within the pool")
				.param("Name", "name of the resource pool") // FIXME: STRING: srogers
				.param("Memory Limit", "the upper RAM size limit") // FIXME: STRING: srogers
				.param("Memory Expandable", "whether the RAM size can be expanded") // FIXME: STRING: srogers
				.param("Memory Reservation", "how much RAM to set aside for the resource pool") // FIXME: STRING: srogers
				.param("CPU Limit", "the upper CPU limit") // FIXME: STRING: srogers
				.param("CPU Expandable", "whether the CPU limit can be expanded") // FIXME: STRING: srogers
				.param("CPU Reservation", "how much CPU to set aside for the resource pool") // FIXME: STRING: srogers
				.build();
	}

	@Override
	public String getName() {
		return "Resource Pool"; // FIXME: STRING: srogers
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
		ParameterImpl name = new ParameterImpl.Builder()
				.setLabel("Name") // FIXME: STRING: srogers
				.setName("name") // FIXME: STRING: srogers
				.setType("string")
				.setRequired(true)
				.build();

		ParameterImpl memoryLimit = new ParameterImpl.Builder()
				.setLabel("Memory Limit") // FIXME: STRING: srogers
				.setName("memoryLimit") // FIXME: STRING: srogers
				.setType("number")
				.setDefaultValue("-1")
				.setRequired(false)
				.build();
		ParameterImpl memoryExpandable = new ParameterImpl.Builder()
				.setLabel("Memory Expandable") // FIXME: STRING: srogers
				.setName("memoryExpandable") // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("true")
				.setRequired(false)
				.build();
		ParameterImpl memoryReservation = new ParameterImpl.Builder()
				.setLabel("Memory Reservation") // FIXME: STRING: srogers
				.setName("memoryReservation") // FIXME: STRING: srogers
				.setType("number")
				.setDefaultValue("0")
				.setRequired(false)
				.build();

		ParameterImpl cpuLimit = new ParameterImpl.Builder()
				.setLabel("CPU Limit") // FIXME: STRING: srogers
				.setName("cpuLimit") // FIXME: STRING: srogers
				.setType("number")
				.setDefaultValue("-1")
				.setRequired(false)
				.build();
		ParameterImpl cpuExpandable = new ParameterImpl.Builder()
				.setLabel("CPU Expandable") // FIXME: STRING: srogers
				.setName("cpuExpandable") // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("true")
				.setRequired(false)
				.build();
		ParameterImpl cpuReservation = new ParameterImpl.Builder()
				.setLabel("CPU Reservation") // FIXME: STRING: srogers
				.setName("cpuReservation") // FIXME: STRING: srogers
				.setType("number")
				.setDefaultValue("0")
				.setRequired(false)
				.build();

		return Arrays.asList(name,
				memoryLimit,
				memoryExpandable,
				memoryReservation,
				cpuLimit,
				cpuExpandable,
				cpuReservation);
	}

}
