package com.siegetechnologies.cqf.core.experiment;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImplTestSubject;
import com.siegetechnologies.cqf.core.experiment.design.elements.WorkspaceDesignElement;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

/**
 * Created by cbancroft on 1/17/2017.
 */
public class ExperimentImplTest
{
	@Test
	public void getWorkspace() throws Exception {
		ExperimentImpl e = newExperiment();

		assertNotNull(e.getRoot());
	}

	@Test
	public void getName() throws Exception {
		ExperimentImpl e = newExperiment();
		assertEquals("Experiment", e.getName());
	}

	@Test
	public void getResults() throws Exception {
		ExperimentImpl e = newExperiment();

		assertFalse(e.getResult().isPresent());

		e.setResult(mock(ExecutionResultImpl.class));

		assertTrue(e.getResult().isPresent());
	}


	@Test
	public void stream() throws Exception {
		long count = newExperiment().stream().count();

		assertEquals(4, count);
	}

	@Test
	public void walk() throws Exception {
		AtomicInteger i = new AtomicInteger(0);
		newExperiment().walk(instance -> i.incrementAndGet());
		assertEquals(4, i.get());
	}

	ExperimentImpl newExperiment() {
		ExperimentElementImpl workspace = newExperimentWorkspace(3);
		return new ExperimentImplTestSubject(workspace, "Experiment");
	}

	ExperimentElementImpl newExperimentWorkspace(int childCount) {
		char child_id_uniquifier = 'a';

		WorkspaceDesignElement workspace_design = new WorkspaceDesignElement(null);
		ExperimentElementImpl workspace = new ExperimentElementImpl(workspace_design, null);

		for (int i = 0; i < childCount; i++) {
			ExperimentElementId child_id = new ExperimentElementId("" + child_id_uniquifier++);

			ExperimentDesignElementImpl child_design = new ExperimentDesignElementImplTestSubject(
					null, child_id + "_design", "test_category"
			);

			ExperimentElementImpl child = new ExperimentElementImpl(child_design, workspace, child_id, i);

			workspace.getChildren().add(child);
		}

		return workspace;
	}

}

