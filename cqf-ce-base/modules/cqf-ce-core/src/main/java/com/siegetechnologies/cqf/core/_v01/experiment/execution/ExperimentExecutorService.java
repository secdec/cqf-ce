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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExperimentExecutorService is a simple wrapper around a static {@link ExecutorService}
 * that is used by the default {@link ExperimentElementExecutionHandler} methods, and can
 * be used by other CQF methods.
 *
 * @author taylorj
 *
 */
public class ExperimentExecutorService {
	private static final Logger logger = LoggerFactory.getLogger(ExperimentExecutorService.class);
	
	private static final ThreadFactory CQF_FACTORY = new ThreadFactory()
    {
    	private final ThreadFactory backingThreadFactory = Executors.defaultThreadFactory();
    	private final AtomicInteger threadNumber = new AtomicInteger(1);
    	private static final String NAME_PREFIX = "cqf-pool-"; // FIXME: STRING: srogers
    	
		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = backingThreadFactory.newThread(r);
			t.setName(NAME_PREFIX+threadNumber.getAndIncrement()); // FIXME: extract string construction into a dedicated method
			t.setUncaughtExceptionHandler(
					(thread, cause) -> logger.error("Terminal exception within thread: " + thread.getName(), cause));
			return t;
		}
		
	};
    
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(CQF_FACTORY);
	private static ExecutorService executorService = EXECUTOR_SERVICE;
	private ExperimentExecutorService() {}

	/**
	 * @return the executor service
	 */
	public static ExecutorService getExecutorService() {
		logger.trace("Getting ExperimentExecutorService.");
		return executorService;
	}
	
	public static void setExecutorService( ExecutorService service )
	{
		executorService = service;
	}

	/**
	 * Calls the executor service's {@link ExecutorService#shutdownNow()}, and
	 * returns the result.  If the executor service has already been shutdown,
	 * an empty list is returned.
	 *
	 * @return a list of runnables that were not run
	 */
	public static List<Runnable> shutdownNow() {
	    if (EXECUTOR_SERVICE.isShutdown()) {
	        logger.trace("Executor service is already shut down.");
	        return Collections.emptyList();
	    }
	    else {
	        logger.trace("Shutting down executor service.");
	        return EXECUTOR_SERVICE.shutdownNow();
	    }
	}
}
