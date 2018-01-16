package com.siegetechnologies.cqf.core._v01.experiment.execution.util;

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

/**
 * ExperimentDesignElement scripts run in one of a number of time slots.
 * All the scripts associated with a given time slot
 * are run in a block.
 */
public enum ExecutionTimeSlot {

	/**
	 * Time slot for steps that must be performed prior
	 * to steps performed during the SETUP time slot.
	 */
	INITIALIZE,

	/**
	 * Time slot for initialize scripts that install items
	 * on a virtual machine.
	 */
	SETUP,

	/**
	 * Time slot for scripts that start running sensors.
	 */
	STARTSENSORS,

	/**
	 * Time slot for scripts that perform the primary action
	 * of the item.
	 */
	MAIN,

	/**
	 * Time slot for scripts that stop running sensors.
	 */
	STOPSENSORS,

	/**
	 * Time slot for scripts that cleanup the machine.
	 */
	CLEANUP,

	/**
	 * Time slot for scripts that are executed when the
	 * virtual machine is rebooted.
	 */
	ONREBOOT;

}
