package com.siegetechnologies.cqf.core.experiment.design.elements;

/*-
 * #%L
 * cqf-ce-core
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
 * The root workspace item.
 *
 * @author taylorj
 */
public class WorkspaceDesignElement extends ExperimentDesignElementImpl {

	private static final String NAME = "Workspace"; // FIXME: STRING: srogers
	private static final String CATEGORY = "Util"; // FIXME: STRING: srogers
	private static final DocumentationImpl DOCUMENTATION = createDocumentation();
	private static final List<ParameterImpl> PARAMETERS = createParameters();

	public WorkspaceDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		super(resolver);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getCategory() {
		return CATEGORY;
	}

	@Override
	public DocumentationImpl getDocumentation() {
		return DOCUMENTATION;
	}

	@Override
	public List<ParameterImpl> getOwnParameters() {
		return PARAMETERS;
	}

	/**
	 * Returns parameters for the workspace item. This is intended to produce
	 * the single static parameters list returned by all instances.
	 *
	 * @return the list of parameters
	 */
	private static final List<ParameterImpl> createParameters() {
		ParameterImpl initialize = new ParameterImpl.Builder()
				.setLabel("Initialize") // FIXME: STRING: srogers
				.setName("INITIALIZE") // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("true")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();

		ParameterImpl run = new ParameterImpl.Builder()
				.setLabel("Run") // FIXME: STRING: srogers
				.setName("RUN") // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("true")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();

		ParameterImpl duration = new ParameterImpl.Builder()
				.setLabel("Duration (minutes)") // FIXME: STRING: srogers
				.setName("DURATION") // FIXME: STRING: srogers
				.setType("number")
				.setDefaultValue("0")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();

		ParameterImpl retrieveData = new ParameterImpl.Builder()
				.setLabel("Retrieve Data") // FIXME: STRING: srogers
				.setName("RETRIEVE_DATA") // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("true")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();

		ParameterImpl cleanup = new ParameterImpl.Builder()
				.setLabel("Cleanup") // FIXME: STRING: srogers
				.setName("CLEANUP") // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("true")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();

		ParameterImpl cancel = new ParameterImpl.Builder()
				.setLabel("Cancel") // FIXME: STRING: srogers
				.setName("CANCEL") // FIXME: STRING: srogers
				.setType("checkbox")
				.setDefaultValue("false")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();

		return Arrays.asList(initialize, run, duration, retrieveData, cleanup, cancel);
	}

	/**
	 * Returns documentation for the workspace item. This is intended to produce
	 * the single static documentation that is returned by all instances.
	 *
	 * @return the documentation
	 */
	private static DocumentationImpl createDocumentation() {
		return DocumentationImpl.Builder.info("A workspace in which other items can be placed, and top-level options can be configured.")
				.param("Initialize", "Whether or not to execute the Initialize phase.") // FIXME: STRING: srogers
				.param("Run", "Whether or not to execute the Run phase.") // FIXME: STRING: srogers
				.param("Duration (minutes)", "The number of minutes this workspace will be alive.") // FIXME: STRING: srogers
				.param("Retrieve Data", "Whether or not to execute the Retrieve Data phase.") // FIXME: STRING: srogers
				.param("Cleanup", "Whether or not to execute the Cleanup phase.") // FIXME: STRING: srogers
				.build(); // FIXME: srogers: extract string construction into a dedicated method
	}
	
	/**
	 * Determines if another experiment is identical to this design
	 * @param x the desgin element 
	 * @return boolean value
	 */
	public static boolean matches(ExperimentDesignElementImpl x){
		if (x == null) {
			return false;
		}
		if (x.getCategory().equalsIgnoreCase("archetype")) {
			return true;
		}
		if (x.getCategory().equals(CATEGORY) && x.getName().equals(NAME)) {
			return true;
		}
		return false;
	}
}
