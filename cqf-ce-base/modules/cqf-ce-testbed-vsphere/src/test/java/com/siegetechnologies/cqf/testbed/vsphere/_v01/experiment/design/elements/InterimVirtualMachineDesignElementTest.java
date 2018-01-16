package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements;

/*-
 * #%L
 * cqf-ce-testbed-vsphere
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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class InterimVirtualMachineDesignElementTest {

	/**
	 * Returns a repository that doesn't resolve any items, and that doesn't
	 * support listeners.
	 * 
	 * @return the repository
	 */
	private static <T extends ExperimentDesignElementImpl> ExperimentDesignElementIdResolver<T> getRepo() {
		return new ExperimentDesignElementIdResolver<T>() {
			@Override
			public Optional<T> resolve(ExperimentDesignElementId designElementId) {
				return Optional.empty();
			}
		};
	}

	/**
	 * Checks that the parameters on the clone vm item look like what we'd
	 * expect.
	 */
	@Test
	public void testParameters() {
		List<ParameterImpl> parameters = new InterimVirtualMachineDesignElement(getRepo()).getParameters();
		assertEquals(1, parameters.size());
		ParameterImpl p0 = parameters.get(0);
		assertEquals("Clone Name", p0.getLabel());
		assertEquals("cloneName", p0.getName());
		assertEquals("${name}-${cqf.id}", p0.getDefaultValue());
	}
}
