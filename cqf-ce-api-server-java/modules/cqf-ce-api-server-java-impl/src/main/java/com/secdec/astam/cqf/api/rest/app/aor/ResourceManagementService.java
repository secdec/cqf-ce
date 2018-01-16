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

import java.io.IOException;
import org.apache.commons.configuration2.ImmutableConfiguration;

/**
 * @author srogers
 */
public interface ResourceManagementService
{
	/**
	 * Starts up a resource management service; maintains the boolean property 'running' accordingly.
	 *
	 * @throws IOException
	 */
	void startup(ImmutableConfiguration configuration) throws IOException;
	
	/**
	 * Shuts down a resource management service; maintains the boolean property 'running' accordingly.
	 *
	 */
	void shutdown();
	
	/**
	 * Returns whether the resource management service is running.
	 * It is <em>running</em> if it has successfully started up, and has not yet been shutdown.
	 *
	 * @return whether the resource management service is running
	 */
	boolean isRunning();
	
	/**/
	
	/**
	 * Returns the configuration of this resource management service.
	 * The configuration is established at startup time, and is immutable until shutdown.
	 *
	 * @return the configuration of this resource management service
	 */
	ImmutableConfiguration getConfiguration();
	
	/**/
	
}
