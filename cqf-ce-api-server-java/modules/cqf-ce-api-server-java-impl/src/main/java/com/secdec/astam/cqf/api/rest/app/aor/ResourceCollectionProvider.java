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
public interface ResourceCollectionProvider
{
	/**
	 * Loads the resource collection managed by this provider; maintains the boolean property 'available' accordingly.
	 * Upon return, the resource collection is available for use.
	 *
	 * @throws IOException
	 */
	void load(ImmutableConfiguration configuration) throws IOException;
	
	/**
	 * Unloads the resource collection managed by this provider; maintains the boolean property 'available' accordingly.
	 * Upon return, the resource collection is no longer available.
	 *
	 * @throws IOException
	 */
	void unload();
	
	/**
	 * Returns whether the resource collection is available.
	 * It is <em>available</em> if it has successfully loaded, and has not yet been unloaded.
	 *
	 * @return whether the resource collection is available
	 */
	boolean isAvailable();
	
	/**/
	
	/**
	 * Returns the configuration of this resource collection provider.
	 * The configuration is established at load time, and is immutable until unload.
	 *
	 * @return the configuration of this resource collection provider
	 */
	ImmutableConfiguration getConfiguration();
	
	/**/
	
}

