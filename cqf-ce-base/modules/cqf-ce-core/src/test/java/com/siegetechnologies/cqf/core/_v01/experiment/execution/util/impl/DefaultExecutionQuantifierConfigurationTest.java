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


import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionQuantifierConfiguration;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Test;

public class DefaultExecutionQuantifierConfigurationTest {

	/**
	 * Returns a configuration with some values set, and
	 * some that will fallback to provided default values,
	 * and some that will fallback to system default values.
	 *
	 * @return a configuration
	 */
	private Configuration createBaseConfiguration() {
		Configuration base = new BaseConfiguration();

		base.addProperty("cqf.quantify.default.tries", 5);
		// base.addProperty("cqf.quantify.default.delay", 2);
		base.addProperty("cqf.quantify.default.unit", TimeUnit.SECONDS.name());

		base.addProperty("cqf.quantify.initialize.tries", 10);
		base.addProperty("cqf.quantify.initialize.delay", 10);
		base.addProperty("cqf.quantify.initialize.unit", TimeUnit.MINUTES.name());

		// base.addProperty("cqf.quantify.run.tries", 5);
		// base.addProperty("cqf.quantify.run.delay", 5);
		// base.addProperty("cqf.quantify.run.unit", TimeUnit.SECONDS.name());

		// base.addProperty("cqf.quantify.retrieve_data.tries", 2);
		base.addProperty("cqf.quantify.retrieve_data.delay", 9);
		base.addProperty("cqf.quantify.retrieve_data.unit", TimeUnit.MICROSECONDS.name());

		base.addProperty("cqf.quantify.cleanup.tries", 5);
		base.addProperty("cqf.quantify.cleanup.delay", 5);
		base.addProperty("cqf.quantify.cleanup.unit", TimeUnit.SECONDS.name());
		return base;
	}

	/**
	 * Check that the process for loading test phase indexed information
	 * from the configuration works as expected.
	 */
	@Test
	public void testConfigProcessing() {
		Map<ExecutionPhase, DefaultExecutionQuantifierConfiguration.PhaseInfo> map = DefaultExecutionQuantifierConfiguration.getPhaseInfoMap(createBaseConfiguration());

		Assert.assertEquals(10, map.get(ExecutionPhase.INITIALIZE).getTries());
		Assert.assertEquals(10, map.get(ExecutionPhase.INITIALIZE).getDelay());
		Assert.assertEquals(TimeUnit.MINUTES, map.get(ExecutionPhase.INITIALIZE).getUnit());

		Assert.assertEquals(5, map.get(ExecutionPhase.RUN).getTries());
		Assert.assertEquals(DefaultExecutionQuantifierConfiguration.DEFAULT_DELAY, map.get(ExecutionPhase.RUN).getDelay());
		Assert.assertEquals(DefaultExecutionQuantifierConfiguration.DEFAULT_UNIT, map.get(ExecutionPhase.RUN).getUnit());

		Assert.assertEquals(5, map.get(ExecutionPhase.RETRIEVE_DATA).getTries());
		Assert.assertEquals(9, map.get(ExecutionPhase.RETRIEVE_DATA).getDelay());
		Assert.assertEquals(TimeUnit.MICROSECONDS, map.get(ExecutionPhase.RETRIEVE_DATA).getUnit());

		Assert.assertEquals(5, map.get(ExecutionPhase.CLEANUP).getTries());
		Assert.assertEquals(5, map.get(ExecutionPhase.CLEANUP).getDelay());
		Assert.assertEquals(TimeUnit.SECONDS, map.get(ExecutionPhase.CLEANUP).getUnit());
	}

	/**
	 * Check that the values from the higher-level methods work as
	 * expected too.  These are checking the same values as {@link #testConfigProcessing()},
	 * but without looking at the intermediate map.
	 */
	@Test
	public void test00() {
		ExecutionQuantifierConfiguration qc = new DefaultExecutionQuantifierConfiguration(createBaseConfiguration());

		Assert.assertEquals(10, qc.getNumberOfTries(ExecutionPhase.INITIALIZE));
		Assert.assertEquals(10, qc.getDelayMeasure(ExecutionPhase.INITIALIZE));
		Assert.assertEquals(TimeUnit.MINUTES, qc.getDelayTimeUnit(ExecutionPhase.INITIALIZE));

		Assert.assertEquals(5, qc.getNumberOfTries(ExecutionPhase.RUN));
		Assert.assertEquals(DefaultExecutionQuantifierConfiguration.DEFAULT_DELAY, qc.getDelayMeasure(ExecutionPhase.RUN));
		Assert.assertEquals(DefaultExecutionQuantifierConfiguration.DEFAULT_UNIT, qc.getDelayTimeUnit(ExecutionPhase.RUN));

		Assert.assertEquals(5, qc.getNumberOfTries(ExecutionPhase.RETRIEVE_DATA));
		Assert.assertEquals(9, qc.getDelayMeasure(ExecutionPhase.RETRIEVE_DATA));
		Assert.assertEquals(TimeUnit.MICROSECONDS, qc.getDelayTimeUnit(ExecutionPhase.RETRIEVE_DATA));

		Assert.assertEquals(5, qc.getNumberOfTries(ExecutionPhase.CLEANUP));
		Assert.assertEquals(5, qc.getDelayMeasure(ExecutionPhase.CLEANUP));
		Assert.assertEquals(TimeUnit.SECONDS, qc.getDelayTimeUnit(ExecutionPhase.CLEANUP));
	}
}
