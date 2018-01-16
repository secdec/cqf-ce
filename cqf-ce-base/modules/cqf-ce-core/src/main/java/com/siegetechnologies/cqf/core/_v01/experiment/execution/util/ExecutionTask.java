package com.siegetechnologies.cqf.core._v01.experiment.execution.util;

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

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by cbancroft on 12/15/16.
 */
public interface ExecutionTask {

    /**
     * Gets the id associated with this task
     * @return id for this task
     */
    int getId();

    /**
     * Get the name of this task
     * @return Name of the task
     */
    String getName();

    /**
     * Gets the time this task started
     * @return Instant the task started
     */
    Optional<Instant> getStartTime();

    /**
     * Gets the time that this task should be completed by
     * @return Instant the task should be done
     */
    Optional<Instant> getEndTime();

    /**
     * Gets the estimated duration of this task.
     * @return Estimated duration of the task, if any
     */
    Optional<Duration> getDuration();

    /**
     * Sets the estimated duration of this task
     * @param duration estimated duration of this task
     */
    void setDuration( Duration duration );

    /**
     * Retrieves the future associated with this task, if any.
     * @return future associated with this task.
     */
    Optional<CompletableFuture<Void>> getFuture();

    /**
     * Sets the future for this task
     * @param future Future for this task
     */
    void setFuture( CompletableFuture<Void> future );

    /**
     * Gets the status of this task.
     * @return task status
     */
    String getStatus( );

    /**
     * Invokes this task
     */
    void invoke();

    /**
     * Logs an event from this task
     * @param eventName Name of the event
     * @param data Associated data
     */
    void logEvent( String eventName, Object data );



    /**
     * Shuts down and cleans up after completion.
     */
    void shutdown();
}
