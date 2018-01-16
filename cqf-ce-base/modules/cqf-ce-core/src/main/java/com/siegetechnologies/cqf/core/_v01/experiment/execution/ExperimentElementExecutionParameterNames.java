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
 * This set of names follows generation 01 of the naming convention. Other generations of
 * the naming convention are retained in the code base as well. Each is a similarly named
 * type with a suffix such as "_nc01" added to indicate its order in the naming convention
 * history. All but the currently active naming convention are decorated with a suffix
 * like that, and all but the currently active naming convention are marked as deprecated.
 *
 * @author srogers
 */
public interface ExperimentElementExecutionParameterNames
{
	public static final String ADMIN_LOGIN_NAME =
			"admin_login_name";

	public static final String ADMIN_LOGIN_PASSWORD =
			"admin_login_password";

	public static final String PROGRAM_PATH =
			"program_path";

	public static final String PROGRAM_ARGUMENTS =
			"program_arguments";

	public static final String PROGRAM_WORKING_DIRECTORY =
			"program_working_directory";

	public static final String HOST_LIST_SPEC =
			"host_list";

	public static final String IP_ADDRESS =
			"ip_address";

	public static final String IPv4_ADDRESS =
			IP_ADDRESS;

	public static final String IPv6_ADDRESS =
			"ip_v6_address";

	public static final String INTERIM_NETWORK_NAME_STEM =
			"network_name";

	public static final String INTERIM_PORT_GROUP_NAME_STEM =
			"port_group_name";

	public static final String INTERIM_PORT_GROUP_NIC_ALLOW_PROMISCUOUS =
			"port_group_nic_allow_promiscuous";

	public static final String INTERIM_PORT_GROUP_NIC_VLAN_ID =
			"port_group_nic_vlan_id";

	public static final String INTERIM_VIRTUAL_SWITCH_NAME_STEM =
			"virtual_switch_name";

	public static final String INTERIM_VIRTUAL_SWITCH_NIC_ALLOW_PROMISCUOUS =
			"virtual_switch_nic_allow_promiscuous";

	public static final String INTERIM_VIRTUAL_SWITCH_NIC_LIST_SPEC =
			"virtual_switch_nic_list_spec";

	public static final String INTERIM_RESOURCE_POOL_CPU_LIMIT =
			"resource_pool_cpu_limit";

	public static final String INTERIM_RESOURCE_POOL_CPU_RESERVATION =
			"resource_pool_cpu_reservation";

	public static final String INTERIM_RESOURCE_POOL_CPU_RESERVATION_EXPANDABLE =
			"resource_pool_cpu_reservation_expandable";

	public static final String INTERIM_RESOURCE_POOL_NAME_STEM =
			"resource_pool_name";

	public static final String INTERIM_RESOURCE_POOL_RAM_LIMIT =
			"resource_pool_ram_limit";

	public static final String INTERIM_RESOURCE_POOL_RAM_RESERVATION =
			"resource_pool_ram_reservation";

	public static final String INTERIM_RESOURCE_POOL_RAM_RESERVATION_EXPANDABLE =
			"resource_pool_ram_reservation_expandable";

	public static final String INTERIM_VIRTUAL_MACHINE_DONOR_NAME =
			"virtual_machine_donor_name";

	public static final String INTERIM_VIRTUAL_MACHINE_NAME_STEM =
			"virtual_machine_name";

	public static final String NETWORK_ADAPTER_NUMBER =
			"network_adapter_number";

	public static final String OPERATING_SYSTEM_FAMILY =
			"operating_system_family";

	public static final String PERMANENT_VIRTUAL_MACHINE_NAME =
			"virtual_machine_name";

	public static final String PERMANENT_VIRTUAL_SWITCH_NAME =
			"virtual_switch_name";

	public static final String USER_LOGIN_NAME =
			"user_login_name";

	public static final String USER_LOGIN_PASSWORD =
			"user_login_password";

}
