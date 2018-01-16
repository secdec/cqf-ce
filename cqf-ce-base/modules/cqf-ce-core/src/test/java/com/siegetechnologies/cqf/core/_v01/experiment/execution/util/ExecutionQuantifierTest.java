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

import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ExecutionQuantifierTest {

	private final static Logger logger = LoggerFactory.getLogger(ExecutionQuantifierTest.class);

	private final static EnumMap<ExecutionPhase, Integer> phaseTries = new EnumMap<>(ExecutionPhase.class);
	static {
		phaseTries.put(ExecutionPhase.INITIALIZE, 1);
		phaseTries.put(ExecutionPhase.RUN, 1);
		phaseTries.put(ExecutionPhase.RETRIEVE_DATA, 3);
		phaseTries.put(ExecutionPhase.CLEANUP, 2);
	}

	static class MockQuantificationConfigurationExecution implements ExecutionQuantifierConfiguration {
		@Override
		public int getNumberOfTries(ExecutionPhase phase) {
			int result = phaseTries.get(phase);
			logger.trace("getNumberOfTries({}) => {}", phase, result);
			return result;
		}

		@Override
		public TimeUnit getDelayTimeUnit(ExecutionPhase phase) {
			return TimeUnit.MILLISECONDS;
		}

		@Override
		public long getDelayMeasure(ExecutionPhase phase) {
			return 250;
		}
	}

	static class MockQuantifierContext implements ExecutionQuantifierContext {
		// Call counts for checking after quantification
		int nCancel = 0, nHandle = 0, nPostDelay = 0;
		EnumMap<ExecutionPhase,Integer> attempts = new EnumMap<>(ExecutionPhase.class);
		{
			for (ExecutionPhase p : ExecutionPhase.values()) {
				attempts.put(p, 0);
			}
		}

		@Override
		public boolean isCancelled() {
			nCancel++;
			boolean result = false;
			logger.trace("isCancelled() => {}", result);
			return result;
		}

		@Override
		public ExecutionPhase handle(ExecutionPhase phase) {
			logger.trace("handle({}) => {}", phase, phase);
			attempts.put(phase, attempts.get(phase)+1);
			int nAttempts = attempts.get(phase);
			if (nAttempts != phaseTries.get(phase)) {
				throw new RuntimeException("not time to succeed yet");
			}
			nHandle++;
			// Only return a non-null result for INITIALIZE
			return phase == ExecutionPhase.INITIALIZE ? phase : null;
		}

		@Override
		public EnumSet<ExecutionPhase> getEnabledPhases() {
			EnumSet<ExecutionPhase> result = EnumSet.of(ExecutionPhase.INITIALIZE, ExecutionPhase.RUN, ExecutionPhase.CLEANUP);
			logger.trace("getEnabledPhases() => {}", result);
			return result;
		}

		@Override
		public TimeUnit getDurationTimeUnit(ExecutionPhase phase) {
			return TimeUnit.MILLISECONDS;
		}

		@Override
		public long getDurationMeasure(ExecutionPhase phase) {
			nPostDelay++;
			int result = 500;
			logger.trace("getPostDelay({}) => {}", phase, result);
			return result;
		}
	}

	@Test
	public void test0() throws ExecutionException, InterruptedException {
		ExecutionQuantifierConfiguration config = new MockQuantificationConfigurationExecution();
		MockQuantifierContext context = new MockQuantifierContext();
		ExecutionQuantifier executionQuantifier = new ExecutionQuantifier(config);
		Optional<ExecutionQuantifier.Result<Object>> result = executionQuantifier.quantify(context);

		Assert.assertEquals("should have 9 cancel checks", 9, context.nCancel);
		Assert.assertEquals("should have 3 handled phases", 3, context.nHandle);
		Assert.assertEquals("should have 3 post-delayed phase", 3, context.nPostDelay);
		Assert.assertEquals("should have INITIALIZE as result", ExecutionPhase.INITIALIZE, result.map(ExecutionQuantifier.Result::getValue).orElse(null));
	}

	@Test
	public void testResultOrder() {
		EnumMap<ExecutionPhase, Integer> result = new EnumMap<>(ExecutionPhase.class);

		result.put(ExecutionPhase.INITIALIZE, 0);
		result.put(ExecutionPhase.RUN, null);
		result.put(ExecutionPhase.RETRIEVE_DATA, 2);
		result.put(ExecutionPhase.CLEANUP, 3);

		Optional<ExecutionQuantifier.Result<Integer>> r = ExecutionQuantifier.getMostSignificantPhaseResult(result);
		Assert.assertEquals(2, (int)r.get().getValue());
		Assert.assertEquals(ExecutionPhase.RETRIEVE_DATA, r.get().getExecutionPhase());

		result.remove(ExecutionPhase.RETRIEVE_DATA);
		r = ExecutionQuantifier.getMostSignificantPhaseResult(result);
		Assert.assertEquals(3, (int)r.get().getValue());
		Assert.assertEquals(ExecutionPhase.CLEANUP, r.get().getExecutionPhase());

		result.remove(ExecutionPhase.CLEANUP);
		result.put(ExecutionPhase.RETRIEVE_DATA, null);
		r = ExecutionQuantifier.getMostSignificantPhaseResult(result);
		Assert.assertEquals(0, (int)r.get().getValue());
		Assert.assertEquals(ExecutionPhase.INITIALIZE, r.get().getExecutionPhase());

		result.put(ExecutionPhase.RETRIEVE_DATA, 4);
		r = ExecutionQuantifier.getMostSignificantPhaseResult(result);
		Assert.assertEquals(4, (int)r.get().getValue());
		Assert.assertEquals(ExecutionPhase.RETRIEVE_DATA, r.get().getExecutionPhase());

		result.clear();
		r = ExecutionQuantifier.getMostSignificantPhaseResult(result);
		Assert.assertFalse(r.isPresent());
	}

	/*
	 * Should cancel in every phase and result an empty result.
	 */
	@Test
	public void testCancellation() throws ExecutionException, InterruptedException {
		ExecutionQuantifierContext context = new ExecutionQuantifierContext() {
			@Override
			public boolean isCancelled() { return true; }
			@Override
			public Object handle(ExecutionPhase phase) { return null; }
			@Override
			public EnumSet<ExecutionPhase> getEnabledPhases() { return EnumSet.allOf(ExecutionPhase.class); }
			@Override
			public TimeUnit getDurationTimeUnit(ExecutionPhase phase) { return TimeUnit.SECONDS; }
			@Override
			public long getDurationMeasure(ExecutionPhase phase) { return 100; }
		};
		ExecutionQuantifier q = new ExecutionQuantifier(new MockQuantificationConfigurationExecution());
		Optional<ExecutionQuantifier.Result<Object>> result = q.quantify(context);
		Assert.assertFalse(result.isPresent());
	}
	
	@Test
	public void testHandlerException() throws ExecutionException, InterruptedException {
		ExecutionQuantifierContext context = new ExecutionQuantifierContext() {
			@Override
			public boolean isCancelled() { return false; }
			
			@Override
			public Object handle(ExecutionPhase phase) {
				if (phase != ExecutionPhase.INITIALIZE) {
					throw new IllegalArgumentException("Throwing during non-initialize.");
				}
				return "hello";
			}
			
			@Override
			public EnumSet<ExecutionPhase> getEnabledPhases() { return EnumSet.of(ExecutionPhase.INITIALIZE, ExecutionPhase.RUN, ExecutionPhase.CLEANUP); }
			
			@Override
			public TimeUnit getDurationTimeUnit(ExecutionPhase phase) { return TimeUnit.SECONDS; }
			
			@Override
			public long getDurationMeasure(ExecutionPhase phase) { return 0; }
		};
		ExecutionQuantifier q = new ExecutionQuantifier(new MockQuantificationConfigurationExecution());
		Assert.assertEquals("hello", q.quantify(context).get().getValue());
		
		
	}
}
