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

import java.util.Optional;

import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionTimeSlot;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import com.siegetechnologies.cqf.core.util.Enums;

/**
 * Utility object responsible for generating new paths for scripts
 * based on contextual information such as runmode, instance index
 * within parent, test plan id, and platform of the host to which
 * the file will be copied.
 */
public class ScriptRenamer {
	
	private static final Logger logger = LoggerFactory.getLogger(ScriptRenamer.class);
	
	private final String itemName;
	private final long indexInParent;
	private final OperatingSystemFamily family;
	
	/**
	 * Pattern used in {@link #doRename(String, String)}. When used as a format
	 * string, the arguments passed to it are the basename of a script file, 
	 * the renamer's index in parent, the renamer's run mode (lowercased), the 
	 * renamer's test plan, and the script's extension.
	 */
	public static final String RENAME_FORMAT = "%s-%04d-%s.%s";

	/**
	 * Creates a new ScriptRenamer.
	 * 
	 * @param indexInParent  the index of the item in its parent
	 * @param itemName the name of the item
	 * @param family the family of the testbed machine of the item
	 */
	public ScriptRenamer(long indexInParent, String itemName, OperatingSystemFamily family) {
		this.indexInParent = indexInParent;
		this.itemName = itemName;
		this.family = family;
	}
	
	/**
	 * Returns true a file with the given extension should be renamed and
	 * copied to a system with the specified platform.  Currently, the extension
	 * "bat" is compatible with the platform "Windows", and "sh" with "Unix".
	 * All other combinations are incompatible.
	 * 
	 * @param extension the extension of the file
	 * @return whether the extension is compatible
	 */
	boolean isCompatible(String extension) {
		switch (family) {
		case WINDOWS:
			return "bat".equals(extension); // FIXME: STRING: srogers
		case UNIX:
			return "sh".equals(extension); // FIXME: STRING: srogers
		default:
			throw new AssertionError("Unrecognized OS family: "+family+".");
		}
	}

	/**
	 * Returns an (optional) path to which a script file should be copied.
	 * If the script's extension is incompatible with the renamer's
	 * platform, then an empty optional is returned.  Otherwise, the
	 * script filename is optionally renamed, to produce the final
	 * filename, which is then resolved against the parent directory
	 * (if it is non-null). If the file name corresponds to one of the
	 * {@link ExecutionTimeSlot}s, then the file will be renamed. The new
	 * filename is generated using {@link #RENAME_FORMAT}.
	 *
	 * @param script the script
	 * @return the new path
	 * 
	 * @see #RENAME_FORMAT
	 */
	public Optional<String> rename(String script) {
		String extension = FilenameUtils.getExtension(script);
		if (!isCompatible(extension)) {
			return Optional.empty();
		}
		String basename = FilenameUtils.getBaseName(script);
		boolean isTimeSlot = Enums.isValueIgnoreCase(ExecutionTimeSlot.class, basename);
		String result = isTimeSlot ? doRename(basename, extension) : FilenameUtils.getName(script);
		logger.trace("renamed: {} => {}", script, result);
		return Optional.of(result);
	}
	
	/**
	 * Returns the result of formatting {@link #RENAME_FORMAT} with 
	 * basename, the index in parent, the run mode, the test plan, and
	 * extension.
	 * 
	 * @param basename the basename of the script
	 * @param extension the extension of the script
	 * @return the renamed script name
	 */
	private final String doRename(String basename, String extension) {
		return String.format(RENAME_FORMAT, basename, indexInParent, itemName, extension);
	}
}
