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

import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A handler specification contains enough information to identify the handler
 * to be used in handling a particular instance context. It bundles an {@link #getExperimentDesignElementSpec() item specification},
 * a {@link #getExecutionPhase() test phase}, and the list of {@link #getAncestors() item ancestors}
 * (in order to walk up the ancestor chain looking for applicable handlers).
 */
public class ExperimentElementExecutionHandlerSpec {
	private final ExperimentDesignElementSpec designElementSpec;
	private final ExecutionPhase executionPhase;
	private final List<ExperimentDesignElementSpec> ancestors;

	/**
	 * Creates a new ExperimentElementExecutionHandlerSpec with values extracted from the ExperimentElementExecutionContext.
	 *
	 * @param context the context that provides the item name, category, ancestry and the test phase
	 */
	public ExperimentElementExecutionHandlerSpec(ExperimentElementExecutionContext context) {
		this(
				context.getExperimentElement().getDesign().getName(),
				context.getExperimentElement().getDesign().getCategory(),
				context.getExecutionPhase(),
				context.getExperimentElement().getDesign().getAncestry().stream()
						.map(item -> new ExperimentDesignElementSpec(item.getName(), item.getCategory()))
						.collect(Collectors.toList())
		);
	}

	/**
	 * Creates a new ExperimentElementExecutionHandlerSpec with the given values.
	 *
	 * @param name the name for the item specification
	 * @param category the category for the item specification
	 * @param executionPhase the test phase
	 * @param ancestors the ancestry of the item
	 */
	public ExperimentElementExecutionHandlerSpec(String name, String category, ExecutionPhase executionPhase, List<ExperimentDesignElementSpec> ancestors) {
		this.designElementSpec = new ExperimentDesignElementSpec(name, category);
		this.ancestors = ancestors;
		this.executionPhase = executionPhase;
	}

	@Override
	public String toString() {
		return String.format("%s/%s [%s]", designElementSpec.getCategory(), designElementSpec.getName(),ancestors);
	}

	public ExperimentDesignElementSpec getExperimentDesignElementSpec() {
		return designElementSpec;
	}

	public ExecutionPhase getExecutionPhase() {
		return executionPhase;
	}

	public List<ExperimentDesignElementSpec> getAncestors() {
		return ancestors;
	}
}
