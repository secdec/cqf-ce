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

import com.secdec.astam.cqf.api.models.ParameterBinding;

import java.util.List;

/**
 * @author srogers
 */
public class ParameterBindingValidator {

	public static boolean accept(ParameterBinding pb, boolean must_be_valid, int depth) {

		boolean result = true;

		String       n  = pb.getName();
		String       v  = pb.getValue();
		List<String> cl = pb.getCodec();

		result = result && (n != null && n.trim().length() != 0);

		result = result && (v == null || v.trim().length() >= 0);

		result = result && (cl.stream()
				.allMatch(c -> c != null && c.trim().length() != 0)
		);

		// FIXME: TESTING: srogers: add further validation of codecs

		// FIXME: TESTING: srogers: add validation about parameters that must exist (only) in the root element

		result = result && (depth >= 0);

		assert ! must_be_valid || result;
		return result;
	}

}
