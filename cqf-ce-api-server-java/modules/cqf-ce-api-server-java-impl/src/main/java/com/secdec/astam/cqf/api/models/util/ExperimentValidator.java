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

import com.secdec.astam.cqf.api.models.ExperimentElement;
import com.secdec.astam.cqf.api.models.ExperimentElementRef;

/**
 * @author srogers
 */
public class ExperimentValidator {

	public static boolean accept(ExperimentElement root, boolean must_be_valid, boolean has_been_created) {

		return accept(root, must_be_valid, has_been_created, 0);
	}

	private static boolean accept(ExperimentElement element, boolean must_be_valid, boolean has_been_created, int depth) {

		boolean result = true;

		result = result && ExperimentIdValidator.accept(
				element.getId(), must_be_valid, has_been_created, depth
		);
		result = result && ExperimentDesignValidator.accept(
				element.getDesign(), must_be_valid, depth
		);
		result = result && (element.getParameterBindings().stream()
				.allMatch(pb -> ParameterBindingValidator.accept(
						pb, must_be_valid, depth
				))
		);
		result = result && ExecutionStateValidator.accept(
				element.getExecution(), must_be_valid, has_been_created, depth
		);
		result = result && (element.getChildren().stream()
				.allMatch(er -> ExperimentValidator.accept(
						er, must_be_valid, has_been_created, depth+1
				))
		);
		assert ! must_be_valid || result;
		return result;
	}

	private static boolean accept(ExperimentElementRef elementRef, boolean must_be_valid, boolean has_been_created, int depth) {

		boolean result = true;

		String            r = elementRef.getObjectRole();
		String            k = elementRef.getObjectKey();
		ExperimentElement o = elementRef.getObject();

		result = result && (r == null || r.trim().length() != 0);

		result = result && (k != null || o != null);

		if (result && k != null && o != null) {

			result = result && k.equals(o.getId());
		}

		result = result && ExperimentValidator.accept(o, must_be_valid, has_been_created, depth);

		assert ! must_be_valid || result;
		return result;
	}

}
