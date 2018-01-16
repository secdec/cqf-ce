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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TreeTest {
	
	@Test
	public void testTree_default() {
		Tree<String> tree = new Tree<String>();
		Assert.assertSame(null, tree.getData());
		Assert.assertEquals(Collections.emptyList(), tree.getChildren());
	}
	
	@Test
	public void testTree_data() {
		String x = "x";
		Tree<String> tree = new Tree<>(x);
		Assert.assertSame(x, tree.getData());
		Assert.assertEquals(Collections.emptyList(), tree.getChildren());
	}

	@Test
	public void testTree_children() {
		List<Tree<String>> children = Arrays.asList(tree("a"), tree("b"));
		Tree<String> tree = new Tree<>(children);
		Assert.assertSame(null, tree.getData());
		Assert.assertEquals(children, tree.getChildren());
		Assert.assertNotSame(children, tree.getChildren());
	}

	private Tree<String> tree(String x) {
		return new Tree<>(x);
	}
	
	@Test
	public void testClear() {
		List<Tree<String>> children = Arrays.asList(tree("a"), tree("b"));
		Tree<String> tree = new Tree<>("foo", children);
		Assert.assertEquals(children, tree.getChildren());
		Assert.assertNotSame(children, tree.getChildren());
		tree.clear();
		Assert.assertSame(null, tree.getData());
		Assert.assertNotEquals(children, tree.getChildren());
	}
	
	@Test
	public void testSet() {
		List<Tree<String>> children = Arrays.asList(tree("a"), tree("b"));
		Tree<String> t1 = new Tree<String>("x", children);
		Tree<String> t2 = new Tree<String>();
		
		Assert.assertNotEquals(t1.getData(), t2.getData());
		Assert.assertNotEquals(t1.getChildren(), t2.getChildren());
		
		t2.set(t1);

		Assert.assertSame(t1.getData(), t2.getData());
		Assert.assertEquals(t1.getChildren(), t2.getChildren());
		Assert.assertNotSame(t1.getChildren(), t2.getChildren());
	}

}
