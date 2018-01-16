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

/**
 * Utility methods for working with enumerations.
 * 
 * @author taylorj
 */
public class Enums {
	 
	private Enums() {}
	
	/**
	 * Returns true if a provided name names an enumeration value in the 
	 * the enumeration, ignoring case.
	 * 
	 * @param enumClass the enumeration class
	 * @param name the name 
	 * @return whether the name names an enumeration value
	 */
	public static final <E extends Enum<E>> boolean isValueIgnoreCase(Class<E> enumClass, String name) {
		for (E e : enumClass.getEnumConstants()) {
			if (e.name().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}
