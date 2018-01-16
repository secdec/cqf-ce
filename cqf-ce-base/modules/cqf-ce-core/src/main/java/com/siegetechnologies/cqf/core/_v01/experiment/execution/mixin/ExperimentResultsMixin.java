package com.siegetechnologies.cqf.core._v01.experiment.execution.mixin;

/*-
 * #%L
 * astam-cqf-ce-core
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

/**
 * Mixin that saves an {@link ExecutionResultImpl} result on an experiment executor.
 *
 * @author taylorj
 */
public class ExperimentResultsMixin implements Mixin {
  public static final String RESULT_KEY = "ExperimentResultsMixinResult";
  public static final String ID = "ExperimentResultsMixinId";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public void apply(ExperimentExecutor executor) {
    executor.registerAfterHook((theExecutor, experiment, executionPhase, result) -> {
      if (result instanceof ExecutionResultImpl) {
        theExecutor.saveResult(experiment, RESULT_KEY, result);
        return result;
      } else {
        return null;
      }
    });
  }
}
