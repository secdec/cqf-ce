package com.siegetechnologies.cqf.core.experiment.design.variant;

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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;

import java.util.List;

/**
 * Generator used to build variants of an item
 */
@FunctionalInterface
public interface VariantGenerator {

    /**
     * Generates a list of variant specifiers for a design element.  This can be
     * fed into another engine to do the actual construction of ExperimentDesignElement
     * variants
     * @param designElement ExperimentDesignElement to generate variants of
     * @return List of specifiers for new items.
     */
    List<VariantSpec> generate(ExperimentDesignElementImpl designElement);
}
