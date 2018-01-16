package com.secdec.astam.cqf.api.rest.app;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-webapp-jersey
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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages all active resources for a CQFJerseyApplication.
 */
public class CQFJerseyResourceManager extends CQFResourceManager {

    private static final Logger logger = Logger.getLogger(CQFJerseyResourceManager.class.getName());

    static {
        logger.log(Level.FINEST, "loading: {0}", CQFJerseyResourceManager.class);
    }

    /**
     * Creates a (normally singleton) object of this type.
     * <p/>
     * <em>Implementation note:</em><br>
     * In testing scenarios, multiple objects of this type can exist at the same time.
     */
    protected CQFJerseyResourceManager() {

        super();
    }

    /**/
    
	@Override
	protected void initRootResourceAndProviderAndFeatureClasses() {

		super.initRootResourceAndProviderAndFeatureClasses();

		rootResourceAndProviderAndFeatureClasses.addAll(Arrays.asList(
				org.glassfish.jersey.jackson.JacksonFeature.class,
				org.glassfish.jersey.media.multipart.MultiPartFeature.class
		));
	}

	@Override
    protected void initRootResourceAndProviderAndFeatureSingletons() {

        super.initRootResourceAndProviderAndFeatureSingletons();

        /**/
    }

    @Override
    protected void initProperties() {

        super.initProperties();

        properties.put("jersey.config.server.application.name", "cqf-ce"); // FIXME: STRING: srogers

        properties.put("jersey.config.server.disableAutoDiscovery", "true");
        properties.put("jersey.config.server.disableJsonProcessing", "false");
        properties.put("jersey.config.server.disableMetainfServicesLookup", "false");

        properties.put("jersey.config.server.provider.packages", "com.secdec.astam.cqf.api.rest.responders"); // FIXME: STRING: srogers
        properties.put("jersey.config.server.provider.scanning.recursive", "true");

        properties.put("jersey.config.server.resource.validation.disable", "true");
        properties.put("jersey.config.server.resource.validation.ignoreErrors", "true");

        properties.put("jersey.config.server.tracing.type", "ALL");
        properties.put("jersey.config.server.tracing.threshold", "VERBOSE");
    }

}
