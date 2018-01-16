package com.siegetechnologies.cqf.testbed.vsphere.experiment.execution;

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

import static org.apache.commons.lang3.Validate.validState;

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentExecutorService;
import com.siegetechnologies.cqf.core.util.Concurrency;
import com.siegetechnologies.cqf.core.util.Exceptions;
import com.siegetechnologies.cqf.core.util.HttpStatusCodes;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.TestbedMachine;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.ProgramExecutionSpec;
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util.VSphereTestbedException;
import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtil;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import com.vmware.vim25.FileAlreadyExists;
import com.vmware.vim25.FileTransferInformation;
import com.vmware.vim25.GuestAuthentication;
import com.vmware.vim25.GuestFileAttributes;
import com.vmware.vim25.GuestFileInfo;
import com.vmware.vim25.GuestFileType;
import com.vmware.vim25.GuestListFileInfo;
import com.vmware.vim25.GuestPosixFileAttributes;
import com.vmware.vim25.GuestProcessInfo;
import com.vmware.vim25.GuestProgramSpec;
import com.vmware.vim25.GuestWindowsFileAttributes;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.GuestFileManager;
import com.vmware.vim25.mo.GuestProcessManager;
import com.vmware.vim25.mo.VirtualMachine;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * vSphere-based implementation of TestbedMachine.
 *
 * @author taylorj
 */
public class VSphereTestbedMachine implements TestbedMachine
{
	private static final Logger logger = LoggerFactory.getLogger(VSphereTestbedMachine.class);

	private final AUVirtualMachine delegate;

	/**
	 * Creates a new object of this type.
	 *
	 * @param machine
	 *
	 * @return a new object of this type
	 */
	protected VSphereTestbedMachine(
			AUVirtualMachine/*         */ machine
	)
	{
		this.delegate = machine;
	}

	/**
	 * Returns an object of this type based on the virtual machine specified.
	 * <p/>
	 * The object might be new, or it might be a cached instance.
	 *
	 * @return an object of this type based on the virtual machine specified.
	 */
	public static VSphereTestbedMachine from(
			AUVirtualMachine/*         */ machine
	)
	{

		return new VSphereTestbedMachine(machine);
	}

	/**/

	/**
	 * Makes a best effort to wipe the delegate's admin credentials from memory.
	 */
	@Override
	public void close()
	{

		delegate.resetAdminCredentials();
	}

	/**/

	@Override
	public int hashCode()
	{
		return Objects.hash(this, this.delegate);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) {
			return true;
		}

		if (o instanceof VSphereTestbedMachine) {

			VSphereTestbedMachine that = (VSphereTestbedMachine) o;

			return Objects.equals(this.delegate, that.delegate);
		}

		return false;
	}

	@Override
	public String toString()
	{
		return String.format("%s { delegate: %s }",
				this.getClass().getSimpleName(), this.delegate
		);
	}

	/**/

	public static AUVirtualMachine underlying(VSphereTestbedMachine machine)
	{

		return machine.getDelegate();
	}

	public/*for_mocking_otherwise_protected*/ AUVirtualMachine getDelegate()
	{

		return delegate;
	}

	/**/

	protected AUVirtualMachine getDonor()
	{

		return delegate.getDonor();
	}

	protected String getName()
	{

		return delegate.getName();
	}

	@Override
	public OperatingSystemFamily getFamily()
	{

		return delegate.getFamily().toOperatingSystemFamily();
	}

	protected GuestAuthentication getAdminCredentials_in_vSphere_form()
	{

		return delegate.getAdminCredentials_in_vSphere_form();
	}

	/**/

	@Override
	public CompletableFuture<Void> upload(byte[] content, int permissions, String pathname)
	{
		return CompletableFuture.runAsync(() -> {
			try {
				synchronized (this) {
					doUpload(FileContentHttpEntityAdapter.of(content), permissions, pathname);
				}
			}
			catch (IOException | InterruptedException | ExecutionException e) {
				String message = String
						.format("Failed in upload(byte[%s], %s, %s). [%s]", content.length, permissions, pathname, this);
				throw new VSphereTestbedException(message, e);
			}
		}, getFileTransferExecutorService());
	}

	@Override
	public CompletableFuture<Void> upload(File content, int permissions, String pathname)
	{
		return CompletableFuture.runAsync(() -> {
			try {
				synchronized (this) {
					doUpload(FileContentHttpEntityAdapter.of(content), permissions, pathname);
				}
			}
			catch (InterruptedException | ExecutionException | IOException e) {
				String message = String.format("Failed in upload(%s, %s, %s). [%s]", content, permissions, pathname, this);
				throw new VSphereTestbedException(message, e);
			}
		});
	}

	@Override
	public CompletableFuture<byte[]> download(String pathname)
	{
		return CompletableFuture.supplyAsync(() -> {
			try {
				synchronized (this) {
					return doDownload(pathname);
				}
			}
			catch (IOException | InterruptedException | ExecutionException e) {
				String message = String.format("Failed in download(%s). [%s]", pathname, this);
				throw new VSphereTestbedException(message, e);
			}
		}, getFileTransferExecutorService());
	}

	@Override
	public CompletableFuture<Integer> runCommand(ProgramExecutionSpec command)
	{
		return CompletableFuture.supplyAsync(() -> {
			try {
				synchronized (this) {
					return doRunCommand(command);
				}
			}
			catch (RemoteException | InterruptedException | ExecutionException e) {
				String message = String.format("Failed in runCommand(%s). [%s]", command, this);
				throw new VSphereTestbedException(message, e);
			}
		}, getFileTransferExecutorService());
	}

	/*
	 * Implementation Methods
	 */

	/**
	 * Uploads content to a file in the virtual machine.
	 *
	 * @param content     the file content
	 * @param permissions the permissions
	 * @param pathname    the path of the file
	 *
	 * @throws IOException          if an I/O error occurs
	 * @throws ExecutionException   if an exception occurs while waiting for the machine to become ready
	 * @throws InterruptedException if the process is interrupted while waiting for the machine to become ready
	 */
	void doUpload(FileContentHttpEntityAdapter content, int permissions, String pathname)
			throws IOException, InterruptedException, ExecutionException
	{
		logger.debug("doUpload(content={}, permissions={}, pathname={})", content, permissions, pathname);
		ensureReadyForInteraction();
		VirtualMachine vm = delegate.getDelegate();
		GuestFileManager manager = vm.getServerConnection()
				.getServiceInstance()
				.getGuestOperationsManager()
				.getFileManager(vm);

		GuestAuthentication auth = this.getAdminCredentials_in_vSphere_form();

		GuestFileAttributes fileAttributes = createAttributes(permissions);
		boolean overwrite = true;
		String url = manager.initiateFileTransferToGuest(
				auth,
				pathname,
				fileAttributes,
				content.getLength(),
				overwrite
		);
		url = replaceStarWithHost(url);

		HttpPut put = new HttpPut(url);
		put.setEntity(content.toHttpEntity());
		try (
				CloseableHttpClient client = VSphereAPIUtil.getTrustingClient();
				CloseableHttpResponse response = client.execute(put)
		) {
			StatusLine line = response.getStatusLine();
			int code = line.getStatusCode();
			validState(HttpStatusCodes.isSuccess(code), "Non-successful response: %s", line);
		}
	}

	/**
	 * Returns guest file attributes based on a UNIX-style permissions value.
	 *
	 * @param permissions the permissions
	 *
	 * @return the guest file attributes
	 */
	GuestFileAttributes createAttributes(int permissions)
	{
		OperatingSystemFamily family = this.getFamily();
		switch (family) {
		case UNIX:
			GuestPosixFileAttributes posixAttributes = new GuestPosixFileAttributes();
			posixAttributes.setPermissions((long) permissions);
			return posixAttributes;
		case WINDOWS:
			GuestWindowsFileAttributes windowsAttributes = new GuestWindowsFileAttributes();
			return windowsAttributes;
		default:
			throw new AssertionError("Unknown family: " + family + ".");
		}
	}

	/**
	 * Downloads file content from the virtual machine.
	 *
	 * @param pathname the path of the file
	 *
	 * @return the content of the file
	 *
	 * @throws IOException          if an I/O error occurs
	 * @throws ExecutionException   if an exception occurs while waiting for the machine to become ready
	 * @throws InterruptedException if interrupted while waiting for the machine to become ready
	 */
	byte[] doDownload(String pathname)
			throws IOException, InterruptedException, ExecutionException
	{
		logger.debug("doDownload(pathname={}) [begin]", pathname);

		ensureReadyForInteraction();
		VirtualMachine vm = delegate.getDelegate();
		GuestFileManager manager = vm.getServerConnection()
				.getServiceInstance()
				.getGuestOperationsManager()
				.getFileManager(vm);

		GuestAuthentication auth = this.getAdminCredentials_in_vSphere_form();

		FileTransferInformation info = manager.initiateFileTransferFromGuest(auth, pathname);
		String url = replaceStarWithHost(info.getUrl());
		Long longSize = info.getSize();
		validState(longSize <= Integer.MAX_VALUE, "File size %s is too big.", longSize);
		int size = longSize.intValue();

		HttpGet get = new HttpGet(url);
		try (
				CloseableHttpClient client = VSphereAPIUtil.getTrustingClient();
				CloseableHttpResponse response = client.execute(get);
				ByteArrayOutputStream content = new ByteArrayOutputStream(size)
		) {
			StatusLine line = response.getStatusLine();
			int code = line.getStatusCode();
			validState(HttpStatusCodes.isSuccess(code), "Non-success response: " + line);
			HttpEntity entity = response.getEntity();
			entity.writeTo(content);
			byte[] result = content.toByteArray();
			logger.debug("doDownload(pathname={}) => byte[{}] [complete]", pathname, result.length);
			return result;
		}
	}

	/**
	 * Replaces "*" in the a string with the host from the server connection. This is needed because the file transfer APIs
	 * leave a "*" in the URLs that have to be replaced.
	 *
	 * @param string the string
	 *
	 * @return the updated string
	 */
	String replaceStarWithHost(String string)
	{
		String host = delegate.getDelegate()
				.getServerConnection()
				.getUrl()
				.getHost();
		return string.replaceFirst("\\*", host);
	}

	/**
	 * Executes a command on the virtual machine and returns the status code. This method blocks until the process is
	 * completed.
	 *
	 * @param command the command
	 *
	 * @return the exit code
	 *
	 * @throws RemoteException      if an error occurs
	 * @throws InterruptedException if interrupted while waiting for the machine to become ready
	 * @throws ExecutionException   if an exception occurs while waiting for the machine to become ready
	 */
	int doRunCommand(ProgramExecutionSpec command)
			throws RemoteException, InterruptedException, ExecutionException
	{
		logger.debug("doRunCommand({}) [begin]", command);
		ensureReadyForInteraction();
		VirtualMachine vm = delegate.getDelegate();
		GuestProcessManager manager = vm.getServerConnection()
				.getServiceInstance()
				.getGuestOperationsManager()
				.getProcessManager(vm);

		GuestAuthentication auth = delegate.getAdminCredentials_in_vSphere_form();

		GuestProgramSpec spec = new GuestProgramSpec();
		spec.setArguments(command.getArguments());
		spec.setProgramPath(command.getPath());
		spec.setEnvVariables(null);
		spec.setWorkingDirectory(command.getWorkingDirectory());

		long[] pid = {- 1};
		pid[0] = manager.startProgramInGuest(auth, spec);

		while (true) {
			GuestProcessInfo[] infos = manager.listProcessesInGuest(auth, pid);
			if (infos != null && infos.length == 1) {
				GuestProcessInfo info = infos[0];
				Integer code = info.getExitCode();
				if (code != null) {
					logger.debug("doRunCommand({}) => {} [complete]", command, code);
					return code;
				}
			}
			TimeUnit.SECONDS.sleep(2);
		}
	}

	/**
	 * Ensures that a virtual machine is ready for interaction. This means that the machine is powered on and the guest
	 * tools are available.
	 *
	 * @throws ExecutionException   if an exception occurs
	 * @throws InterruptedException if interrupted
	 */
	void ensureReadyForInteraction()
			throws InterruptedException, ExecutionException
	{
		logger.debug("ensureReadyForInteraction() for {}", delegate.getDelegate());
		VirtualMachine vm = delegate.getDelegate();
		VSphereAPIUtil.ensurePowerStateOfVirtualMachine(vm, AUVirtualMachine.PowerState.ON);
		Concurrency.await(
				() -> VSphereAPIUtil.isToolsRunning(vm),
				10,
				TimeUnit.MINUTES,
				5,
				TimeUnit.SECONDS,
				"guest tools starting on " + vm
		);
		VSphereAPIUtil.ensureGuestOperationsAvailable(vm);
	}

	/**
	 * Create a directory on the virtual machine. The pathname string should include a final separator. For instance, to
	 * create /usr/bin, the argument should be "/usr/bin/". If a directory already exists at the specified location, then an
	 * exception is thrown unless <code>succeedIfAlreadyExists</code> is true.
	 *
	 * @param directoryPath          the path
	 * @param succeedIfAlreadyExists whether to succeed if the directory already exists
	 *
	 * @throws RemoteException            if an error occurs while interacting with the remote machine
	 * @throws InterruptedException       if interrupted while waiting for the machine to become ready
	 * @throws ExecutionException         if an exception occurs while waiting for the machine to become ready
	 * @throws FileAlreadyExistsException if a file denoted by the path already exists and succeedIfAlreadyExists is false
	 */
	void doCreateDirectory(String directoryPath, boolean succeedIfAlreadyExists)
			throws RemoteException, InterruptedException, ExecutionException, FileAlreadyExistsException
	{
		logger.debug("doCreateDirectory({}. succeedIfAlreadyExists={}) [begin]", directoryPath, succeedIfAlreadyExists);
		ensureReadyForInteraction();

		Optional<GuestFileInfo> oInfo = getFileInfoOnGuest(FilenameUtils.getFullPathNoEndSeparator(directoryPath));
		if (oInfo.isPresent()) {
			GuestFileInfo info = oInfo.get();
			GuestFileType type = GuestFileType.valueOf(info.getType());
			if (type == GuestFileType.directory && succeedIfAlreadyExists) {
				logger.trace(
						"Directory ({}) already exists, and succeedIfAlreadyExists=true; succeeding.",
						directoryPath
				);
			}
			else {
				logger.trace(
						"Path ({}) already exists with type {}, failing to create directory.",
						directoryPath,
						type
				);
				throw new FileAlreadyExistsException(directoryPath);
			}
		}

		VirtualMachine vm = delegate.getDelegate();
		GuestFileManager manager = vm.getServerConnection()
				.getServiceInstance()
				.getGuestOperationsManager()
				.getFileManager(vm);
		GuestAuthentication auth = delegate.getAdminCredentials_in_vSphere_form();
		boolean createParentDirectories = true;
		try {
			logger.trace(
					"makeDirectoryInGuest(auth={}, directoryPath={}, createParentDirectories={}) [begin]",
					auth,
					directoryPath,
					createParentDirectories
			);
			manager.makeDirectoryInGuest(auth, directoryPath, createParentDirectories);
			logger.trace(
					"makeDirectoryInGuest(auth={}, directoryPath={}, createParentDirectories={}) [complete]",
					auth,
					directoryPath,
					createParentDirectories
			);
		}
		catch (RemoteException e) {
			if (Exceptions.hasCause(e, FileAlreadyExists.class)) {
				if (succeedIfAlreadyExists) {
					logger.trace(
							"Directory ({}) already exists and succeedIfAlreadyExists=true, succeeding.",
							directoryPath
					);
				}
				else {
					throw new FileAlreadyExistsException(directoryPath);
				}
			}
			else {
				throw e;
			}
		}
		logger.debug("doCreateDirectory({}, {}) [complete]", directoryPath, succeedIfAlreadyExists);
	}

	@Override
	public CompletableFuture<Integer> createDirectory(String pathname)
	{
		return CompletableFuture.supplyAsync(() -> {
			try {
				boolean succeedIfAlreadyExists = true;
				synchronized (this) {
					doCreateDirectory(pathname, succeedIfAlreadyExists);
				}
				return 0; // general "success"
			}
			catch (RemoteException | InterruptedException | ExecutionException | FileAlreadyExistsException e) {
				String message = String.format("Failed to create directory (%s). [%s]", pathname, this);
				throw new VSphereTestbedException(message, e);
			}
		}, getFileTransferExecutorService());
	}

	/**
	 * Returns an information on a file in the guest. If the file does not exist on the guest, then the result is empty.
	 * Otherwise, it contains the information about the file (which may be a directory) on the guest.
	 *
	 * @param pathname the pathname
	 *
	 * @return the information about the file
	 *
	 * @throws RemoteException if a remote exception occurs
	 */
	Optional<GuestFileInfo> getFileInfoOnGuest(String pathname)
			throws RemoteException
	{
		logger.trace("getFileInfoOnGuest({}) [begin]", pathname);
		VirtualMachine vm = delegate.getDelegate();
		GuestFileManager manager = vm.getServerConnection()
				.getServiceInstance()
				.getGuestOperationsManager()
				.getFileManager(vm);
		GuestAuthentication auth = delegate.getAdminCredentials_in_vSphere_form();
		String dirName = FilenameUtils.getName(pathname);
		String filePath = FilenameUtils.getFullPath(pathname);
		String matchPattern = "^" + dirName + "$";
		int index = 0;
		int maxResults = 1;
		logger.trace(
				"listFilesInGuest(auth={}, filePath={}, index={}, maxResults={}, matchPattern={})",
				auth,
				filePath,
				index,
				maxResults,
				matchPattern
		);
		GuestListFileInfo fileInfos = manager.listFilesInGuest(auth, filePath, index, maxResults, matchPattern);
		GuestFileInfo[] files = fileInfos.getFiles();
		Optional<GuestFileInfo> result;
		if (files == null) {
			result = Optional.empty();
		}
		else {
			validState(files.length == 1, "File list should have exactly one element, but had %s.", files.length);
			result = Optional.of(files[0]);
		}
		logger.trace("getFileInfoOnGuest({}) => {} [complete]", pathname, result);
		return result;
	}

	/**
	 * Encapsulation of things that can be used as files for uploads.
	 *
	 * @author taylorj
	 */
	private static interface FileContentHttpEntityAdapter
	{
		/**
		 * Returns the length of the file content.
		 *
		 * @return the length of the file content
		 */
		long getLength();

		/**
		 * Returns an HttpEntity containing the content.
		 *
		 * @return the HttpEntity of the content
		 */
		HttpEntity toHttpEntity();

		/**
		 * Returns an adapter based on a file. The length is the value returned by {@link File#length()}, and the {@link
		 * HttpEntity} is a {@link FileEntity}.
		 *
		 * @param file the file
		 *
		 * @return the adapter
		 */
		static FileContentHttpEntityAdapter of(File file)
		{
			return new FileContentHttpEntityAdapter()
			{
				@Override
				public long getLength()
				{
					return file.length();
				}

				@Override
				public FileEntity toHttpEntity()
				{
					return new FileEntity(file);
				}
			};
		}

		/**
		 * Returns an adapter based on a byte array. The length is length of the array, and the {@link HttpEntity} is a
		 * {@link ByteArrayEntity}.
		 *
		 * @param content the byte array
		 *
		 * @return the adapter
		 */
		static FileContentHttpEntityAdapter of(byte[] content)
		{
			return new FileContentHttpEntityAdapter()
			{
				@Override
				public ByteArrayEntity toHttpEntity()
				{
					return new ByteArrayEntity(content);
				}

				@Override
				public long getLength()
				{
					return content.length;
				}
			};
		}
	}

	private static ExecutorService getFileTransferExecutorService()
	{
		ExecutorService result = ExperimentExecutorService.getExecutorService();
		//^-- FIXME: srogers: introduce a separate server-wide ExecutorService for file transfer threads

		return result;
	}

}
