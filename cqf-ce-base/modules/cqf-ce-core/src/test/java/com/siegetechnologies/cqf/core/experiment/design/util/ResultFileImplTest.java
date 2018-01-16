package com.siegetechnologies.cqf.core.experiment.design.util;

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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.io.IOException;

public class ResultFileImplTest {

	@Test
	public void testConstruction() {
		ResultFileImpl temporary = new ResultFileImpl("temporary", "1", "2");
		assertEquals(temporary.getHostPath(), "1");
	}
	
	@Test
	public void testContentStream() {
		ResultFileImpl temporary = new ResultFileImpl("temporary", "1", "2");
		byte[] b = new byte[1];
		b[0] = (byte) 1;
		try {
			temporary.setContent(b);
			assertNotNull(temporary.getContentInputStream());
		} catch(IOException e){
			fail();
		}
	}
	
	@Test
	public void testEquals() {
		ResultFileImpl r1 = new ResultFileImpl("platform", "a", "b");
		ResultFileImpl r2 = new ResultFileImpl("platform", "a", "b");
		ResultFileImpl r3 = new ResultFileImpl("platform", "a", "c");
		ResultFileImpl r4 = new ResultFileImpl("platform", "c", "b");
		ResultFileImpl r5 = new ResultFileImpl("platf0rm", "a", "b");
		
		assertNotEquals(r1, "a string");
		assertEquals(r1, r2);
		assertNotEquals(r1,r3);
		assertNotEquals(r1,r4);
		assertNotEquals(r1,r5);
		
	}

}
