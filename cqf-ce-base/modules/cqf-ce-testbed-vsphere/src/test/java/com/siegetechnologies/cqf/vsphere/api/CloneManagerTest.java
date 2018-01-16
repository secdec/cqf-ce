package com.siegetechnologies.cqf.vsphere.api;

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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vmware.vim25.OptionValue;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.VirtualMachine;

@RunWith(MockitoJUnitRunner.class)
public class CloneManagerTest {
	
	@Mock
	EntityManager entityManager;
	
	@Mock
	VirtualMachine virtualMachine;
	
	CloneManager cloneManager;
	
	@Before
	public void init() {
		cloneManager = new CloneManager(entityManager);
	}
	
	@Test
	public void testConstructor() {
		new CloneManager(entityManager);
	}
	
	@Test
	public void testGetClone() {
		when(entityManager.getEntities(VirtualMachine.class)).thenReturn(Stream.empty());
		assertEquals(0, cloneManager.getClones().count());
	}
	
	@Test
	public void testIsClone_yes() {
		VirtualMachineConfigInfo info = mock(VirtualMachineConfigInfo.class);
		when(virtualMachine.getConfig()).thenReturn(info);
		OptionValue[] values = new OptionValue[1];
		values[0] = new OptionValue();
		values[0].setKey(CloneProperty.CREATOR_UUID.getValue());
		values[0].setValue("someUuidValue");
		when(info.getExtraConfig()).thenReturn(values);
		assertTrue(cloneManager.isClone(virtualMachine));
	}
	
	@Test
	public void testIsClone_no() {
		VirtualMachineConfigInfo info = mock(VirtualMachineConfigInfo.class);
		when(virtualMachine.getConfig()).thenReturn(info);
		when(info.getExtraConfig()).thenReturn(new OptionValue[] {});
		assertFalse(cloneManager.isClone(virtualMachine));
	}

}
