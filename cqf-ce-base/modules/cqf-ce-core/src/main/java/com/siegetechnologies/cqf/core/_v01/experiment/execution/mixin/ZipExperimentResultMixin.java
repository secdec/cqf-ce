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
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.zip.ZippedExecutionResultExporter;
import com.siegetechnologies.cqf.core.util.TemporaryFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

/**
 * Saves the result of an experiment as a zip file.
 */
public class ZipExperimentResultMixin implements Mixin {
    private static final Logger logger = LoggerFactory.getLogger(ZipExperimentResultMixin.class);
    public static final String ID = "com.siegetechnologies.cqf.execution.strategy.ZipExperimentResultMixin"; // FIXME: srogers: extract string construction into a dedicated method
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-kkmmss"); // FIXME: srogers: extract string construction into a dedicated method

    /**
     * Saves the result of the experiment to a file
     * @param experiment
     * @param phase
     * @param result
     * @return
     * @throws IOException
     * @throws ExecutionException
     */
    public Object save(ExperimentExecutor executor, ExperimentImpl experiment, ExecutionPhase phase, Object result)
            throws IOException, ExecutionException {
        if (!(result instanceof ExecutionResultImpl)) {
            return result;
        }
        ExecutionResultImpl executionResult = (ExecutionResultImpl) result;
        Path path = new ZippedExecutionResultExporter().exportResult(executionResult);
        Path renamed = TemporaryFiles.create(getFileName(experiment));
        Files.move(path, renamed);

        //Save off path for future usage by other mixins
        executor.saveResult(experiment, getId(), renamed );

        return result;
    }

    /**
     * Returns a filename for a result file for a specified experiment.  The filename
     * has the form "{context}-result-[ID]-[TIME].zip" where the time is produced
     * by taking the current time and formatting it according to {@link
     * #DATE_FORMATTER}.
     *
     * @param experiment
     * @return the result file name
     */
    public String getFileName(ExperimentImpl experiment) {
        return String.format("%s-result-%s-%s.zip", experiment.getContextName(),
                experiment.getRoot().getId(),
                LocalDateTime.now().format(DATE_FORMATTER)); // FIXME: STRING: srogers
    }

    public void apply(ExperimentExecutor executor) {
        executor.registerAfterHook(this::save);
    }

    @Override
    public String getId() {
        return ID;
    }

    public static String id() { return ID; }
}
