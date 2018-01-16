package com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util;

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

import static org.apache.commons.lang3.Validate.isTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class SnapshotDescriptionParametersParserTest {

	private SnapshotDescriptionParametersParser parser;

	@Before
	public void init() {
		parser = new SnapshotDescriptionParametersParser();
	}

	/**
	 * Returns a map from successive key value pairs from the arguments.
	 * 
	 * @param strings the key/value paris
	 * @return the map
	 */
	private Map<String, String> makeMap(String... strings) {
		isTrue((strings.length & 1) == 0, "makeMap requires an even number of arguments");
		Map<String, String> result = new HashMap<>();
		for (int i = 0, n = strings.length; i < n; i += 2) {
			result.put(strings[i], strings[i + 1]);
		}
		return result;
	}

	@Test
	public void testParseDescription() {
		String string = "CQF[adminusername=test=2;"
				+ "adminpassword=test123; "
				+ "platform=Unix; "
				+ "variant=name=v1, a=a1, b= b0;"
				+ "variant=name=v2, a=a2, b = b0 ; "
				+ "variant=name=v3, a=a3, b  = v2"
				+ "]CQF";
		
		SnapshotDescriptionParametersParser.Description d = parser.parseDescription(string).get();
		assertNotNull(d.toString());
		
		Map<String, String> parameters = d.getParameters();
		Map<String, String> exp = makeMap("adminusername", "test=2", "adminpassword", "test123", "platform", "Unix");
		assertEquals(exp, parameters);
		
		List<SnapshotDescriptionParametersParser.Variant> vs = d.getVariants();
		assertEquals(3, vs.size());
	}

	@Test
	public void testParse_missingBegin() {
		assertFalse(parser.parseDescription("la la la")
				.isPresent());
	}

	@Test
	public void testParse_missingEnd() {
		assertFalse(parser.parseDescription("CQF[la la la")
				.isPresent());
	}

	@Test
	public void testParse_beginEndSwapped() {
		assertFalse(parser.parseDescription("]CQF CQF[")
				.isPresent());
	}

	@Test
	public void testParse_skipsEntriesWithNoEquals() {
		assertEquals(makeMap("k0", "v0", "k2", "v2"),
				parser.parseDescription("CQF[k0=v0;k1v1v2;k2=v2]CQF")
						.map(SnapshotDescriptionParametersParser.Description::getParameters)
						.get());
	}
	
	@Test
	public void testParseVariant() {
		SnapshotDescriptionParametersParser.Variant v = SnapshotDescriptionParametersParser.Variant.parse("name=x,p1=v1,p2=v2").get();
		
		assertEquals("x", v.getName());
		assertEquals(makeMap("p1","v1","p2","v2"), v.getParameters());
		
		assertNotNull(v.toString());
	}
	
	@Test
	public void testParseVariant_empty() {
		assertFalse(SnapshotDescriptionParametersParser.Variant.parse("").isPresent());
	}

}
