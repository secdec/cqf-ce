package com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.handlers;

/*-
 * #%L
 * cqf-ce-testbed-base
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

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;

/**
 * Handler for running commands on a testbed machine.
 *
 * @author taylorj
 *
 * @param <R_before>
 */
public class ProgramExecutionHandler<R_before>
		extends BasicExecutionHandlerForTestbedBase<R_before, Long>
{
	@Override
	public String getDesignName() {
		return "Run Command"; // FIXME: STRING: srogers
	}

	@Override
	public String getDesignCategory() {
		return "Util"; // FIXME: STRING: srogers
	}

	/**/

	@Override
	public Long run(ExperimentElementExecutionContext context, R_before result_before) {
		try {
			super.run(context, result_before);
		}
		finally {
			return base_toolkit_of(context).invokeProgramInExecutionContext(context);
		}
	}

	/**/

}
