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

import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VirtualMachineSnapshotTree;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Abstraction over a vSphere virtual machine.
 * 
 * @author taylorj
 */
public class VirtualMachine {

	/**
	 * Creates a new instance wrapping a vSphere virtual machine.
	 *
	 * @param vm the vSphere virtual machine
	 */
	public VirtualMachine(com.vmware.vim25.mo.VirtualMachine vm) {
		this.vm = vm;
	}

	private final com.vmware.vim25.mo.VirtualMachine vm;

	@Override
	public String toString() {
		return String.format("VirtualMachine(name={}, backing={})", vm.getName(), vm);
	}

	/**
	 * Returns the status of tools on the virtual machine.
	 * 
	 * @return the tools status
	 */
	public GuestToolsAvailability getToolsStatus() {
		final com.vmware.vim25.VirtualMachineToolsStatus status = vm.getGuest().getToolsStatus();
		switch (status) {
		case toolsNotInstalled:
			return GuestToolsAvailability.TOOLS_NOT_INSTALLED;
		case toolsNotRunning:
			return GuestToolsAvailability.TOOLS_NOT_RUNNING;
		case toolsOk:
			return GuestToolsAvailability.TOOLS_OK;
		case toolsOld:
			return GuestToolsAvailability.TOOLS_OLD;
		default:
			throw new AssertionError("Unknown tools status: "+status+".");
		}
	}

	/**
	 * Returns the power state of the virtual machine.
	 * 
	 * @return the power state of the virtual machine
	 */
	public PowerState getPowerState() {
		final com.vmware.vim25.VirtualMachinePowerState state = vm.getRuntime().getPowerState();
		switch (state) {
		case poweredOff:
			return PowerState.POWERED_OFF;
		case poweredOn:
			return PowerState.POWERED_ON;
		case suspended:
			return PowerState.SUSPENDED;
		default:
			throw new AssertionError("Unknown power state: "+state+".");
		}
	}

	/**
	 * Returns the name of the virtual machine. This value is cached, to avoid
	 * successive calls to the server. To re-query the virtual machine's name,
	 * use {@link #getName(boolean)}.
	 * 
	 * @return the name of the virtual machine
	 */
	public String getName() {
		return getName(false);
	}

	/**
	 * Returns the name of the virtual machine, optionally refreshing
	 * the cached name.  If {@code refresh} is true or if name has not
	 * yet been retrieved, then the name is retrieved.  Otherwise, the
	 * cached name is returned.
	 *
	 * @param refresh whether to query the server if the name is already cached
	 * @return the name
	 */
	public String getName(boolean refresh) {
		if (refresh || cachedName == null) {
			cachedName = vm.getName();
		}
		return cachedName;
	}

	private String cachedName = null;

	/**
	 * Returns the root snapshots of the virtual machine
	 * 
	 * @return the virtual machine's root snapshots
	 */
	public Stream<Snapshot> getRootSnapshots() {
		return Optional.ofNullable(vm.getSnapshot())
				.map(VirtualMachineSnapshotInfo::getRootSnapshotList)
				.map(Stream::of)
				.orElseGet(Stream::empty)
				.map(Snapshot::new);
	}

	/**
	 * Returns a stream of all the snapshots of the virtual machine.
	 * 
	 * @return a stream of all the virtual machine's snapshots
	 */
	public Stream<Snapshot> getSnapshots() {
		return getRootSnapshots().flatMap(s -> Stream.concat(Stream.of(s), s.getChildren()));
	}

	/**
	 * Returns the current snapshot of the virtual machine.
	 * 
	 * @return the virtual machine's current snapshot
	 */
	public Optional<Snapshot> getCurrentSnapshot() {
		// Get the current snapshot (which is actually snapshot info), get the
		// MOR of the snapshot, and then search through the snapshot trees for
		// one with the same MOR. THat's the current snapshot tree. Finally,
		// get a snapshotImpl from it.
		return Optional.ofNullable(vm.getSnapshot())
				.map(VirtualMachineSnapshotInfo::getCurrentSnapshot)
				.flatMap(mor -> getTrees().filter(t -> mor.equals(t.getSnapshot()))
						.findFirst()
						.map(Snapshot::new));
	}

	/**
	 * Returns a stream over all the snapshot trees in the snapshot hierarchy.
	 * For each snapshot, there is a tree rooted at that snapshot.
	 *
	 * @return a stream over the trees
	 */
	private Stream<VirtualMachineSnapshotTree> getTrees() {
		return Optional.ofNullable(vm.getSnapshot())
				.map(VirtualMachineSnapshotInfo::getRootSnapshotList)
				.map(Stream::of)
				.orElseGet(Stream::empty)
				.flatMap(this::getTrees);
	}

	/**
	 * Returns a stream of the snapshot tree and all its descendent trees.
	 *
	 * @param tree the tree
	 * @return the stream
	 */
	private Stream<VirtualMachineSnapshotTree> getTrees(VirtualMachineSnapshotTree tree) {
		return Stream.concat(Stream.of(tree), Optional.ofNullable(tree.getChildSnapshotList())
				.map(Stream::of)
				.orElseGet(Stream::empty)
				.flatMap(this::getTrees));
	}

}
