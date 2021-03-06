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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExecutionPhaseTest {
	/**
	 * Check that the serialization of each run mode has a label and and a value 
	 * field.  These aren't used in the Java code, but are important when the 
	 * run modes are serialized via JSON. 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSerialization() throws IOException {
		ObjectMapper m = new ObjectMapper();
		for (ExecutionPhase phase : ExecutionPhase.values()) {
			JsonNode n = m.readTree(m.writeValueAsString(phase));
			Assert.assertTrue(phase+" should have label field.", n.has("label"));
			Assert.assertTrue(phase+" should have value field.", n.has("value"));
		}
	}

	@Test
	public void executionPhaseOrder() {
		List<ExecutionPhase> phases = Arrays.asList(ExecutionPhase.values());
		int init = phases.indexOf(ExecutionPhase.INITIALIZE);
		int run = phases.indexOf(ExecutionPhase.RUN);
		int retrieve = phases.indexOf(ExecutionPhase.RETRIEVE_DATA);
		int cleanup = phases.indexOf(ExecutionPhase.CLEANUP);

		Assert.assertTrue("INITIALIZE should come before RUN", init < run);
		Assert.assertTrue("RUN should come before RETRIEVE_DATA", run < retrieve);
		Assert.assertTrue("RETRIEVE_DATA should come before CLEANUP", retrieve < cleanup);


	}
}
