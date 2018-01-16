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

import com.siegetechnologies.cqf.core.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Manages execution of top level runnable tasks in CQF.
 */
public class ExecutionTaskManager {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionTaskManager.class);
    private static final int MAX_CONCURRANT = Config.getConfiguration().getInt("cqf.maxConcurrentTasks", 5);
    private static final ExecutorService executor = Executors.newFixedThreadPool(MAX_CONCURRANT);

    private final List<ExecutionTask> pending = new ArrayList<>();
    private final List<ExecutionTask> active = new ArrayList<>();
    private final List<ExecutionTask> finished = new ArrayList<>();

    /**
     * Submits a executionTask for execution.
     *
     * @param executionTask ExecutionTask to execute
     *
     * @return CompletableFuture for the executionTask
     */
    public CompletableFuture<Void> submit(ExecutionTask executionTask) {
        pending.add(executionTask);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.invoke(executionTask), executor)
                .whenComplete((res, err) -> {
                    active.remove(executionTask);
                    finished.add(executionTask);
                });
        executionTask.setFuture(future);
        return future;
    }

    public CompletableFuture<Void> submitGroup(List<ExecutionTask> executionTasks, int maxSimultaneous) {
        pending.addAll(executionTasks);
        return CompletableFuture.runAsync(() -> this.invokeTaskGroup(executionTasks, maxSimultaneous));
    }

    public List<ExecutionTask> getPending() {
        return pending;
    }

    public List<ExecutionTask> getActive() {
        return active;
    }

    public List<ExecutionTask> getFinished() {
        return finished;
    }

    private void invoke(ExecutionTask executionTask) {
        pending.remove(executionTask);
        active.add(executionTask);
        executionTask.invoke();
    }

    private void invokeTaskGroup(List<ExecutionTask> executionTasks, int maxSimultaneous) {
        List<ExecutionTask> executionTaskList = Collections.synchronizedList(new ArrayList<>(executionTasks));
        AtomicInteger counter = new AtomicInteger(0);
        Runnable groupTasks = () -> {
            int id = counter.getAndIncrement();
            while (!executionTaskList.isEmpty()) {
                ExecutionTask t = executionTaskList.remove(0);
                logger.info("ExecutionTask[{}]: Starting {}", id, t.getName());
                this.invoke(t);
                active.remove(t);
                finished.add(t);
            }
        };
        executionTaskList.forEach(t -> t.setFuture(CompletableFuture.completedFuture(null)));
        List<CompletableFuture<Void>> futureList =  IntStream.range(0, maxSimultaneous).mapToObj(i -> CompletableFuture.runAsync(groupTasks,executor)).collect(Collectors.toList());
        CompletableFuture<Void> future = CompletableFuture.allOf( futureList.toArray( new CompletableFuture[futureList.size()]));

        //Wait for everything to be processed
        future.join();
    }
}
