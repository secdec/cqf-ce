package com.secdec.astam.cqf.api.rest.app;

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

import com.secdec.astam.cqf.api.rest.app.aor.ConfigurationProviderTestSubject;
import com.secdec.astam.cqf.api.rest.app.aor.DesignCatalogMapProviderTestSubject;
import com.secdec.astam.cqf.api.rest.app.aor.ExecutionPlatformManagerTestSubject;

/**
 * @author srogers
 */
public class CQFResourceManagerTestSubject extends CQFResourceManager
{
	@Override
	protected void initConfigurationProvider() {

		this.configurationProvider = new ConfigurationProviderTestSubject();
	}
	
	@Override
	protected void initDesignCatalogMapProvider() {

		this.designCatalogMapProvider = new DesignCatalogMapProviderTestSubject();
	}

	@Override
	protected void initExecutionHandlerRegistrar() {
		
		super.initExecutionHandlerRegistrar();
		//^-- TODO: REVIEW: srogers: no test subject needed??
	}
	
	@Override
	protected void initExecutionPlatformManager() {
		
		this.executionPlatformManager = new ExecutionPlatformManagerTestSubject();
	}

}
