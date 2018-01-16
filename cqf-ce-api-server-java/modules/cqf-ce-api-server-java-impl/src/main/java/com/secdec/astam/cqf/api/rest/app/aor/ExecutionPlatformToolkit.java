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

import com.secdec.astam.cqf.api.rest.app.aor.impl.e2e.ExecutionPlatformToolkitForE2ETesting;
import com.secdec.astam.cqf.api.rest.app.aor.impl.vsphere.ExecutionPlatformToolkitForVSphere;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPlatform;

/**
 * @author srogers
 */
public interface ExecutionPlatformToolkit
{
	public static ExecutionPlatformToolkit getDefault() {
		
		return new ExecutionPlatformToolkitForE2ETesting();
	}
	
	public static ExecutionPlatformToolkit get(ExecutionPlatform executionPlatform) {
		
		switch (executionPlatform) {
		case Default:
			return getDefault();
		case Test:
			return new ExecutionPlatformToolkitForE2ETesting();
		case vSphere:
			return new ExecutionPlatformToolkitForVSphere();
		default:
		case Nil:
			throw new UnsupportedOperationException("no toolkit for execution platform: " + executionPlatform);
		}
	}

	/**/
	
	SessionManager newSessionManager(ExecutionPlatformManager parentExecutionPlatformManager);
	
	ExperimentManager newExperimentManager(ExecutionPlatformManager parentExecutionPlatformManager);
	
	/**/
	
}

//^-- FIXME: srogers: finish implementing ExecutionPlatformToolkit as service provider framework
