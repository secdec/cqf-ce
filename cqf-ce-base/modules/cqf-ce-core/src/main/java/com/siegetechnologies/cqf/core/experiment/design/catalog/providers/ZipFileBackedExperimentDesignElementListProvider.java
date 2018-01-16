package com.siegetechnologies.cqf.core.experiment.design.catalog.providers;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-impl
 * %%
 * Copyright (C) 2016 - 2017 Applied Visions, Inc.
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

import com.siegetechnologies.cqf.core.util.zip.fs.FileSystemBuilderFromZipFileEntryStream;
import com.siegetechnologies.cqf.core.util.zip.fs.ZipFileEntryStreamProcessing;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.FileBackedExperimentDesignElementListProvider;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author srogers
 */
public class ZipFileBackedExperimentDesignElementListProvider extends FileBackedExperimentDesignElementListProvider
{
	/**
	 * Creates a new object of this type.
	 *
	 * @param zipFileResourceName
	 */
	public ZipFileBackedExperimentDesignElementListProvider(String zipFileResourceName) throws IOException {

		initRoot(newDesignItemRootFromZipFileResource(zipFileResourceName));
	}

	private Path newDesignItemRootFromZipFileResource(String zipFileResourceName) throws IOException {

		String zipFileResourceStem = zipFileResourceName.replaceAll("\\.zip$", "");

		String fs_host = newDesignItemFileSystemHostName();
		String top_pnp = "/var/cqf/design/" + zipFileResourceStem + ".d/";

		FileSystemBuilderFromZipFileEntryStream processor =
				new FileSystemBuilderFromZipFileEntryStream(fs_host, top_pnp);

		ClassLoader this_classLoader = this.getClass().getClassLoader();

		ZipFileEntryStreamProcessing.scan(zipFileResourceName, this_classLoader, processor);

		FileSystem fs = processor.result();
		assert fs != null;

		Path result = fs.getPath(top_pnp);
		assert result != null;

		return result;
	}
	//^-- FIXME: srogers: rig a hook to explicitly close hosting filesystem when catalog is unloaded

	String newDesignItemFileSystemHostName() {

		String result = "cqf-design-item-file-system-host-" + designItemFileSystemHostSerialNumber.getAndIncrement();
		return result; // must be unique; otherwise JIMFS will punt
	}

	private static final AtomicLong designItemFileSystemHostSerialNumber = new AtomicLong(1);

}
