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

/**
 * State of VMware tools on a virtual machine.
 * 
 * @author taylorj
 */
public enum GuestToolsAvailability {

	/**
	 * Tools are not installed.
	 */
	TOOLS_NOT_INSTALLED,

	/**
	 * Tools are not running.
	 */
	TOOLS_NOT_RUNNING,

	/**
	 * Tools are OK.
	 */
	TOOLS_OK,

	/**
	 * Tools are old.
	 */
	TOOLS_OLD;
}
