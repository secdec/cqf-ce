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

import com.secdec.astam.cqf.api.rest.app.aor.ResourceCollectionProvider;
import java.io.IOException;
import org.apache.commons.configuration2.ImmutableConfiguration;

/**
 * @author srogers
 */
public abstract class ResourceCollectionProviderBase implements ResourceCollectionProvider
{
	private boolean available = false;
	
	protected ImmutableConfiguration configuration = null;
	
	@Override
	public final synchronized void load(ImmutableConfiguration configuration) throws IOException {
		
		if (! this.available) {
			
			this.configuration = configuration;
			load_internal();
			
			this.available = true;
		}
		else if (this.configuration != configuration) {
			
			throw new IllegalStateException("configuration has already been set");
		}
		
		assert this.available;
	}
	
	/**
	 * Called by {@link #load}; subclasses should override this method instead of {@link #load}.
	 * That way {@link #load} can guarantee the invariant for the property 'loaded'.
	 */
	protected void load_internal() throws IOException {
		
		/* default implementation does nothing */
	}
	
	/**
	 * Called by {@link #unload}; subclasses should override this method instead of {@link #unload()}.
	 * That way {@link #unload} can guarantee the invariant for the property 'loaded'.
	 */
	protected void unload_internal() {
		
		/* default implementation does nothing */
	}
	
	@Override
	public final synchronized void unload() {
		
		if (this.available) {
			
			this.available = false;
			
			unload_internal();
			this.configuration = null;
		}
		
		assert ! this.available;
	}
	
	@Override
	public synchronized boolean isAvailable() {
		
		return this.available;
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
