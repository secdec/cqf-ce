package com.siegetechnologies.cqf.core.experiment.design.catalog.providers;

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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileBackedExperimentDesignElementListProviderTest
{

	private FileSystem fs;

	@Before
	public void setUp() {
		fs = Jimfs.newFileSystem(Configuration.unix());
	}

	@Test
	public void testLoad_directoryNotExists() {
		FileBackedExperimentDesignElementListProvider p = new FileBackedExperimentDesignElementListProvider(fs.getPath("/items"));
		List<? extends ExperimentDesignElementImpl> items = p.getDesignElements(null);
		assertTrue("Should be no items for non-existent root.", items.isEmpty());
	}

	@Test
	public void testLoad_rootExistsButIsRegularFile() throws IOException {
		Path root = fs.getPath("/items");
		Files.createFile(root);
		FileBackedExperimentDesignElementListProvider p = new FileBackedExperimentDesignElementListProvider(root);
		assertTrue("Should be no items for existing but non-directory root.", p.getDesignElements(null).isEmpty());
	}

	@Test
	public void testLoad() throws IOException {
		Files.createDirectory(fs.getPath("/items"));
		Files.createDirectory(fs.getPath("/items/Sensor"));

		Files.createDirectory(fs.getPath("/items/Sensor/Sensor 1"));
		Files.createFile(fs.getPath("/items/Sensor/Sensor 1/config.json"));

		Files.createDirectory(fs.getPath("/items/Sensor/Sensor 2"));
		Files.createFile(fs.getPath("/items/Sensor/Sensor 2/config.json"));

		Files.createDirectory(fs.getPath("/items/Node"));

		Files.createDirectory(fs.getPath("/items/Node/Node 1"));
		Files.createFile(fs.getPath("/items/Node/Node 1/config.json"));

		// A directory but without a config.json
		Files.createDirectory(fs.getPath("/items/Node/Node 2"));
		Files.createFile(fs.getPath("/items/Node/Node 2/documentation.txt"));

		FileBackedExperimentDesignElementListProvider d = new FileBackedExperimentDesignElementListProvider(fs.getPath("/items"));
		ExperimentDesignElementIdResolver<ExperimentDesignElementImpl> repo = null;

		List<? extends ExperimentDesignElementImpl> items = d.getDesignElements(repo);
		assertEquals("In-memory file system should contain 3 items.", 3, items.size());
	}

}
