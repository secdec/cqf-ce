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

import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;

import java.util.Comparator;

/**
 * An entry in the execution handler registry.
 * Associates an element's design with the corresponding execution handler.
 *
 * @author taylorj
 */
class ExperimentElementExecutionHandlerEntry
{

	private final Class<? extends ExperimentElementExecutionHandler<?, ?>> handlerClass;
	private final ExperimentDesignElementSpec designElementSpec;

	/**
	 * A comparator for {@link ExperimentElementExecutionHandlerEntry} that are both
	 * {@link ExperimentElementExecutionHandlerEntry#compatibleWith(ExperimentElementExecutionHandlerSpec)
	 * compatible} with a {@link ExperimentElementExecutionHandlerSpec}.
	 */
	public static final Comparator<ExperimentElementExecutionHandlerEntry> LESS_SPECIFIC = Comparator
			.<ExperimentElementExecutionHandlerEntry, String> comparing(s -> s.getExperimentDesignElementSpec().getName()).thenComparing(
					s -> s.getExperimentDesignElementSpec().getCategory());

	/**
	 * Creates a new HandlerClass instance. The provided class must have a
	 * zero-argument constructor; an instance is created with
	 * {@link Class#newInstance()}, and the name and category for the design element
	 * specification are taken from its
	 * {@link ExperimentElementExecutionHandler#getDesignCategory() design category} and
	 * {@link ExperimentElementExecutionHandler#getDesignName() design name}.
	 *
	 * @param klass the instance context handler class
	 *
	 * @throws InstantiationException from {@link Class#newInstance()}
	 * @throws IllegalAccessException from {@link Class#newInstance()}
	 */
	public ExperimentElementExecutionHandlerEntry(Class<? extends ExperimentElementExecutionHandler<?, ?>> klass)
			throws InstantiationException, IllegalAccessException {
		this.handlerClass = klass;
		ExperimentElementExecutionHandler<?, ?> tmp = klass.newInstance();
		this.designElementSpec = new ExperimentDesignElementSpec(tmp.getDesignName(), tmp.getDesignCategory());
	}
	
	public Class<? extends ExperimentElementExecutionHandler<?, ?>> getHandlerClass() {
		return handlerClass;
	}

	public ExperimentDesignElementSpec getExperimentDesignElementSpec() {
		return designElementSpec;
	}

	@Override
	public String toString() {
		return String.format("[HandlerClass: %s:'%s'/'%s']", handlerClass.getSimpleName(), designElementSpec.getCategory(),
				designElementSpec.getName());
	}

	/**
	 * Returns true if this handler class is compatible with the provided
	 * handler specification.
	 *
	 * @param experimentElementExecutionHandlerSpec a handler specification
	 * @return a boolean
	 */
	public boolean compatibleWith(ExperimentElementExecutionHandlerSpec experimentElementExecutionHandlerSpec) {
		// Names must be equal, except annotation name can be ""
		if (!("".equals(getExperimentDesignElementSpec().getName()) || experimentElementExecutionHandlerSpec.getExperimentDesignElementSpec().getName().equals(getExperimentDesignElementSpec().getName()))) {
			return false;
		}

		// Categories must be equal, except annotation name can be ""
		if (!("".equals(getExperimentDesignElementSpec().getCategory()) || experimentElementExecutionHandlerSpec.getExperimentDesignElementSpec().getCategory()
				.equals(getExperimentDesignElementSpec().getCategory()))) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if the handler specification's item specification matches
	 * this handler class.
	 *
	 * @see #exactlyMatches(ExperimentDesignElementSpec)
	 *
	 * @param experimentElementExecutionHandlerSpec the handler specification
	 * @return a boolean
	 */
	public boolean exactlyMatches(ExperimentElementExecutionHandlerSpec experimentElementExecutionHandlerSpec) {
		return exactlyMatches(experimentElementExecutionHandlerSpec.getExperimentDesignElementSpec());
	}

	/**
	 * Returns true if this HandlerClass's item specification equals the given
	 * item specification.
	 *
	 * @param designElementSpec an item specification
	 * @return a boolean
	 */
	public boolean exactlyMatches(ExperimentDesignElementSpec designElementSpec) {
		return getExperimentDesignElementSpec().equals(designElementSpec);
	}

	/**
	 * Compare this with another instance using {@link #LESS_SPECIFIC}.
	 *
	 * @param other the other spec
	 * @return result from {@link #LESS_SPECIFIC}
	 */
	public int compareTo(ExperimentElementExecutionHandlerEntry other) {
		return LESS_SPECIFIC.compare(this, other);
	}
}
