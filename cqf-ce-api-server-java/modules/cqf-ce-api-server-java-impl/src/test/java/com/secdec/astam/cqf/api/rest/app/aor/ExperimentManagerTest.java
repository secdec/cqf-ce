package com.secdec.astam.cqf.api.rest.app.aor;

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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentImplTestSubject;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.elements.WorkspaceDesignElement;
import org.junit.Test;

/**
 * @author srogers
 */
public class ExperimentManagerTest extends ResourceManagementServiceTest
{
	private ExperimentManager em01;
	private ExperimentManager em02;

	private ExperimentDesignElementImpl e01a_root_design;
	private ExperimentDesignElementImpl e01b_root_design;
	private ExperimentDesignElementImpl e01c_root_design;

	private ExperimentElementImpl e01a_root;
	private ExperimentElementImpl e01b_root;
	private ExperimentElementImpl e01c_root;
	
	private ExperimentImpl e01a;
	private ExperimentImpl e01b;
	private ExperimentImpl e01c;
	
	@Override
	public void setUp() throws Exception {
		
		super.setUp();
	
		e01a_root_design = new WorkspaceDesignElement(app01.getResourceManager().getMainDesignCatalog());
		e01b_root_design = new WorkspaceDesignElement(app01.getResourceManager().getMainDesignCatalog());
		e01c_root_design = new WorkspaceDesignElement(app01.getResourceManager().getMainDesignCatalog());
		
		e01a_root = new ExperimentElementImpl(e01a_root_design, null);
		e01b_root = new ExperimentElementImpl(e01b_root_design, null);
		e01c_root = new ExperimentElementImpl(e01c_root_design, null);
		
		e01a = new ExperimentImplTestSubject(e01a_root, "e01a_name");
		e01b = new ExperimentImplTestSubject(e01b_root, "e01b_name");
		e01c = new ExperimentImplTestSubject(e01c_root, "e01c_name");
	}
	
	protected void setUpResourceManagementServiceTestSubjects() {
		
		em01 = new ExperimentManagerTestSubject();
		em02 = new ExperimentManagerTestSubject();
		
		rms01 = em01;
		rms02 = em02;
	}
	
	/**/
	
	@Test
	public void testTrackingExperimentsDuringExecution() throws Exception {
	
		assertTrue(em01.getExperiments().isEmpty());
		
		em01.execute(e01a);
		assertTrue(em01.getExperiments().contains(e01a));
		assertTrue(! em01.getExperiments().contains(e01b));
		assertTrue(! em01.getExperiments().contains(e01c));
		assertEquals(1, em01.getExperiments().size());
		
		em01.execute(e01b);
		assertTrue(em01.getExperiments().contains(e01a));
		assertTrue(em01.getExperiments().contains(e01b));
		assertTrue(! em01.getExperiments().contains(e01c));
		assertEquals(2, em01.getExperiments().size());
		
		em01.execute(e01c);
		assertTrue(em01.getExperiments().contains(e01a));
		assertTrue(em01.getExperiments().contains(e01b));
		assertTrue(em01.getExperiments().contains(e01c));
		assertEquals(3, em01.getExperiments().size());
	}
	
	@Test
	public void testLookingUpExperimentsById() throws Exception {
		
		assertTrue(! em01.resolve(e01a.getId()).isPresent());
		assertTrue(! em01.resolve(e01b.getId()).isPresent());
		assertTrue(! em01.resolve(e01c.getId()).isPresent());
		
		em01.execute(e01a);
		assertTrue(em01.resolve(e01a.getId()).isPresent());
		assertTrue(! em01.resolve(e01b.getId()).isPresent());
		assertTrue(! em01.resolve(e01c.getId()).isPresent());
		
		em01.execute(e01b);
		assertTrue(em01.resolve(e01a.getId()).isPresent());
		assertTrue(em01.resolve(e01b.getId()).isPresent());
		assertTrue(! em01.resolve(e01c.getId()).isPresent());
		
		em01.execute(e01c);
		assertTrue(em01.resolve(e01a.getId()).isPresent());
		assertTrue(em01.resolve(e01b.getId()).isPresent());
		assertTrue(em01.resolve(e01c.getId()).isPresent());
	}
	
}
