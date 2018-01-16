package com.secdec.astam.cqf.api.rest.app.aor;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-webapp-jersey
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Provides a JSON object mapper for this JAX application.
 * Pretty printing is enabled.
 *
 * @author taylorj
 */
@Provider
public class JSONObjectMapperProvider implements ContextResolver<ObjectMapper>
{
	/**
	 * JSON object mapper for this JAX application; pre-configured.
	 */
	private static final ObjectMapper theJSONObjectMapper = (new ObjectMapper()
			
			.configure(SerializationFeature.INDENT_OUTPUT, true)
	);

	/**
	 * Returns the JSON object mapper for this JAX application; idempotent.
	 *
	 * @return the JSON object mapper for this JAX application; idempotent
	 */
	@Override
	public ObjectMapper getContext(Class<?> type) {

		return theJSONObjectMapper;
	}

}
