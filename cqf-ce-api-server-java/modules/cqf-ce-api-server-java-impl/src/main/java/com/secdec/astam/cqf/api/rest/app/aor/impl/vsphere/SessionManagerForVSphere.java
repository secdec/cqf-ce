package com.secdec.astam.cqf.api.rest.app.aor.impl.vsphere;

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

import com.secdec.astam.cqf.api.rest.app.aor.ExecutionPlatformManager;
import com.secdec.astam.cqf.api.rest.app.aor.impl.SessionManagerBase;
import com.siegetechnologies.cqf.vsphere.api.util.LoginInfo;
import com.siegetechnologies.cqf.vsphere.api.util.LoginManager;
import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtil;
import com.vmware.vim25.mo.ServiceInstance;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author srogers
 */
public class SessionManagerForVSphere extends SessionManagerBase
{
	private static final Logger logger = Logger.getLogger(SessionManagerForVSphere.class.getName());

	private LoginManager/*    */ delegate;
	private ServiceInstance/* */ delegate_serviceInstance;
	private VSphereAPIUtil/*  */ delegate_apiUtil;

	/**/
	
	/**
	 * Creates an object of this type.
	 *
	 * @param parentExecutionPlatformManager
	 */
	public SessionManagerForVSphere(ExecutionPlatformManager parentExecutionPlatformManager) {
		
		super(parentExecutionPlatformManager);
	}

	/**/
	
	@Override
	public void startup_internal() {

		if (delegate != null) {
			return; // startup is idempotent
		}

		LoginInfo loginInfo = newLoginInfoFromConfiguration();
		if (loginInfo == null) {
			return;
		}

		this.delegate = new LoginManager(loginInfo);
		this.delegate_serviceInstance/* */ = this.delegate.login();
		this.delegate_apiUtil/*         */ = new VSphereAPIUtil(delegate);
	}

	@Override
	public void shutdown_internal() {

		if (delegate == null) {
			return; // shutdown is idempotent
		}

		this.delegate_apiUtil/*         */ = null;
		this.delegate_serviceInstance/* */ = null;
		this.delegate.logout();
		this.delegate = null;
	}

	/**
	 * FIXME: srogers: document me
	 */
	public VSphereAPIUtil getAPIUtil() {

		assert delegate_apiUtil != null;

		return delegate_apiUtil;
	}

	protected LoginInfo newLoginInfoFromConfiguration() {

		LoginInfo result = null;

		String loginName/*     */ = this.configuration.getString("esxi.user");/*   */ // FIXME: STRING: srogers
		String loginPassword/* */ = this.configuration.getString("esxi.pwd");/*    */ // FIXME: STRING: srogers
		String vSphereServer/* */ = this.configuration.getString("esxi.server");/* */ // FIXME: STRING: srogers

		//^-- FIXME: SECURITY: srogers: handle vSphere login credentials more securely

		if (loginName != null && loginPassword != null && vSphereServer != null) {

			result = new LoginInfo(loginName, loginPassword, vSphereServer);
			
		} else {
			final String message = "No ESXi login information in configuration; cannot execute experiments";
			logger.log(Level.FINE, message);
		}

		return result;
	}

}
