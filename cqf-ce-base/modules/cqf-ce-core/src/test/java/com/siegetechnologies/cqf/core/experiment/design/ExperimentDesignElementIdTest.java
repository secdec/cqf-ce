package com.siegetechnologies.cqf.core.experiment.design;

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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by cbancroft on 2/7/17.
 */
public class ExperimentDesignElementIdTest {

    public class ExperimentDesignElementTestSubject extends ExperimentDesignElementImplBase
    {
        public ExperimentDesignElementTestSubject(String name, String category, String variant) {
            super(null, name, category, variant);
        }
    }

    private ExperimentDesignElementImpl buildItem(String name, String category, String variant ) {
        return new ExperimentDesignElementTestSubject(name, category, variant);
    }
    @Test
    public void getItemId() throws Exception {

    }

    @Test
    public void of() throws Exception {
        String id_value = "theId";
        ExperimentDesignElementId id = new ExperimentDesignElementId(id_value);

        assertEquals(id.value(), id_value);
    }

    @Test
    public void of1() throws Exception {
        ExperimentDesignElementImpl designElement = buildItem( "Test ExperimentDesignElement", "Test", null );
        ExperimentDesignElementId id = ExperimentDesignElementId.of(designElement);

        assertEquals(ExperimentDesignElementId.VALUE_PREFIX + ".test.test_experimentdesignelement", id.value());
    }

    @Test
    public void of2() throws Exception {
        ExperimentDesignElementId id = ExperimentDesignElementId.of("Test ExperimentDesignElement", "Test", null);

        assertEquals(ExperimentDesignElementId.VALUE_PREFIX + ".test.test_experimentdesignelement", id.value());
    }

    @Test
    public void of3() throws Exception {
        ExperimentDesignElementId id = ExperimentDesignElementId.of("Test ExperimentDesignElement", "Test", "variant" );

        assertEquals(ExperimentDesignElementId.VALUE_PREFIX + ".test.test_experimentdesignelement.variant", id.value());
    }

    @Test
    public void equals() throws Exception {
        ExperimentDesignElementId id1 = ExperimentDesignElementId.of( "Test ExperimentDesignElement", "Test", "variant" );
        ExperimentDesignElementId id2 = buildItem( "Test ExperimentDesignElement", "Test", "variant").getId();

        assertTrue( id1.equals(id2) );


    }

}
