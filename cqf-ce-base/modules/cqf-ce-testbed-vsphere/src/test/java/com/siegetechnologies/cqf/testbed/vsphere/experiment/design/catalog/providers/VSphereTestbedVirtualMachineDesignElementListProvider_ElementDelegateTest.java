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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.siegetechnologies.cqf.vsphere.api.Snapshot;
import com.siegetechnologies.cqf.vsphere.api.VirtualMachine;
import com.siegetechnologies.cqf.vsphere.api.PowerState;
import com.siegetechnologies.cqf.vsphere.api.GuestToolsAvailability;

@RunWith(MockitoJUnitRunner.class)
public class VSphereTestbedVirtualMachineDesignElementListProvider_ElementDelegateTest
{
	
	@Mock(answer=Answers.RETURNS_DEEP_STUBS)
	private VirtualMachine vm;
	
	private VSphereTestbedVirtualMachineDesignElementListProvider.ElementDelegate adapter;
	
	@Before
	public void init() {
		adapter = new VSphereTestbedVirtualMachineDesignElementListProvider.ElementDelegate(vm);
	}
	
	@Test
	public void testToString() {
		assertNotNull(adapter.toString());
	}
	
	@Test
	public void testIsTestbed() {
		Snapshot s = mock(Snapshot.class);
		when(vm.getCurrentSnapshot()).thenReturn(Optional.of(s));
		
		// Start with a good testbed
		when(s.getDescription()).thenReturn("CQF[adminusername=test; adminpassword=test123]CQF");
		when(vm.getPowerState()).thenReturn(PowerState.POWERED_OFF);
		when(vm.getToolsStatus()).thenReturn(GuestToolsAvailability.TOOLS_OK);
		assertTrue(adapter.isTestbed());
		
		// There are three ways it can fail: bad power, bad tools, or bad snapshot data
		
		// bad power
		when(vm.getPowerState()).thenReturn(PowerState.POWERED_ON);
		assertFalse(adapter.isTestbed());
		when(vm.getPowerState()).thenReturn(PowerState.POWERED_OFF);
		
		// bad tools
		when(vm.getToolsStatus()).thenReturn(GuestToolsAvailability.TOOLS_NOT_INSTALLED);
		assertFalse(adapter.isTestbed());
		when(vm.getToolsStatus()).thenReturn(GuestToolsAvailability.TOOLS_OK);
		
		// bad snapshot
		when(s.getDescription()).thenReturn("");
		assertFalse(adapter.isTestbed());
	}
	
	@Test
	public void testGetName() {
		String name = "theName";
		when(vm.getName()).thenReturn(name);
		assertEquals(name, adapter.getName());
	}
	
	@Test
	public void testGetParameters() {
		Snapshot s = mock(Snapshot.class);
		when(vm.getName()).thenReturn("vm name");
		when(vm.getCurrentSnapshot()).thenReturn(Optional.of(s));
		Map<String,String> ps = adapter.getParameters();
		assertEquals(1, ps.size());
		assertEquals("vm name", ps.get("name"));
	}
	
	@Test
	public void testGetVariants() {
		Snapshot s = mock(Snapshot.class);
		when(vm.getCurrentSnapshot()).thenReturn(Optional.of(s));
		assertEquals(0, adapter.getVariants().size());
		
		
	}
	
}
