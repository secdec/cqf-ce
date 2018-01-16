package com.siegetechnologies.cqf.core.experiment.design;

/*-
 * #%L
 * astam-cqf-ce-core
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

import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import java.util.ArrayList;
import java.util.List;

public class ExperimentDesignElementImplBase extends ExperimentDesignElementImpl
{
	protected String/*              */ name;
	protected String/*              */ category;
	protected String/*              */ variant;
	protected List<ParameterImpl>/* */ parameters/*    */ = new ArrayList<>();
	protected DocumentationImpl/*   */ documentation/* */ = new DocumentationImpl();
	
	/**/
	
	public ExperimentDesignElementImplBase() {
		
		this(null, "unknown_name", "unknown_category");
	}
	
	public ExperimentDesignElementImplBase(
			ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver,
			String name,
			String category
	) {
		this(resolver, name, category, null);
	}
	
	public ExperimentDesignElementImplBase(
			ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver,
			String name,
			String category,
			String variant
	) {
		super(resolver);
		
		this.name = name;
		this.category = category;
		this.variant = variant;
	}
	
	/**/
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public DocumentationImpl getDocumentation() {
		return this.documentation;
	}
	
	@Override
	public String getCategory() {
		return this.category;
	}
	
	@Override
	public String getVariantName() {
		return this.variant;
	}
	
	@Override
	public List<ParameterImpl> getOwnParameters() {
		return this.parameters;
	}
	
};

