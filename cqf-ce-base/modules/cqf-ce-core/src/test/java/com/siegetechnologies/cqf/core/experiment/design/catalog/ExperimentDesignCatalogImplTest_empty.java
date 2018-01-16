package com.siegetechnologies.cqf.core.experiment.design.catalog;

/*-
 * #%L
 * astam-cqf-ce-core
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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author srogers
 */
public class ExperimentDesignCatalogImplTest_empty {

    @Before
    public void setUp() throws Exception {

        this.sut = new ExperimentDesignCatalogImpl();
    }

    @After
    public void tearDown() throws Exception {

        this.sut = null;
    }

    protected ExperimentDesignCatalogImpl sut; // subject under test

    @Test
    public void loadItems() throws Exception {

        assertEquals(0, sut.getItems().size());

        sut.loadItems();
        assertEquals(0, sut.getItems().size());
    }

    @Test
    public void unloadItems() throws Exception {

        assertEquals(0, sut.getItems().size());

        sut.unloadItems();
        assertEquals(0, sut.getItems().size());
    }

    @Test
    public void reloadItems() throws Exception {

        assertEquals(0, sut.getItems().size());

        sut.reloadItems();
        assertEquals(0, sut.getItems().size());
    }

    @Test
    public void getItems() throws Exception {

        assertEquals(0, sut.getItems().size());
    }

    @Test
    public void getItem() throws Exception {

        assertFalse(sut.resolve(new ExperimentDesignElementId("foo")).isPresent());
    }

}
