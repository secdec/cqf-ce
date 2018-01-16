package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.handlers;

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

import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_NETWORK_NAME_STEM;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.NETWORK_ADAPTER_NUMBER;
import static com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.VSphereTestbedMachine.underlying;
import static com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine.underlying;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTaskId;
import com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution.ExperimentExecutionToolkitForTestbedVSphere;
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.VSphereTestbedMachine;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUObjectManager;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import com.siegetechnologies.cqf.vsphere.api.util.mo.proxies.AUProxyManager;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.mo.VirtualMachine;
import java.util.NoSuchElementException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class NetworkInterfaceCardExecutionHandlerTest {

	NetworkInterfaceCardExecutionHandler<?,?> handler;

	@Before
	public void init() {
		handler = new NetworkInterfaceCardExecutionHandler<>();
	}

	@Test
	public void testNameAndCategory() {
		assertEquals("Network Interface Card", handler.getDesignName());
		assertEquals("Hardware", handler.getDesignCategory());
	}

	@Test
	public void testNicInfoFrom() {
		ExperimentElementExecutionContext context = mock(ExperimentElementExecutionContext.class);
		ExperimentExecutionToolkitForTestbedVSphere executionToolkit = new ExperimentExecutionToolkitForTestbedVSphere();
		when(context.getInstanceParameter(INTERIM_NETWORK_NAME_STEM)).thenReturn("theNetworkNameStem");
		when(context.getInstanceParameter(NETWORK_ADAPTER_NUMBER)).thenReturn("2");
		when(context.getExecutionTaskId()).thenReturn(ExecutionTaskId.of("theTaskId"));
		when(context.getExecutionToolkit()).thenReturn(executionToolkit);

		assertEquals(2, executionToolkit.getNetworkAdapterNumberInExecutionContext(context));
//		assertEquals("theTaskId-network", executionToolkit.getInterimPortGroupNameForVirtualSwitchInExecutionContext(context));
//		^-- FIXME: COVERAGE: srogers: re-enable test of getInterimPortGroupNameForVirtualSwitchInExecutionContext()
	}

	@Test
	@Ignore // FIXME: srogers: finish adapting to AUObjectManager framework
	public void testGetVirtualMachine() {
		VirtualMachine vm = mock(VirtualMachine.class);

		AUObjectManager mom = new AUProxyManager();
		VSphereTestbedMachine testbedMachine = mock(VSphereTestbedMachine.class);
	//	when(testbedMachine.getDelegate()).thenReturn(mom.AUVirtualMachineProxy_from(vm));

		ExperimentElementExecutionContext context = mock(ExperimentElementExecutionContext.class);
		ExperimentExecutionToolkitForTestbedVSphere executionToolkit = new ExperimentExecutionToolkitForTestbedVSphere();
		when(context.getResult(VSphereTestbedMachine.class)).thenReturn(testbedMachine);
		when(context.getExecutionToolkit()).thenReturn(executionToolkit);

		assertEquals(vm, underlying(underlying(executionToolkit.getVirtualMachineResultInExecutionContext(context))));
	}

	@Test(expected=NoSuchElementException.class)
	@Ignore // FIXME: MOCK: srogers: vm.getDelegate().getConfig()
	public void testConfigureNetworkAdapterForVirtualMachine_noNetworkAdapter() {
		String networkAdapterName = "networkAdapterName", networkName = "networkName";

		VirtualDevice vd0 = mock(VirtualDevice.class, RETURNS_DEEP_STUBS);
		when(vd0.getDeviceInfo().getLabel()).thenReturn("Device 0");

		AUVirtualMachine vm = mock(AUVirtualMachine.class, RETURNS_DEEP_STUBS);
		when(vm.getDelegate().getConfig().getHardware().getDevice()).thenReturn(new VirtualDevice[] {vd0} );

		ExperimentExecutionToolkitForTestbedVSphere executionToolkit = new ExperimentExecutionToolkitForTestbedVSphere();
		executionToolkit.configureNetworkAdapterForVirtualMachine(vm, networkAdapterName, networkName);

		// ...
	}

	@Test
	@Ignore // FIXME: MOCK: srogers: vm.getDelegate().getConfig()
	public void testConfigureNetworkAdapterForVirtualMachine() {
		String networkAdapterName = "networkAdapterName", networkName = "networkName";

		VirtualDevice vd0 = mock(VirtualDevice.class, RETURNS_DEEP_STUBS);
		when(vd0.getDeviceInfo().getLabel()).thenReturn("Device 0");

		VirtualDevice vd1 = mock(VirtualEthernetCard.class, RETURNS_DEEP_STUBS);
		when(vd1.getDeviceInfo().getLabel()).thenReturn(networkAdapterName);

		AUVirtualMachine vm = mock(AUVirtualMachine.class, RETURNS_DEEP_STUBS);
		when(vm.getDelegate().getConfig().getHardware().getDevice()).thenReturn(new VirtualDevice[] { vd0, vd1 });

		ExperimentExecutionToolkitForTestbedVSphere executionToolkit = new ExperimentExecutionToolkitForTestbedVSphere();
		executionToolkit.configureNetworkAdapterForVirtualMachine(vm, networkAdapterName, networkName);

		// ...
	}

}
