package com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.handlers;

/*-
 * #%L
 * cqf-ce-testbed-base
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionException;
import com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.ExperimentExecutionToolkitForTestbedBase;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.TestbedMachine;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.ProgramExecutionSpec;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.mockito.Mockito;

public class ScheduledRunStepExecutionHandlerTest {

	@Test
	public void testNameAndCategory() {
		ScheduledRunStepExecutionHandler<?> handler = new ScheduledRunStepExecutionHandler<>();
		assertEquals("Scheduler", handler.getDesignName());
		assertEquals("CQF", handler.getDesignCategory());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testProgramPath_badOS() {
		ExperimentExecutionToolkitForTestbedBase.getScheduleScriptPathForOperatingSystemFamily(null);
	}

	@Test
	public void testPreRun() throws InterruptedException, ExecutionException {
		ExperimentExecutionToolkitForTestbedBase executionToolkit = new ExperimentExecutionToolkitForTestbedBase();
		ScheduledRunStepExecutionHandler<?> handler = new ScheduledRunStepExecutionHandler<>();
		ExperimentElementExecutionContext context = mock(ExperimentElementExecutionContext.class);
		ExperimentElementExecutionContext root = mock(ExperimentElementExecutionContext.class);
		when(root.getCurrentTime()).thenReturn(LocalDateTime.now());
		TestbedMachine machine = mock(TestbedMachine.class);
		when(context.getRootContext()).thenReturn(root);
		when(context.getResult(TestbedMachine.class)).thenReturn(machine);
		when(machine.getFamily()).thenReturn(OperatingSystemFamily.UNIX);
		when(context.getExecutionToolkit()).thenReturn(executionToolkit);
		@SuppressWarnings("unchecked")
		CompletableFuture<Integer> future = mock(CompletableFuture.class);
		when(future.get()).thenReturn(2);
		when(machine.runCommand(Mockito.any(ProgramExecutionSpec.class))).thenReturn(future);
		handler.run_beforeHook(context);
	}

	@Test(expected=ExperimentElementExecutionException.class)
	public void testPreRun_processFails() throws InterruptedException, ExecutionException {
		ExperimentExecutionToolkitForTestbedBase executionToolkit = new ExperimentExecutionToolkitForTestbedBase();
		ScheduledRunStepExecutionHandler<?> handler = new ScheduledRunStepExecutionHandler<>();
		ExperimentElementExecutionContext context = mock(ExperimentElementExecutionContext.class);
		ExperimentElementExecutionContext root = mock(ExperimentElementExecutionContext.class);
		when(root.getCurrentTime()).thenReturn(LocalDateTime.now());
		TestbedMachine machine = mock(TestbedMachine.class);
		when(context.getRootContext()).thenReturn(root);
		when(context.getResult(TestbedMachine.class)).thenReturn(machine);
		when(machine.getFamily()).thenReturn(OperatingSystemFamily.UNIX);
		when(context.getExecutionToolkit()).thenReturn(executionToolkit);
		@SuppressWarnings("unchecked")
		CompletableFuture<Integer> future = mock(CompletableFuture.class);
		when(future.get()).thenThrow(new InterruptedException());
		when(machine.runCommand(Mockito.any(ProgramExecutionSpec.class))).thenReturn(future);
		handler.run_beforeHook(context);
	}
}
