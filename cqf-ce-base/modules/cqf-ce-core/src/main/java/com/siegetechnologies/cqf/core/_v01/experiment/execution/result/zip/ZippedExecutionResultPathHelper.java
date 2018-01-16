package com.siegetechnologies.cqf.core._v01.experiment.execution.result.zip;

/*-
 * #%L
 * cqf-ce-core
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
 * Path parser helper for V0 result files
 */
public class ZippedExecutionResultPathHelper {
	private String[] tokens;

	/**
	 * Enumeration of symbolic indices into the array of tokens obtained by
	 * splitting paths of files within individual data results.
	 *
	 * @author taylorj
	 */
	private enum TokenIndex {
		START(0), WORKSPACE(1), WORKSPACE_INDEX(2), NODE(3), NODE_INDEX(4), SOFTWARE(5), SOFTWARE_INDEX(5);

		private final int index;

		TokenIndex(int index) {
			this.index = index;
		}

		/**
		 * Returns the index value of this TokenIndex.
		 * 
		 * @return the index value
		 */
		public int toIndex() {
			return this.index;
		}
	}

	private ZippedExecutionResultPathHelper(String path) {
		this.tokens = path.split("/");
	}

	// Write save/get template functions
	public String getNode() {
		int offset = 0;
		// Need at least 5 directory levels to get a node name
		if (tokens.length < 5) {
			return null;
		}
		if (tokens.length < 5 + offset) {
			return null;
		}
		return String.format("%s-%s",
				tokens[TokenIndex.NODE.toIndex() + offset],
				tokens[TokenIndex.NODE_INDEX.toIndex() + offset]);
	}

	public String getNodeUuid() {
		return extractUuid(getNode());
	}

	public String getSoftware() {
		int offset = 0;
		if (tokens.length < 6) {
			return null;
		}
		if (tokens.length < 6 + offset) {
			return null;
		}
		return tokens[TokenIndex.SOFTWARE.toIndex()];
	}

	public String getSoftwareUuid() {
		return extractUuid(getSoftware());
	}

	public String getWorkspace() {
		if (tokens.length < 2) {
			return null;
		}
		tokens = tokens[TokenIndex.WORKSPACE.toIndex()].split("Util-Workspace-");
		if (tokens.length < 2) {
			return null;
		}
		return tokens[1];
	}

	public String getWorkspaceUuid() {
		return extractUuid(getWorkspace());
	}

	/**
	 * Given a name of the form <Category>-<Name>-<UUID> extract the uuid part.
	 * 
	 * @param string ExperimentDesignElement identifier
	 * @return extracted uuid
	 */
	private String extractUuid(String string) {
		return string.split("-", 3)[2];
	}

	/**
	 * Returns a path helper for a path.
	 * 
	 * @param path the path
	 * @return the path helper
	 */
	public static ZippedExecutionResultPathHelper parse(String path) {
		return new ZippedExecutionResultPathHelper(path);
	}
}
