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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ExperimentDesignElement for creating a clone of an existing virtual machine.
 * 
 * @author taylorj
 */
public class InterimVirtualMachineDesignElement extends ExperimentDesignElementImpl {

	private static final DocumentationImpl DOCUMENTATION = createDocumentation();

	private static final List<ParameterImpl> PARAMETERS = createParameters();

	public InterimVirtualMachineDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		super(resolver);
	}
	
	@Override
	public Optional<? extends ExperimentDesignElementImpl> getSuperDesignElement() {
	  ExperimentDesignElementId x = ExperimentDesignElementId.of("Existing VM", "Node", null); // FIXME: STRING: srogers
	  return this.resolver.resolve(x);
	}

	@Override
	public DocumentationImpl getDocumentation() {
		return DOCUMENTATION;
	}

	@Override
	public List<ParameterImpl> getOwnParameters() {
		return PARAMETERS;
	}

	@Override
	public String getName() {
		return "Clone VM"; // FIXME: STRING: srogers
	}

	@Override
	public String getCategory() {
		return "Node"; // FIXME: STRING: srogers
	}

	private static DocumentationImpl createDocumentation() {
		return DocumentationImpl.Builder.info("Create a clone of a testbed machine.")
				.param("Clone Name", "base name of the created clones") // FIXME: STRING: srogers
				.build();
	}

	private static List<ParameterImpl> createParameters() {
		ParameterImpl cloneName = new ParameterImpl.Builder()
				.setLabel("Clone Name") // FIXME: STRING: srogers
				.setName("cloneName")   // FIXME: STRING: srogers
				.setType("string")
				.setDefaultValue("${name}-${cqf.id}") // FIXME: STRING: srogers
				.setRequired(true)
				.build();

		return Arrays.asList(cloneName);
	}

}
