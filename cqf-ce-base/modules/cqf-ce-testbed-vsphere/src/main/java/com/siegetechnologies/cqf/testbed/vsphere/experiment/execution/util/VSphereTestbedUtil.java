package com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util;

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

import static com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtil.runRemoteTask;
import static com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtilCutpoint.runRemoteAction;
import static com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool.underlying;
import static com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine.underlying;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutorService;
import com.siegetechnologies.cqf.core.util.Exceptions;
import com.siegetechnologies.cqf.core.util.Pair;
import com.siegetechnologies.cqf.vsphere.api.Clone;
import com.siegetechnologies.cqf.vsphere.api.CloneManager;
import com.siegetechnologies.cqf.vsphere.api.CloneMetadata;
import com.siegetechnologies.cqf.vsphere.api.CloneProperty;
import com.siegetechnologies.cqf.vsphere.api.EntityManager;
import com.siegetechnologies.cqf.vsphere.api.util.GuestFamily;
import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtil;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineFileLayoutEx;
import com.vmware.vim25.VirtualMachineFileLayoutExDiskLayout;
import com.vmware.vim25.VirtualMachineFileLayoutExDiskUnit;
import com.vmware.vim25.VirtualMachineFileLayoutExFileInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.FileManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.VirtualMachine;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CQF-specific utilities for working with vSphere.
 *
 * @author taylorj
 */
public class VSphereTestbedUtil {

	private static final Logger logger = LoggerFactory.getLogger(VSphereTestbedUtil.class);

	/**
	 * Value returned by {@link VirtualMachineFileLayoutExFileInfo#getType()}
	 * for configuration files.
	 */
	private static final String CONFIG_TYPE = "config";

	/**
	 * A map from virtual machine names to semaphores (used as mutual exclusion
	 * locks) for virtual machine operations. The map should not be used
	 * directly, but rather through {@link #getVmSemaphore(String)}.
	 */
	private static final Map<String, Semaphore> vmSemaphores = new HashMap<>();

	private VSphereTestbedUtil() {}

	/**
	 * Returns a list of virtual machine files that are needed to perform a
	 * clone operation. These files included are the latest descriptor and
	 * extent files for the virtual machine's disks, and the virtual machine's
	 * config file.
	 *
	 * @param vm the virtual machine
	 * @return a list of files
	 */
	public static List<VirtualMachineFileLayoutExFileInfo> getCloneFiles(VirtualMachine vm) {
		List<VirtualMachineFileLayoutExFileInfo> cloneFiles = new ArrayList<>();

		// Get the file that make up the VM.
		VirtualMachineFileLayoutEx layout = runRemoteAction(vm::getLayoutEx, () -> "Unable to get VM layout info.");
		VirtualMachineFileLayoutExFileInfo[] vmFiles = layout.getFile();

		// Get the VM's config file.
		Stream.of(vmFiles)
				.filter(f -> CONFIG_TYPE.equals(f.getType()))
				.forEach(cloneFiles::add);

		// Since the files have unique keys, we can create a map from the
		// key to the files without any risk of collision.
		Map<Integer, VirtualMachineFileLayoutExFileInfo> filesByKey = Stream.of(vmFiles)
				.collect(Collectors.toMap(f -> f.getKey(), f -> f));

		// Get the files that make up the current VM's disks. VM should be
		// snapshot,
		// so these files should be relatively small. They should be disk
		// descriptors
		// and disk extent files.
		VirtualMachineFileLayoutExDiskLayout[] diskLayouts = layout.getDisk();
		for (VirtualMachineFileLayoutExDiskLayout diskLayout : diskLayouts) {
			VirtualMachineFileLayoutExDiskUnit[] units = diskLayout.getChain();
			// The last unit in the chain the is "latest" unit, and
			// these are the files that we want to copy from. The filekeys
			// are not indices into vmFiles, but rather identifiers of
			// files in vmFiles.
			VirtualMachineFileLayoutExDiskUnit lastUnit = units[units.length - 1];
			for (int fileKey : lastUnit.getFileKey()) {
				VirtualMachineFileLayoutExFileInfo file = filesByKey.get(fileKey);
				Objects.requireNonNull(file, "file must not be null");
				cloneFiles.add(file);
			}
		}
		return cloneFiles;
	}

	/**
	 * An exception indicating a {@link VSphereAPIUtil} was expected,
	 * but was not available.  This may occur when a user should be logged
	 * in, but is not.
	 *
	 * @author taylorj
	 */
	public static class NoVSphereAPIUtilException extends NoSuchElementException {
		private static final long serialVersionUID = -3955342213164647439L;

		/**
		 * Creates a new exception with a default message.
		 */
		public NoVSphereAPIUtilException() {
			super("No VSphereAPIUtil available.");
		}
	}

	/**
	 * Acquires the semaphore for a given name and executes a callable,
	 * returning its result.
	 *
	 * @param vmName the name of the virtual machine of the semaphore
	 * @param callable the callable
	 * @return the result of the callable
	 *
	 * @param <T> the type returned by the callable
	 */
	private static <T> T runWithSemaphore(String vmName, Callable<T> callable) {
		Semaphore s = getVmSemaphore(vmName);
		try {
			s.acquire();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new VSphereTestbedException(e);
		}

		try {
			return callable.call();
		}
		catch (Exception e) {
			throw new VSphereTestbedException(e);
		}
		finally {
			s.release();
		}
	}

	/**
	 * Returns the semaphore for the virtual machine, creating it if necessary.
	 * The semaphore has one permit, and behaves as usual, with the exception
	 * that when the semaphore is released, if there are no more threads waiting
	 * on the semaphore, then it is removed from {@link #vmSemaphores}.
	 *
	 * @param vmName the name of the virtual machine
	 * @return a semaphore
	 */
	private static final Semaphore getVmSemaphore(String vmName) {
		Semaphore semaphore;
		synchronized (vmSemaphores) {
			if (vmSemaphores.containsKey(vmName)) {
				semaphore = vmSemaphores.get(vmName);
			}
			else {
				semaphore = new Semaphore(1, true) {
					private static final long serialVersionUID = -7886300829263042657L;

					@Override
					public void release() {
						synchronized (vmSemaphores) {
							if (!hasQueuedThreads()) {
								vmSemaphores.remove(vmName);
							}
						}
						super.release();
					}
				};
				vmSemaphores.put(vmName, semaphore);
			}
		}
		return semaphore;
	}

	/**
	 * Reconfigures the virtual machine with
	 * {@link VirtualMachine#reconfigVM_Task(VirtualMachineConfigSpec)}, but
	 * with synchronization on the tasks such that only one reconfiguration task
	 * is launched at a time.
	 *
	 * @param vm the virtual machine to reconfigure
	 * @param spec the virtual machine specification
	 */
	public static final void reconfigureVirtualMachine(
			VirtualMachine vm,
			VirtualMachineConfigSpec spec
	) {
		runWithSemaphore(vm.getName(), () -> {

			runRemoteAction(
					() -> {
							runRemoteTask(
									() -> vm.reconfigVM_Task(spec),
									() -> "Could not reconfigure virtual machine."
							);

							return null;
					},
					() -> "Could not reconfigure virtual machine.", e -> Exceptions.hasCause(e, InvalidState.class)
			);

			return null;
		});
	}

	/**
	 * Reconfigures a virtual machine.
	 *
	 * @see #reconfigureVirtualMachine(VirtualMachine, VirtualMachineConfigSpec)
	 *
	 * @param machine the virtual machine
	 * @param spec the configuration spec
	 */
	public static final void reconfigureVirtualMachine(AUVirtualMachine machine, VirtualMachineConfigSpec spec) {
		reconfigureVirtualMachine(underlying(machine), spec);
	}

	/**
	 * Data object containing the prototype directory of a linked clone creation
	 * process and the path to the virtual machine configuration file.
	 *
	 * @see VSphereTestbedUtil#createClonePrototype(String, VirtualMachine,
	 *      FileManager, VSphereAPIUtil)
	 *
	 * @author taylorj
	 */
	private interface ClonePrototypeInfo {
		/**
		 * Returns the directory containing the clone prototype files.
		 *
		 * @return the directory containing the files
		 */
		String getPrototypeDirectory();

		/**
		 * Returns the path of the virtual machine configuration file
		 *
		 * @return the path of the configuration file
		 */
		String getVmxPath();

		/**
		 * Creates a new instance with provided fields.
		 *
		 * @param prototypeDirectory the prototype directory
		 * @param vmxPath the configuration file path
		 * @return the instance
		 */
		static ClonePrototypeInfo of(String prototypeDirectory, String vmxPath) {
			return new ClonePrototypeInfo() {
				@Override
				public String getVmxPath() {
					return vmxPath;
				}

				@Override
				public String getPrototypeDirectory() {
					return prototypeDirectory;
				}
			};
		}
	}

	/**
	 * Returns the datacenter that a managed entity is located on. This method
	 * is based on the assumption that the datacenter appears in the
	 * {@link ManagedEntity#getParent() ancestry} of the managed entity. This
	 * assumption should hold for some entities, such as
	 * {@link VirtualMachine}s.
	 *
	 * @param managedEntity the managed entity
	 * @return a datacenter
	 *
	 * @deprecated use {@link EntityManager#getDatacenter(ManagedEntity)} instead
	 */
	@Deprecated
	public static Optional<Datacenter> getDatacenterForManagedEntity(ManagedEntity managedEntity) {
		return new EntityManager(managedEntity).getDatacenter(managedEntity);
	}

	/**
	 * Creates a directory on the server containing all the files needed for a
	 * virtual clone of a donor virtual machine. Creating a prototype folder on
	 * the server means that the files that need to be copied for numerous
	 * virtual machines can be copied on the server, without being downloaded
	 * from the server and then sent back to the server.
	 *
	 * @param datastoreName the name of the datastore
	 * @param donor the donor virtual machine
	 * @param fileManager the file manager
	 * @param util the vsphere util
	 * @return information about the clone prototype
	 */
	private static ClonePrototypeInfo createClonePrototype(
			String datastoreName,
			VirtualMachine donor,
			FileManager fileManager,
			VSphereAPIUtil util) {
		Datacenter datacenter = getDatacenterForManagedEntity(donor).orElse(null);
		String datacenterName = datacenter == null ? null
				: runRemoteAction(datacenter::getName, () -> "Could not get name of datacenter, " + datacenter + ".");

		String prototypeName = "scratchClone-" + UUID.randomUUID() // FIXME: STRING: srogers
				.toString();
		String prototypeDirectory = String.format("[%s] %s", datastoreName, prototypeName);

		boolean createParentDirectories = false;
		logger.info("attempting to make prototype directory (makeDirectory({}, {}, {}, {}))",
				fileManager,
				prototypeDirectory,
				datacenter,
				createParentDirectories);
		VSphereAPIUtil.makeDirectory(fileManager, prototypeDirectory, datacenter, createParentDirectories);

		Map<String, List<VirtualMachineFileLayoutExFileInfo>> filesByType = getCloneFiles(donor).stream()
				.collect(Collectors.groupingBy(f -> f.getType()));

		// Copy disk descriptors over. This looks scarier than it is. We have
		// to replace the
		//
		// parentFileNameHint="<parentName>"
		//
		// line with
		//
		// parentFileNameHint="../<sourceVMFolder>/<parentName>"
		//
		// We do that by "downloading" the disk descriptor file (into memory)
		// wrapping it in a ReplacingBufferedReader that will replace
		// 'parentFileNameHint="' with
		// 'parenFileNameHint="../<sourceVmFolder?/'.
		// That means we're depending on all the VM folders being in the same
		// directory, but that's OK on ESXi.
		for (VirtualMachineFileLayoutExFileInfo diskDescriptor : filesByType.get("diskDescriptor")) {
			String ddName = diskDescriptor.getName();
			String downloadPath = ddName.replaceFirst("\\[.*\\] ?", "");
			// "[DSName] Old-VM/its-disk-000003.vmdk" => "Old-VM/its-disk-000003.vmdk"

			String originDir = downloadPath.replaceFirst("/.*", "");
			// "Old-VM/its-disk-000003.vmdk" => "Old-VM"

			String pattern = "parentFileNameHint=\"";
			// "parentFileNameHint <= \""

			String replacement = "parentFileNameHint=\"../" + originDir + "/";
			// "parentFileNameHint=\"../Old-VM/"

			String uploadPath = ddName.replaceFirst(".*/", prototypeName + "/");
			// "[DSName] Old-VM/its-disk-000003.vmdk" => "<prototype>/its-disk-000003.vmdk"

			byte[] content = util.downloadFile(downloadPath, datastoreName, datacenterName);
			SequentialReplacer replacer = new SequentialReplacer(pattern, replacement);
			try (ByteArrayInputStream in = new ByteArrayInputStream(content);
					StringWriter sout = new StringWriter();
					InputStreamReader reader = new InputStreamReader(in);
					BufferedReader br = new BufferedReader(reader)) {
				// Generate a new byte array with replaced content, and then
				// "upload"
				// it to the new VM folder. We could be a bit more efficient
				// with this
				// by starting the PUT as soon as the GET returns, and copying
				// the entity
				// over line by line (and this would avoid having the whole
				// thing in
				// memory), but for now we can use the uploadFile and
				// downloadFile
				// methods defined in our FileManager, which simplifies
				// everything.
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					replacer.replace(line).ifPresent(ln -> {
						sout.write(ln);
						sout.write('\n');
					});
				}
				util.uploadFile(uploadPath, sout.toString()
						.getBytes(), datastoreName, datacenterName);
			}
			catch (IOException e) {
				throw new VSphereTestbedException("Error while copying files.", e);
			}
		}

		// Copy the config file and disk extents file without modification.
		// The disk extent files need no modification, and we update the
		// VMX with a reconfigure call.
		List<VirtualMachineFileLayoutExFileInfo> remainingFiles = new ArrayList<>();
		remainingFiles.addAll(filesByType.get(CONFIG_TYPE));
		remainingFiles.addAll(filesByType.get("diskExtent"));
		for (VirtualMachineFileLayoutExFileInfo file : remainingFiles) {
			String sourceName = file.getName();
			String destinationName = sourceName.replaceFirst(".*/", prototypeDirectory + "/");
			Datacenter sourceDatacenter = datacenter;
			Datacenter destinationDatacenter = datacenter;
			boolean force = false;
			VSphereAPIUtil.copyDatastoreFile(fileManager,
					sourceName,
					sourceDatacenter,
					destinationName,
					destinationDatacenter,
					force);
		}

		String vmxPath = filesByType.get(CONFIG_TYPE)
				.get(0)
				.getName();
		return ClonePrototypeInfo.of(prototypeDirectory, vmxPath);
	}

	/**
	 * Returns the first of a virtual machine's datastores.
	 *
	 * @param vm the virtual machine
	 * @return the first datastore of the virtual machine
	 */
	public static Datastore getFirstDatastore(VirtualMachine vm) {
		return runRemoteAction(vm::getDatastores, () -> "Could not get datastores.")[0];
	}

	/**
	 * Returns the datastore path of the clone's directory.
	 *
	 * @see #getCloneDirectory(String, String)
	 *
	 * @param donor the donor virtual machine
	 * @param cloneName the clone name
	 * @return the name of the directory, as a datastore path
	 */
	public static String getCloneDirectory(VirtualMachine donor, String cloneName) {
		String datastoreName = VSphereAPIUtil.getName(getFirstDatastore(donor));
		return getCloneDirectory(datastoreName, cloneName);
	}

	/**
	 * Returns the name of the directory in which a clone's file will be placed,
	 * based on the the name of a directory and the name of the clone.
	 *
	 * @param datastoreName the name of the datastore
	 * @param cloneName the name of the clone
	 * @return the datastore path of the clone's directory
	 */
	public static String getCloneDirectory(String datastoreName, String cloneName) {
		return String.format("[%s] %s", datastoreName, cloneName);
	}

	/**
	 * Returns a map from clone names to datastore directory names for a
	 * collection of clone names, which should be unique, and the name of the
	 * datastore on which the donor virtual machine is located.
	 *
	 * @param datastoreName the name of the datastore
	 * @param cloneNames the clone names
	 *
	 * @return a map from clone names to datastore paths
	 */
	public static Map<String, String> getCloneDirectories(String datastoreName, Collection<String> cloneNames) {
		return cloneNames.stream()
				.collect(Collectors.toMap(cloneName -> cloneName,
						cloneName -> getCloneDirectory(datastoreName, cloneName)));
	}


	/**
	 * Returns a virtual machine wrapper for the new clone.
	 *
	 * @param donor the donor virtual machine
	 * @param cloneName the name of the clone to create
	 * @param creatorUUID the id of the creator
	 * @param pool the resource pool in which to create clones
	 * @param util an auxiliary vSphere utility used
	 * @param cloneAdminCredentials authentication for the guest OS
	 * @return the stream
	 */
	public static AUVirtualMachine createLinkedClone(
			AUVirtualMachine donor,
			String cloneName,
			String creatorUUID,
			AUResourcePool pool,
			VSphereAPIUtil util,
			PasswordAuthentication cloneAdminCredentials
	) {
		Collection<String> cloneNames_1 = Collections.singleton(cloneName);

		return createLinkedClones(donor, cloneNames_1, creatorUUID, pool, util, cloneAdminCredentials)
				.findFirst().get().get().getRight();
	}

	/**
	 * Returns a stream of suppliers of pairs of clone names and virtual
	 * machine wrappers.
	 *
	 * @param donor the donor virtual machine
	 * @param cloneNames the names of the clones to create
	 * @param creatorUUID the id of the creator
	 * @param resourcePool the resource pool in which to create clones
	 * @param util an auxiliary vSphere utility used
	 * @param cloneAdminCredentials authentication for the guest OS
	 * @return the stream
	 */
	public static Stream<Supplier<Pair<String, AUVirtualMachine>>> createLinkedClones(
			AUVirtualMachine donor,
			Collection<String> cloneNames,
			String creatorUUID,
			AUResourcePool resourcePool,
			VSphereAPIUtil util,
			PasswordAuthentication cloneAdminCredentials
	) {
		logger.trace("createLinkedClones(donor={},cloneNames={})", donor, cloneNames);

		if (cloneNames.isEmpty()) {
			return Stream.empty();
		}

		String donorName = VSphereAPIUtil.getName(underlying(donor));
		GuestFamily family = GuestFamily.valueOf(underlying(donor));


		VirtualMachinePowerState powerState = VSphereAPIUtil.getRuntime(underlying(donor)).getPowerState();
		isTrue(VirtualMachinePowerState.poweredOff == powerState, "Virtual machine, %s, must be powered off to clone", donorName);

		cloneNames.forEach(cloneName -> isTrue(cloneName.length() <= 80,
				"Clone name " + cloneName + " is longer than 80 characters."));

		VirtualMachineSnapshotInfo snapshotInfo = runRemoteAction(underlying(donor)::getSnapshot, () -> "Could not get snapshot information.");
		notNull(snapshotInfo, "No snapshot information for donor virtual machine, %s.", donorName);

		String datastoreName = VSphereAPIUtil.getName(getFirstDatastore(underlying(donor)));

		Map<String, String> cloneDirectories = getCloneDirectories(datastoreName, cloneNames);

		FileManager fileManager = underlying(donor).getServerConnection()
				.getServiceInstance()
				.getFileManager();
		Datacenter datacenter = getDatacenterForManagedEntity(underlying(donor)).orElse(null);

		ClonePrototypeInfo info = createClonePrototype(datastoreName, underlying(donor), fileManager, util);
		String prototypeDirectory = info.getPrototypeDirectory();
		String configFileName = info.getVmxPath();
		logger.trace("Completed prototype clone directory, {}.", prototypeDirectory);

		Folder vmFolder = (Folder) runRemoteAction(underlying(donor)::getParent, () -> "Could not get parent folder.");
		String parentValue = underlying(donor).getMOR().getVal();

		// In parallel, copy the prototype directory to each of the clone
		// directories. The copy is recursive, and its all on the server
		// side, so it goes relatively quickly if the files aren't too big.
		// Then, after copying the files over, register the new virtual
		// machines. Then, reconfigure the machine with some special
		// meta-properties.
		ExecutorService fileTransferExecutorService = getFileTransferExecutorService();

		// Set a count down latch to delete the prototype directory after the
		// clone directories have been created.
		CountDownLatch prototypeDirectoryCleanup = new CountDownLatch(cloneNames.size());
		fileTransferExecutorService.submit(() -> {
			prototypeDirectoryCleanup.await();
			runRemoteAction(() -> {
				fileManager.deleteDatastoreFile_Task(prototypeDirectory, datacenter);
				logger.trace("Deleted prototype directory, {}.", prototypeDirectory);
			}, () -> "Unable to clean up prototype directory, " + prototypeDirectory + ".");
			return null;
		});

		// Get this as a list because the "collect" causes us to go through
		// all the clones immediately. We submit them for execution to the
		// executor service, and return pair suppliers whose get() methods
		// will actually get the value from the future. It's important that
		// we get all the tasks submitted early on.
		List<Supplier<Pair<String, AUVirtualMachine>>> immediateResult = cloneNames.stream()
				.map(cloneName -> {
					String cloneDirectory = cloneDirectories.get(cloneName);
					boolean force = false;
					Callable<VirtualMachine> cloneMaker = () -> {
						logger.trace("Clone {}: creating.", cloneName);
						runRemoteTask(
								() -> fileManager.copyDatastoreFile_Task(prototypeDirectory,
										datacenter,
										cloneDirectory,
										datacenter,
										force),
								() -> "Could not copy prototype directory, " + prototypeDirectory + ", to "
										+ "clone directory, " + cloneDirectory + "."
						);
						// count down for deleting the prototype directory
						prototypeDirectoryCleanup.countDown();
						logger.trace("Clone {}: populated directory {}, and decremented latch.",
								cloneName, cloneDirectory
						);
						String newVMX = configFileName.replaceFirst(".*/", cloneDirectory + "/");
						VirtualMachine cloneMachine = VSphereAPIUtil.registerVM(
								vmFolder, newVMX, cloneName, underlying(resourcePool), null
						);
						logger.trace("Clone {}: registered.", cloneName);

						VirtualMachineConfigSpec spec = new VirtualMachineConfigSpec();
						OptionValue[] ovs = new OptionValue[5];

						// Allow VMware to create a new UUID for the machine
						// when
						// it's booted up. Otherwise, we get the prompt asking
						// whether
						// we moved the machine, copied it, etc.
						ovs[0] = new OptionValue();
						ovs[0].setKey("id.action");
						ovs[0].setValue("create");

						// We use "" instead of null. The API docs say that the
						// value can be null to clear the value, but that
						// actually
						// causes an error. "" works in practice.
						ovs[1] = new OptionValue();
						ovs[1].setKey("sched.swap.derivedName"); // FIXME: STRING: srogers
						ovs[1].setValue("");

						ovs[2] = new OptionValue();
						ovs[2].setKey(CloneProperty.PARENT.getValue());
						ovs[2].setValue(parentValue);

						ovs[3] = new OptionValue();
						ovs[3].setKey(CloneProperty.DIRECTORY.getValue());
						ovs[3].setValue(cloneDirectory);

						ovs[4] = new OptionValue();
						ovs[4].setKey(CloneProperty.CREATOR_UUID.getValue());
						ovs[4].setValue(creatorUUID);

						spec.setExtraConfig(ovs);

						reconfigureVirtualMachine(cloneMachine, spec);
						logger.trace("Clone {}: reconfigured with CQF properties.", cloneName);

						return cloneMachine;
					};
					Future<VirtualMachine> f = fileTransferExecutorService.submit(cloneMaker);
					return (Supplier<Pair<String, AUVirtualMachine>>) () -> {
						VirtualMachine cloneMachine;
						try {
							cloneMachine = f.get();
						}
						catch (Exception e) {
							throw new VSphereTestbedException("Error while creating clone, " + cloneName + ".", e);
						}
						AUVirtualMachine clone = util.AUVirtualMachine_from(
								cloneMachine, cloneName, family, cloneAdminCredentials, donor, resourcePool
						);
						return Pair.of(cloneName, clone);
					};
				})
				.collect(Collectors.toList());
		return immediateResult.stream();
	}

	/**
	 * Deletes a linked clone and releases all of the resources it holds.
	 */
	public static void deleteLinkedClone(AUVirtualMachine cloneCachedInfo) {
		deleteLinkedClone(underlying(cloneCachedInfo));
	}

	/**
	 * Deletes a linked clone and releases all of the resources it holds.
	 */
	public static void deleteLinkedClone(VirtualMachine cloneVM) {
		final EntityManager entityManager = new EntityManager(cloneVM);

		final CloneManager cloneManager = new CloneManager(entityManager);

		final Clone clone = Clone.of(cloneVM, CloneMetadata.parse(cloneVM.getConfig()));

		cloneManager.deleteClone(clone);
	}

	public static void deleteLinkedClones(Stream<AUVirtualMachine> clones) {
		clones.forEach(x -> deleteLinkedClone(x)); // FIXME: PERF: CONCURRENCY: BEST-EFFORT: REVIEW: srogers
	}

	private static ExecutorService getFileTransferExecutorService() {
		ExecutorService result = ExperimentExecutorService.getExecutorService();
		//^-- FIXME: srogers: introduce a separate server-wide ExecutorService for file transfer threads

		return result;
	}
}
