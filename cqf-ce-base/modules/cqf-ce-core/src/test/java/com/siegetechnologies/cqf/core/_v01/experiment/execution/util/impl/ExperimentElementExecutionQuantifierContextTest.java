package com.siegetechnologies.cqf.core._v01.experiment.execution.util.impl;

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

import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionHandler;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentElementExecutionQuantifierContextTest {

	@Mock
	private ExperimentElementExecutionContext experimentElementExecutionContext;
	@Mock
	private ExperimentElementImpl experimentElement;
	@Mock
	private ExperimentElementExecutionHandler<?, ?> experimentElementExecutionHandler;

	private ExperimentElementExecutionQuantifierContext qContext;

	@Before
	public void init() {
		qContext = new ExperimentElementExecutionQuantifierContext(experimentElementExecutionContext, experimentElementExecutionHandler);
		Mockito.when(experimentElementExecutionContext.getExperimentElement())
				.thenReturn(experimentElement);
	}

	@Test
	public void testIsCancelled() {
		Mockito.when(experimentElementExecutionContext.getOptionalInstanceParameter("CANCEL"))
				.thenReturn(Optional.empty());
		Assert.assertFalse(qContext.isCancelled());

		Mockito.when(experimentElementExecutionContext.getOptionalInstanceParameter("CANCEL"))
				.thenReturn(Optional.of("false"));
		Assert.assertFalse(qContext.isCancelled());

		Mockito.when(experimentElementExecutionContext.getOptionalInstanceParameter("CANCEL"))
				.thenReturn(Optional.of("true"));
		Assert.assertTrue(qContext.isCancelled());
	}

	@Test
	public void testHandle() throws Exception {
		for (ExecutionPhase phase : ExecutionPhase.values()) {
			ExperimentElementExecutionContext withPhase = Mockito.mock(ExperimentElementExecutionContext.class);
			Mockito.when(experimentElementExecutionContext.withExecutionPhase(phase))
					.thenReturn(withPhase);
			qContext.handle(phase);
			Mockito.verify(experimentElementExecutionContext)
					.withExecutionPhase(phase);
			Mockito.verify(experimentElementExecutionHandler)
					.handle(withPhase);
		}
	}

	@Test
	public void testGetDurationTimeUnit() {
		for (ExecutionPhase phase : ExecutionPhase.values()) {
			Assert.assertSame(TimeUnit.MINUTES, qContext.getDurationTimeUnit(phase));
		}
	}

	@Test
	public void testGetDurationMeasure_noDurationAndRun() {
		EnumSet<ExecutionPhase> noDurationPhases = EnumSet.of(ExecutionPhase.INITIALIZE, ExecutionPhase.CLEANUP,
				ExecutionPhase.RETRIEVE_DATA);
		for (ExecutionPhase phase : noDurationPhases) {
			Assert.assertEquals(0, qContext.getDurationMeasure(phase));
		}

		Mockito.when(experimentElementExecutionContext.getOptionalInstanceParameter("DURATION"))
				.thenReturn(Optional.empty());
		Assert.assertEquals(15, qContext.getDurationMeasure(ExecutionPhase.RUN));

		Mockito.when(experimentElementExecutionContext.getOptionalInstanceParameter("DURATION"))
				.thenReturn(Optional.of("42"));
		Assert.assertEquals(42, qContext.getDurationMeasure(ExecutionPhase.RUN));
	}

	@Test(expected = AssertionError.class)
	public void testGetDurationMeasure_quantify() {
		qContext.getDurationMeasure(ExecutionPhase.QUANTIFY);
	}

	@Test
	public void testGetEnabledPhases() {
		// The default is true, and phases are only disabled when explicitly set
		// to false. We use INITIALIZE(default), RUN(false),
		// RETRIEVE_DATA(true), and CLEANUP(true), so we should end up with
		// INITIALIZE, RETRIEVE_DATA, and CLEANUP.
		when(experimentElementExecutionContext.getOptionalInstanceParameter(ExecutionPhase.INITIALIZE.name())).thenReturn(Optional.empty());
		when(experimentElementExecutionContext.getOptionalInstanceParameter(ExecutionPhase.RUN.name())).thenReturn(Optional.of("false"));
		when(experimentElementExecutionContext.getOptionalInstanceParameter(ExecutionPhase.RETRIEVE_DATA.name())).thenReturn(
				Optional.of("true"));
		when(experimentElementExecutionContext.getOptionalInstanceParameter(ExecutionPhase.CLEANUP.name())).thenReturn(Optional.of("true"));
		EnumSet<ExecutionPhase> expected = EnumSet.of(ExecutionPhase.INITIALIZE, ExecutionPhase.RETRIEVE_DATA, ExecutionPhase.CLEANUP);
		Assert.assertEquals(expected, qContext.getEnabledPhases());
	}

}
