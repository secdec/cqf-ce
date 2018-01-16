package com.secdec.astam.cqf.api.rest.app.aor.impl.e2e;

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

import com.secdec.astam.cqf.api.rest.app.aor.ExecutionPlatformManager;
import com.secdec.astam.cqf.api.rest.app.aor.ExecutionPlatformToolkit;
import com.secdec.astam.cqf.api.rest.app.aor.ExperimentManager;
import com.secdec.astam.cqf.api.rest.app.aor.SessionManager;

/**
 * An execution platform toolkit that facilitates end-to-end testing.
 * Experiments are executed on a simulated execution platform.
 *
 * @author srogers
 */
public class ExecutionPlatformToolkitForE2ETesting implements ExecutionPlatformToolkit
{
	@Override
	public SessionManager newSessionManager(ExecutionPlatformManager parentExecutionPlatformManager) {
		
		return new SessionManagerForE2ETesting(parentExecutionPlatformManager);
	}
	
	@Override
	public ExperimentManager newExperimentManager(ExecutionPlatformManager parentExecutionPlatformManager) {
		
		return new ExperimentManagerForE2ETesting(parentExecutionPlatformManager);
	}
	
}
