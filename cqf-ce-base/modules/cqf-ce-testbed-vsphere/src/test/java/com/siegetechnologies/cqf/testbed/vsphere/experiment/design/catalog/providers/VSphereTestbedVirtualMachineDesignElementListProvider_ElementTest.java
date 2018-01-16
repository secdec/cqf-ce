package com.siegetechnologies.cqf.testbed.vsphere.experiment.design.catalog.providers;

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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import com.siegetechnologies.cqf.core.experiment.design.variant.VariantSpec;
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util.SnapshotDescriptionParametersParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VSphereTestbedVirtualMachineDesignElementListProvider_ElementTest
{
	@Mock
	private VSphereTestbedVirtualMachineDesignElementListProvider.ElementDelegate adapter;

	@Mock
	private ExperimentDesignElementIdResolver<ExperimentDesignElementImpl> resolver;
	
	private VSphereTestbedVirtualMachineDesignElementListProvider.Element item;
	
	@Before 
	public void init() {
		this.item = new VSphereTestbedVirtualMachineDesignElementListProvider.Element(resolver, adapter);
	}

	@Test
	public void testToString() {
		assertNotNull(item.toString());
	}

	@Test
	public void testGetDocumentation() {
		assertNotNull(item.getDocumentation());
	}

	@Test
	public void testGetName() {
		when(adapter.getName()).thenReturn("MyVM");
		assertEquals("Clone VM MyVM", item.getName());
	}

	@Test
	public void testGetParameters() {
		Map<String, String> own = new HashMap<>();
		own.put("x", "X");
		own.put("y", "Y");
		when(adapter.getParameters()).thenReturn(own);
		List<ParameterImpl> ps = item.getOwnParameters();
		assertEquals(2, ps.size());
	}
	
	@Test
	public void testGetVariants() {
		SnapshotDescriptionParametersParser.Variant v1 = SnapshotDescriptionParametersParser.Variant.of("x", Collections.emptyMap());
		Map<String,String> yps = new HashMap<>();
		yps.put("a", "a1");
		yps.put("b", "b1");
		SnapshotDescriptionParametersParser.Variant v2 = SnapshotDescriptionParametersParser.Variant.of("y", yps);
		when(adapter.getVariants()).thenReturn(Arrays.asList(v1, v2));
		List<VariantSpec> vs = item.getVariants();
		
		assertEquals(2, vs.size());
		
		assertEquals("x", vs.get(0).getName());
		assertTrue(vs.get(0).getParameters().isEmpty());
		
		assertEquals("y", vs.get(1).getName());
		assertEquals(2, vs.get(1).getParameters().size());
	}

}
