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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for running the CQF Test Scheduling item.
 *
 * @param <R_main>
 *
 * @author taylorj
 */
public class ScheduledRunStepExecutionHandler<R_main>
		extends BasicExecutionHandlerForTestbedBase<Long, R_main>
{
	private static final Logger logger = LoggerFactory.getLogger(ScheduledRunStepExecutionHandler.class);

	/**/

	@Override
	public String getDesignName() {
		return "Scheduler"; // FIXME: STRING: srogers
	}

	@Override
	public String getDesignCategory() {
		return "CQF"; // FIXME: STRING: srogers
	}

	/**/

	@Override
	public Long run_beforeHook(ExperimentElementExecutionContext context) {

		logger.trace("ScheduledRunStepExecutionHandler.run_beforeHook({})", context);

		return base_toolkit_of(context).invokeScheduleScriptInExecutionContext(context);
	}

	/**/

}
