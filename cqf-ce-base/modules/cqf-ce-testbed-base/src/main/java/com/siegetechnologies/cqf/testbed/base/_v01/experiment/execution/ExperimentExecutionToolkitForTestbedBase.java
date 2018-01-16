package com.siegetechnologies.cqf.testbed.base._v01.experiment.execution;

/*-
 * #%L
 * astam-cqf-ce-testbed-base
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

import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.PROGRAM_ARGUMENTS;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.PROGRAM_PATH;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.PROGRAM_WORKING_DIRECTORY;
import static org.apache.commons.lang3.Validate.isTrue;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionException;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutionToolkit;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.TestbedMachine;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.ProgramExecutionSpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.concurrent.ExecutionException;

/**
 * @author srogers
 */
public class ExperimentExecutionToolkitForTestbedBase implements ExperimentExecutionToolkit
{
	static final EnumMap<OperatingSystemFamily, String> scheduleScriptPathMap;

	static {
		scheduleScriptPathMap = new EnumMap<>(OperatingSystemFamily.class);
		scheduleScriptPathMap.put(OperatingSystemFamily.UNIX, "/cqf/schedule.sh"); // FIXME: STRING: srogers
		scheduleScriptPathMap.put(OperatingSystemFamily.WINDOWS, "c:\\cqf\\schedule.bat"); // FIXME: STRING: srogers
	}

	private static final DateTimeFormatter EXPERIMENT_START_DATE_TIME_FORMAT_FOR_SCHEDULE_SCRIPT =
			DateTimeFormatter.ofPattern("s m k d M u");

	/**/

	/**
	 * Creates an object of this type.
	 */
	public ExperimentExecutionToolkitForTestbedBase() {}

	/**/

	public static String getScheduleScriptPathForTestbedMachine(
			TestbedMachine testbedMachine
	) {
		OperatingSystemFamily osFamily = testbedMachine.getFamily();

		return getScheduleScriptPathForOperatingSystemFamily(osFamily);
	}

	public static String getScheduleScriptPathForOperatingSystemFamily(
			OperatingSystemFamily osFamily
	) {
		isTrue(scheduleScriptPathMap.containsKey(osFamily));

		return scheduleScriptPathMap.get(osFamily);
	}

	public long invokeScheduleScriptOnTestbedMachine(
			TestbedMachine/* */ testbedMachine,
			LocalDateTime/*  */ experimentStartDateTime
	) {
		String programPath = getScheduleScriptPathForTestbedMachine(testbedMachine);

		String arguments = experimentStartDateTime.format(EXPERIMENT_START_DATE_TIME_FORMAT_FOR_SCHEDULE_SCRIPT);

		ProgramExecutionSpec programExecutionSpec = ProgramExecutionSpec.of(programPath, arguments, null);
		try {
			return testbedMachine.runCommand(programExecutionSpec).get();
		}
		catch (InterruptedException | ExecutionException e) {

			throw new ExperimentElementExecutionException("while running command: " + programExecutionSpec, e);
		}
	}

	public long invokeProgramOnTestbedMachine(
			TestbedMachine/* */ testbedMachine,
			String/*         */ programPath,
			String/*         */ arguments,
			String/*         */ workingDirectory
	) {
		ProgramExecutionSpec programExecutionSpec = ProgramExecutionSpec.of(programPath, arguments, workingDirectory);
		try {
			return testbedMachine.runCommand(programExecutionSpec).get();
		}
		catch (InterruptedException | ExecutionException e) {

			throw new ExperimentElementExecutionException("while running command: " + programExecutionSpec, e);
		}
	}

	/**/

	/*
	 *======================================================================*
	 * NO DEPENDENCIES ON ExperimentElementExecutionContext ABOVE THIS LINE *
	 *======================================================================*
	 */

	/**/

	public long invokeScheduleScriptInExecutionContext(ExperimentElementExecutionContext context) {

		TestbedMachine testbedMachine = context.getResult(TestbedMachine.class);

		LocalDateTime experimentStartDateTime = context.getRootContext().getCurrentTime();

		return invokeScheduleScriptOnTestbedMachine(testbedMachine, experimentStartDateTime);
	}

	public long invokeProgramInExecutionContext(ExperimentElementExecutionContext context) {

		TestbedMachine testbedMachine = context.getResult(TestbedMachine.class);

		String programPath = context.getInstanceParameter(PROGRAM_PATH);
		String arguments = context.getInstanceParameter(PROGRAM_ARGUMENTS);
		String workingDirectory = context.getInstanceParameter(PROGRAM_WORKING_DIRECTORY); // FIXME: STRING: srogers

		return invokeProgramOnTestbedMachine(testbedMachine, programPath, arguments, workingDirectory);
	}

	/**/

}
