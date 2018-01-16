package com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util;

/*-
 * #%L
 * cqf-ce-testbed-vsphere
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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SequentialReplacerTest
{
    /**
     * Should build a list of replacements
     */
    @Test
    public void testBuildReplacements()
    {
        List<String[]> list = SequentialReplacer.buildReplacements(new String[]{"0", "1", "3", "4", "7", "8"});
        for (String[] e : list)
        {
            int k = Integer.parseInt(e[0]);
            int v = Integer.parseInt(e[1]);
            Assert.assertEquals(k + 1, v);
        }
    }

    /**
     * Should throw for odd number of strings
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBuildReplacementsThrows()
    {
        SequentialReplacer.buildReplacements(new String[]{"a"});
    }

    @Test
    public void testReplacement()
    {
        SequentialReplacer br = new SequentialReplacer("1", "x", "0", "1", "a", null);
        // 0 gets replaced
        Assert.assertEquals("111", br.replace("000").get());

        // 1 gets replaced, then 0 gets replaced
        Assert.assertEquals("21x", br.replace("201").get());

        // a makes the line empty
        Assert.assertFalse(br.replace("a").isPresent());
    }
}
