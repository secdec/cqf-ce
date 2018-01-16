package com.siegetechnologies.cqf.testbed.base.util;

/*-
 * #%L
 * astam-cqf-ce-testbed-vsphere
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

import com.siegetechnologies.cqf.core.util.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.PasswordAuthentication;
import java.util.Properties;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Utility methods for resources that provide password-based authentication info.
 *
 * @author srogers
 */
public abstract class PasswordBasedCredentialsResourceUtil
{
	public static PasswordAuthentication findPasswordBasedCredentials(
			String stem, String suffix, String... selectors
	)
			throws IOException
	{
		return findPasswordBasedCredentialsLoadedBy(
				PasswordBasedCredentialsResourceUtil.class, stem, suffix, selectors
		);
	}

	public static PasswordAuthentication findPasswordBasedCredentialsLoadedBy(
			Class klass, String stem, String suffix, String... selectors

	)
			throws IOException
	{
		return findPasswordBasedCredentialsLoadedBy(
				klass.getClassLoader(), stem, suffix, selectors
		);
	}

	public static PasswordAuthentication findPasswordBasedCredentialsLoadedBy(
			ClassLoader loader, String stem, String suffix, String... selectors
	)
			throws IOException
	{
		PasswordAuthentication result = null;

		final String suffix_final = (suffix == null) ? ".properties" : suffix;

		Pair<String, InputStream> match = Resources.firstMatchingAsInputStream(loader, () ->
				Resources.nameStreamFrom(stem, suffix_final, selectors)
		);

		if (match != null) {

			Properties properties = new Properties();
			properties.load(new InputStreamReader(match.getValue()));

			String loginName/*     */ = properties.getProperty("loginName");
			String loginPassword/* */ = properties.getProperty("loginPassword");

			assert loginName != null;
			assert loginPassword != null;

			if (loginName != null && loginPassword != null) {

				char[] loginPassword_chars = chars_of(loginPassword);

				result = new PasswordAuthentication(loginName, loginPassword_chars);
			}
		}

		return result;
	}

	//^-- FIXME: SECURITY: REVIEW: srogers: never load a password into a String; use a char[] so that you can clear it
	//^-- FIXME: SECURITY: REVIEW: srogers: implement special .ini file reading for passwords; don't rely on java.util.Properties

	/**/

	protected static char[] chars_of(String s)
	{
		if (s == null) {
			return null;
		}

		char[] result = new char[s.length()];

		s.getChars(0, s.length(), result, 0);

		return result;
	}

	//^-- TODO: srogers: move to StringUtil in astam-cqf-ce-core

}
