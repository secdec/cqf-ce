package com.siegetechnologies.cqf.core._v01.experiment;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//TODO: Make InstanceResourceSpec extend from this?

/**
 * A data transfer object for instance information.  Unless otherwise specified,
 * all fields may be null.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentElementSpec
{
    protected ExperimentDesignElementSpec item;
    protected String id;
    protected Integer executionIndex;
    protected Map<String, String> parameters;
    protected List<ExperimentElementSpec> children;

    public ExperimentElementSpec() {
    	// do nothing
    }

    /**
     * Removes the id value from this specification and from
     * any children.
     */
    public void clearUuids()
    {
        setId(null);
        children.forEach(ExperimentElementSpec::clearUuids);
    }

    @Override
    public String toString()
    {
        return String.format("InstanceResourceSpec(item=%s,id=%s,parameters=%s,children=%s)", item, id, parameters, children);
    }
	
	/**
	 * @return this design specification 
	 */
	public ExperimentDesignElementSpec getItem()
    {
        return item;
    }
	
	/**
	 * Set this design specification
	 * 
	 * @param item 
	 */
	public void setItem(ExperimentDesignElementSpec item)
    {
        this.item = item;
    }
	
	/**
	 * @return list of experiment children
	 */
	public List<ExperimentElementSpec> getChildren()
    {
        return children;
    }
	
	/**
	 * Set children for this current specification 
	 * 
	 * @param children
	 */
	public void setChildren(List<ExperimentElementSpec> children)
    {
        this.children = children;
    }
	
	/**
	 * @return the current experiment index 
	 */
	public Integer getExecutionIndex()
    {
        return executionIndex;
    }
	
	public void setExecutionIndex(Integer executionIndex)
    {
        this.executionIndex = executionIndex;
    }

    public Map<String, String> getParameters()
    {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }
	
	/**
	 * @return specification ID
	 */
	public String getId()
    {
        return id;
    }
	
	/**
	 * Set this specification ID
	 * 
	 * @param id
	 */
	public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns an experimentElement spec create from an experimentElement.
     *
     * @param experimentElement the experimentElement
     *
     * @return the experimentElement spec
     *
     * @throws IOException if an I/O error occurs
     */
    public static ExperimentElementSpec from(ExperimentElementImpl experimentElement) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        byte[] content = mapper.writeValueAsBytes(experimentElement);
        return mapper.readValue(content, ExperimentElementSpec.class);
    }

    /**
     * Returns a shallow copy of an instance spec.
     * 
     * @param of the instance spec
     * @return the shallow copy
     */
    public static ExperimentElementSpec shallowCopy(ExperimentElementSpec of)
    {
        ExperimentElementSpec spec = new ExperimentElementSpec();
        spec.setItem(of.getItem());
        spec.setExecutionIndex(of.getExecutionIndex());
        spec.setId(of.getId());
        spec.setChildren(null);
        Optional.ofNullable(of.getParameters()).ifPresent(p -> spec.setParameters(new HashMap<>(p)));
        return spec;
    }
}
