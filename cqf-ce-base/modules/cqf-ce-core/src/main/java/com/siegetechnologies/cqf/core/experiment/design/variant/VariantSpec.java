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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Specifies modifications to an item in order to generate a variant
 */
public class VariantSpec {

    /**
     * Source item to build the variant of
     */
    private final ExperimentDesignElementId source;

    /**
     * Name of the new variant (put in the variantName parameter of an item)
     */
    private final String name;

    /**
     * List of files to replace in the new item
     */
    private final List<String> files = new ArrayList<>();

    /**
     * List of parameters to replace/add
     */
    private final List<ParameterImpl> parameters = new ArrayList<>();
	
	/**
	 * Sets variant based on Design ID
	 * @param source
	 * @param variantName
	 */
    public VariantSpec(ExperimentDesignElementId source, String variantName) {
        this.source = source;
        this.name = variantName;
    }
	
	/**
	 * Sets variant based on implementation
	 * @param source
	 * @param variantName
	 */
    public VariantSpec(ExperimentDesignElementImpl source, String variantName) {
        this.source = source.getId();
        this.name = variantName;
    }


    /**
     * Constructor used for deserializing a VariantSpec from a file.
     *
     * @param source
     * @param name
     * @param files
     * @param parameters
     */
    @JsonCreator
    public VariantSpec(@JsonProperty("source") ExperimentDesignElementId source,
                       @JsonProperty("name") String name,
                       @JsonProperty("files") List<String> files,
                       @JsonProperty("parameters") List<ParameterImpl> parameters) {
        this.source = source;
        this.name = name;
        Optional.ofNullable(files).ifPresent(this.files::addAll);
        Optional.ofNullable(parameters).ifPresent(this.parameters::addAll);
    }


    public List<String> getFiles() {
        return files;
    }

    public void addFile(String file) {
        this.files.add(file);
    }

    public List<ParameterImpl> getParameters() {
        return parameters;
    }

    public void addParameter(ParameterImpl param) {
        this.parameters.add(param);
    }

    public ExperimentDesignElementId getSource() {
        return source;
    }

    public String getName() {
        return name;
    }
}
