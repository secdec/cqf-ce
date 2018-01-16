package com.siegetechnologies.cqf.core.util;

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

import java.util.regex.Pattern;

/**
 * @author srogers
 */
public abstract class Strings
{
	/**
	 * Matches candidate against string s. Candidate can be null, in which case the match always fails. String s can be
	 * null, in which case it matches any non-null candidate.
	 *
	 * @param candidate
	 * @param s
	 *
	 * @return true iff there is a match
	 */
	public static boolean matchesAgainstCandidate(String candidate, String s)
	{
		return (candidate != null) && (s == null || s.equals(candidate));
	}

	/**
	 * Matches candidate against every string in s[] (exactly). Candidate can be null, in which case the match always fails.
	 * A string in s[] can be null, in which case that string does not prevent a match.
	 * <p/>
	 * The character opcode indicates how to combine individual matches: <ol> <li>'|'&nbsp;&nbsp;individual matches are
	 * or'ed together</li> <li>'&amp;'&nbsp;&nbsp;individual matches are and'ed together</li> </ol>
	 *
	 * @param candidate
	 * @param opcode
	 * @param s
	 *
	 * @return true iff there is a match against every string in s[] (according to opcode)
	 *
	 * @throws IllegalArgumentException when opcode is not recognized
	 */
	public static boolean matchesAgainstCandidate(String candidate, char opcode, String... s)
	{
		throw new UnsupportedOperationException("not yet implemented"); // TODO: srogers: implement stub when needed
	}

	/**/

	/**
	 * Matches candidate against string s, ignoring case. Candidate can be null, in which case the match always fails.
	 * String s can be null, in which case it matches any non-null candidate.
	 *
	 * @param candidate
	 * @param s
	 *
	 * @return true iff there is a match
	 */
	public static boolean matchesIgnoreCaseAgainstCandidate(String candidate, String s)
	{
		return (candidate != null) && (s == null || s.equalsIgnoreCase(candidate));
	}

	/**
	 * Matches candidate against every string in s[] (exactly, while ignoring case). Candidate can be null, in which case
	 * the match always fails. A string in s[] can be null, in which case that string does not prevent a match.
	 * <p/>
	 * The character opcode indicates how to combine individual matches: <ol> <li>'|'&nbsp;&nbsp;individual matches are
	 * or'ed together</li> <li>'&amp;'&nbsp;&nbsp;individual matches are and'ed together</li> </ol>
	 *
	 * @param candidate
	 * @param opcode
	 * @param s
	 *
	 * @return true iff there is a match against every string in s[] (according to opcode)
	 *
	 * @throws IllegalArgumentException when opcode is not recognized
	 */
	public static boolean matchesIgnoreCaseAgainstCandidate(String candidate, char opcode, String... s)
	{
		throw new UnsupportedOperationException("not yet implemented"); // TODO: srogers: implement stub when needed
	}

	/**/

	/**
	 * Matches candidate against pattern p. Candidate can be null, in which case the match always fails. Pattern p can be
	 * null, in which case it matches any non-null candidate.
	 *
	 * @param candidate
	 * @param p
	 *
	 * @return true iff there is a match
	 */
	public static boolean matchesAgainstCandidate(String candidate, Pattern p)
	{
		return (candidate != null) && (p == null || p.matcher(candidate).matches());
	}

	/**
	 * Matches candidate against every pattern in p[]. Candidate can be null, in which case the match always fails. A
	 * pattern in p[] can be null, in which case that pattern does not prevent a match.
	 * <p/>
	 * The character opcode indicates how to combine individual matches: <ol> <li>'|'&nbsp;&nbsp;individual matches are
	 * or'ed together</li> <li>'&amp;'&nbsp;&nbsp;individual matches are and'ed together</li> </ol>
	 *
	 * @param candidate
	 * @param opcode
	 * @param p
	 *
	 * @return true iff there is a match against every pattern in p[] (according to opcode)
	 *
	 * @throws IllegalArgumentException when opcode is not recognized
	 */
	public static boolean matchesAgainstCandidate(String candidate, char opcode, Pattern... p)
	{
		throw new UnsupportedOperationException("not yet implemented"); // TODO: srogers: implement stub when needed
	}

	/**/

	public static String regexFromCommaSeparatedList(String listSpecification)
	{
		String result = listSpecification.replaceAll("\\s*,\\s*", "|");
		return result;
	}

	/**/

}
