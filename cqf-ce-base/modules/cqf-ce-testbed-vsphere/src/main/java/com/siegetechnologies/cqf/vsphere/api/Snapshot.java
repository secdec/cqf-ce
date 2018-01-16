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
import com.vmware.vim25.mo.VirtualMachineSnapshot;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A virtual machine snapshot. This provides a more convenient abstraction over
 * the {@link VirtualMachineSnapshot}, {@link VirtualMachineSnapshotInfo}, and
 * {@link VirtualMachineSnapshotTree} classes provided by the vSphere API.
 * 
 * @author taylorj
 */
public class Snapshot {

	/**
	 * Creates an object of this type
	 *
	 * @param tree
	 */
	public Snapshot(VirtualMachineSnapshotTree tree) {
		this.tree = tree;
	}

	/**
	 * Returns a stream of the child snapshots.
	 * 
	 * @return the child snapshots
	 */
	public Stream<Snapshot> getChildren() {
		return Optional.ofNullable(getTree())
				.map(VirtualMachineSnapshotTree::getChildSnapshotList)
				.map(Stream::of)
				.orElseGet(Stream::empty)
				.map(Snapshot::new);
	}

	/**
	 * Returns the name of the snapshot.
	 * 
	 * @return the snapshot name
	 */
	public String getName() {
		return getTree().getName();
	}

	/**
	 * Returns the description of the snapshot.
	 * 
	 * @return the snapshot description
	 */
	public String getDescription() {
		return getTree().getDescription();
	}

	private final VirtualMachineSnapshotTree tree;

	private VirtualMachineSnapshotTree getTree() {
		return tree;
	}
}
