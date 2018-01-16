package com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.util.copying;

/*-
 * #%L
 * cqf-ce-testbed-base
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;

public class ScriptRenamerTest {

	private static void testRename(
			long indexInParent, String itemName, OperatingSystemFamily family,
			String input, String expected) {
		ScriptRenamer sr = new ScriptRenamer(indexInParent, itemName, family);
		String actual = sr.rename(input).orElse(null);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void shouldRenameBatOnWindows() {
		testRename(36, "purefunction", OperatingSystemFamily.WINDOWS, "main.bat", "main-0036-purefunction.bat");
	}
	
	@Test
	public void shouldRenameShOnUnix() {
		testRename(36, "purefunction", OperatingSystemFamily.UNIX, "setup.sh", "setup-0036-purefunction.sh");
	}

	@Test
	public void shouldSkipShOnWindows() {
		testRename(36, "purefunction", OperatingSystemFamily.WINDOWS, "setup.sh", null);
	}

	@Test
	public void shouldSkipBatOnUnix() {
		testRename(36, "purefunction", OperatingSystemFamily.UNIX,"setup.bat", null);
	}

	@Test
	public void shouldReturnOtherUnchanged() {
		testRename(36, "purefunction", OperatingSystemFamily.WINDOWS, "run.bat", "run.bat");
	}
	
	@Test
	public void testIsCompatible() {
		ScriptRenamer windowsRenamer = new ScriptRenamer(0, "x", OperatingSystemFamily.WINDOWS);
		assertFalse(windowsRenamer.isCompatible("sh"));
		assertFalse(windowsRenamer.isCompatible("csh"));
		assertFalse(windowsRenamer.isCompatible("foo"));
		assertTrue(windowsRenamer.isCompatible("bat"));
		
		ScriptRenamer unixRenamer = new ScriptRenamer(0, "x", OperatingSystemFamily.UNIX);
		assertFalse(unixRenamer.isCompatible("bat"));
		assertFalse(unixRenamer.isCompatible("exe"));
		assertTrue(unixRenamer.isCompatible("sh"));
	}
}
