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

import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIException;
import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtil;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.FileManager;
import com.vmware.vim25.mo.VirtualMachine;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages clones on a vCenter.
 *
 * @author taylorj
 */
public class CloneManager {

	private static final Logger logger = LoggerFactory.getLogger(CloneManager.class);

	private final EntityManager entityManager;

	/**
	 * Creates a new clone manager.
	 *
	 * @param entityManager an entity manager
	 */
	public CloneManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Returns a stream of the virtual machines that are clones.
	 *
	 * @return the stream
	 *
	 * @see #isClone(VirtualMachine)
	 */
	public Stream<Clone> getClones() {
		return this.entityManager.getEntities(VirtualMachine.class)
				.filter(this::isClone)
				.map(vm -> Clone.of(vm, CloneMetadata.parse(vm.getConfig())));
	}

	/**
	 * Returns a stream of the clones each of whose name begins with a provided
	 * username.  This is in accordance with the naming convention that clones
	 * created for a user begin with that user's username.
	 *
	 * @param user the username
	 * @return the stream
	 */
	public Stream<Clone> getClones(String user) {
		return entityManager.getEntities(VirtualMachine.class)
				.filter(this::isClone)
				.filter(vm -> vm.getName()
						.startsWith(user))
				.map(vm -> Clone.of(vm, CloneMetadata.parse(vm.getConfig())));
	}

	/**
	 * Returns true if the virtual machine is a clone. Whether a machine is a
	 * clone is determined by whether the virtual machine's configuration has a
	 * a key/value pair whose key is {@link CloneProperty#CREATOR_UUID}.
	 *
	 * @param vm the virtual machine
	 * @return whether the VM is a clone
	 */
	public boolean isClone(VirtualMachine vm) {
		VirtualMachineConfigInfo config = vm.getConfig();
		OptionValue[] extra = config.getExtraConfig();
		return Stream.of(extra)
				.map(OptionValue::getKey)
				.anyMatch(CloneProperty.CREATOR_UUID.getValue()::equals);
	}

	/**
	 * Deletes a clone. Deleting a linked clone is different from deleting a
	 * normal virtual machine. Because the virtual machine's disks are linked to
	 * the parent disks, the VM's own disks must be deleted simply as files on
	 * the datastore; the standard disk deletion would walk the disk tree and
	 * delete the disks of the donor virtual machines.
	 *
	 * @param clone the clone
	 */
	public void deleteClone(Clone clone) {
		logger.trace("deleteClone(clone={})", clone);
		final CloneMetadata metadata = clone.getMetadata();
		final VirtualMachine virtualMachine = clone.getVirtualMachine();

		// the metadata should always have a non-null directory, but we *really*
		// don't want to start destroying the VM if we can't delete its
		// directory, so we check here, just to be absolutely sure,
		final String directory = Objects.requireNonNull(metadata.getDirectory(), "directory must not be null");

		final Datacenter datacenter = entityManager.getDatacenter(virtualMachine).orElse(null);

		VSphereAPIUtil.ensurePowerStateOfVirtualMachine(virtualMachine, AUVirtualMachine.PowerState.OFF);
		try {
			virtualMachine.unregisterVM();
		}
		catch (RemoteException e) {
			throw new VSphereAPIException(e);
		}
		FileManager fileManager = virtualMachine.getServerConnection()
				.getServiceInstance()
				.getFileManager();
		VSphereAPIUtil.deleteDatastoreFile(fileManager, directory, datacenter);
	}
}
