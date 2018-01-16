package com.secdec.astam.cqf.api.rest.responders;

/*-
 * #%L
 * cqf-ce-api-server-java-impl
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

import com.secdec.astam.cqf.api.models.Error;
import com.secdec.astam.cqf.api.models.ExperimentElement;
import com.secdec.astam.cqf.api.rest.ExperimentElementsApi;

import javax.ws.rs.core.Response;

/**
 * Responds to REST API requests pertaining to ExperimentElement resources.
 * 
 * @author srogers
 *
 */
public class ExperimentElementsApiResponder extends ExperimentElementsApi  {

    @Override
    public Response getExperimentElement(String id) {

    	return Response.ok().entity("experiment element magic!").build();
    }

    @Override
    public Response getExperimentElements(String subtypeRegexp, String categoryRegexp) {

    	return Response.ok().entity("experiment element(s) magic!").build();
    }
}

