package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.execution;

/*-
 * #%L
 * astam-cqf-ce-testbed-vsphere
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

import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.ADMIN_LOGIN_NAME;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.ADMIN_LOGIN_PASSWORD;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.HOST_LIST_SPEC;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_NETWORK_NAME_STEM;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_PORT_GROUP_NAME_STEM;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_PORT_GROUP_NIC_ALLOW_PROMISCUOUS;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_PORT_GROUP_NIC_VLAN_ID;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_RESOURCE_POOL_CPU_LIMIT;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_RESOURCE_POOL_CPU_RESERVATION;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_RESOURCE_POOL_CPU_RESERVATION_EXPANDABLE;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_RESOURCE_POOL_NAME_STEM;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_RESOURCE_POOL_RAM_LIMIT;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_RESOURCE_POOL_RAM_RESERVATION;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_RESOURCE_POOL_RAM_RESERVATION_EXPANDABLE;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_VIRTUAL_MACHINE_DONOR_NAME;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_VIRTUAL_MACHINE_NAME_STEM;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_VIRTUAL_SWITCH_NAME_STEM;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_VIRTUAL_SWITCH_NIC_ALLOW_PROMISCUOUS;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.INTERIM_VIRTUAL_SWITCH_NIC_LIST_SPEC;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.NETWORK_ADAPTER_NUMBER;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.OPERATING_SYSTEM_FAMILY;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.PERMANENT_VIRTUAL_MACHINE_NAME;
import static com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionParameterNames.PERMANENT_VIRTUAL_SWITCH_NAME;
import static com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.VSphereTestbedMachine.underlying;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.isTrue;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutionToolkit;
import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTaskId;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementId;
import com.siegetechnologies.cqf.core.util.Strings;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.VSphereTestbedMachine;
import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtilCutpoint;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualPortGroup;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualSwitch;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHost;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author srogers
 */
public class ExperimentExecutionToolkitForTestbedVSphere implements ExperimentExecutionToolkit
{
	private static final Logger logger = LoggerFactory.getLogger(ExperimentExecutionToolkitForTestbedVSphere.class);

	private final VSphereAPIUtilCutpoint delegate;

	/**
	 * Creates an object of this type.
	 */
	public ExperimentExecutionToolkitForTestbedVSphere()
	{
		this(null);
	}

	/**
	 * Creates an object of this type.
	 */
	public ExperimentExecutionToolkitForTestbedVSphere(VSphereAPIUtilCutpoint delegate)
	{
		this.delegate = delegate;
	}

	/**/

	/**
	 * Format string for a port group name.
	 * <p/>
	 * The arguments are: the execution context task ID, and the network name stem.
	 */
	private static final String INTERIM_NETWORK_NAME_FORMAT_FOR_EXECUTION_TASK = "%s-%s";

	/**
	 * Format string for virtual-switch names.
	 * <p/>
	 * The arguments are: the execution context task ID, and the virtual-switch name stem.
	 */
	private static final String INTERIM_VIRTUAL_SWITCH_NAME_FORMAT_FOR_EXECUTION_TASK = "%s-%s";

	/**
	 * Format string for a port group name.
	 * <p/>
	 * The arguments are: the virtual-switch name, and the port group name stem.
	 */
	private static final String INTERIM_PORT_GROUP_NAME_FORMAT_FOR_VIRTUAL_SWITCH = "%s_%s";

	/**
	 * Format string for resource-pool names.
	 * <p/>
	 * The arguments are: the resource-pool name stem and experiment element ID.
	 */
	private static final String INTERIM_RESOURCE_POOL_NAME_FORMAT_FOR_EXPERIMENT_ELEMENT = "%s-%s";

	/**
	 * Format string for interim virtual machine names (clones). <p/>The arguments are: the current user's name, and the
	 * clone name stem.
	 */
	private static final String INTERIM_VIRTUAL_MACHINE_NAME_FORMAT_FOR_USER = "%s-%s";

	/**
	 * Format string for network adapter names.
	 * <p/>
	 * The arguments are: the network adapter number.
	 */
	private static final String NETWORK_ADAPTER_NAME_FOR_NETWORK_ADAPTER_NUMBER = "Network adapter %s";

	/**/

	public String getInterimNetworkNameForExecutionTask(ExecutionTaskId executionTaskId, String networkNameStem)
	{
		return String.format(
				INTERIM_NETWORK_NAME_FORMAT_FOR_EXECUTION_TASK,
				executionTaskId, networkNameStem
		);
	}

	/**/

	public String getInterimVirtualSwitchNameForExecutionTask(
			ExecutionTaskId/*            */ executionTaskId,
			String/*                     */ virtualSwitchNameStem
	)
	{
		String result = String.format(
				INTERIM_VIRTUAL_SWITCH_NAME_FORMAT_FOR_EXECUTION_TASK,
				executionTaskId, virtualSwitchNameStem
		);
		isTrue(
				result.length() <= 32,
				"length of virtual switch name > 32: " + result
		);
		return result;
	}

	public AUDistributedVirtualSwitch createInterimVirtualSwitch(
			String/*                     */ name,
			List<AUHost>/*               */ attachedHosts,
			String/*                     */ nicListSpec,
			boolean/*                    */ allowPromiscuous
	)
	{
		String[] nicList = null;

		if (nicListSpec != null) {

			nicList = Stream.of(nicListSpec.split(","))

					.map(String::trim).filter(s -> ! s.isEmpty())

					.collect(toList()).toArray(new String[0]);
		}

		return delegate.createDistributedVirtualSwitch(name, attachedHosts, nicList, allowPromiscuous);
	}

	public void deleteInterimVirtualSwitch(
			AUDistributedVirtualSwitch/* */ virtualSwitch
	)
	{
		delegate.deleteDistributedVirtualSwitch(virtualSwitch);
	}

	/**/

	public AUDistributedVirtualSwitch getPermanentVirtualSwitch(
			String/*                     */ virtualSwitchName,
			List<AUHost>/*               */ attachedHosts_expected
	)
	{
		Optional<AUDistributedVirtualSwitch> virtualSwitch_maybe =
				delegate.findDistributedVirtualSwitch(virtualSwitchName);

		AUDistributedVirtualSwitch virtualSwitch =
				virtualSwitch_maybe.orElse(null);

		if (virtualSwitch == null) {

			throw new NoSuchElementException(
					"while getting existing virtual switch: " + virtualSwitchName
			);
		}

		List<AUHost> attachedHostList =
				virtualSwitch.getAttachedHostList();

		for (AUHost host_expected : attachedHosts_expected) {

			if (! attachedHostList.contains(host_expected)) {

				throw new NoSuchElementException(
						"while confirming each host has virtual switch: " + virtualSwitchName +
								"; missing host: " + host_expected
				);
			}
		}

		return delegate.AUDistributedVirtualSwitch_from(
				null, virtualSwitchName, attachedHosts_expected, null
		);
	}

	/**/

	public String getInterimPortGroupNameForVirtualSwitch(
			AUDistributedVirtualSwitch/* */ virtualSwitch,
			String/*                     */ portGroupNameStem
	)
	{
		String virtualSwitchName = virtualSwitch.getName();

		return String.format(INTERIM_PORT_GROUP_NAME_FORMAT_FOR_VIRTUAL_SWITCH, virtualSwitchName, portGroupNameStem);
	}

	public AUDistributedVirtualPortGroup getInterimPortGroupForVirtualSwitch(
			AUDistributedVirtualSwitch/* */ virtualSwitch,
			String/*                     */ portGroupNameStem
	)
	{
		String portGroupName = getInterimPortGroupNameForVirtualSwitch(virtualSwitch, portGroupNameStem);

		return delegate.AUDistributedVirtualPortGroup_from(null, portGroupName, virtualSwitch);
	}

	public AUDistributedVirtualPortGroup createInterimPortGroupForVirtualSwitch(
			AUDistributedVirtualSwitch/*    */ virtualSwitch,
			String/*                        */ portGroupNameStem,
			int/*                           */ vlanId,
			boolean/*                       */ allowPromiscuous
	)
	{
		String portGroupName = getInterimPortGroupNameForVirtualSwitch(virtualSwitch, portGroupNameStem);

		return delegate.createDistributedVirtualPortGroup(portGroupName, virtualSwitch, vlanId, allowPromiscuous);
	}

	public void deleteInterimPortGroupForVirtualSwitch(
			AUDistributedVirtualPortGroup/* */ portGroup
	)
	{
		delegate.deleteDistributedVirtualPortGroup(portGroup);
	}

	/**/

	public String getInterimResourcePoolNameForExperimentElement(
			ExperimentElementId/*        */ elementId,
			String/*                     */ resourcePoolNameStem
	)
	{
		return String.format(INTERIM_RESOURCE_POOL_NAME_FORMAT_FOR_EXPERIMENT_ELEMENT, resourcePoolNameStem, elementId);
	}

	public AUResourcePool createInterimResourcePool(
			String/*                        */ resourcePoolName,
			AUResourcePool/*                */ resourcePoolParent,
			AUResourcePool.AllocationInfo/* */ cpuAllocationInfo,
			AUResourcePool.AllocationInfo/* */ ramAllocationInfo
	)
	{
		AUResourcePool result = delegate.createResourcePool(
				resourcePoolName, resourcePoolParent, cpuAllocationInfo, ramAllocationInfo
		);
		return result;
	}

	public void deleteInterimResourcePool(
			String/*                     */ resourcePoolName
	)
	{
		delegate.findResourcePool(resourcePoolName).ifPresent(x -> delegate.deleteResourcePool(x));
	}

	/**/

	public String getInterimVirtualMachineNameForUser(
			String/*                     */ userName,
			String/*                     */ cloneNameStem
	)
	{
		return String.format(INTERIM_VIRTUAL_MACHINE_NAME_FORMAT_FOR_USER, userName, cloneNameStem);
	}

	public VSphereTestbedMachine getInterimVirtualMachine(
			String/*                     */ cloneName,
			String/*                     */ donorName,
			PasswordAuthentication/*     */ adminCredentials
	)
	{
		AUVirtualMachine clone = delegate.findVirtualMachine(cloneName).orElseThrow(() ->
				new NoSuchElementException("while getting virtual machine clone: " + cloneName));

		AUVirtualMachine donor = delegate.findVirtualMachine(donorName).orElseThrow(() ->
				new NoSuchElementException("while getting virtual machine donor: " + donorName));

		clone.setDonor(donor);
		clone.setAdminCredentials(adminCredentials);

		return VSphereTestbedMachine.from(clone);
	}

	/**
	 * Creates the interim virtual machine (as a linked clone).
	 *
	 * @param cloneName
	 * @param donorName
	 * @param adminCredentials
	 * @param experimentElementId
	 * @param resourcePool
	 *
	 * @return
	 */
	public VSphereTestbedMachine createInterimVirtualMachine(
			String/*                     */ cloneName,
			String/*                     */ donorName,
			PasswordAuthentication/*     */ adminCredentials,
			ExperimentElementId/*        */ experimentElementId,
			AUResourcePool/*             */ resourcePool
	)
	{
		AUVirtualMachine donor = delegate.findVirtualMachine(donorName).orElseThrow(() ->
				new NoSuchElementException("while getting virtual machine donor: " + donorName));

		Optional<AUVirtualMachine> clone_maybe = delegate.findVirtualMachine(cloneName);
		AUVirtualMachine clone;

		if (clone_maybe.isPresent()) {

			logger.trace("Clone {} already exists; reusing it.", cloneName);

			clone = clone_maybe.get();

			clone.setDonor(donor);
			clone.setAdminCredentials(adminCredentials);
		}
		else {
			logger.trace("Clone {} does not exist; creating it.", cloneName);

			if (resourcePool == null) {
				resourcePool = donor.getResourcePool();
			}

			String creatorUUID = experimentElementId.value();

			clone = delegate.createLinkedClone(donor, cloneName, creatorUUID, resourcePool, adminCredentials);
		}

		delegate.ensurePowerStateOfVirtualMachine(clone, AUVirtualMachine.PowerState.ON);

		return VSphereTestbedMachine.from(clone);
	}

	public void deleteInterimVirtualMachine(
			AUVirtualMachine virtualMachine
	)
	{
		delegate.deleteLinkedClone(virtualMachine);
	}

	/**/

	public VSphereTestbedMachine getPermanentVirtualMachine(
			String/*                     */ name,
			OperatingSystemFamily/*      */ osFamily,
			PasswordAuthentication/*     */ adminCredentials
	)
	{
		AUVirtualMachine machine = delegate.findVirtualMachine(name).orElse(null);

		if (machine == null) {

			throw new NoSuchElementException("while getting virtual machine: " + name);
		}

		assert machine.getName().equals(name);

		if (! machine.getFamily().toOperatingSystemFamily().equals(osFamily)) {

			throw new NoSuchElementException("while getting virtual machine: " + name + "; os-family: " + osFamily);
		}

		machine.setAdminCredentials(adminCredentials);

		return VSphereTestbedMachine.from(machine);
	}

	/**/

	public String getNetworkAdapterNameFromNetworkAdapterNumber(int networkAdapterNumber)
	{

		return String.format(NETWORK_ADAPTER_NAME_FOR_NETWORK_ADAPTER_NUMBER, networkAdapterNumber);
	}

	public void configureNetworkAdapterForVirtualMachine(
			AUVirtualMachine/*           */ virtualMachine,
			String/*                     */ networkAdapterName,
			String/*                     */ networkName
	)
	{
		delegate.configureNetworkAdapterForVirtualMachine(virtualMachine, networkAdapterName, networkName);
	}

	/**/

	/*
	 *======================================================================*
	 * NO DEPENDENCIES ON ExperimentElementExecutionContext ABOVE THIS LINE *
	 *======================================================================*
	 */

	/**/

	public AUDistributedVirtualSwitch getVirtualSwitchResultInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getResult(AUDistributedVirtualSwitch.class);
	}

	public AUDistributedVirtualPortGroup getPortGroupResultInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getResult(AUDistributedVirtualPortGroup.class);
	}

	public AUResourcePool getResourcePoolResultInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getResult()
				.filter(AUResourcePool.class::isInstance)
				.map(AUResourcePool.class::cast)
				.orElse(null);
	}

	public VSphereTestbedMachine getVirtualMachineResultInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getResult(VSphereTestbedMachine.class);
	}

	/**/

	public String getHostListSpecificationInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getRequiredInstanceParameter(HOST_LIST_SPEC);
	}

	public String getHostListRegexInExecutionContext(ExperimentElementExecutionContext context)
	{
		String hostListSpecification = getHostListSpecificationInExecutionContext(context);

		return Strings.regexFromCommaSeparatedList(hostListSpecification);
	}

	/**/

	public String getInterimNetworkNameStemInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getInstanceParameter(INTERIM_NETWORK_NAME_STEM);
	}

	public String getInterimNetworkNameInExecutionContext(ExperimentElementExecutionContext context)
	{
		ExecutionTaskId executionTaskId = context.getExecutionTaskId();

		String networkNameStem = getInterimNetworkNameStemInExecutionContext(context);

		return getInterimNetworkNameForExecutionTask(executionTaskId, networkNameStem);
	}

	/**/

	public String getInterimVirtualSwitchNameInExecutionContext(ExperimentElementExecutionContext context)
	{
		ExecutionTaskId executionTaskId = context.getExecutionTaskId();

		String virtualSwitchNameStem = context.getInstanceParameter(INTERIM_VIRTUAL_SWITCH_NAME_STEM);

		return getInterimVirtualSwitchNameForExecutionTask(executionTaskId, virtualSwitchNameStem);
	}

	public AUDistributedVirtualSwitch getInterimVirtualSwitchInExecutionContext(ExperimentElementExecutionContext context)
	{
		String virtualSwitchName = getInterimVirtualSwitchNameInExecutionContext(context);

		String hostListRegex = getHostListRegexInExecutionContext(context);

		List<AUHost> attachedHosts = delegate.findMatchingHosts(hostListRegex);

		return delegate.AUDistributedVirtualSwitch_from(
				null, virtualSwitchName, attachedHosts, null
		);
	}

	public AUDistributedVirtualSwitch createInterimVirtualSwitchInExecutionContext(
			ExperimentElementExecutionContext context)
	{
		String virtualSwitchName = getInterimVirtualSwitchNameInExecutionContext(context);

		String hostListRegex = getHostListRegexInExecutionContext(context);

		List<AUHost> hosts = delegate.findMatchingHosts(hostListRegex);

		String nicListSpec = context
				.getOptionalInstanceParameter(INTERIM_VIRTUAL_SWITCH_NIC_LIST_SPEC)
				.orElse(null);

		boolean allowPromiscuous = context
				.getOptionalInstanceParameter(INTERIM_VIRTUAL_SWITCH_NIC_ALLOW_PROMISCUOUS)
				.map(Boolean::valueOf)
				.orElse(false);

		return createInterimVirtualSwitch(virtualSwitchName, hosts, nicListSpec, allowPromiscuous);
	}

	public void deleteInterimVirtualSwitchInExecutionContext(ExperimentElementExecutionContext context)
	{
		AUDistributedVirtualSwitch virtualSwitch = getInterimVirtualSwitchInExecutionContext(context);

		AUDistributedVirtualSwitch x = getVirtualSwitchResultInExecutionContext(context);
		assert Objects.equals(virtualSwitch, x);

		deleteInterimVirtualSwitch(virtualSwitch);
	}

	/**/

	public String getPermanentVirtualSwitchNameInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getRequiredInstanceParameter(PERMANENT_VIRTUAL_SWITCH_NAME);
	}

	public AUDistributedVirtualSwitch getPermanentVirtualSwitchInExecutionContext(ExperimentElementExecutionContext context)
	{
		String virtualSwitchName = getPermanentVirtualSwitchNameInExecutionContext(context);

		String hostListRegex = getHostListRegexInExecutionContext(context);

		List<AUHost> hosts = delegate.findMatchingHosts(hostListRegex);

		return getPermanentVirtualSwitch(virtualSwitchName, hosts);
	}

	/**/

	public String getInterimPortGroupNameStemForVirtualSwitchInExecutionContext(
			ExperimentElementExecutionContext context
	)
	{
		return context.getInstanceParameter(INTERIM_PORT_GROUP_NAME_STEM);
	}

	public String getInterimPortGroupNameForVirtualSwitchInExecutionContext(
			ExperimentElementExecutionContext context
	)
	{
		AUDistributedVirtualSwitch virtualSwitch = getVirtualSwitchResultInExecutionContext(context);

		String portGroupNameStem = getInterimPortGroupNameStemForVirtualSwitchInExecutionContext(context);

		return getInterimPortGroupNameForVirtualSwitch(virtualSwitch, portGroupNameStem);
	}

	public AUDistributedVirtualPortGroup getInterimPortGroupForVirtualSwitchInExecutionContext(
			ExperimentElementExecutionContext context
	)
	{
		AUDistributedVirtualSwitch virtualSwitch = getVirtualSwitchResultInExecutionContext(context);

		String portGroupNameStem = getInterimPortGroupNameStemForVirtualSwitchInExecutionContext(context);

		return getInterimPortGroupForVirtualSwitch(virtualSwitch, portGroupNameStem);
	}

	public AUDistributedVirtualPortGroup createInterimPortGroupForVirtualSwitchInExecutionContext(
			ExperimentElementExecutionContext context
	)
	{
		AUDistributedVirtualSwitch virtualSwitch = context.getResult(AUDistributedVirtualSwitch.class);

		String portGroupNameStem = getInterimPortGroupNameStemForVirtualSwitchInExecutionContext(context);

		int vlanId = context.getOptionalInstanceParameter(INTERIM_PORT_GROUP_NIC_VLAN_ID)
				.map(Integer::valueOf).orElse(0);

		boolean allowPromiscuous = context.getOptionalInstanceParameter(INTERIM_PORT_GROUP_NIC_ALLOW_PROMISCUOUS)
				.map(Boolean::valueOf).orElse(false);

		return createInterimPortGroupForVirtualSwitch(
				virtualSwitch, portGroupNameStem, vlanId, allowPromiscuous);
	}

	public void deleteInterimPortGroupForVirtualSwitchInExecutionContext(
			ExperimentElementExecutionContext context
	)
	{
		AUDistributedVirtualPortGroup portGroup = getPortGroupResultInExecutionContext(context);

		deleteInterimPortGroupForVirtualSwitch(portGroup);
	}

	/**/

	public String getInterimResourcePoolNameStemInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getInstanceParameter(INTERIM_RESOURCE_POOL_NAME_STEM);
	}

	public String getInterimResourcePoolNameInExecutionContext(ExperimentElementExecutionContext context)
	{
		ExperimentElementId elementId = context.getExperimentElement().getId();

		String resourcePoolNameStem = getInterimResourcePoolNameStemInExecutionContext(context);

		return getInterimResourcePoolNameForExperimentElement(elementId, resourcePoolNameStem);
	}

	public AUResourcePool createInterimResourcePoolInExecutionContext(ExperimentElementExecutionContext context)
	{
		String resourcePoolName = getInterimResourcePoolNameInExecutionContext(context);

		AUResourcePool resourcePoolParent = getResourcePoolResultInExecutionContext(context);

		AUResourcePool.AllocationInfo cpuAllocationInfo = new AUResourcePool.AllocationInfo();
		AUResourcePool.AllocationInfo ramAllocationInfo = new AUResourcePool.AllocationInfo();

		long cpuLimit = context.getInstanceParameter(
				INTERIM_RESOURCE_POOL_CPU_LIMIT, Long::parseLong);

		long cpuReservation = context.getInstanceParameter(
				INTERIM_RESOURCE_POOL_CPU_RESERVATION, Long::parseLong);

		boolean cpuReservationExpandable = context.getInstanceParameter(
				INTERIM_RESOURCE_POOL_CPU_RESERVATION_EXPANDABLE, Boolean::parseBoolean);

		cpuAllocationInfo.setLimit(cpuLimit);
		cpuAllocationInfo.setReservation(cpuReservation);
		cpuAllocationInfo.setReservationExpandable(cpuReservationExpandable);

		long ramLimit = context.getInstanceParameter(
				INTERIM_RESOURCE_POOL_RAM_LIMIT, Long::parseLong);

		long ramReservation = context.getInstanceParameter(
				INTERIM_RESOURCE_POOL_RAM_RESERVATION, Long::parseLong);

		boolean ramReservationExpandable = context.getInstanceParameter(
				INTERIM_RESOURCE_POOL_RAM_RESERVATION_EXPANDABLE, Boolean::parseBoolean);

		ramAllocationInfo.setLimit(ramLimit);
		ramAllocationInfo.setReservation(ramReservation);
		ramAllocationInfo.setReservationExpandable(ramReservationExpandable);

		return createInterimResourcePool(
				resourcePoolName, resourcePoolParent, cpuAllocationInfo, ramAllocationInfo
		);
	}

	public void deleteInterimResourcePoolInExecutionContext(ExperimentElementExecutionContext context)
	{
		String resourcePoolName = getInterimResourcePoolNameInExecutionContext(context);

		deleteInterimResourcePool(resourcePoolName);
	}

	/**/

	public String getInterimVirtualMachineDonorNameInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getInstanceParameter(INTERIM_VIRTUAL_MACHINE_DONOR_NAME);
	}

	public String getInterimVirtualMachineNameStemInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getInstanceParameter(INTERIM_VIRTUAL_MACHINE_NAME_STEM);
	}

	public String getInterimVirtualMachineNameInExecutionContext(ExperimentElementExecutionContext context)
	{
		String userName = delegate.getLoginInfo().getUsername();

		String cloneNameStem = getInterimVirtualMachineNameStemInExecutionContext(context);

		return getInterimVirtualMachineNameForUser(userName, cloneNameStem);
	}

	public VSphereTestbedMachine getInterimVirtualMachineInExecutionContext(ExperimentElementExecutionContext context)
	{
		String donorName = getInterimVirtualMachineDonorNameInExecutionContext(context);
		String cloneName = getInterimVirtualMachineNameInExecutionContext(context);

		PasswordAuthentication adminCredentials = getAdminCredentialsInExecutionContext(context);

		return getInterimVirtualMachine(cloneName, donorName, adminCredentials);
	}

	public VSphereTestbedMachine createInterimVirtualMachineInExecutionContext(ExperimentElementExecutionContext context)
	{
		String cloneName = getInterimVirtualMachineNameInExecutionContext(context);

		String donorName = getInterimVirtualMachineDonorNameInExecutionContext(context);

		PasswordAuthentication adminCredentials = getAdminCredentialsInExecutionContext(context);

		AUResourcePool resourcePool = getResourcePoolResultInExecutionContext(context);

		return createInterimVirtualMachine(cloneName, donorName,
				adminCredentials, context.getExperimentElement().getId(), resourcePool
		);
	}

	public void deleteInterimVirtualMachineInExecutionContext(ExperimentElementExecutionContext context)
	{
		VSphereTestbedMachine clone = getInterimVirtualMachineInExecutionContext(context);

		VSphereTestbedMachine x = getVirtualMachineResultInExecutionContext(context);
		assert Objects.equals(clone, x);

		deleteInterimVirtualMachine(underlying(clone));
	}

	/**/

	public String getPermanentVirtualMachineNameInExecutionContext(ExperimentElementExecutionContext context)
	{
		return context.getInstanceParameter(PERMANENT_VIRTUAL_MACHINE_NAME);
	}

	public VSphereTestbedMachine getPermanentVirtualMachineInExecutionContext(ExperimentElementExecutionContext context)
	{
		String vmName = getPermanentVirtualMachineNameInExecutionContext(context);

		OperatingSystemFamily osFamily = getOperatingSystemFamilyInExecutionContext(context);

		PasswordAuthentication adminCredentials = getAdminCredentialsInExecutionContext(context);

		return getPermanentVirtualMachine(vmName, osFamily, adminCredentials);
	}

	/**/

	public int getNetworkAdapterNumberInExecutionContext(ExperimentElementExecutionContext context)
	{
		return Integer.parseInt(context.getInstanceParameter(NETWORK_ADAPTER_NUMBER));
	}

	public String getNetworkAdapterNameInExecutionContext(ExperimentElementExecutionContext context)
	{
		int networkAdapterNumber = getNetworkAdapterNumberInExecutionContext(context);

		return getNetworkAdapterNameFromNetworkAdapterNumber(networkAdapterNumber);
	}

	public void configureNetworkAdapterForVirtualMachineResultInExecutionContext(ExperimentElementExecutionContext context)
	{
		VSphereTestbedMachine testbedMachine = getVirtualMachineResultInExecutionContext(context);

		String networkAdapterName = getNetworkAdapterNameInExecutionContext(context);

		String networkName = getInterimNetworkNameInExecutionContext(context);

		configureNetworkAdapterForVirtualMachine(underlying(testbedMachine), networkAdapterName, networkName);
	}

	/**/

	public OperatingSystemFamily getOperatingSystemFamilyInExecutionContext(ExperimentElementExecutionContext context)
	{
		String osFamilyName = context.getRequiredInstanceParameter(OPERATING_SYSTEM_FAMILY);

		return OperatingSystemFamily.valueOf(osFamilyName.toUpperCase());
	}

	public PasswordAuthentication getAdminCredentialsInExecutionContext(ExperimentElementExecutionContext context)
	{
		String name = context.getRequiredInstanceParameter(ADMIN_LOGIN_NAME);

		String password = context.getRequiredInstanceParameter(ADMIN_LOGIN_PASSWORD);

		return new PasswordAuthentication(name, password.toCharArray());
	}

	/**/

}
