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


import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Responsible for executing experiments created in CQF.
 */
public interface ExperimentExecutor {
	
	/**
     * Executes an experiment in the given execution phase.
     *
     * @param experiment Experiment to execute
     * @param phase      Test Phase to execute in
     * @return The result of executing the experiment.
     */
    default
    Object execute(ExperimentImpl experiment, ExecutionPhase phase)
            throws InterruptedException, IOException, ExecutionException {
        
        execute_beforeHooks(experiment, phase);
    
        Object result;
        try {
            result = execute_internal(experiment, phase);
            
        } catch (InterruptedException | ExecutionException e) {
            
            execute_exceptionHandlers(e, experiment, phase);
            throw e;
        }

        result = execute_afterHooks(experiment, phase, result);
        return result;
    }
	
	/**
	 * Validates that the Experiment can be executed in the given phase. Note, this is
	 * not the same as a 'valid experiment'.  An experiment can be perfectly valid, but
	 * the executor may not be able to run it with the given configuration.  For example,
	 * it is possible to have a valid configuration with identical UUIDs.  This is fine for
	 * viewing, but is unable to execute.  If the experiment is not valid, then execution
	 * will not continue.  In severe cases, the validate method may also throw exceptions.
	 *
	 * @param experiment Experiment to validate before execution
	 * @param phase      Phase to execute in
	 * @return True if valid to execute, false otherwise.
	 */
	default
	boolean validate(ExperimentImpl experiment, ExecutionPhase phase) {
		
		return true;
	}

    /**/
	
	/**
	 * Executes the experiment; before- and after-hooks are handled elsewhere.
	 *
	 * @return result of the experiment
	 *
	 * @param experiment
	 * @param phase ExecutionPhase to execute in
	 */
	Object execute_internal(ExperimentImpl experiment, ExecutionPhase phase)
			throws InterruptedException, IOException, ExecutionException;
	
	/**
     * Applies each registered before-hook immediately before executing the experiment.
     *
     * @param experiment
     * @param phase
     */
    void execute_beforeHooks(ExperimentImpl experiment, ExecutionPhase phase);

    /**
     * Applies each registered after-hook immediately after executing the experiment.
     *
     * @param experiment experiment executed
     * @param phase      phase executed in
     * @param result     result of execution
     * @return result of postprocessing
     */
    Object execute_afterHooks(ExperimentImpl experiment, ExecutionPhase phase, Object result);

    /**
     * Applies each registered exception handler to an exception thrown during execution of an experiment.
     *
     * @param e          exception caught
     * @param experiment experiment being executed
     * @param phase      phase executing
     * @throws ExecutionException
     */
    void execute_exceptionHandlers(Exception e, ExperimentImpl experiment, ExecutionPhase phase)
		    throws ExecutionException;

    /**/
    
    /**
     * Returns an experiment's execution result.
     * <p>
     * As {@link AfterHook}s execute, they can save off individual
     * results in a key store for processing by other post operations.  This fetches the
     * result of a particular post operation, if it exists.
     *
     * @param key identifier for the result
     * @return stored value
     */
    Optional<Object> getResult(ExperimentImpl experiment, String key);

    /**
     * Posts an experiment's execution result for subsequent retrieval.
     *
     * @param key
     * @param result
     */
    void saveResult(ExperimentImpl experiment, String key, Object result);

    /**/
    
    /**
     * A piece of functionality/behavior that should occur immediately before executing the experiment.
     * Allows for a simple aggregation of common pre operations without needed separate class mixins.
     */
    @FunctionalInterface
    interface BeforeHook
    {
        void run(ExperimentExecutor executor, ExperimentImpl experiment, ExecutionPhase executionPhase);
    }
	
	/**
	 * Returns the registered before-hooks.
	 *
	 * @return list of before-hooks
	 */
	List<BeforeHook> getRegisteredBeforeHooks();
	
	void registerBeforeHook(BeforeHook beforeHook);
	
	void withdrawBeforeHook(BeforeHook beforeHook);

	/**/
	
	/**
     * A piece of functionality/behavior that should occur immediately after executing the experiment.
     * Allows for simple aggregation of common post operations without the need for separate class mixins.
     * <p/>
     * *NOTE* After-hooks are applied in the order registered, with the final produced result being returned
     * via the {@link ExperimentExecutor::execute} method.
     */
    @FunctionalInterface
    interface AfterHook
    {
        Object run(ExperimentExecutor executor, ExperimentImpl experiment, ExecutionPhase executionPhase, Object result)
                throws IOException, ExecutionException;
    }
	
	/**
	 * Returns the registered after-hooks.
	 * The last value returned will be the return value of {@link ExperimentExecutor::execute}.
	 *
	 * @return list of after-hooks
	 */
	List<AfterHook> getRegisteredAfterHooks();
	
	void registerAfterHook(AfterHook afterHook);
	
	void withdrawAfterHook(AfterHook afterHook);

	/**/
	
	/**
	 * A piece of functionality/behavior that should occur when an exception is thrown during experiment execution.
	 */
	@FunctionalInterface
    interface ExceptionHandler
    {
        void handle(Exception exception, ExperimentImpl experiment, ExecutionPhase phase) throws ExecutionException;
    }
	
	/**
	 * Returns the registered execution handlers.
	 *
	 * @return list of exception handlers
	 */
	List<ExceptionHandler> getRegisteredExceptionHandlers();
	
    void registerExceptionHandler(ExceptionHandler exceptionHandler);

    void withdrawExceptionHandler(ExceptionHandler exceptionHandler);

    /**/
    
}
