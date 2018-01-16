package com.siegetechnologies.cqf.core.experiment.execution.util;

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
 * Designates one of the platforms currently supported by CQF for executing experiments.
 *
 * @author srogers
 */
public enum ExecutionPlatform
{
	Default("Default"),

	Test("Test"),
	
	Nil("Nil"),

	/**/

//	Docker("Docker"),

//	KVM("KVM"),

//	Libvirt("Libvirt"),

//	LocalHost("Local-Host"),

//	RemoteHost("Remote-Host"),

//	Vagrant("Vagrant"),

	vSphere("vSphere");

	/**/

	private final String value;

	ExecutionPlatform(String value) {
		this.value = value;
	}

//	public static ExecutionPlatform parse(String value) throws IllegalArgumentException {
//
//		if (value == null) {
//			value = "default";
//		}
//
//		for (ExecutionPlatform x : ExecutionPlatform.values()) {
//			if (x.value.equals(value)) {
//				return x;
//			}
//		}
//		throw new IllegalArgumentException("invalid execution platform: " + value);
//	}

}
