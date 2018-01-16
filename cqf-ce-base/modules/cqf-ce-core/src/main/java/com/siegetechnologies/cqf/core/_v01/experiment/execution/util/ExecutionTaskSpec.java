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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Duration;
import java.time.Instant;

/**
 * Data Transfer Object for the ExecutionTask class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutionTaskSpec {
    private final String name;
    private final Integer id;
    private final Long startTime;
    private final Long endTime;
    private final Long duration;
    private final String status;

    private ExecutionTaskSpec(String name,
                              Integer id,
                              Long startTime,
                              Long endTime,
                              Long duration,
                              String status )
    {
        this.name = name;
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.status = status;
    }

    public static ExecutionTaskSpec of(ExecutionTask executionTask)
    {
        return new ExecutionTaskSpec(
                executionTask.getName(),
                executionTask.getId(),
                executionTask.getStartTime().map(Instant::getEpochSecond).orElse(null),
                executionTask.getEndTime().map(Instant::getEpochSecond).orElse(null),
                executionTask.getDuration().map(Duration::getSeconds).orElse(null),
                executionTask.getStatus()
        );
    }
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public Long getDuration() {
        return duration;
    }

    public String getStatus() {
        return status;
    }
}
