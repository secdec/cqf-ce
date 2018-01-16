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

import com.secdec.astam.cqf.api.models.ExperimentDesignElement;
import com.secdec.astam.cqf.api.models.ExperimentDesignElementRef;

/**
 * @author srogers
 */
public class ExperimentDesignValidator {

	public static boolean accept(ExperimentDesignElement designRoot, boolean must_be_valid) {

		return accept(designRoot, must_be_valid, 0);
	}

	public static boolean accept(ExperimentDesignElement designElement, boolean must_be_valid, int depth) {

		boolean result = true;

		// FIXME: TESTING: srogers: add further validation of ExperimentDesignElement

		result = result && (depth >= 0);

		assert ! must_be_valid || result;
		return result;
	}

	public static boolean accept(ExperimentDesignElementRef designElementRef, boolean must_be_valid, int depth) {

		boolean result = true;

		String                  r = designElementRef.getObjectRole();
		String                  k = designElementRef.getObjectKey();
		ExperimentDesignElement o = designElementRef.getObject();

		result = result && (r == null || r.trim().length() != 0);

		result = result && (k != null || o != null);

		if (result && k != null && o != null) {

			result = result && k.equals(o.getName());
		}

		result = result && ExperimentDesignValidator.accept(o, must_be_valid, depth);

		assert ! must_be_valid || result;
		return result;
	}

}
