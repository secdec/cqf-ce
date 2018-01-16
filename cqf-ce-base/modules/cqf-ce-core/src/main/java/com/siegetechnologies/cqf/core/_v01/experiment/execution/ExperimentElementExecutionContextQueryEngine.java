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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
 * Executes queries against instance contexts.
 * 
 * @author taylorj
 */
public class ExperimentElementExecutionContextQueryEngine {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ExperimentElementExecutionContextQueryEngine.class);
	
	private static final Map<String,TerminalQueryHandler> TERMINAL_HANDLERS = new HashMap<>();

	private ExperimentElementExecutionContextQueryEngine() {}
	
	/**
	 * Executes a query against an instance context and returns the result.
	 * 
	 * @param context the instance context
	 * @param query the query
	 * @return the result of the query
	 * 
	 * @throws IllegalArgumentException if the query cannot be handled by
	 *             the context
	 */
	public static Object query(ExperimentElementExecutionContext context, String query) throws IllegalArgumentException {
		List<String> parts = Arrays.asList(query.split("\\.", -1));
		ExperimentElementExecutionContext curr = context;
		for (ListIterator<String> it = parts.listIterator(); it.hasNext();) {
			String part = it.next();
			switch (part) {
			case "parent":
				curr = curr.getContext().orElseThrow(IllegalArgumentException::new);
				break;
			case "root":
				curr = curr.getRootContext();
				break;
			default:
				return handleTerminalQuery(curr, part, it);
			}
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Interface for handling terminal queries.
	 * 
	 * @author taylorj
	 */
	@FunctionalInterface
	interface TerminalQueryHandler {
		/**
		 * Handle a terminal query for an instance context, field name, and parts.
		 * 
		 * @param context the context
		 * @param field the field name
		 * @param parts the parts
		 * @return the result
		 * 
		 * @throws IllegalArgumentException if a query error occurs
		 */
		Object handle(ExperimentElementExecutionContext context, String field, ListIterator<String> parts) throws IllegalArgumentException;
	}
	
	static void registerHandler(String field, TerminalQueryHandler handler) {
		TERMINAL_HANDLERS.put(field, handler);
	}
	
	static void registerHandler(String field, Function<ExperimentElementExecutionContext,Object> handler) {
		TERMINAL_HANDLERS.put(field, (c,f,p) -> handler.apply(c));
	}

	private static Object handleTerminalQuery(ExperimentElementExecutionContext context, String part, ListIterator<String> parts)
			throws IllegalArgumentException {
		final Object result;
		if (!"cqf".equals(part)) {
			result = context.getInstanceParameter(part);
		}
		else {
			isValidQuery(parts.hasNext(), "No parts after cqf");
			String field = parts.next();
			isValidQuery(TERMINAL_HANDLERS.containsKey(field), "No handler for field: %s.", field);
			TerminalQueryHandler handler = TERMINAL_HANDLERS.get(field);
			result = handler.handle(context, field, parts);
		}
		isValidQuery(!parts.hasNext(), "Extra fields in query string.");
		return result;
	}
	
	static void isValidQuery(boolean condition, String message, Object... arguments) throws IllegalArgumentException {
		if (!condition) {
			throw new IllegalArgumentException(String.format(message, arguments));
		}
	}
	
	static {
		registerHandler("indexInParent", ExperimentElementExecutionContext::getIndexInParent);
		registerHandler("result", c -> c.getResult().orElse(null));
		registerHandler("runMode", ExperimentElementExecutionContext::getExecutionMode);
		registerHandler("executionPhase", ExperimentElementExecutionContext::getExecutionPhase);
		
		// ExperimentElement Context Time fields
		registerHandler("year", (c,f,p) -> c.getCurrentTime().getYear());
		registerHandler("dayOfYear", (c,f,p) -> c.getCurrentTime().getYear());
		registerHandler("month", c -> c.getCurrentTime().getMonth());
		registerHandler("dayOfMonth", c -> c.getCurrentTime().getDayOfMonth());
		registerHandler("dayOfWeek", c -> c.getCurrentTime().getDayOfWeek());
		registerHandler("hour", c -> c.getCurrentTime().getHour());
		registerHandler("minute", c -> c.getCurrentTime().getMinute());
		registerHandler("second", c -> c.getCurrentTime().getSecond());
		registerHandler("nano", c -> c.getCurrentTime().getNano());
		
		// ExperimentElement Fields
		registerHandler("id", (c,f,p) -> handleTerminalId(c, p));
		registerHandler("name", c -> c.getExperimentElement().getDesign().getName());
		registerHandler("category", c -> c.getExperimentElement().getDesign().getCategory());
		registerHandler("getnetwork", (c,f,p) -> handleGetNetwork(c, p));
		registerHandler("genclassb", ExperimentElementExecutionContextQueryEngine::handleGenClassB);
		registerHandler("getvar", (c,f,p) -> handleGetVar(c, p));
	}
	
	static Object handleGenClassB(ExperimentElementExecutionContext context) {
		int m = context.getUnique();
		return (m / 253) + 1 + "." + (m % 253);
	}
	
	static Object handleGetVar(ExperimentElementExecutionContext context, ListIterator<String> parts) throws IllegalArgumentException {
		if (!parts.hasNext()) {
			throw new IllegalArgumentException();
		} else {
			String param = parts.next();
			return context.getStringValue(param);
		}
	}
	
	static Object handleGetNetwork(ExperimentElementExecutionContext context, ListIterator<String> parts) {
		if (!parts.hasNext()) {
			return context.getExperimentElement().getId();
		} else {
			String portgroupID = parts.next();
			return context.getNetwork(portgroupID);
		}
	}
	
	static Object handleTerminalId(ExperimentElementExecutionContext context, ListIterator<String> parts) {
		if (!parts.hasNext()) {
			return context.getExperimentElement().getId();
		} else {
			// TODO Simplify this?
			// It might be possible to simplify this.  In the #query()
			// method, we use parent and root to get to other instance
			// contexts and start searching from there.  It *might* be
			// possible to get the instance by ID and then continue
			// the query from there.  However, it also might *not* be
			// possible, because we don't have an instance context for
			// it, just the instance, whereas we have the instance context
			// for the parent and the root.
			String id = parts.next();
			String param = parts.next();
			return context.getIDvar(id, param);
		}
	}

}
