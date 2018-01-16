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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.vmware.vim25.mo.ServiceInstance;
import org.junit.Test;

/**
 * @author srogers
 */
public class LoginManagerIntegrationTest extends VSphereAPIUtilCutpointIntegrationTestBase
{
	@Test
	public void testSetUpTearDown()
			throws Exception
	{
		/**/
	}

	/**/

	@Test
	public void testLoginLogout()
			throws Exception
	{
		// tested by static setUp/tearDown
	}

	@Test
	public void testLoginLogout_static()
			throws Exception
	{
		assertTrue(loginManager01.isLoggedIn());

		LoginInfo loginInfo02 = LoginInfoResourceUtil.findLoginInfo();
		ServiceInstance loginManager02_serviceInstance = LoginManager.login(loginInfo02);

		assertSame(loginInfo01, loginManager01.getLoginInfo());
		assertNotSame(loginInfo01, loginInfo02);

		assertSame(loginManager01_serviceInstance, loginManager01.getServiceInstance());
		assertNotSame(loginManager01_serviceInstance, loginManager02_serviceInstance);

		assertTrue(loginManager01.isLoggedIn());
	}

	/**/

	@Test
	public void testIsLoggedIn()
			throws Exception
	{
		// tested by static setUp/tearDown.
	}

	@Test
	public void testGetServiceInstance()
			throws Exception
	{
		assertSame(loginManager01_serviceInstance, loginManager01.getServiceInstance());
	}

	@Test
	public void testGetLoginInfo()
			throws Exception
	{
		assertSame(loginInfo01, loginManager01.getLoginInfo());
	}

	/**/

}
