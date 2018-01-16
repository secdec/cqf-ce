package com.siegetechnologies.cqf.core.experiment.execution.util;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * Instances in a test environment can be run in a number of phases.
 * ItemHandlers are registered for different test phases. See the
 * documentation on each ExecutionPhase instance for a description of
 * what happens at that phase.
 *
 * The order that these phases are declared is significant, and
 * represents the order in which the phases are run (with the exception
 * of the {@link #QUANTIFY} phase, which is a pseudo-phase that
 * incorporates all the other phases).
 */
@JsonFormat(shape=Shape.OBJECT)
public enum ExecutionPhase {
	/**
	 * During the initialization phase, actions
	 * that need to happen before a test can be run are performed.  For
	 * virtual machines, this includes any directory management (clearing
	 * away or creating any directories that will be used in the test), and
	 * creating any clones needed for the test.  For software items, this
	 * is when scripts and required files are copied over to the host.
	 */
	INITIALIZE("Initialize"),

	/**
	 * During the run phase, actions are performed that
	 * should happen during the actual test execution.  Most items should
	 * not perform actions at the run phase, as the controlling CQF instance
	 * will run the scripts that other instances copied over during the
	 * {@link ExecutionPhase#INITIALIZE} phase.
	 */
	RUN("Run"),

	/**
	 * After a test has been run, data must be collected from the
	 * hosts.  The data that will be collected will depend on the
	 * particular instances' items, but will be bundled together
	 * and returned to the user.
	 */
	RETRIEVE_DATA("Retrieve Data"),

	/**
	 * After a test has been run and data collected, it will often
	 * be useful to cleanup just the resources associated with the
	 * test.  This is what the cleanup phase is used for.
	 */
	CLEANUP("Cleanup"),
	
	/**
	 * A special meta-phase which indicates that all test phases should be executed (except cleanup).
	 */
	QUANTIFY("Quantify"),

	/**
	 * A no-op phase that marks the execution of an experiment as complete.
	 */
	COMPLETE("Complete");

	private final String label;

	ExecutionPhase(String label) {
		this.label = label;
	}

	public String getValue() {
		return this.toString();
	}

	public String getLabel() {
		return this.label;
	}
}
