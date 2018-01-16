package com.siegetechnologies.cqf.core.experiment.execution.util;

/*-
 * #%L
 * astam-cqf-ce-core
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

import java.time.Instant;

public class ExecutionTraceRecordImplTest {
	@Test
	public void testConstructor(){
		ExecutionTraceRecordImpl etri = new ExecutionTraceRecordImpl();
		Assert.assertNull(etri.getTimestamp());
		
		Instant s = Instant.now();
		
		etri.setTimestamp(s);
		Assert.assertEquals(etri.getTimestamp(), s);
	}
	
	@Test
	public void testUpdate(){
		ExecutionTraceRecordImpl etri1 = new ExecutionTraceRecordImpl();
		ExecutionTraceRecordImpl etri2 = new ExecutionTraceRecordImpl();
		
		Instant s = Instant.now();
		etri1.setTimestamp(s);
		
		ExecutionTraceRecordImpl etri_result = etri2.copyAndUpdateWithDefaultsFrom(etri1);
		Assert.assertTrue(etri_result.equals(etri1));
		
	}
}
