/**
 * This package implements the server's REST API responders.
 *
 * The server is organized as layers of an onion. The outermost layer
 * receives requests, and is auto-generated at build time based on a Swagger
 * specification of the REST API. The middle layer is a specialization of the
 * outermost layer, formed by sub-classing; it assembles responses to requests
 * and delegates to the domain logic. The remaining layers provide the domain 
 * logic itself. This keeps the most valuable piece of the puzzle, the domain
 * logic, free from from application container dependencies, which can change
 * drastically over time and across platforms.
 * 
 * @author srogers
 */
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
