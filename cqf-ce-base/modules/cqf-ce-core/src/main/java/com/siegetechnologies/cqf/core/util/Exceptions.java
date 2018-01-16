/**
 *  Copyright (c) 2016 Siege Technologies.
 */
package com.siegetechnologies.cqf.core.util;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility methods for inspecting {@link Exception}s (and {@link Throwable}s, more generally).
 * 
 * @author taylorj
 */
public class Exceptions {
	
	private Exceptions() {}
	
	/**
	 * Returns the root cause of a throwable.
	 * 
	 * @param throwable the throwable
	 * @return the root cause
	 */
	public static Throwable getRootCause(Throwable throwable) {
		Throwable t = throwable;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t;
	}
	
	/**
	 * Returns a list of the causes of the a throwable.
	 * 
	 * @param throwable the throwable
	 * @return the list of causes
	 */
	public static List<Throwable> getCauses(Throwable throwable) {
		List<Throwable> causes = new ArrayList<>();
		for (Throwable t = throwable; t != null; t = t.getCause()) {
			causes.add(t);
		}
		return causes;
	}
	
	/**
	 * Returns true if one of the causes of a throwable
	 * satisfies a predicate. 
	 * 
	 * @param t the throwable 
	 * @param test the predicate
	 * @return true if one of the causes satisfies the test
	 */
	public static boolean hasCause(Throwable t, Predicate<Throwable> test) {
		return getCauses(t).stream().anyMatch(test);
	}
	
	/**
	 * Returns true if one of the causes of a throwable is an instance
	 * of any of the cause classes.
	 * 
	 * @param t the throwable
	 * @param causeClasses the causes
	 * @return whether the throwable is caused by one of the classes
	 */
	public static boolean hasCause(Throwable t, Class<?>... causeClasses) {
		for (Class<?> klass : causeClasses) {
			if (hasCause(t, klass::isInstance)) {
				return true;
			}
		}
		return false;
	}

    /**
     * Returns a string indicating the deepest message in a
     * throwable's cause chain, along with the names of the 
     * classes of the throwables in the chain.
     * 
     * @param throwable the throwable
     * @param ignoredClasses a collection of classes to ignore in the chain
     * @return a string
     */
    public static String getSummary(Throwable throwable, Collection<Class<? extends Throwable>> ignoredClasses) {
    	List<Class<?>> classes = new ArrayList<>();
    	String message = null;
    	for (Throwable t = throwable; t != null; t = t.getCause()) {
    		Class<?> klass = t.getClass();
    		if (!ignoredClasses.contains(klass)) {
    			classes.add(klass);
    		}
    		if (t.getMessage() != null) {
    			message = t.getMessage();
    		}
    	}
    	StringBuilder sb = new StringBuilder();
    	if (message != null) {
    		sb.append(message);
    	}
    	List<String> classNames = classes.stream().map(Class::getSimpleName).collect(Collectors.toList());
    	sb.append(" ");
    	sb.append(classNames.toString());
    	return sb.toString();
    }
    
    /**
     * Like {@link #getSummary(Throwable, Collection)};  the collection
     * of classes to ignore is contains just {@link ExecutionException}.
     * 
     * @see #getSummary(Throwable, Collection)
     * 
     * @param t the throwable
     * @return a summary of the throwable
     */
    public static String getSummary(Throwable t) {
        return getSummary(t, Arrays.asList(ExecutionException.class));
    }
}
