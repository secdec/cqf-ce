package com.secdec.astam.cqf.api.rest.app.aor.impl;

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

import com.secdec.astam.cqf.api.rest.app.aor.ResourceManagementService;
import java.io.IOException;
import org.apache.commons.configuration2.ImmutableConfiguration;

/**
 * @author srogers
 */
public class ResourceManagementServiceBase implements ResourceManagementService
{
	private boolean running = false;
	
	protected ImmutableConfiguration configuration = null;
	
	@Override
	public final synchronized void startup(ImmutableConfiguration configuration) throws IOException {
		
		if (! this.running) {
			
			this.configuration = configuration;
			startup_internal();
			
			this.running = true;
		}
		else if (this.configuration != configuration && configuration != null) {
			
			throw new IllegalStateException("configuration has already been set");
		}
		
		assert this.running;
	}
	
	/**
	 * Called by {@link #startup}; subclasses should override this method instead of {@link #startup}.
	 * That way {@link #startup} can guarantee the invariant for the property 'running'.
	 */
	protected void startup_internal() throws IOException {
		
		/* default implementation does nothing */
	}
	
	/**
	 * Called by {@link #shutdown()}; subclasses should override this method instead of {@link #shutdown()}.
	 * That way {@link #shutdown()} can guarantee the invariant for the property 'running'.
	 */
	protected void shutdown_internal() {
		
		/* default implementation does nothing */
	}
	
	@Override
	public final synchronized void shutdown() {
		
		if (this.running) {
			
			this.running = false;
			
			shutdown_internal();
			this.configuration = null;
		}
		
		assert ! this.running;
	}
	
	@Override
	public synchronized boolean isRunning() {
		
		return this.running;
	}
	
	/**/
	
	@Override
	public synchronized ImmutableConfiguration getConfiguration() {
		
		if (this.configuration == null) {
			throw new IllegalStateException("configuration has not been set");
		}
		return this.configuration;
	}

	/**/
	
}
