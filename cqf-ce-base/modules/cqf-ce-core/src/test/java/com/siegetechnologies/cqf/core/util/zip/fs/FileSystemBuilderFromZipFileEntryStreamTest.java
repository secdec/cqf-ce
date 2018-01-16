package com.siegetechnologies.cqf.core.util.zip.fs;

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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.FileBackedExperimentDesignElementListProvider;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.FileBackedExperimentDesignElementListProvider.Element;
import org.junit.Test;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author srogers
 */
public class FileSystemBuilderFromZipFileEntryStreamTest {

	@Test
	public void testBuilding_simpleDirectoryTree() throws Exception {

		String host = "tester01";
		String top_pnp = "/var/cqf/design/items/";

		String zipFileResourceName = "zip-files/a-b-b1.b2.b3-c-c1.c2.c3-c31-d.zip";

		ClassLoader this_classLoader = this.getClass().getClassLoader();

		FileSystemBuilderFromZipFileEntryStream processor =
				new FileSystemBuilderFromZipFileEntryStream(host, top_pnp);

		ZipFileEntryStreamProcessing.scan(zipFileResourceName, this_classLoader, processor);


		FileSystem fs = processor.result();
		assertNotNull(fs);

		Path top = fs.getPath(top_pnp);
		assertNotNull(top);

		URI top_uri = top.toUri();
		assertEquals("jimfs://"+host+top_pnp, top_uri.toString());

		ExperimentDesignElementIdResolver<Element> resolver = new MockFileBackedExperimentDesignElementIdResolver();
		FileBackedExperimentDesignElementListProvider fbip = new FileBackedExperimentDesignElementListProvider(top);
		assertEquals(0, fbip.getDesignElements(resolver).size());
	}

	@Test
	public void testBuilding_itemsDirectoryTree() throws Exception {

		String host = "tester02";
		String top_pnp = "/var/cqf/design/items/";

		String zipFileResourceName =
				"catalogs/astam-cqf-ce-items-1.0.0-SNAPSHOT.sample.zip";

		ClassLoader this_classLoader = this.getClass().getClassLoader();

		FileSystemBuilderFromZipFileEntryStream processor =
				new FileSystemBuilderFromZipFileEntryStream(host, top_pnp);

		ZipFileEntryStreamProcessing.scan(zipFileResourceName, this_classLoader, processor);

		FileSystem fs = processor.result();
		assertNotNull(fs);

		Path top = fs.getPath(top_pnp);
		assertNotNull(top);

		URI top_uri = top.toUri();
		assertEquals("jimfs://"+host+top_pnp, top_uri.toString());

		ExperimentDesignElementIdResolver<Element> resolver = new MockFileBackedExperimentDesignElementIdResolver();
		FileBackedExperimentDesignElementListProvider fbip = new FileBackedExperimentDesignElementListProvider(top);
		fbip.getDesignElements(resolver).forEach(x -> System.out.print(x));
		// Checks the number of items in the *items resolver.  If items
		// are added or removed, this number will need to be adjusted.
		assertEquals(21, fbip.getDesignElements(resolver).size());
	}

}
