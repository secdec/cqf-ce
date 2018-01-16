package com.siegetechnologies.cqf.core._v01.experiment.execution.mixin;

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


import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutor;

/**
 * Mixin that extends functionality of an {@link ExperimentExecutor}.  The apply
 * will add pre and/or post operations to the given ExperimentExecutor.  The goal of
 * these classes is to provide minor extra functionality without the need for excessive
 * subclassing
 */
public interface Mixin {
    void apply(ExperimentExecutor executor);

    String getId();
}
