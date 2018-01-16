package com.siegetechnologies.cqf.core.util;

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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

public class TemporaryFileTest {
	
	@Test
	public void testCreate() throws IOException {
		Path path = null;
		try {
			path = TemporaryFiles.create("foo.txt");
			assertEquals("foo.txt", path.getFileName().toString());
		}
		finally {
			if (path != null) {
				path.toFile().delete();
			}
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreate_rejectComplex() throws IOException {
		TemporaryFiles.create("foo/bar.txt");
	}

}
