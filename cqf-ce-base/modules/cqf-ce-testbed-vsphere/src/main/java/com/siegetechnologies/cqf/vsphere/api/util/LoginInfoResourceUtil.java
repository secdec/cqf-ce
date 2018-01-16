package com.siegetechnologies.cqf.vsphere.api.util;

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
import java.util.Properties;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Utility methods for resources that provide vSphere login info.
 *
 * @author srogers
 */
public abstract class LoginInfoResourceUtil
{
	public static LoginInfo findLoginInfo(String... selectors)
			throws IOException
	{
		return findLoginInfoLoadedBy(LoginInfoResourceUtil.class, selectors);
	}

	public static LoginInfo findLoginInfoLoadedBy(Class klass, String... selectors)
			throws IOException
	{
		return findLoginInfoLoadedBy(klass.getClassLoader(), selectors);
	}

	public static LoginInfo findLoginInfoLoadedBy(ClassLoader loader, String... selectors)
			throws IOException
	{
		LoginInfo result = null;

		Pair<String, InputStream> match = Resources.firstMatchingAsInputStream(loader, () ->
				Resources.nameStreamFrom("vSphereLoginInfo", ".properties", selectors)
		);

		if (match != null) {

			Properties properties = new Properties();
			properties.load(new InputStreamReader(match.getValue()));

			String loginName/*     */ = properties.getProperty("loginName");
			String loginPassword/* */ = properties.getProperty("loginPassword");
			String vSphereServer/* */ = properties.getProperty("vSphereServer");

			assert loginName != null;
			assert loginPassword != null;
			assert vSphereServer != null;

			if (loginName != null && loginPassword != null && vSphereServer != null) {

				result = new LoginInfo(loginName, loginPassword, vSphereServer);
			}
		}

		return result;
	}

	/**/

}
