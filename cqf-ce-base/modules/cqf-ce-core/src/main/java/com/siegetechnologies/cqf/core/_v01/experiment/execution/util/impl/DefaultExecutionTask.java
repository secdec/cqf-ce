package com.siegetechnologies.cqf.core._v01.experiment.execution.util.impl;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-impl
 * %%
 * Copyright (C) 2016 - 2017 Applied Visions, Inc.
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

import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author cbancroft
 */
public class DefaultExecutionTask implements ExecutionTask {
    private static final Logger logger = LoggerFactory.getLogger(DefaultExecutionTask.class);
    private final int id;
    private final Consumer<ExecutionTask> task;
    private String status = "PENDING"; // FIXME: STRING: srogers
    private CompletableFuture<Void> future;
    private Duration duration;
    private Instant start;
    private Instant doneBy;
    private final String name;

    /**
     * The counter for task ids
     */
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    public DefaultExecutionTask(String name, Duration duration, Consumer<ExecutionTask> task )
    {
        this.id = idCounter.getAndIncrement();
        this.duration = duration;
        this.task = task;
        this.name = name;
    }

    /**
     * Gets the time this task started
     *
     * @return Instant the task started
     */
    @Override
    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(start);
    }

    /**
     * Gets the time that this task should be completed by
     *
     * @return Instant the task should be done
     */
    @Override
    public Optional<Instant> getEndTime() {
        return Optional.ofNullable(doneBy);
    }

    /**
     * Invokes this task
     */
    @Override
    public void invoke() {
        logger.debug( "Starting task: {}", getName() );
        this.start = Instant.now();
        this.status = "STARTED"; // FIXME: STRING: srogers
		this.doneBy = Optional.ofNullable(this.duration)
				.map(start::plus)
				.orElse(null);
        try {
            task.accept(this);
        }
        catch( Exception e )
        {
            logger.error ("Exception while executing task '{}':", getName(), e );
            this.status = "ERROR"; // FIXME: STRING: srogers
        }

        this.doneBy = Instant.now();
        logger.debug( "Finished task: {}", getName());
        this.status = "COMPLETE"; // FIXME: STRING: srogers
    }

    /**
     * Logs an event from this task
     *
     * @param eventName Name of the event
     * @param data      Associated data
     */
    @Override
    public void logEvent(String eventName, Object data) {
        logger.debug( "[{}] Logging event: {} -- {}", getName(), eventName, data );
    }

    @Override
    public Optional<Duration> getDuration() {
        return Optional.ofNullable(this.duration);
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
        this.getStartTime().ifPresent(s -> this.doneBy = s.plus(duration));
    }

    /**
     * Retrieves the future associated with this task, if any.
     *
     * @return future associated with this task.
     */
    @Override
    public Optional<CompletableFuture<Void>> getFuture() {
        return Optional.ofNullable(this.future);
    }

    /**
     * Sets the future for this task
     *
     * @param future Future for this task
     */
    @Override
    public void setFuture(CompletableFuture<Void> future) {
        this.future = future;
    }

    /**
     * Gets the status of this task.
     *
     * @return task status
     */
    @Override
    public String getStatus() {
        return this.status;
    }

    /**
     * Gets the id associated with this task
     *
     * @return id for this task
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Get the name of this task
     *
     * @return Name of the task
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Shuts down and cleans up after completion.
     */
    @Override
    public void shutdown() {
    }

}
