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
 * ExperimentDesignElement for referencing an existing virtual machine.
 * 
 * @author taylorj
 */
public class PermanentVirtualMachineDesignElement extends ExperimentDesignElementImpl {

	private static final DocumentationImpl DOCUMENTATION = DocumentationImpl.Builder
			.info("Use an existing vSphere VM as a testbed machine")
			.param("VM Name", "the name of the virtual machine") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
			.param("Admin Username", "administrator username on VM") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
			.param("Admin Password", "administrator password on VM") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
			.build();

	private static final List<ParameterImpl> PARAMETERS = createParameters();

	public PermanentVirtualMachineDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		super(resolver);
	}

	@Override
	public DocumentationImpl getDocumentation() {
		return DOCUMENTATION;
	}

	@Override
	public String getName() {
		return "Existing VM"; // FIXME: STRING: srogers // BUG: srogers: it is called something else now
	}

	@Override
	public String getCategory() {
		return "Node"; // FIXME: STRING: srogers
	}
	
	private static List<ParameterImpl> createParameters() {
		ParameterImpl vmName = new ParameterImpl.Builder()
				.setLabel("VM Name") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setName("name") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setType("string")
				.setRequired(true)
				.build();
		ParameterImpl platform = new ParameterImpl.Builder()
				.setLabel("Platform") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setName("platform") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setType("string")
				.setDefaultValue("Unix") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setRequired(true)
				.build();
		ParameterImpl username = new ParameterImpl.Builder()
				.setLabel("Admin Username") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setName("adminusername") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setType("string")
				.setDefaultValue("root") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setRequired(true)
				.build();
		ParameterImpl password = new ParameterImpl.Builder()
				.setLabel("Admin Password") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setName("adminpassword") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setType("string")
				.setDefaultValue("root") // FIXME: STRING: srogers // BUG: srogers: it is called something else now
				.setRequired(true)
				.build();
		return Arrays.asList(vmName, platform, username, password);
	}

	@Override
	public List<ParameterImpl> getOwnParameters() {
		return PARAMETERS;
	}

}
