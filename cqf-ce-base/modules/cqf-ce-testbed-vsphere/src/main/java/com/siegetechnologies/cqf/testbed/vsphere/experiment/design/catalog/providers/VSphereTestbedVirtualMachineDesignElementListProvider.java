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

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import com.siegetechnologies.cqf.core.experiment.design.variant.VariantSpec;
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util.SnapshotDescriptionParametersParser;
import com.siegetechnologies.cqf.vsphere.api.GuestToolsAvailability;
import com.siegetechnologies.cqf.vsphere.api.PowerState;
import com.siegetechnologies.cqf.vsphere.api.Snapshot;
import com.siegetechnologies.cqf.vsphere.api.VirtualMachine;
import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtil;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An items provider that uses a vSphere connection to look for VMs, determine
 * which are candidate testbeds, and returns specializations of the Clone VM for
 * each candidate testbed.
 *
 * @author taylorj
 */
public class VSphereTestbedVirtualMachineDesignElementListProvider implements ExperimentDesignElementListProvider<VSphereTestbedVirtualMachineDesignElementListProvider.Element>
{
	private static final Logger logger = LoggerFactory.getLogger(VSphereTestbedVirtualMachineDesignElementListProvider.class);

	private VSphereAPIUtil util;

	/**
	 * Creates a new instance with an empty set of listeners and no VSphereAPIUtil.
	 */
	public VSphereTestbedVirtualMachineDesignElementListProvider() {
		this.util = null;
	}

	@Override
	public String toString() {
		return super.toString() + String.format(" [vSphereUtil=%s]", util);
	}

	/**
	 * Sets the VSphereAPIUtil of the instance.
	 *
	 * @param newUtil the new VSphereAPIUtil, or null
	 */
	public void setVSphereUtil(VSphereAPIUtil newUtil) {
		if (newUtil != this.util) {
			this.util = newUtil;
		}
	}

	/**
	 * Gets a stream of virtual machines using the vSphere util (or an empty
	 * stream, if there is no vSphere util), determines which can be testbed
	 * machines, and returns a list of corresponding testbed VM items.
	 */
	@Override
	public List<Element> getDesignElements(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
		logger.info("Get testbed items for scanned VMs.");
		return Optional.ofNullable(util)
				.map(this::scanForVms)
				.orElseGet(Stream::empty)
				.map(vm -> new Element(resolver, vm))
				.collect(toList());
	}

	/**
	 * Returns a stream of testbed item finishers for virtual machines that can
	 * be serve as testbeds. All the virtual machines are retrieved using the
	 * vSphere utility. Each one is wrapped in a {@link ElementDelegate} and
	 * examined for {@link ElementDelegate#isTestbed() whether it can be a
	 * testbed}. Each one that can be is turned into a finisher, and the stream
	 * of finishers is returned.
	 *
	 * @param vSphereAPIUtil the vSphereAPIUtil
	 * @return the stream of finishers
	 */
	private Stream<ElementDelegate> scanForVms(VSphereAPIUtil vSphereAPIUtil) {
		logger.debug("Scanning vSphere for candidate testbed VMs.");

		Map<Boolean, List<ElementDelegate>> testbedCandidates = vSphereAPIUtil.getEntities(com.vmware.vim25.mo.VirtualMachine.class)
				.map(VirtualMachine::new)
				.map(ElementDelegate::new)
				.collect(partitioningBy(ElementDelegate::isTestbed));

		testbedCandidates.get(false)
				.forEach(vm -> logger.debug("Virtual Machine {} will not be used as a testbed.", vm));

		return testbedCandidates.get(true)
				.stream()
				.peek(vm -> logger.debug("Virtual Machine {} will be used as a testbed.", vm));
	}

	/**
	 * Testbed VM item. This is an extension of the clone VM item that fixes the
	 * name of virtual machine.
	 *
	 * @author taylorj
	 */
	static class Element extends ExperimentDesignElementImpl {

		private final ElementDelegate vm;

		/**
		 * Creates a new instance for a provided virtual machine name.
		 *
		 * @param resolver an item resolver, as to {@link ExperimentDesignElementImpl}
		 *            constructor
		 * @param vm the name of the virtual machine
		 */
		public Element(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver, ElementDelegate vm) {
			super(resolver);
			this.vm = vm;
		}

		/**
		 * Returns an item parameter whose name and default value are the key and
		 * value of a map entry.
		 *
		 * @param entry the map entry
		 * @return the item parameter
		 */
		private static ParameterImpl createItemParameter(Map.Entry<String, String> entry) {
			return new ParameterImpl.Builder()
					.setName(entry.getKey())
					.setDefaultValue(entry.getValue())
					.setRequired(true)
					.build();
		}

		@Override
		public String toString() {
			return String.format("[Element: %s]", getName());
		}

		@Override
		public DocumentationImpl getDocumentation() {
			return DocumentationImpl.Builder.info("Create a clone of " + vm.getName() + ".")
					.build();
		}

		@Override
		public String getName() {
			return "Clone VM " + vm.getName();
		}

		@Override
		public String getCategory() {
			return "Node";
		}

		@Override
		public Optional<? extends ExperimentDesignElementImpl> getSuperDesignElement() {
		  ExperimentDesignElementId x = ExperimentDesignElementId.of("Clone VM", "Node", null); // FIXME: STRING: srogers
		  return this.resolver.resolve(x);
		}

		@Override
		public List<ParameterImpl> getOwnParameters() {
			return vm.getParameters()
					.entrySet()
					.stream()
					.map(Element::createItemParameter)
					.collect(toList());
		}

		private VariantSpec variantSpecFrom(SnapshotDescriptionParametersParser.Variant v) {
			VariantSpec spec = new VariantSpec(this, v.getName());
			v.getParameters()
					.forEach((key, value) -> {
						spec.addParameter(new ParameterImpl.Builder()
								.setName(key)
								.setDefaultValue(value)
								.setRequired(false)
								.build()
						);
					});
			return spec;
		}

		@Override
		public List<VariantSpec> getVariants() {
			return vm.getVariants()
					.stream()
					.map(this::variantSpecFrom)
					.collect(toList());
		}
	}

	/**
	 * Wrapper around a virtual machine for checking whether the VM
	 * can be used as testbed baseinstall.
	 *
	 * @author taylorj
	 */
	static class ElementDelegate {

		/**
		 * Map indicating whether a tool status is "good" or not
		 */
		private static final EnumMap<GuestToolsAvailability, Boolean> TESTBED_TOOL_STATUS = createTestbedToolStatuses();

		/**
		 * Map indicating whether a power status is "good" or not
		 */
		private static final EnumMap<PowerState, Boolean> TESTBED_POWER_STATUS = createTestbedPowerStatus();

		/**
		 * The adapted virtual machine
		 */
		private final VirtualMachine vm;

		/**
		 * Creates a new instance wrapping a virtual machine.
		 *
		 * @param vm the virtual machine
		 */
		public ElementDelegate(VirtualMachine vm) {
			this.vm = vm;
		}

		@Override
		public String toString() {
			return String.format("ElementDelegate(vm=%s)", vm);
		}

		/**
		 * Returns the name of the virtual machine.
		 *
		 * @return the name of the virtual machine
		 */
		public String getName() {
			return vm.getName();
		}

		/**
		 * Returns true if the VM is powered off.
		 *
		 * @return true if the VM is powered off
		 */
		private boolean isTestbedPowerStatus() {
			return TESTBED_POWER_STATUS.get(vm.getPowerState());
		}

		/**
		 * Returns a map of the parameters declared in the current snapshot's
		 * description.
		 *
		 * @return the parameters
		 */
		private Optional<SnapshotDescriptionParametersParser.Description> getDescription() {
			return vm
					.getCurrentSnapshot()
					.map(Snapshot::getDescription)
					.flatMap(new SnapshotDescriptionParametersParser()::parseDescription);
		}

		/**
		 * Returns a map of the parameters declared in the current snapshot's
		 * description and a "name" parameter whose value is the name of the virtual
		 * machine.
		 *
		 * @return the parameters
		 */
		public Map<String, String> getParameters() {
			final Map<String, String> result = new HashMap<>();
			getDescription().map(SnapshotDescriptionParametersParser.Description::getParameters).ifPresent(result::putAll);
			result.put("name", vm.getName()); // FIXME: STRING: srogers
			return result;
		}

		/**
		 * Returns a list of the variants declared in the current snapshot's description.
		 *
		 * @return the variants
		 */
		public List<SnapshotDescriptionParametersParser.Variant> getVariants() {
			return getDescription().map(SnapshotDescriptionParametersParser.Description::getVariants)
					.orElseGet(Collections::emptyList);
		}

		/**
		 * Returns true if the VM has a current snapshot and parameters can be
		 * extracted from the snapshot's description.
		 *
		 * @return whether the VM has a cloneable snapshot
		 */
		private boolean isTestbedSnapshot() {
			return getDescription().isPresent();
		}

		/**
		 * Returns true if VMware tools are installed on the VM.
		 *
		 * @return true if VMware tools are installed
		 */
		private boolean isTestbedToolsStatus() {
			return TESTBED_TOOL_STATUS.get(vm.getToolsStatus());
		}

		/**
		 * Returns true if the VM can be used as a testbed machine. All three of the
		 * other predicates ({@link #isTestbedPowerStatus()},
		 * {@link #isTestbedSnapshot()}, and {@link #isTestbedToolsStatus()}) must
		 * return true.
		 *
		 * @return true if the VM can be used as a testbed machine
		 */
		public boolean isTestbed() {
			return isTestbedPowerStatus() && isTestbedToolsStatus() && isTestbedSnapshot();
		}

		/**
		 * Returns a map indicating whether a power status is suitable for a testbed.
		 *
		 * @return the map
		 */
		private static EnumMap<PowerState, Boolean> createTestbedPowerStatus() {
			EnumMap<PowerState, Boolean> s = new EnumMap<>(PowerState.class);
			s.put(PowerState.POWERED_OFF, true);
			s.put(PowerState.POWERED_ON, false);
			s.put(PowerState.SUSPENDED, false);
			return s;
		}

		/**
		 * Returns a map indicating whether a tools status is suitable for a testbed.
		 *
		 * @return the map
		 */
		private static EnumMap<GuestToolsAvailability, Boolean> createTestbedToolStatuses() {
			EnumMap<GuestToolsAvailability, Boolean> s = new EnumMap<>(GuestToolsAvailability.class);
			s.put(GuestToolsAvailability.TOOLS_NOT_INSTALLED, false);
			s.put(GuestToolsAvailability.TOOLS_NOT_RUNNING, true);
			s.put(GuestToolsAvailability.TOOLS_OK, true);
			s.put(GuestToolsAvailability.TOOLS_OLD, true);
			return s;
		}
	}
}
