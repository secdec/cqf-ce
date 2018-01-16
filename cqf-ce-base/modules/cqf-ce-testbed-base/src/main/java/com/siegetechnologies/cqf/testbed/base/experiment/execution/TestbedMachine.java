package com.siegetechnologies.cqf.testbed.base.experiment.execution;

/*-
 * #%L
 * cqf-ce-testbed-base
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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.ProgramExecutionSpec;
import org.apache.commons.io.FileUtils;

/**
 * Interface for testbed machines. Testbed machines generalize the concept of
 * virtual machines, physical machines, and so on.
 *
 * @author taylorj
 */
public interface TestbedMachine extends AutoCloseable {
	/**
	 * Returns the family of the operating system on the testbed machine.
	 * 
	 * @return the family of the operating system
	 */
	OperatingSystemFamily getFamily();

	/**
	 * Upload file content to a file on the testbed machine. The completable
	 * future provides no value on success, but may complete exceptionally if an
	 * error occurs.
	 * 
	 * @param content the content of the file
	 * @param permissions the UNIX style permissions
	 * @param pathname the pathname on the testbed machine
	 * @return a void completeable future
	 */
	CompletableFuture<Void> upload(byte[] content, int permissions, String pathname);
	
	/**
	 * Uploads the content of a file to a file on the testbed machine.  The 
	 * default method reads the content of the file into a byte array and 
	 * then calls {@link #upload(byte[], int, String)}.  Implementations are 
	 * encouraged to provide more efficient methods, if possible.
	 * 
	 * @param file the file
	 * @param permissions the UNIX style permissions
	 * @param pathname the pathname on the testbed machine
	 * @return a void completable future
	 */
	default CompletableFuture<Void> upload(File file, int permissions, String pathname) {
		byte[] content;
		try {
			content = FileUtils.readFileToByteArray(file);
		}
		catch (IOException e) {
			CompletableFuture<Void> result = new CompletableFuture<>();
			result.completeExceptionally(e);
			return result;
		}
		return upload(content, permissions, pathname);
	}

	/**
	 * Download content of file from a testbed machine.
	 * 
	 * @param pathname the pathname of the file on the testbed machine
	 * @return the content of the file
	 */
	CompletableFuture<byte[]> download(String pathname);

	/**
	 * Creates a directory on the testbed machine. The default implementation
	 * calls {@link OperatingSystemFamily#getCreateDirectoryCommand(String)} on
	 * the result of {@link #getFamily()} to produce a command passed to
	 * {@link #runCommand(ProgramExecutionSpec)}. Implementations may provide
	 * more efficient or robust mechanisms for creating directories.
	 * 
	 * @param pathname the pathname
	 * @return the result of the command
	 */
	default CompletableFuture<Integer> createDirectory(String pathname) {
		return runCommand(getFamily().getCreateDirectoryCommand(pathname));
	}

	/**
	 * Launches a command on the testbed machine. The future contains the exit
	 * code of the process if it completed successfully and completes
	 * exceptionally if an exception occurs.
	 * 
	 * @param specification the command specification
	 * @return a future of the command exit code
	 */
	CompletableFuture<Integer> runCommand(ProgramExecutionSpec specification);
	
	/**
	 * Close the testbed machine.
	 */
	@Override
	void close() throws IOException;
}
