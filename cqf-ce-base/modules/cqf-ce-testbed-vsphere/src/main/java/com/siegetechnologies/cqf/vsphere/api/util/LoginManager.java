package com.siegetechnologies.cqf.vsphere.api.util;

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

import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util.VSphereTestbedException;
import com.vmware.vim25.UserSession;
import com.vmware.vim25.mo.ServiceInstance;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple wrapper for the process of logging into a vSphere
 * server and obtaining a service instance.  A LoginManager is
 * created with a {@link LoginInfo} specifying a username, password
 * and server address.  Login and logout are handled by {@link #login()}
 * and {@link #logout()}.
 */
public class LoginManager {
	private static final Logger logger = LoggerFactory.getLogger(LoginManager.class);

	private static final String NO_MESSAGE = "(The global message is unavailable or you do not have the System.View ESXi Privilege)";
	private static final String NO_SESSIONS = "(the session list is unavailable or you do not have the Sessions.TerminateSession ESXi Privilege)";

	/**
	 * The login information
	 */
	private final LoginInfo info;

	/**
	 * The service instance, when the user is logged in
	 */
	private Optional<ServiceInstance> instance;

	/**
	 * The heartbeat task, while the user is logged in
	 */
	private Optional<Timer> heartbeat;

	/**
	 * Creates a new login manager with the specified credentials.
	 *
	 * @param info the username, password, and server address
	 */
	public LoginManager(LoginInfo info) {
		this.info = info;
		this.instance = Optional.empty();
	}

	/**
	 * Log into the server specified in the {@link #getLoginInfo() login information},
	 * using the specified username and password.
	 *
	 * @return the service instance
	 */
	public ServiceInstance login() {
		logger.trace("logging in");
		this.instance = Optional.of(LoginManager.login(this.info));

		this.instance.ifPresent(lm -> {
			// REF: https://www.vmware.com/support/developer/vc-sdk/visdk41pubs/ApiReference/vim.SessionManager.html
			final UserSession[] sessionList = lm.getSessionManager().getSessionList();
			if(null != sessionList) {
				logger.info("ESXi User Session Created; there are {} active sessions", sessionList.length);
			} else {
				logger.info("ESXi User Session Created; "+NO_SESSIONS);
			}

		});

		/* This heartbeat timer is an attempt at solving bug #475 by causing
		 * periodic interaction with the User Session that exists between
		 * the CQF server and ESXi. The behavior itself is somewhat arbitrary
		 * as long as it stimulates the SOAP interface (as far as we know).
		 */
		this.heartbeat = instance.map(lm -> {
			final long initialDelayMs = 0;
			final long periodMs = TimeUnit.MINUTES.toMillis(5);
			final Timer t = new Timer();
			t.scheduleAtFixedRate(new TimerTask(){
				@Override
				public void run() {
					// REF: https://www.vmware.com/support/developer/vc-sdk/visdk41pubs/ApiReference/vim.SessionManager.html
					final String m = lm.getSessionManager().getMessage();
					final UserSession[] sessionList = lm.getSessionManager().getSessionList();

					final String mMessage = null != m ? "Current Global Message: "+m : NO_MESSAGE;
					final String slMessage = null != sessionList ? "there are "+sessionList.length+" active sessions" : NO_SESSIONS;

					logger.trace("ESXi User Session Heartbeat; {}; {}", slMessage, mMessage);
				}
			}, initialDelayMs, periodMs);
			return t;
		});

		return this.instance.get();
	}

	/**
	 * Ensures that session is logged out.  This method has no effect
	 * is the user is not currently logged in.
	 */
	public void logout() {
		logger.trace("logging out");
		try {
			this.heartbeat.ifPresent(Timer::cancel);
			this.instance.ifPresent(LoginManager::logout);
		}
		catch( VSphereTestbedException vse ) {
			logger.error( "Problem logging out Service ExperimentElement.", vse );
		}

		this.instance = Optional.empty();
		this.heartbeat = Optional.empty();
	}

	/**
	 * Returns true if the user is logged in.  When the user
	 * is logged in, the {@link #getServiceInstance() service instance}
	 * is available.
	 *
	 * @return whether the user is logged in
	 */
	public boolean isLoggedIn() {
		return this.instance.isPresent();
	}

	/**
	 * Returns the login information associated with
	 * this manager.  <strong>This includes the username,
	 * server, <em>and password</em>.</strong>
	 *
	 * @return the login info
	 */
	public LoginInfo getLoginInfo() {
		return info;
	}

	/**
	 * Returns the service instance is the manager {@link #isLoggedIn() is logged in},
	 * else throws an {@link IllegalStateException}.
	 *
	 * @return the service instance
	 * @throws IllegalStateException if the manager is not logged in
	 */
	public ServiceInstance getServiceInstance() {
		return instance.orElseThrow(() -> new IllegalStateException("Not logged in."));
	}

	/*
	 * Static methods that actually implement the process.
	 */

	/**
	 * Logs in using the specified login information and returns the resulting
	 * ServiceInstance.
	 *
	 * @param loginInfo the login information
	 * @return the service instance
	 */
	public static ServiceInstance login(LoginInfo loginInfo) {
		String username = loginInfo.getUsername();
		String password = loginInfo.getPassword();
		String server = loginInfo.getServer();

		String urlString = "https://"+server+"/sdk";
		ServiceInstance serviceInstance;
		try {
			serviceInstance = new ServiceInstance(new URL(urlString), username, password, true);
		} catch (RemoteException e) {
			throw new VSphereTestbedException("Could not log in.", e);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed server URL, '"+server+"'.", e);
		}
		return serviceInstance;
	}

	/**
	 * Logs out from the serverice instance.
	 *
	 * @param serviceInstance the service instance
	 */
	public static void logout(ServiceInstance serviceInstance) {
		try {
			serviceInstance.getSessionManager().logout();
		} catch (RemoteException e) {
			throw new VSphereTestbedException("Could not log out.", e);
		}
	}
}
