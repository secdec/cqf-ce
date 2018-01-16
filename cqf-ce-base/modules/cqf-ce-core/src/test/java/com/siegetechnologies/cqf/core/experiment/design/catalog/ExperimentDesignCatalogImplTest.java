package com.siegetechnologies.cqf.core.experiment.design.catalog;

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
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImplBase;
import com.siegetechnologies.cqf.core.experiment.design.catalog.providers.FileBackedExperimentDesignElementListProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the item catalog. ExperimentDesignElement catalog is a rather specific combination of
 * functionality, and is built of out components that have themselves already
 * been tested fairly extensively. The test here sets up just enough of an
 * environment for a catalog to show what it can do, and checks that it actually
 * does.
 * 
 * @author taylorj
 */
public class ExperimentDesignCatalogImplTest {

	private FileSystem fs;

	@Before
	public void setUp() throws IOException {
		fs = Jimfs.newFileSystem(Configuration.unix());

		Files.createDirectory(fs.getPath("/A"));
		Files.createDirectory(fs.getPath("/A/a"));
		Files.createFile(fs.getPath("/A/a/config.json"));

		Files.createDirectory(fs.getPath("/B"));
		Files.createDirectory(fs.getPath("/B/b"));
		Files.createFile(fs.getPath("/B/b/config.json"));

		Files.createDirectory(fs.getPath("/C"));

		Files.createDirectory(fs.getPath("/D"));
	}

	private ExperimentDesignElementImpl newDesignElement(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver, String name, String category) {
		return new ExperimentDesignElementImplBase(resolver, name, category);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCatalog() {
		FileBackedExperimentDesignElementListProvider p01 =
				new FileBackedExperimentDesignElementListProvider(fs.getPath("/A"));
		FileBackedExperimentDesignElementListProvider p02 =
				new FileBackedExperimentDesignElementListProvider(fs.getPath("/B"));
		FileBackedExperimentDesignElementListProvider p03 =
				new FileBackedExperimentDesignElementListProvider(fs.getPath("/C"));
		FileBackedExperimentDesignElementListProvider p04 =
				new FileBackedExperimentDesignElementListProvider(fs.getPath("/D"));

		ExperimentDesignElementListProvider<ExperimentDesignElementImpl> p05 =
				mock(ExperimentDesignElementListProvider.class);

		Collection<ExperimentDesignElementListProvider<? extends ExperimentDesignElementImpl>> subProviders =
				Arrays.asList(p01, p02, p03, p04, p05);

		ExperimentDesignCatalogImpl dc01 = new ExperimentDesignCatalogImpl(subProviders);

		ExperimentDesignElementId de01a_id = ExperimentDesignElementId.of("a", "A", null);

		ExperimentDesignElementImpl de05a = newDesignElement(dc01, "de05a", "II");
		ExperimentDesignElementImpl de05b = newDesignElement(dc01, "de05b", "II");
		when(p05.getDesignElements(dc01)).thenReturn(Arrays.asList(de05a, de05b));

		assertTrue(dc01.getItems().isEmpty());
		assertFalse(dc01.resolve(de01a_id).isPresent());

		dc01.reloadItems();

		assertTrue(dc01.resolve(de01a_id).isPresent());
		assertEquals(4, dc01.getItems().size());

	}

}
