package com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.util.copying;

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

import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.util.Enums;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.TestbedMachine;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A temporary class for encapsulating the process of copying files to a testbed
 * machine for the default handler.
 *
 * @author taylorj
 */
public class FileCopier {

	private static final Logger logger = LoggerFactory.getLogger(FileCopier.class);

	private static final String CQF_VERSION = "-cqfversion-"; // FIXME: STRING: srogers
	private static final String CQF_SUBVERSION = "-cqfsubversion-"; // FIXME: STRING: srogers

	/**
	 * Unix CQF directory
	 */
	public static final String CQF_DIRECTORY_UNIX = "/cqf/"; // FIXME: STRING: srogers // BUG: srogers: it is called something else now

	/**
	 * Windows CQF directory
	 */
	public static final String CQF_DIRECTORY_WINDOWS = "c:\\cqf\\"; // FIXME: STRING: srogers

	/**
	 * Returns an entry whose key is a local source and whose value is a remote
	 * destination for a provided required file.  If the required file is such
	 * that it should not be copied to the remote host, then the result is empty.
	 *
	 * @param requiredFile the required file
	 * @param platform the platform
	 * @param version the version
	 * @param subversion the subversion
	 * @param directory the directory
	 * @param convertPath a path mapping function
	 * @return an entry
	 */
	static Optional<Map.Entry<String,String>> getRequiredFileMapping(
			String requiredFile,
			String platform,
			String version,
			String subversion,
			String directory,
			FileSystem localFilesystem,
			Function<String, String> convertPath) {
    	// Get the name of the file. E.g., "Windows/run.bat" => "run.bat".
    	// If the requiredFile is (mistakenly) a directory (e.g., "Windows/", then
    	// the name is the empty string.
    	String name = FilenameUtils.getName(requiredFile);
    	if ("".equals(name)) {
    		logger.trace("Ignoring required file with empty name: '{}'.", requiredFile);
    		return Optional.empty();
    	}
    	// Get the path of the requiredFile.  E.g., "Windows/run.bat" => "Windows"
    	// and "Unix\\run.sh" => "Unix".  Direction of the slashes doesn't matter.
    	// If there is no path before the filename, path is the empty string.
    	String path = FilenameUtils.getPathNoEndSeparator(requiredFile);

    	// If there's a non-empty path, and the path is not the same as the platform,
    	// then we skip this required file: it's not for this platform.
    	if (!"".equals(path) && !Objects.equals(path,platform)) {
			logger.trace("Skipping '{}' for platform '{}'.", requiredFile, platform);
			return Optional.empty();
    	}
    	// The list of required files looks like ["common.txt", "Windows",
    	// "Windows/win.bat" "Unix", "Unix/unix.sh"], with the directory names
    	// appearing as entries.  We want to skip the entries where the "name"
    	// is actually the directory naming a platform.
    	if ("".equals(path) && isPlatform(name)) {
    		logger.trace("Ignoring platform directory '{}'.", name);
    		return Optional.empty();
    	}

    	if (requiredFile.contains(CQF_VERSION) && !requiredFile.contains(CQF_VERSION+version)) {
    		logger.trace("Ignoring version mismatch {} - {}", name, version);
    		return Optional.empty();
    	}

    	if (requiredFile.contains(CQF_SUBVERSION) && !requiredFile.contains(CQF_SUBVERSION+subversion)) {
    		logger.trace("Ignoring subversion mismatch {} - {}", name, subversion);
    		return Optional.empty();
    	}

    	// Otherwise, convert the required file path to use system specific
    	// slashes, and install the mapping.  Note that FilenameUtils#concat()
    	// normalizes the result for the local system, so localSource is always
    	// in the right format. The remove destination must be converted,
    	// however, to be sure that it's correct for the remote system.

    	String localSource = localFilesystem.getPath("RequiredFiles", requiredFile ).toString();
		String remoteDestination = convertPath
				.apply(FilenameUtils.concat(directory, name.replace(CQF_VERSION + version, "") // FIXME: srogers: extract string construction into a dedicated method
						.replace(CQF_SUBVERSION + subversion, ""))); // FIXME: srogers: extract string construction into a dedicated method
    	logger.trace("Installing required file mapping {} => {}.", localSource, remoteDestination);
    	Map.Entry<String,String> entry = new AbstractMap.SimpleImmutableEntry<>(localSource, remoteDestination);
    	return Optional.of(entry);
	}

	/**
	 * Returns true if a string designates a platform.  The string designates
	 * a platform if it is either "Windows" or "Unix".
	 *
	 * @param string the string
	 * @return whether the string designates a platform
	 */
	static boolean isPlatform(String string) {
		return "Windows".equals(string) || "Unix".equals(string); // FIXME: STRING: srogers
	}

	/**
	 * Copy the files from the item into the VM created by the parent.
	 * Authentication information is taken from the parent instance's
	 * parameters, which should include administrator credentials.
	 *
	 * Note that the adminusername and adminpassword require genuine
	 * accounts on the host.  VMwareTools does not use sudo
	 * or su to access administrator accounts.  On Unix systems where
	 * the root account is not enabled, it may be necessary to first
	 * use <code>sudo passwd root</code> to set a password for the
	 * root account, and then <code>sudo passwd -u root</code> to
	 * enable root logins.
	 *
	 * @param context the instance context
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public static void copyFilesToTestbed(ExperimentElementExecutionContext context) throws IOException {

		logger.trace("copyFilesToTestbed({})", context);

	    ExperimentDesignElementImpl designElement = context.getExperimentElement().getDesign();

	    ExperimentElementExecutionContext parentContext = context.getContext().orElseThrow(() ->
	    new IllegalArgumentException("No parent context."));

	    if (!context.getResult().isPresent()) {
	      throw new NoSuchElementException("No parent result while copying files for context: designElement="+ designElement +", context="+context);
	    }

	    TestbedMachine tm = context.getResult(TestbedMachine.class);
	    OperatingSystemFamily family = tm.getFamily();

	    String platform = parentContext.getInstanceParameter("platform"); // FIXME: STRING: srogers

	    String version = context.getInstanceParameter("version"); // FIXME: STRING: srogers
	    String subversion = context.getInstanceParameter("subversion"); // FIXME: STRING: srogers

		Function<String,String> convertPath = tm.getFamily()::separatorsToFamily;

		String directory = FileCopier.getCqfDirectory(tm.getFamily());

	    // Create the directory.  Later, when we copy the files into the
	    // directory, we just concatenate the paths.
	    try {
			tm.createDirectory(getCqfDirectory(tm.getFamily())).get();
		}
		catch (InterruptedException | ExecutionException e) {
			throw new IOException("Could not create directory.", e);
		}

	    Predicate<String> isTextFileFilename = path -> {
	        String filename = FilenameUtils.getName(path);
	        String extension = FilenameUtils.getExtension(filename);
	        return Enums.isValueIgnoreCase(TextFileExtensions.class, extension);
	    };

	    /*
	     * Get the list of paths to the required files.  These are resource
	     * paths, so we can use the servlet context to get the corresponding
	     * required files.
	     */
	    Map<String,String> pathMappings = new HashMap<>();

	    List<String> requiredFiles = designElement.getRequiredFiles();

	    for (String requiredFile : requiredFiles) {
			getRequiredFileMapping(requiredFile, platform, version, subversion, directory, designElement.getDirectory().get().getFileSystem(),
					convertPath)
					.ifPresent(e -> pathMappings.put(e.getKey(), e.getValue()));
	    }

	    ScriptRenamer renamer = new ScriptRenamer(context.getIndexInParent(), context.getExperimentElement().getDesign().getName(), family);
	    for (String scriptFile : designElement.getScriptFiles()) {
	        // We only want to copy files recognized as text files
	        // over as scripts.  See the TextFileExtensions enumeration
	        // for the full list.
	        if (isTextFileFilename.test(scriptFile)) {
	        	renamer.rename(scriptFile)
	        		.map(filename -> directory+filename)
	        		.map(convertPath)
	        		.ifPresent(scrFile -> pathMappings.put("Scripts/"+scriptFile, scrFile)); // FIXME: srogers: extract string construction into a dedicated method
	        }
	        else {
	            logger.warn("Ignoring script file with non-text file extension: {}", scriptFile);
	        }
	    }

	    logger.trace("* pathMappings: {}", pathMappings);

	    /*
	     * Now, get an HTTP client, iterate through the resource paths, and for
	     * each one, load its contents into a buffer, setup a file transfer, and
	     * send the content over to the server.  It's not great that there's
	     * not much error checking here, but that can be added in later if we
	     * experience problems.
	     */
	    String itemDirectory = designElement.getDirectory().get().toString();
	    for ( Map.Entry<String, String> entry : pathMappings.entrySet() ) {
	    	String source = entry.getKey();
	    	String destination = entry.getValue();
	    	String filename = FilenameUtils.getName(destination);
	    	String extension = FilenameUtils.getExtension(filename);
	    	logger.trace("{} => {}",source,destination);


	    	boolean isTextFile = isTextFileFilename.test(filename);
	    	boolean isExecutable = Enums.isValueIgnoreCase(ExecutableFileExtensions.class, extension);

				String resourcePath = designElement.getDirectory().get().resolve(source).toString();
	    	java.nio.file.Path resource = designElement.getDirectory().get().getFileSystem().getPath(resourcePath);

				// See older versions of this code that use makeDirectoryInGuest, etc.

				// The file content is either the raw file, if it's not a text file, or
				// a byte array if it is a text file, since substitution must be applied.
	    	byte[] content;
	    	if( !isTextFile ) {
	    		content = Files.readAllBytes( resource );
				}
				else {
					try (InputStream input = Files.newInputStream(resource);
							ByteArrayOutputStream output = new ByteArrayOutputStream()) {
						ParameterTextSubstitution.replacingCopy(input, output, context.getContextSubstitutor());
						content = output.toByteArray();
					}
				}

	    	int permissions = isExecutable ? 0777 : 0666;
	    	CompletableFuture<Void> upload;
	    	upload = tm.upload(content, permissions, destination);
	    	try {
				upload.get();
			}
			catch (InterruptedException | ExecutionException e) {
				throw new IOException("Failed to upload.", e);
			}
	    }
	}

	/**
	 * General representation of file content, as either
	 * an array of bytes, or a file.
	 *
	 * @author taylorj
	 */
	interface BytesOrFile {
		/**
		 * Returns true if the representation is an array of bytes.
		 *
		 * @return true if the representation is bytes
		 */
		boolean isBytes();

		/**
		 * Returns the bytes
		 *
		 * @return the bytes
		 */
		byte[] getBytes();

		/**
		 * Returns the file.
		 *
		 * @return the file
		 */
		File getFile();

		/**
		 * Returns a string representation of the objectg.
		 *
		 * @param bof the object
		 * @return the string representation
		 */
		static String toString(BytesOrFile bof) {
			if (bof.isBytes()) {
				return String.format("BytesOrFile.ofBytes(byte[%d])", bof.getBytes().length);
			}
			else {
				return String.format("BytesOrFile.ofFile(%s)", bof.getFile());
			}
		}

		/**
		 * Returns an instance for a file.
		 *
		 * @param file the file
		 * @return the instance
		 */
		static BytesOrFile ofFile(File file) {
			return new BytesOrFile() {
				@Override
				public String toString() {
					return BytesOrFile.toString(this);
				}

				@Override
				public boolean isBytes() { return false; }

				@Override
				public File getFile() { return file; }

				@Override
				public byte[] getBytes() { throw new IllegalStateException(); }
			};
		}

		/**
		 * Returns an instance for a byte array.
		 *
		 * @param bytes the bytes
		 * @return the instance
		 */
		static BytesOrFile ofBytes(byte[] bytes) {
			return new BytesOrFile() {
				@Override
				public String toString() {
					return BytesOrFile.toString(this);
				}

				@Override
				public boolean isBytes() { return true; }

				@Override
				public File getFile() { throw new IllegalStateException(); }

				@Override
				public byte[] getBytes() { return bytes; }
			};
		}
	}

	/**
	 * Returns the CQF directory for an operating system family.
	 * The value is either {@link #CQF_DIRECTORY_UNIX} or
	 * {@link #CQF_DIRECTORY_WINDOWS}, depending on the family.
	 *
	 * @param family the family
	 * @return the CQF directory
	 */
	static final String getCqfDirectory(OperatingSystemFamily family) {
		switch (family) {
		case UNIX:
			return CQF_DIRECTORY_UNIX;
		case WINDOWS:
			return CQF_DIRECTORY_WINDOWS;
		default:
			throw new AssertionError("Unknown OperatingSystemFamily: "+family+".");
		}
	}

}
