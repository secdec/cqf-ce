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
 * ExperimentDesignElement for referencing an existing virtual switch.
 *
 * @author taylorj
 */
public class PermanentVirtualSwitchDesignElement extends ExperimentDesignElementImpl {

	private static final List<ParameterImpl> PARAMETERS = createParameters();

	public PermanentVirtualSwitchDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		super(resolver);
	}

	@Override
	public DocumentationImpl getDocumentation() {
		return DocumentationImpl.Builder.info("Reference to an existing virtual switch")
				.param("Switch Name", "Name of the switch to pull into the test.") // FIXME: STRING: srogers
				.param("Hosts", "Comma separated list of hosts to refer to") // FIXME: STRING: srogers
				.build();
	}

	@Override
	public String getName() {
		return "Existing Virtual Switch"; // FIXME: STRING: srogers
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
		ParameterImpl switchName = new ParameterImpl.Builder()
				.setLabel("Switch Name") // FIXME: STRING: srogers
				.setName("name") // FIXME: STRING: srogers
				.setType("string")
				.setDefaultValue(null)
				.setAcceptableValues(null)
				.setRequired(true)
				.build();

		ParameterImpl hosts = new ParameterImpl.Builder()
				.setLabel("Hosts") // FIXME: STRING: srogers
				.setName(HOST_LIST_SPEC) // FIXME: STRING: srogers
				.setType("string")
				.setDefaultValue("")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();

		return Arrays.asList(switchName, hosts);
	}

}
