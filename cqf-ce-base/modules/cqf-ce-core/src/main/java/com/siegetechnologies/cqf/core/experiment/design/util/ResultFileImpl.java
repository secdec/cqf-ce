package com.siegetechnologies.cqf.core.experiment.design.util;

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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Standard implementation of result files.
 * 
 * @author taylorj
 */
public class ResultFileImpl {

	private final String hostPath;
	private final String resultPath;
	private final String platform;
	private byte[] content;

	/**
	 * Creates a new result file with specified fields.
	 * 
	 * @param platform the platform
	 * @param hostPath the host path
	 * @param resultPath the result path
	 */
	@JsonCreator
	public ResultFileImpl(@JsonProperty("platform") String platform, @JsonProperty("target_file") String hostPath,
	                      @JsonProperty("local_destination") String resultPath) {
		this.hostPath = Objects.requireNonNull(hostPath, "hostPath must not be null");
		this.resultPath = resultPath != null ? resultPath : FilenameUtils.getName(hostPath);
		this.platform = platform != null ? platform : "NULL";
	}

	/**
	 * Returns the path on the host that the result file is located. This is
	 * probably platform specific, and should be interpreted in light of the
	 * result of {@link #getPlatform()}.
	 *
	 * @return the host path of the result file
	 */
	@JsonProperty("target_file")
	public String getHostPath() {
		return hostPath;
	}

	/**
	 * Returns the path at which retrieved files should be stored. This is
	 * typically just a filename. This provides a mechanism for renaming files
	 * upon retrieval. For instance, a retrieved "/cqf/cqf.log" might be renamed
	 * by specifying a result path of "cqflinux.log".
	 *
	 * @return the result path
	 */
	@JsonProperty("local_destination")
	public String getResultPath() {
		return resultPath;
	}

	/**
	 * Returns the name of the result file. 
	 *
	 * @return the name of the result file
	 */
	public String getResultName() {
		return FilenameUtils.getName(resultPath);
	}

	/**
	 * Sets the result file content. 
	 *
	 * @param value
	 */
	public void setContent(byte[] value) {
		if (content != null) {
			throw new IllegalStateException("content has already been set");
		}
		this.content = value;
	}

	/**
	 * Returns byte stream of result file content. 
	 *
	 * @return byte stream of result file content
	 * @throws IOException
	 */
	public InputStream getContentInputStream() throws IOException {
		if (content == null) {
			throw new IllegalStateException("content has not been set");
		}
		return new ByteArrayInputStream(content);
	}

	/**
	 * Returns the applicable platform for the result file. This is typically a
	 * string like "Unix" or "Windows", and indicates that the file should only
	 * be retrieved from systems of that platform.
	 *
	 * @return the result file's platform
	 */
	@JsonProperty("platform")
	public String getPlatform() {
		return platform;
	}

	@Override
	public int hashCode() {
		return Objects.hash(platform, hostPath, resultPath);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ResultFileImpl) {
			ResultFileImpl that = (ResultFileImpl) o;
			return Objects.equals(this.getPlatform(), that.getPlatform())
					&& Objects.equals(this.getHostPath(), that.getHostPath())
					&& Objects.equals(this.getResultPath(), that.getResultPath());
		}

		return false;
	}

	@Override
	public String toString() {
		return String.format("ResultFile(hostPath=%s, resultPath=%s)", getHostPath(), getResultPath());
	}
}
