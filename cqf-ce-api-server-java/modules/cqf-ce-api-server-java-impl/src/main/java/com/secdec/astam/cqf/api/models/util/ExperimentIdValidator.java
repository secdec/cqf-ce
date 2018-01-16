package com.secdec.astam.cqf.api.models.util;

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

/**
 * @author srogers
 */
public class ExperimentIdValidator {

	public static boolean accept(String id, boolean must_be_valid, boolean has_been_created, int depth) {

		boolean result = true;

		result = result && (id != null || ! has_been_created);

		result = result && (id == null || id.trim().length() != 0);

		result = result && (depth >= 0);

		assert ! must_be_valid || result;
		return result;
	}

}
