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

import com.secdec.astam.cqf.api.rest.app.aor.impl.ExecutionPlatformManagerBase;
import java.io.IOException;

/**
 * @author srogers
 */
public class ExecutionPlatformManager extends ExecutionPlatformManagerBase
{
	protected ExecutionPlatformToolkit/* */ toolkit;
	
	protected SessionManager/*           */ sessionManager;
	protected ExperimentManager/*        */ experimentManager;
	
	/**/
	
	/**
	 * Creates an object of this type.
	 */
	public ExecutionPlatformManager() {
		
		this(null);
	}
	
	/**
	 * Creates an object of this type.
	 */
	public ExecutionPlatformManager(ExecutionPlatformManager parentExecutionPlatformManager) {
		
		super(parentExecutionPlatformManager);
		
		initToolkit();
		initSessionManager();
		initExperimentManager();
	}

	/**/
	
	protected void initToolkit() {
		
		if (this.parentExecutionPlatformManager != null) {
			this.toolkit = this.parentExecutionPlatformManager.getToolkit();
		}
		else {
			this.toolkit = ExecutionPlatformToolkit.getDefault();
			//^-- FIXME: srogers: select toolkit based on configuration provided
		}
		assert this.toolkit != null;
	}
	
	/**
	 * Returns the toolkit used by this execution platform manager.
	 *
	 * @return the toolkit used by this execution platform manager
	 */
	public final ExecutionPlatformToolkit getToolkit() {
		
		if (this.toolkit == null) {
			throw new IllegalStateException("toolkit has not been set");
		}
		return toolkit;
	}

	/**/
	
	protected void initSessionManager() {
		
		if (this.parentExecutionPlatformManager != null) {
			this.sessionManager = this.parentExecutionPlatformManager.getSessionManager();
		}
		else {
			this.sessionManager = this.toolkit.newSessionManager(this);
		}
		assert this.sessionManager != null;
	}
	
	protected final synchronized void startupSessionManager() throws IOException {
		
		this.sessionManager.startup(getConfiguration());
	}
	
	protected final synchronized void shutdownSessionManager() {
		
		this.sessionManager.shutdown();
	}
	
	/**
	 * Returns the session manager used by this execution platform manager.
	 *
	 * @return the session manager used by this execution platform manager
	 */
	public final SessionManager getSessionManager() {
		
		if (this.sessionManager == null) {
			throw new IllegalStateException("session manager has not been set");
		}
		return this.sessionManager;
	}
	
	public final <U extends SessionManager> U getSessionManagerAs(Class<U> sessionManagerSubclass) {
		
		U result = sessionManagerSubclass.cast(this.getSessionManager());
		return result;
	}

	/**/
	
	protected void initExperimentManager() {
		
		if (this.parentExecutionPlatformManager != null) {
			this.experimentManager = this.parentExecutionPlatformManager.getExperimentManager();
		}
		else {
			this.experimentManager = this.toolkit.newExperimentManager(this);
		}
		assert this.experimentManager != null;
	}
	
	protected final synchronized void startupExperimentManager() throws IOException {
		
		this.experimentManager.startup(getConfiguration());
	}
	
	protected final synchronized void shutdownExperimentManager() {
		
		this.experimentManager.shutdown();
	}
	
	/**
	 * Returns the experiment manager used by this execution platform manager.
	 *
	 * @return the experiment manager used by this execution platform manager
	 */
	public final synchronized ExperimentManager getExperimentManager() {
		
		if (this.experimentManager == null) {
			throw new IllegalStateException("experiment manager has not been set");
		}
		return this.experimentManager;
	}
	
	public final <U extends ExperimentManager> U getExperimentManagerAs(Class<U> experimentManagerSubclass) {
		
		U result = experimentManagerSubclass.cast(this.getExperimentManager());
		return result;
	}

	/**/
	
	protected void startup_internal() throws IOException {
		
		startupSessionManager();
		startupExperimentManager();
	}
	
	protected void shutdown_internal() {
		
		shutdownExperimentManager();
		shutdownSessionManager();
	}
	
}
