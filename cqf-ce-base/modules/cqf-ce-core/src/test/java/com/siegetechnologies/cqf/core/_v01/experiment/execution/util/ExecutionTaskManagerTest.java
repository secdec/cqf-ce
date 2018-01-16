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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by cbancroft on 12/15/16.
 */
public class ExecutionTaskManagerTest {
    private static Logger logger = LoggerFactory.getLogger(ExecutionTaskManagerTest.class);
    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );
    }
    @Test
    public void submit() throws Exception {
        ExecutionTaskManager tm = new ExecutionTaskManager();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        logger.info( "Scheduling 5 tasks" );
        for( int i = 0; i < 5; i++ )
        {
            logger.info("Scheduling task {}", i );
            futures.add(tm.submit(newTask(i)));
        }

        sequence(futures).whenComplete((r,err) -> logger.info("Done: {}, {}", r, err )).join();
        logger.info("Done");
    }

    @Test
    public void submitGroup() throws Exception {
        ExecutionTaskManager tm = new ExecutionTaskManager();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        logger.info( "Scheduling 10 executionTasks" );
        List<ExecutionTask> executionTasks = IntStream.range(1,5).mapToObj(this::newTask).collect(Collectors.toList());
        CompletableFuture<Void> future = tm.submitGroup(executionTasks, 2 );
        for( int i = 5; i < 10; i++ )
        {
            logger.info("Scheduling task {}", i );
            futures.add(tm.submit(newTask(i)));
        }
//        TimeUnit.SECONDS.sleep(20);
//        sequence(futures).whenComplete((r,err) -> logger.info("Done: {}, {}", r, err )).get();
        future.join();
        logger.info("Done");
    }

    private ExecutionTask newTask(int id)
    {
        return new ExecutionTask() {
            @Override
            public Optional<Instant> getStartTime() {
                return null;
            }

            @Override
            public Optional<Instant> getEndTime() {
                return null;
            }

            @Override
            public Optional<Duration> getDuration() {
                return null;
            }

            @Override
            public void setDuration(Duration duration) {

            }

            /**
             * Retrieves the future associated with this task, if any.
             *
             * @return future associated with this task.
             */
            @Override
            public Optional<CompletableFuture<Void>> getFuture() {
                return null;
            }

            /**
             * Sets the future for this task
             *
             * @param future Future for this task
             */
            @Override
            public void setFuture(CompletableFuture<Void> future) {

            }

            /**
             * Gets the status of this task.
             *
             * @return task status
             */
            @Override
            public String getStatus() {
                return null;
            }

            @Override
            public void invoke() {
                logger.info( "Invoking task {}", id );
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void logEvent(String eventName, Object data)  {
                logger.info("Logging event: {}:{}", eventName, data );
            }

            @Override
            public int getId() {
                return id;
            }

            @Override
            public void shutdown() {

            }

            /**
             * Get the name of this task
             *
             * @return Name of the task
             */
            @Override
            public String getName() {
                return "ExecutionTask " + String.valueOf(id);
            }
        };
    }

}
