package com.siegetechnologies.cqf.core.experiment.design.variant;

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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cbancroft on 12/3/16.
 */
public class VariantGeneratorBasedOnElementRequiredFileVersionsTest {
	
	private static final Logger logger = LoggerFactory.getLogger(VariantGeneratorBasedOnElementRequiredFileVersionsTest.class);

    @Test
    public void generate() throws Exception {
        String testName = "Windows/windowspatch-cqfversion-KB912883-1.2.123.sh";
        String versionName = testName.split("-cqfversion-|-cqfsubversion-")[1];
        logger.debug("Version name is: {}.", versionName);
        if( versionName.matches(".*\\.[a-zA-Z]{2,}$"))
        {
        	logger.debug("Have a nonversion suffix");
            String replaced = versionName.replaceFirst("\\.[a-zA-Z]{2,}$", "");
            logger.debug("replaced: {}", replaced);
        }

    }

}
