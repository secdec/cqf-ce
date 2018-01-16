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

import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * JAX-RS application entry point for CQF in a Jersey-based web container.
 *
 * @author srogers
 */
@ApplicationPath("/api/v1")
public class CQFJerseyApplication extends CQFApplication {

    private static final Logger logger = Logger.getLogger(CQFJerseyApplication.class.getName());

    static {
        logger.log(Level.FINEST, "loading: {0}", CQFJerseyApplication.class);
    }

    /**
     * Creates a (singleton) object of this type.
     */
    public CQFJerseyApplication() {

        this(new CQFJerseyResourceManager());
        
        assert this == CQFApplication.theInstance();
        //^-- By design: the JAX container (provided by Jersey) creates exactly one CQFJerseyApplication
    }

	protected CQFJerseyApplication(CQFResourceManager resourceManager) {

    	super(resourceManager);
	}

}
