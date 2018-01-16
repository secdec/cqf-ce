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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * A sequential replacer takes a number of replacement pairs and performs 
 * iterated replacement on an input string.  SequentialReplacer supports
 * the additional feature that if any replacement string is null, then 
 * no result is returned.  That is, sequential replacer can be used to
 * filter out lines, in addition to modifying them.
 * 
 * @author taylorj
 */
public class SequentialReplacer {
	/**
	 * The list of replacements 
	 */
	private final List<String[]> replacements;
	
	/**
	 * Creates a new instance from a provided reader and list
	 * of replacements.
	 * 
	 * @param replacements the replacements
	 */
	public SequentialReplacer(List<String[]> replacements) {
		this.replacements = replacements;
	}
	
	/**
	 * Creates a new instance from a provided reader and
	 * replacements generated from 
	 * an array of Strings using {@link #buildReplacements(String[])}.
	 * 
	 * @param replacements the replacements
	 */
	public SequentialReplacer(String... replacements) {
		this(buildReplacements(replacements));
	}
	
	/**
	 * Returns a list of replacement entries based on an array of strings.
	 * The array must have an even number of elements.  Successive pairs
	 * of strings are keys and values.  For instance, replacements[2] will 
	 * be replaced by replacements[3].
	 *  
	 * @param replacements the strings
	 * @return a list of replacement entries
	 */
	static final List<String[]> buildReplacements(String[] replacements) {
		isTrue((replacements.length & 1) == 0, "replacements must contain an even number of elements");
		
		List<String[]> builtReplacements = new ArrayList<>();
		for (int i=0; i<replacements.length; i+=2) {
			builtReplacements.add(new String[] { replacements[i], replacements[i+1] });
		}
		return builtReplacements;
	}

	/**
	 * Returns the result of replacement.  Given a line, applies the 
	 * replacements in order.  If any of the replacement patterns match
	 * but the replacement text is null, the an empty optional is returned.
	 * 
	 * @param inputLine a string
	 * @return the replacement result
	 */
	public Optional<String> replace(String inputLine) {
		String line = inputLine;
		for (String[] e : replacements) {
			String pattern = e[0];
			String replacement = e[1];
			Matcher m = Pattern.compile(pattern).matcher(line);
			if (m.find()) {
				if (replacement == null) {
					return Optional.empty();
				}
				else {
					line = m.replaceAll(replacement);
				}
			}
		}
		return Optional.of(line);
	}
}
