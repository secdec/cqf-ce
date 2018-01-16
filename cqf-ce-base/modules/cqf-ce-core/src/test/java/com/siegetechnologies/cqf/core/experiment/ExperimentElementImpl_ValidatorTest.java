package com.siegetechnologies.cqf.core.experiment;

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

import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExperimentElementImpl_ValidatorTest
{
	
	@Test
	public void testReportEmpty() {
		ExperimentElementImpl.Validator.Report report = new ExperimentElementImpl.Validator.Report();
		assertEquals(0, report.getWarnings().size());
		assertEquals(0, report.getWarnings().size());
	}
	
	/**
	 * A null value in the a parameters map should produce a warning, 
	 * but no errors, and should not throw an exception.
	 */
	@Test
	public void validatorShouldAllowNullValues() {
		ExperimentElementImpl.Validator iv = new ExperimentElementImpl.Validator();
		ExperimentElementImpl.Validator.Report report = new ExperimentElementImpl.Validator.Report();
		
		iv.checkSubstitutionParameter("id", "item", "key", null, report);
		
		assertEquals(1, report.getWarnings().size());
		assertEquals(0, report.getErrors().size());
	}
	
	@Test
	public void validatorShouldDetectForbiddenValues() {
		ExperimentElementImpl.Validator iv = new ExperimentElementImpl.Validator();
		ExperimentElementImpl.Validator.Report report = new ExperimentElementImpl.Validator.Report();
		
		String forbiddenValue = ExperimentElementImpl.Validator.FORBIDDEN_SUBSTITUTION_PARAMETERS.iterator().next();
		
		iv.checkSubstitutionParameter("id", "item", "key", forbiddenValue, report);
		
		assertEquals(0, report.getWarnings().size());
		assertEquals(1, report.getErrors().size());
	}

}
