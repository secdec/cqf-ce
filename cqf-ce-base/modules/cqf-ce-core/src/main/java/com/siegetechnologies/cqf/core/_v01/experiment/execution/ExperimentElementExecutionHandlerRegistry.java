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

import java.util.*;

/**
 * Registry of element execution handlers.
 *
 * @author taylorj
 */
public class ExperimentElementExecutionHandlerRegistry
{
	private static final String DEFAULT_CONTEXT_NAME = "$$default";
	
	private static final Map<String, Set<ExperimentElementExecutionHandlerEntry>> REGISTRY_MAP = new HashMap<>();
	
	protected static Set<ExperimentElementExecutionHandlerEntry> getRegistryForContextName(String contextName) {
		
		if (contextName == null) { contextName = DEFAULT_CONTEXT_NAME; }
		
		Set<ExperimentElementExecutionHandlerEntry> result =
			REGISTRY_MAP.computeIfAbsent(contextName, k -> new HashSet<>());
		
		return result;
	}

	/**/
	
	/**
	 * Register a new execution handler class.
	 *
	 * @param handlerClass
	 * 		the instance context handler class
	 * @param <T>
	 * 		the handler class type parameter
	 */
	public static <T extends ExperimentElementExecutionHandler<?, ?>>
	void registerHandler(Class<T> handlerClass) {
		
		registerHandler(null, handlerClass);
	}
	
	public static <T extends ExperimentElementExecutionHandler<?, ?>>
	void registerHandler(String contextName, Class<T> handlerClass) {
		
		Set<ExperimentElementExecutionHandlerEntry> registry = getRegistryForContextName(contextName);
		
		try {
			registry.add(new ExperimentElementExecutionHandlerEntry(handlerClass));
		}
		catch (InstantiationException | IllegalAccessException e) {
			
			throw new RuntimeException("Could not register handler class, " + handlerClass.getName() + ".", e);
		}
	}
	
	//^-- FIXME: srogers: implement withdrawHandler()
	
	//^-- FIXME: srogers: implement withdrawAllHandlers()
	
	/**/
	
	/**
	 * Retrieves a handler for the execution context and applies it to the corresponding element.
	 *
	 * @param context
	 * 		the context
	 *
	 * @return the result from the handler
	 */
	public Object executeUsingContext(
		ExperimentElementExecutionContext context
	) {
		String contextName = context.getRegistryContextName().get();
		
		ExperimentElementExecutionHandlerEntry handlerClass =
			findHandler(contextName, new ExperimentElementExecutionHandlerSpec(context));
		
		return executeUsingContext(context, handlerClass);
	}
	
	/**
	 * Applies a handler for an execution context to the corresponding element.
	 * <p/>
	 * First, a class is retrieved from the registry and a handler method
	 * is obtained from the class.  Then the handler is invoked with arguments
	 * constructed for the handler's argument list.  The result of the invocation
	 * is returned.
	 *
	 * @param context
	 * 		the context
	 * @param handlerEntry
	 * 		the handler class spec
	 *
	 * @return the result of the handler
	 */
	public Object executeUsingContext(
		ExperimentElementExecutionContext context,
		ExperimentElementExecutionHandlerEntry handlerEntry
	) {
		ExperimentElementExecutionHandler<?, ?> handler;
		try {
			handler = handlerEntry.getHandlerClass().newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			
			throw new RuntimeException("Error while creating instance of handler class, " + handlerEntry + ".", e);
		}
		return handler.handle(context);
	}
	
	/**
	 * Returns a handler for the given specification.
	 * The process for finding a handler is as follows.
	 * <p>
	 * First, the {@link ExperimentElementExecutionHandlerSpec#ancestors item ancestry} (which includes
	 * the actual item) is walked, and if any handler class is found that
	 * exactly matches the ancestor, it is returned.
	 * <p>
	 * Otherwise, the final ancestor is determined, and the list of all compatible
	 * handler methods is found. These are ordered by specificity (according to
	 * {@link ExperimentElementExecutionHandlerEntry#LESS_SPECIFIC}, and the most specific
	 * method, if there is one, is returned.
	 * <p>
	 * If no handler is found, throws a {@link NoSuchElementException} exception.
	 *
	 * @param spec
	 * 		the handler specification
	 *
	 * @return the handler class for the specification
	 */
	public static ExperimentElementExecutionHandlerEntry findHandler(
		String contextName, ExperimentElementExecutionHandlerSpec spec
	) {
		Set<ExperimentElementExecutionHandlerEntry> registry = getRegistryForContextName(contextName);
		
		/*
		 * Look for an exact match for this immediate spec, or any of its ancestors.
		 */
		for (ExperimentDesignElementSpec ancestor : spec.getAncestors()) {
			
			for (ExperimentElementExecutionHandlerEntry handlerEntry : registry) {
				
				if (handlerEntry.exactlyMatches(ancestor)) { return handlerEntry; }
			}
		}
		
		/*
		 * Otherwise, get the deepest ancestor and look for the most specific compatible handler.
		 */
		ExperimentDesignElementSpec eve = spec.getAncestors().get(spec.getAncestors().size() - 1);
		ExperimentElementExecutionHandlerSpec eveSpec = new ExperimentElementExecutionHandlerSpec(
			eve.getName(), eve.getCategory(), spec.getExecutionPhase(), Collections.emptyList()
		);
		return (registry.stream()
			
			.filter(handlerEntry -> handlerEntry.compatibleWith(eveSpec))
			
			.max(ExperimentElementExecutionHandlerEntry.LESS_SPECIFIC)
			
			.orElseThrow(() -> new NoSuchElementException(
				"No ExperimentElementExecutionHandler for " + spec + "."))
		);
	}
	
}
