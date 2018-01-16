package com.siegetechnologies.cqf.core._v01.experiment.execution.mixin;

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


import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutor;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTask;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;

/**
 * Mixin for when the given ExperimentExecutor is run as a executionTask in the
 * @{link ExecutionTaskManager}
 */
public class AsTaskMixin implements Mixin {
    private final ExecutionTask executionTask;
    private static final String ID = "com.siegetechnologies.cqf.execution.strategy.AsTaskMixin"; // FIXME: STRING: srogers
    private static final Logger logger = LoggerFactory.getLogger(AsTaskMixin.class);

    public AsTaskMixin(ExecutionTask executionTask) {
        Objects.requireNonNull(executionTask, "Cannot create an AsTaskMixin strategy with a null ExecutionTask");
        this.executionTask = executionTask;
    }

    @Override
    public void apply(ExperimentExecutor executor) {
        executor.registerBeforeHook(this::setTaskDuration);
        executor.registerAfterHook(this::saveResult);
        executor.withdrawExceptionHandler(this::onError);
    }

    @Override
    public String getId() {
        return ID;
    }

    public void onError(Exception e, ExperimentImpl exp, ExecutionPhase tp ) {
        this.executionTask.logEvent("error", Exceptions.getSummary(e) );
    }
	
	/**
	 * Calculates the expected execution time based on the experiment and its current phase
	 * 
	 * @param executor
	 * @param experiment
	 * @param phase
	 */
	public void setTaskDuration( ExperimentExecutor executor,
                                 ExperimentImpl experiment,
                                 ExecutionPhase phase )
    {
        logger.debug( "Setting executionTask duration for {}", executionTask.getId() );
        Duration d = calculateTaskDuration( experiment, phase );
        executionTask.setDuration(d);
    }

    /**
     * If a ZipResultMixin mixin has executed, then save the result.
     * @param executor
     * @param experiment
     * @param phase
     * @param result
     * @return
     */
    public Object saveResult( ExperimentExecutor executor,
                            ExperimentImpl experiment,
                            ExecutionPhase phase,
                            Object result ) {
        return result;

    }
    /**
     * Calculates the executionTask duration of the given experiment execution
     * @param experiment experiment being executed
     * @param phase phase executing in
     * @return duration of the experiment
     */
    private Duration calculateTaskDuration(ExperimentImpl experiment,
                                           ExecutionPhase phase ) {
        ExperimentElementImpl root = experiment.getRoot();
        final int retrieveDataEstimationSeconds = 5 * root.getChildren().size();
        final int initializeEstimationSeconds = 15 * root.getChildren().size();
        final int cleanupEstimationSeconds = 3 * root.getChildren().size();
        final int runEstimationMinutes = root.getParameter("DURATION").map(Integer::parseInt).orElse(5); // FIXME: STRING: srogers
        switch (phase) {
            case QUANTIFY:
                return Duration.ZERO
                        .plus(Duration.ofSeconds(retrieveDataEstimationSeconds))
                        .plus(Duration.ofSeconds(initializeEstimationSeconds))
                        .plus(Duration.ofSeconds(cleanupEstimationSeconds))
                        .plus(Duration.ofMinutes(runEstimationMinutes));
            case RUN:
                return Duration.ofMinutes(runEstimationMinutes);
            case INITIALIZE:
                return Duration.ofSeconds(initializeEstimationSeconds);
            case RETRIEVE_DATA:
                return Duration.ofSeconds(retrieveDataEstimationSeconds);
            case CLEANUP:
                return Duration.ofSeconds(cleanupEstimationSeconds);
        }

        return Duration.ofMinutes(5);
    }
}
