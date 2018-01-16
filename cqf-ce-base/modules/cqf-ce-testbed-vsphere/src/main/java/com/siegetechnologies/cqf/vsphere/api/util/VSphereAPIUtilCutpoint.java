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

import com.siegetechnologies.cqf.core.util.Concurrency;
import com.siegetechnologies.cqf.core.util.Concurrency.VoidCallable;
import com.siegetechnologies.cqf.core.util.Config;
import com.siegetechnologies.cqf.core.util.Exceptions;
import com.siegetechnologies.cqf.core.util.Pair;
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util.VSphereTestbedException;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualPortGroup;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualSwitch;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHost;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUNetwork;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUObjectFactory;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import java.net.PasswordAuthentication;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author taylorj
 * @author srogers
 */
public interface VSphereAPIUtilCutpoint extends AUObjectFactory
{
	LoginInfo getLoginInfo();

	/**/

	/**
	 * Returns a host with the specified name (if any). When null is specified for the name,
	 * the first host among the collection of known hosts will be returned.
	 *
	 * @return a host with the specified name (if any)
	 */
	Optional<AUHost>
	findHost(
			String/*                        */ name
	);

	List<AUHost>
	findMatchingHosts(
			String/*                        */ nameRegexp
	);

	/**/

	/**
	 * Returns a network that matches the specified name. When null is specified for the name,
	 * the first network among the collection of known networks will be returned.
	 *
	 * @param name the name of the network
	 *
	 * @return a network with a matching name (if any)
	 */
	Optional<AUNetwork>
	findNetwork(
			String/*                        */ name
	);

	List<AUNetwork>
	findMatchingNetworks(
			String/*                        */ nameRegexp
	);

	/**/

	Optional<AUDistributedVirtualSwitch>
	findDistributedVirtualSwitch(
			String/*                        */ name
	);

	List<AUDistributedVirtualSwitch>
	findMatchingDistributedVirtualSwitches(
			String/*                        */ nameRegexp
	);

	AUDistributedVirtualSwitch createDistributedVirtualSwitch(
			String/*                        */ virtualSwitchName,
			List<AUHost>/*                  */ attachedHosts,
			String[]/*                      */ nicList,
			boolean/*                       */ allowPromiscuous
	);

	void deleteDistributedVirtualSwitch(
			AUDistributedVirtualSwitch/*    */ virtualSwitch
	);

	/**/

	default
	Optional<AUDistributedVirtualPortGroup>
	findDistributedVirtualPortGroup(
			String/*                        */ name
	)
	{
		throw new UnsupportedOperationException("not yet implemented");
		//^-- TODO: DESIGN: srogers: implement findDistributedVirtualPortGroup()
	}

	default
	List<AUDistributedVirtualPortGroup>
	findMatchingDistributedVirtualPortGroups(
			String/*                        */ nameRegexp
	)
	{
		throw new UnsupportedOperationException("not yet implemented");
		//^-- TODO: DESIGN: srogers: implement findMatchingDistributedVirtualPortGroups()
	}

	AUDistributedVirtualPortGroup createDistributedVirtualPortGroup(
			String/*                        */ portGroupName,
			AUDistributedVirtualSwitch/*    */ virtualSwitch,
			int/*                           */ vlanId,
			boolean/*                       */ allowPromiscuous
	);

	void deleteDistributedVirtualPortGroup(
			AUDistributedVirtualPortGroup/* */ portGroup
	);

	/**/

	AUResourcePool
	getRootResourcePool();

	Optional<AUResourcePool>
	findResourcePool(
			String/*                        */ name
	);

	List<AUResourcePool>
	findMatchingResourcePools(
			String/*                        */ nameRegexp
	);

	/**
	 * Creates a new resource pool under a parent resource pool.
	 *
	 * @param name
	 * @param parent
	 * @param cpuAllocationInfo
	 * @param ramAllocationInfo
	 *
	 * @return the new resource pool
	 */
	AUResourcePool
	createResourcePool(
			String/*                        */ name,
			AUResourcePool/*                */ parent,
			AUResourcePool.AllocationInfo/* */ cpuAllocationInfo,
			AUResourcePool.AllocationInfo/* */ ramAllocationInfo
	);

	/**
	 * Destroys a resource pool.
	 *
	 * @param pool the pool to destroy
	 */
	void
	deleteResourcePool(
			AUResourcePool/*                */ pool
	);

	/**/

	Optional<AUVirtualMachine>
	findVirtualMachine(
			String/*                        */ name
	);

	List<AUVirtualMachine>
	findMatchingVirtualMachines(
			String/*                        */ nameRegexp
	);

	/**/

	void configureNetworkAdapterForVirtualMachine(
			AUVirtualMachine/*              */ virtualMachine,
			String/*                        */ networkAdapterName,
			String/*                        */ networkName
	);

	void
	ensurePowerStateOfVirtualMachine(
			AUVirtualMachine/*              */ machine,
			AUVirtualMachine.PowerState/*   */ powerState
	);

	/**/

	AUVirtualMachine
	createLinkedClone(
			AUVirtualMachine/*              */ donor,
			String/*                        */ cloneName,
			String/*                        */ creatorUUID,
			AUResourcePool/*                */ resourcePool,
			PasswordAuthentication/*        */ cloneAdminCredentials
	);

	Stream<Supplier<Pair<String, AUVirtualMachine>>>
	createLinkedClones(
			AUVirtualMachine/*              */ donor,
			Collection<String>/*            */ cloneNames,
			String/*                        */ creatorUUID,
			AUResourcePool/*                */ resourcePool,
			PasswordAuthentication/*        */ cloneAdminCredentials
	);

	void
	deleteLinkedClone(
			AUVirtualMachine/*              */ clone
	);

	void
	deleteLinkedClones(
			Stream<AUVirtualMachine>/* */ clones
	);

	/**/

	/**
	 * Runs a remote action. When an exception is thrown, it will be caught and rethrown as the cause of a {@link
	 * VSphereTestbedException}.
	 *
	 * @param remoteAction           the remote action
	 * @param failureMessageSupplier supplies the detail message of a rethrown exception
	 * @param <T>                    return type of the remote action
	 *
	 * @return the result of the remote action
	 */
	public static <T> T
	runRemoteAction(
			Callable<T>/*                   */ remoteAction,
			Supplier<String>/*              */ failureMessageSupplier
	)
	{
		return runRemoteAction(remoteAction, failureMessageSupplier, e -> false);
	}

	/**
	 * Runs a remote action. When an exception is thrown, and the nature of the exception suggests that a retry is
	 * warranted, the action will be executed again. Once the maximum number of retries per action has occurred, a
	 * subsequent exception will be caught and rethrown as the cause of a {@link VSphereTestbedException}.
	 *
	 * @param remoteAction           the remote action
	 * @param failureMessageSupplier supplies the detail message of a rethrown exception
	 * @param shouldRetry            whether to retry an initial failure (thrown exception)
	 * @param <T>                    return type of the remote action
	 *
	 * @return the result of the action
	 */
	public static <T> T
	runRemoteAction(
			Callable<T>/*                   */ remoteAction,
			Supplier<String>/*              */ failureMessageSupplier,
			Predicate<Throwable>/*          */ shouldRetry
	)
	{
		int tries = Config.getConfiguration().getInt("cqf.vsphere.action.tries"); // FIXME: STRING: srogers
		int delay = Config.getConfiguration().getInt("cqf.vsphere.action.delay"); // FIXME: STRING: srogers
		String tu = Config.getConfiguration().getString("cqf.vsphere.action.timeUnit"); // FIXME: STRING: srogers
		TimeUnit timeUnit = TimeUnit.valueOf(tu);

		Predicate<Throwable> connectionDidReset = VSphereAPIUtilCutpoint::connectionDidReset;
		try {
			return Concurrency.retry(tries, remoteAction, connectionDidReset.or(shouldRetry), timeUnit, delay);
		}
		catch (ExecutionException | InterruptedException e) {

			throw new VSphereTestbedException("while executing remote action: " + failureMessageSupplier.get(), e);
		}
	}

	/**
	 * Like {@link #runRemoteAction(Callable, Supplier)}, but returns no value.
	 *
	 * @param remoteAction           the remote action
	 * @param failureMessageSupplier supplies the detail message of a rethrown exception
	 */
	public static <E extends Exception> void
	runRemoteAction(
			VoidCallable<E>/*               */ remoteAction,
			Supplier<String>/*              */ failureMessageSupplier
	)
	{
		runRemoteAction(remoteAction.toCallable(), failureMessageSupplier);
	}

	/**
	 * Like {@link #runRemoteAction(Callable, Supplier, Predicate)}, but returns no value.
	 *
	 * @param remoteAction           the remote action
	 * @param failureMessageSupplier supplies the detail message of a rethrown exception
	 * @param shouldRetry            whether to retry an initial failure (thrown exception)
	 */
	public static <E extends Exception> void
	runRemoteAction(
			VoidCallable<E>/*               */ remoteAction,
			Supplier<String>/*              */ failureMessageSupplier,
			Predicate<Throwable>/*          */ shouldRetry
	)
	{
		runRemoteAction(remoteAction.toCallable(), failureMessageSupplier, shouldRetry);
	}

	/**
	 * Returns true if an exception is, or is caused by, an exception resulting from a reset connection exception. The
	 * VSphere SDK code that invokes the webservice that throws the RemoteException doesn't include it as a detail. Instead,
	 * it just concatenates it into the message string with:
	 * <p>
	 * <pre>
	 * throw new RemoteException("VI SDK invoke exception:" + e);
	 * </pre>
	 * <p>
	 * This means we can't catch it explicitly, but have to actually match against a message like:
	 * <p>
	 * <pre>
	 * VI SDK invoke exception:java.net.SocketException: Connection reset
	 * </pre>
	 * <p>
	 * On Windows, we sometimes get a message like
	 * <p>
	 * <pre>
	 * Unrecognized Windows Sockets error: 0: recv failed
	 * </pre>
	 */
	public static boolean
	connectionDidReset(Throwable throwable)
	{
		return Exceptions.hasCause(throwable, t -> {

			String m = t.getMessage();

			if (m == null) {
				return false;
			}

			m = m.toLowerCase();

			return m.toLowerCase().contains("connection reset") || m.contains("recv failed");
		});
	}

	/**/

}
