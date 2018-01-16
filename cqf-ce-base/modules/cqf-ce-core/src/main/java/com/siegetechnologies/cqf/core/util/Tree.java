/**
 *  Copyright (c) 2016 Siege Technologies.
 */
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Representation of a tree.
 * 
 * <p>
 * <strong>WARNING</strong>: This class exists predominately for JSON
 * serialization. Modifications to this class will result in a modification of
 * the external REST API (and, likely, a bump in major version number).
 * 
 * @param <T> the type of element in the tree
 * 
 * @author <a href="mailto:charlie.bancroft@siegetechnologies.com">Charlie
 *         Bancroft</a>
 * @author <a href="mailto:rob.hall@siegetechnologies.com">Robert Hall</a>
 */
public class Tree<T> {
	private final List<Tree<T>> children = new LinkedList<>();
	private T data = null;

	/**
	 * Creates a new tree with null data and an empty list of children.
	 */
	public Tree() {
		this(null, Collections.emptyList());
	}

	/**
	 * Creates a new tree with provided data and an empty list of children.
	 * 
	 * @param data the data
	 */
	public Tree(T data) {
		this(data, Collections.emptyList());
	}

	/**
	 * Creates a new tree with null data and provided list of children.
	 * 
	 * @param children the children
	 */
	public Tree(List<Tree<T>> children) {
		this(null, children);
	}

	/**
	 * Creates a new tree with provided data and provided list of children
	 * 
	 * @param data the data
	 * @param children the children
	 */
	public Tree(T data, List<Tree<T>> children) {
		this.setData(data);
		addChildren(children);
	}

	/**
	 * Returns the list of child trees.
	 * 
	 * @return the children
	 */
	public List<Tree<T>> getChildren() {
		return children;
	}

	/**
	 * Add multiple children to this tree.
	 * 
	 * @param children the children
	 */
	public void addChildren(Collection<Tree<T>> children) {
		this.children.addAll(children);
	}

	/**
	 * Adds a single child to this tree.
	 * 
	 * @param child the child
	 */
	public void addChild(Tree<T> child) {
		this.children.add(child);
	}

	/**
	 * Returns the data element of this tree.
	 * 
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * Sets the data element of this tree.
	 * 
	 * @param data the data
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * Clears the list of children and sets the data to null.
	 */
	public void clear() {
		this.data = null;
		this.children.clear();
	}

	/**
	 * Sets the data of this tree to the data of another tree, 
	 * and clears this tree's children and adds all the children 
	 * of the other tree.
	 * 
	 * @param tree the other tree
	 */
	public void set(Tree<T> tree) {
		this.data = tree.getData();
		this.children.clear();
		this.children.addAll(tree.getChildren());
	}
}
