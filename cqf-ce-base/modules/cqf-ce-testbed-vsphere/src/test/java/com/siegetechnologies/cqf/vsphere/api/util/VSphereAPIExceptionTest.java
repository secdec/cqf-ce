package com.siegetechnologies.cqf.vsphere.api;

/*-
 * #%L
 * cqf-ce-testbed-vsphere
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

import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIException;
import org.junit.Test;

public class VSphereAPIExceptionTest {
	
	void checkMessageAndCause(Throwable t, String msg, Throwable cause) {
		assertEquals("mesasge should be: "+msg, msg, t.getMessage());
		assertEquals("cause should be: "+cause, cause, t.getCause());
	}
	
	@Test
	public void testConstructor1() {
		checkMessageAndCause(new VSphereAPIException("msg"), "msg", null);
	}
	
	@Test
	public void testConstructor2() {
		Exception e = new RuntimeException();
		checkMessageAndCause(new VSphereAPIException(e), e.toString(), e);
	}
	
	@Test
	public void testConstructor3() {
		Exception e = new RuntimeException();
		checkMessageAndCause(new VSphereAPIException("msg", e), "msg", e);
	}

}
