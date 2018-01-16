package com.siegetechnologies.cqf.core._v01.experiment.execution.util.impl;

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
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionHandler;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionQuantifierContext;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 * {@link ExperimentElementExecutionQuantifierContext} is the "standard" quantifier context
 * based on a particular instance context and an instance context handler.
 * The {@link #isCancelled()}, {@link #getEnabledPhases()}, and
 * {@link #getDurationMeasure(ExecutionPhase)}, implementations are based on
 * the parameters of the instance context's instance. The
 * {@link #handle(ExecutionPhase)} implementation is based on the handler's
 * {@link ExperimentElementExecutionHandler#handle(ExperimentElementExecutionContext)} method.
 * 
 * @author taylorj
 */
public class ExperimentElementExecutionQuantifierContext implements ExecutionQuantifierContext {

	private final ExperimentElementExecutionContext context;
	private final ExperimentElementExecutionHandler<?, ?> handler;

	/**
	 * Creates a new instance for a provided context and handler
	 *
	 * @param context the context
	 * @param handler the handler
	 */
	public ExperimentElementExecutionQuantifierContext(ExperimentElementExecutionContext context, ExperimentElementExecutionHandler<?, ?> handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	public boolean isCancelled() {
		Boolean isCancelled = context.getOptionalInstanceParameter("CANCEL").map(Boolean::parseBoolean).orElse(false); // FIXME: STRING: srogers
		context.getExperimentElement().getParameterValueMap().put("CANCEL", isCancelled.toString()); // FIXME: STRING: srogers
		return isCancelled;
	}

	@Override
	public Object handle(ExecutionPhase phase) {
		return handler.handle(context.withExecutionPhase(phase));
	}

	@Override
	public EnumSet<ExecutionPhase> getEnabledPhases() {
		EnumSet<ExecutionPhase> enabledPhases = EnumSet.noneOf(ExecutionPhase.class);
		for(ExecutionPhase phase : ExecutionPhase.values()) {
			if (phase != ExecutionPhase.QUANTIFY && phase != ExecutionPhase.COMPLETE) {
				boolean doPhase = context.getOptionalInstanceParameter(phase.getValue()).map(Boolean::parseBoolean).orElse(true);
				if (doPhase) {
					enabledPhases.add(phase);
				}
			}
		}
		return enabledPhases;
	}

	@Override
	public long getDurationMeasure(ExecutionPhase phase) {
		switch (phase) {
		case RUN:
			return context.getOptionalInstanceParameter("DURATION").map(Integer::parseInt).orElse(15); // FIXME: STRING: srogers
		case INITIALIZE:
		case RETRIEVE_DATA:
		case CLEANUP:
		case COMPLETE:
			return 0;
		default:
			throw new AssertionError("Unrecognized phase: "+phase);
		}
	}

	@Override
	public TimeUnit getDurationTimeUnit(ExecutionPhase phase) {
		return TimeUnit.MINUTES;
	}
}
