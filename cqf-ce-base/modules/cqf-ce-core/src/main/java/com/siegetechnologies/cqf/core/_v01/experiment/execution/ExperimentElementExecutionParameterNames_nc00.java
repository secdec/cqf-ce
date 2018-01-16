package com.siegetechnologies.cqf.core._v01.experiment.execution;

/*-
 * #%L
 * astam-cqf-ce-core
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

/**
 * Provides the names of standard experiment element parameters bound at execution time.
 * This set of names follows generation 00 of the naming convention. Other generations of
 * the naming convention are retained in the code base as well. Each is a similarly named
 * type with a suffix such as "_nc00" added to indicate its order in the naming convention
 * history. All but the currently active naming convention are decorated with a suffix
 * like that, and all but the currently active naming convention are marked as deprecated.
 *
 * @author srogers
 */
@Deprecated
public interface ExperimentElementExecutionParameterNames_nc00
{
	public static final String ADMIN_LOGIN_NAME =
			"adminusername";

	public static final String ADMIN_LOGIN_PASSWORD =
			"adminpassword";

	public static final String PROGRAM_PATH =
			"programPath";

	public static final String PROGRAM_ARGUMENTS =
			"arguments";

	public static final String PROGRAM_WORKING_DIRECTORY =
			"workingDirectory";

	public static final String HOST_LIST_SPEC =
			"hostSystems";

	public static final String INTERIM_NETWORK_NAME_STEM =
			"network";

	public static final String INTERIM_PORT_GROUP_NAME_STEM =
			"nameFormat";

	public static final String INTERIM_PORT_GROUP_NIC_ALLOW_PROMISCUOUS =
			"allowPromiscuous";

	public static final String INTERIM_PORT_GROUP_NIC_VLAN_ID =
			"vlanId";

	public static final String INTERIM_VIRTUAL_SWITCH_NAME_STEM =
			"nameFormat";

	public static final String INTERIM_VIRTUAL_SWITCH_NIC_ALLOW_PROMISCUOUS =
			"allowPromiscuous";

	public static final String INTERIM_VIRTUAL_SWITCH_NIC_LIST_SPEC =
			"nic";

	public static final String INTERIM_RESOURCE_POOL_CPU_LIMIT =
			"cpuLimit";

	public static final String INTERIM_RESOURCE_POOL_CPU_RESERVATION =
			"cpuReservation";

	public static final String INTERIM_RESOURCE_POOL_CPU_RESERVATION_EXPANDABLE =
			"cpuExpandable";

	public static final String INTERIM_RESOURCE_POOL_NAME_STEM =
			"name";

	public static final String INTERIM_RESOURCE_POOL_RAM_LIMIT =
			"memoryLimit";

	public static final String INTERIM_RESOURCE_POOL_RAM_RESERVATION =
			"memoryReservation";

	public static final String INTERIM_RESOURCE_POOL_RAM_RESERVATION_EXPANDABLE =
			"memoryExpandable";

	public static final String INTERIM_VIRTUAL_MACHINE_DONOR_NAME =
			"name";

	public static final String INTERIM_VIRTUAL_MACHINE_NAME_STEM =
			"cloneName";

	public static final String NETWORK_ADAPTER_NUMBER =
			"adapterNumber";

	public static final String OPERATING_SYSTEM_FAMILY =
			"platform";

	public static final String PERMANENT_VIRTUAL_MACHINE_NAME =
			"name";

	public static final String PERMANENT_VIRTUAL_SWITCH_NAME =
			"name";

}
