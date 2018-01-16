package com.secdec.astam.cqf.api.rest.app.aor;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-impl
 * %%
 * Copyright (C) 2016 - 2017 Applied Visions, Inc.
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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

public class JSONObjectMapperProviderTest
{
	@Test
	public void testIndentOutput() {
		
		JSONObjectMapperProvider provider = new JSONObjectMapperProvider();
		ObjectMapper mapper = provider.getContext(JSONObjectMapperProvider.class);
		assertTrue("INDENT_OUTPUT should be enabled", mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
	}
	
	@Test
	public void testGetContext() {
		
		ObjectMapper mapper1 = new JSONObjectMapperProvider().getContext(ObjectMapper.class);
		ObjectMapper mapper2 = new JSONObjectMapperProvider().getContext(ObjectMapper.class);
		assertSame("getContext() should be idempotent for mapper object", mapper1, mapper2);
	}
	
}
