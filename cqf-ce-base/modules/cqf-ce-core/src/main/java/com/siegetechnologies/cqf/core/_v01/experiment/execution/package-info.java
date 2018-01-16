/**
 * This package contains the core functionality for experiment execution.
 * After an experiment design is constructed, an {@link com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext}
 * is constructed for the the top-level {@link com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl}
 * in the experiment (typically a type of "Workspace").  Then, an
 * {@link com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionHandler} is used to actually
 * execute the experiment.
 * 
 * @author taylorj
 */
package com.siegetechnologies.cqf.core._v01.experiment.execution;

/*-
 * #%L
 * cqf-ce-core
 * %%
 * Copyright (C) 2009 - 2017 Siege Technologies, LLC
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
