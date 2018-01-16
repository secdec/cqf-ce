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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.vmware.vim25.OptionValue;
import com.vmware.vim25.VirtualMachineConfigInfo;

public class CloneMetadataTest {
	
	@Test
	public void testOf_fields() {
		String parent = "parent";
		String directory = "directory";
		String uuid = "id";
		CloneMetadata cm = CloneMetadata.of(parent, directory, uuid);
		assertSame(parent, cm.getParent());
		assertSame(directory, cm.getDirectory());
		assertSame(uuid, cm.getCreatorUuid());
		assertNotNull(cm.toString());
	}
	
	private OptionValue of(String key, Object value) {
		OptionValue ov = new OptionValue();
		ov.setKey(key);
		ov.setValue(value);
		return ov;
	}
	
	@Test
	public void testParse() {
		String parent = "PAR";
		String directory = "DIR";
		String uuid = "UUID"; 
		VirtualMachineConfigInfo config = mock(VirtualMachineConfigInfo.class);
		OptionValue[] ovs = new OptionValue[] { 
				of(CloneProperty.PARENT.getValue(), parent),
				of(CloneProperty.DIRECTORY.getValue(), directory),
				of(CloneProperty.CREATOR_UUID.getValue(), uuid)
		};
		when(config.getExtraConfig()).thenReturn(ovs);
		CloneMetadata cm = CloneMetadata.parse(config);
		assertSame(parent, cm.getParent());
		assertSame(directory, cm.getDirectory());
		assertSame(uuid, cm.getCreatorUuid());
	}
	

}
