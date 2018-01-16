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

import com.secdec.astam.cqf.api.rest.app.aor.impl.ResourceCollectionProviderBase;
import com.siegetechnologies.cqf.core.util.Config;
import java.io.IOException;
import org.apache.commons.configuration2.CompositeConfiguration;

/**
 * @author srogers
 */
public class ConfigurationProvider extends ResourceCollectionProviderBase
{
	protected CompositeConfiguration/* */ configuration;
	private static final boolean/*     */ configuration_isLoadedFromExternalSources = false;

	/**/

	@Override
	public void load_internal() throws IOException {

		if (this.configuration_isLoadedFromExternalSources) {
			this.configuration = Config.getConfiguration();
		} else {
			this.configuration = new CompositeConfiguration();
		}
	}

	@Override
	public void unload_internal() {

		Config.resetConfiguration();
		this.configuration = null;
	}
	
	/**
	 * Returns the configuration managed by this provider.
	 *
	 * @return the configuration managed by this provider
	 */
	public CompositeConfiguration getConfiguration() {

		if (! this.isAvailable()) {
			throw new IllegalStateException("configuration has not been loaded");
		}
		if (this.configuration == null) {
			throw new IllegalStateException("configuration has not been set");
		}
		return this.configuration;
	}

}
