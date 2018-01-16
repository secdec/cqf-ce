package com.siegetechnologies.cqf.vsphere.api.util;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * Enumeration type for known guest families.
 */
public enum GuestFamily {

	/**
	 * Family of linux guests
	 */
	LINUX,

	/**
	 * Family of Windows guests
	 */
	WINDOWS,

	/**
	 * Could not be determined.
	 */
	RPC_RETURNED_NULL;

	private static final Logger logger = LoggerFactory.getLogger(GuestFamily.class);

	/**
	 * @return the "default" family, {@link #LINUX}
	 */
	public static GuestFamily defaultFamily() {
		return LINUX;
	}

	/**
	 * Returns a corresponding operating system.
	 *
	 * @return an operating system
	 */
	public OperatingSystemFamily toOperatingSystemFamily() {

		switch (this) {
		case LINUX:
			return OperatingSystemFamily.UNIX;
		case WINDOWS:
			return OperatingSystemFamily.WINDOWS;
		default:
			throw new IllegalStateException("unsupported GuestFamily: " + this);
		}
	}

	public static GuestFamily from(OperatingSystemFamily osFamily) {

		if (null == osFamily) {
			return null;
		}
		if (OperatingSystemFamily.UNIX.equals(osFamily)) {
			return GuestFamily.LINUX;
		}
		else if (OperatingSystemFamily.WINDOWS.equals(osFamily)) {
			return GuestFamily.WINDOWS;
		}
		else {
			throw new IllegalStateException("unsupported OperatingSystemFamily: " + osFamily);
		}
	}

	/**
	 * @param vm a virtual machine
	 * @return the virtual machine's guest family
	 */
	public static GuestFamily valueOf(VirtualMachine vm) {
		VirtualMachineConfigInfo info = VSphereAPIUtilCutpoint.runRemoteAction(vm::getConfig, () -> "Could not get VM config.");
		String guestId = info.getGuestId().toLowerCase();
		String guestFullName = info.getGuestFullName().toLowerCase();
		GuestFamily result = guestId.contains("windows") || guestFullName.contains("windows") // FIXME: STRING: srogers
				? WINDOWS
				: LINUX;
		logger.trace("GuestFamily.valueOf(vm={}) (guestId={}, guestFullName={}) => {}",vm, guestId, guestFullName, result);
		return result;
	}
}
