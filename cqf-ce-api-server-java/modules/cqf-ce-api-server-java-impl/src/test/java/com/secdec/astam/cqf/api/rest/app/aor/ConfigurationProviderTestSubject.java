package com.secdec.astam.cqf.api.rest.app.aor;

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

import com.siegetechnologies.cqf.core.util.Config;
import org.apache.commons.configuration2.CompositeConfiguration;

/**
 * @author srogers
 */
public class ConfigurationProviderTestSubject extends ConfigurationProvider
{
    @Override
    public void load_internal() {

        Config.setConfiguration(new CompositeConfiguration(/*empty*/));
        //^-- we do *not* load the configuration file during unit testing

        this.configuration = Config.getConfiguration();
        
        assert this.configuration != null;
    }

    @Override
    public void unload_internal() {

        this.configuration = null;
    }

}
