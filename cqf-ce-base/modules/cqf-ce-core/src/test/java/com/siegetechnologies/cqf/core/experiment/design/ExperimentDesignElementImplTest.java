package com.siegetechnologies.cqf.core.experiment.design;

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

import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExperimentDesignElementImplTest {

	@Mock
	private DocumentationImpl doc;

	@Mock
	private List<ParameterImpl> params;

	@Mock
	private ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> repo;

	private ExperimentDesignElementImpl item;

	static class ExperimentDesignElementTestSubject extends ExperimentDesignElementImpl {

		private final DocumentationImpl doc;
		private final List<ParameterImpl> params;

		public ExperimentDesignElementTestSubject
		(
				ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver,
				DocumentationImpl doc,
				List<ParameterImpl> params
		) {
				super(resolver);
				this.doc = doc;
				this.params = params;
		}

		@Override
		public String getName() {
			return "name";
		}

		@Override
		public DocumentationImpl getDocumentation() {
			return doc;
		}

		@Override
		public String getCategory() {
			return "category";
		}

		@Override
		public List<ParameterImpl> getOwnParameters() {
			return params;
		}
	}

	@Before
	public void init() {
		item = new ExperimentDesignElementTestSubject(repo, doc, params);
	}
	
	@Test
	public void testCompareTo() {
		// FIXME: srogers: implement testCompareTo()
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testGetFile() throws IOException {
		item.getFile("config.json");
	}

	@Test
	public void testConstructor() {
		assertSame(repo, item.resolver);
	}
	
	@Test
	public void testGetChildren() {
		assertTrue(item.getChildren().isEmpty());
	}
	
	@Test
	public void testGetDirectory() {
		assertFalse(item.getDirectory().isPresent());
	}
	
	@Test
	public void testMerge() {
		ParameterImpl superParameter = mock(ParameterImpl.class);
		ParameterImpl ownParameter = null;
		
		assertSame("Should get super when own is null.", superParameter, ExperimentDesignElementImpl.merge(superParameter, ownParameter));
		ownParameter = mock(ParameterImpl.class);
		
		ExperimentDesignElementImpl.merge(superParameter, ownParameter);
		verify(ownParameter).copyAndUpdateWithDefaultsFrom(superParameter);
	}

	@Test
	public void testMergeParameters() {
		ParameterImpl superParameter = new ParameterImpl.Builder()
				.setLabel("X")
				.setName("x")
				.setType(null)
				.setDefaultValue("p1 def")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();
		ParameterImpl ownParameter = new ParameterImpl.Builder()
				.setLabel("X")
				.setName("x")
				.setType(null)
				.setDefaultValue("p2 def")
				.setAcceptableValues(null)
				.setRequired(false)
				.build();
		List<ParameterImpl> ps = ExperimentDesignElementImpl.merge(Arrays.asList(superParameter), Arrays.asList(ownParameter));
		assertEquals("p2 def", ps.get(0).getDefaultValue());
	}

	@Test
	public void getItemId() {
		Assert.assertEquals(ExperimentDesignElementId.of("name", "category", null), item.getId());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void setVariantConfiguration() {
		item.setVariantConfiguration(null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void inheritFrom() {
		item.inheritFrom(null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void setConfig() {
		item.setConfig(null);
	}

	@Test
	public void hasConfig() {
		assertFalse(item.hasConfig());
	}

	@Test
	public void getConfig() {
		assertEquals(Optional.empty(), item.getConfig());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void setDocumentation() {
		item.setDocumentation(null);
	}

	@Test
	public void getAncestry() {
		assertEquals(Arrays.asList(item), item.getAncestry());
	}

	@Test
	public void getRequiredFiles() throws IOException {
		assertTrue(item.getRequiredFiles()
				.isEmpty());
	}

	@Test
	public void getScriptFiles() throws IOException {
		assertTrue(item.getScriptFiles()
				.isEmpty());
	}

	@Test
	public void getFiles() {
		assertTrue(item.getFiles()
				.isEmpty());
	}

	@Test
	public void getStatus() {
		assertNull(item.getStatus());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void setStatus() {
		item.setStatus(null);
	}

	@Test
	public void getResultFiles() {
		assertTrue(item.getResultFiles()
				.isEmpty());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getFile() throws IOException {
		item.getFile(null);
	}

	@Test
	public void getCompatibleWith() {
		assertTrue(item.getCompatibleWith()
				.isEmpty());
	}

	@Test
	public void getVariantName() {
		assertNull(item.getVariantName());
	}

	@Test
	public void getVariantOf() {
		assertNull(item.getVariantOf());
	}

	@Test
	public void getVariants() {
		assertTrue(item.getVariants()
				.isEmpty());
	}

}
