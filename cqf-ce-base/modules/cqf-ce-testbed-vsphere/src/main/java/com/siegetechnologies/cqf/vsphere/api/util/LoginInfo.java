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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Login information for a vSphere server.
 * 
 * @author taylorj
 */
public class LoginInfo {
	// TODO: consider using PasswordAuthentication here
	private final String username;
	private final String password;
	private final String server;

	/**
	 * Creates a new instance with null fields.
	 */
	public LoginInfo() {
		this(null,null,null);
	}
	
	/**
	 * Creates a new instance with provided fields.
	 * 
	 * @param username the username
	 * @param password the password
	 * @param server the server address
	 */
	@JsonCreator
	public LoginInfo(
			@JsonProperty("username") String username,
			@JsonProperty("password") String password,
			@JsonProperty("server") String server) {

		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
		Objects.requireNonNull(server);
		//^-- FIXME: srogers: require non-whitespace username and server (and password?)
		//^-- FIXME: srogers: require server to be a URL(??) or simple domain name?

		this.username = username;
		this.password = password;
		this.server = server;
	}
	
	/**
	 * @return a login info like this, but with a null password
	 */
	public LoginInfo withoutPassword() {
		return new LoginInfo(getUsername(), null, getServer());
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getServer() {
		return server;
	}
}
