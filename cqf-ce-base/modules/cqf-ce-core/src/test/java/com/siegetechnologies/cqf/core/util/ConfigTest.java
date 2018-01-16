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

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigTest {
    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(ConfigTest.class);
    
    private Configuration configuration;
    
    /**
     * Get the configuration.
     */
    @Before
    public void initConfig() {
        this.configuration = Config.getConfiguration();
    }
    
    /**
     * Checks the number of keys in the test config file.
     * This is just a sanity check to ensure that the file
     * hasn't changed while we're not looking.
     */
    @Test
    public void testNumberOfKeys() {
        Stream<?> keys = StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator<?>)configuration.getKeys(), Spliterator.ORDERED),false);
        Assert.assertEquals("Unexpected number of keys.", 28, keys.count());
    }
    
    /**
     * Check expected CQF item directories
     */
    @Test
    public void testCqfItemDirectories() {
        String cqfPrefix = "/home/user/cqf/cqf-webapp/items";
        List<?> itemDirectories = configuration.getList("cqf.itemDirectories.directory");
        List<String> expectedItemDirectories = Stream.of("bar","baz","quux").map(name -> cqfPrefix+"/"+name).collect(Collectors.toList());
        Assert.assertEquals(expectedItemDirectories, itemDirectories);
    }
    
    /**
     * Check expected CMP item directories
     */
    @Test
    public void testCmpItemDirectories() {
        String cmpPrefix = "/home/user/cqf/cmp-webapp/items";
        List<?> itemDirectories = configuration.getList("cmp.itemDirectories.directory");
        List<String> expectedItemDirectories = Stream.of("bar","baz").map(n -> cmpPrefix+"/"+n).collect(Collectors.toList());
        Assert.assertEquals(expectedItemDirectories, itemDirectories);
    }
    
    /**
     * Check expected CMP parse directories
     */
    @Test
    public void testCmpParseDirectory() {
        String parseDirectory = configuration.getString("cmp.parseDirectory");
        String expected = "/home/user/cqf/cmp-webapp/parse";
        Assert.assertEquals(expected, parseDirectory);
    }

}
