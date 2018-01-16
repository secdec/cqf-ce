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

import static com.siegetechnologies.cqf.core.util.Strings.matchesAgainstCandidate;
import static com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIUtilCutpoint.runRemoteAction;
import static com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool.underlying;
import static com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine.underlying;
import static org.apache.commons.lang3.Validate.isTrue;

import com.siegetechnologies.cqf.core.util.Config;
import com.siegetechnologies.cqf.core.util.HttpStatusCodes;
import com.siegetechnologies.cqf.core.util.Pair;
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util.VSphereTestbedException;
import com.siegetechnologies.cqf.testbed.vsphere.experiment.execution.util.VSphereTestbedUtil;
import com.siegetechnologies.cqf.vsphere.api.EntityManager;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualPortGroup;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUDistributedVirtualSwitch;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHost;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUHostNetworkSystem;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUNetwork;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUObjectManager;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUResourcePool;
import com.siegetechnologies.cqf.vsphere.api.util.mo.AUVirtualMachine;
import com.siegetechnologies.cqf.vsphere.api.util.mo.proxies.AUProxyManager;
import com.vmware.vim25.FileTransferInformation;
import com.vmware.vim25.GuestAuthentication;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.GuestProgramSpec;
import com.vmware.vim25.HostNetworkPolicy;
import com.vmware.vim25.HostNetworkSecurityPolicy;
import com.vmware.vim25.HostPortGroup;
import com.vmware.vim25.HostPortGroupConfig;
import com.vmware.vim25.HostPortGroupSpec;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.HostVirtualSwitchBondBridge;
import com.vmware.vim25.HostVirtualSwitchConfig;
import com.vmware.vim25.HostVirtualSwitchSpec;
import com.vmware.vim25.LinkDiscoveryProtocolConfig;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ResourceAllocationInfo;
import com.vmware.vim25.ResourceConfigSpec;
import com.vmware.vim25.SharesInfo;
import com.vmware.vim25.SharesLevel;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineToolsRunningStatus;
import com.vmware.vim25.VirtualMachineToolsVersionStatus;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.FileManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.GuestFileManager;
import com.vmware.vim25.mo.GuestOperationsManager;
import com.vmware.vim25.mo.GuestProcessManager;
import com.vmware.vim25.mo.HostNetworkSystem;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Numerous utility methods for working with the VSphere API.  These methods often supplement the core VSphere methods by
 * using wrapper resource methods that perform caching on attributes that would otherwise require additional requests by the
 * VSphere API, and some contain "retry" code that attempts to work around intermittent issues with reset connections.
 *
 * @author taylorj
 */
public class VSphereAPIUtil implements VSphereAPIUtilCutpoint
{
	private static final Logger logger = LoggerFactory.getLogger(VSphereAPIUtil.class);

	protected static final SSLContext TRUST_SELF_SIGNED_SSL_CONTEXT;
	protected static final SSLConnectionSocketFactory TRUST_SELF_SIGNED_AND_ALLOW_ALL_HOSTNAMES_SOCKET_FACTORY;

	private final AUObjectManager mom = new AUProxyManager();
	private final ServiceInstance serviceInstance;
	private final LoginInfo loginInfo;

	/**
	 * Thrown if the VSphereAPIUtil class cannot be initialized.
	 *
	 * @author taylorj
	 */
	static class VSphereInitializationException extends RuntimeException
	{
		private static final long serialVersionUID = 6068971877539031332L;

		/**
		 * Create a new instance.
		 *
		 * @param message the message
		 * @param cause   the cause
		 *
		 * @see RuntimeException#RuntimeException(String, Throwable)
		 */
		public VSphereInitializationException(String message, Throwable cause)
		{
			super(message, cause);
		}

	}

	static {
		try {
			TRUST_SELF_SIGNED_SSL_CONTEXT =
					new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
			TRUST_SELF_SIGNED_AND_ALLOW_ALL_HOSTNAMES_SOCKET_FACTORY =
					new SSLConnectionSocketFactory(
							TRUST_SELF_SIGNED_SSL_CONTEXT,
							SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
					);
		}
		catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new VSphereInitializationException("Failed to statically initialize VSphereAPIUtil.", e);
		}
	}

	/**
	 * Creates a new VSphereAPIUtil with a connection provided by a login manager.
	 *
	 * @param loginManager the login manager
	 */
	public VSphereAPIUtil(LoginManager loginManager)
	{
		this(Objects.requireNonNull(loginManager, "Login manager must not be null.")
				.getServiceInstance(), loginManager.getLoginInfo());
	}

	/**
	 * Creates a new VSphereAPIUtil with a connection provided by a service instance and login information.
	 *
	 * @param serviceInstance the service instance
	 * @param loginInfo       the login information
	 */
	private VSphereAPIUtil(ServiceInstance serviceInstance, LoginInfo loginInfo)
	{
		this.serviceInstance = Objects.requireNonNull(serviceInstance);
		this.loginInfo = Objects.requireNonNull(loginInfo);
	}

	/**/

	/**
	 * Returns the name of the managed entity.
	 *
	 * @param managedEntity the managed entity
	 *
	 * @return the name of the managed entity
	 */
	public static String getName(ManagedEntity managedEntity)
	{
		return runRemoteAction(managedEntity::getName, () -> "Could not get managed entity name.");
	}

	/**
	 * @return the service instance of this util
	 */
	public ServiceInstance getServiceInstance()
	{
		return this.serviceInstance;
	}

	/**
	 * @return the login information of this util
	 */
	public LoginInfo getLoginInfo()
	{
		return this.loginInfo;
	}

	/**
	 * @return the file manager of this util
	 */
	public FileManager getFileManager()
	{
		return runRemoteAction(
				this.getServiceInstance()::getFileManager,
				() -> "Could not get file manager"
		);
	}

	/**
	 * @return the entity manager of this util
	 */
	EntityManager getEntityManager()
	{
		return new EntityManager(getServiceInstance());
	}

	@Deprecated
	public <T> Stream<T> getEntities(String type, Class<T> klass)
	{
		return getEntityManager().getEntities(type).map(klass::cast);
	}

	/**
	 * Like {@link #getEntities(String, Class)}, but uses the simple name of <code>klass</code> as the type name.  This
	 * works in many cases.
	 *
	 * @param klass the type of entity to retrieve
	 * @param <T>   the type of entity to retrieve
	 *
	 * @return a stream of entities
	 *
	 * @deprecated use {@link EntityManager} instead
	 */
	public <T> Stream<T> getEntities(Class<T> klass)
	{
		return getEntityManager().getEntities(klass);
	}

	/**
	 * Like {@link #getEntities(Class)}, but returns only the entities satisfying the predicate
	 *
	 * @param klass  the type of entity to retrieve
	 * @param filter a filter
	 * @param <T>    the type of entity to retrieve
	 *
	 * @return a stream of entities
	 *
	 * @deprecated use {@link EntityManager} instead
	 */
	@Deprecated
	public <T> Stream<T> getEntities(Class<T> klass, Predicate<? super T> filter)
	{
		return getEntityManager().getEntities(klass)
				.filter(filter);
	}

	/**
	 * Like {@link #getEntities(String, Class)}, but returns just the first entity.
	 *
	 * @param type  as to {@link #getEntities(String, Class)}
	 * @param klass as to {@link #getEntities(String, Class)}
	 * @param <T>   the entity type
	 *
	 * @return an entity
	 *
	 * @deprecated use {@link EntityManager} instead
	 */
	@Deprecated
	public <T> Optional<T> getEntity(String type, Class<T> klass)
	{
		return getEntityManager().getEntities(klass).findFirst();
	}

	/**
	 * Like {@link #getEntities(Class)}, but returns just the first entity.
	 *
	 * @param klass the class of the entityt
	 * @param <T>   the type of the entity
	 *
	 * @return the entity
	 *
	 * @deprecated use {@link EntityManager} instead
	 */
	@Deprecated
	public <T> Optional<T> getEntity(Class<T> klass)
	{
		return getEntityManager().getEntities(klass).findFirst();
	}

	/**
	 * Like {@link #getEntity(Class)}, but throws a {@link NoSuchElementException} if the result does not exist.
	 *
	 * @param klass the entity type
	 * @param <T>   the type of the required entity
	 *
	 * @return the result
	 *
	 * @throws NoSuchElementException if no entity of the given type is found
	 * @deprecated use {@link EntityManager} instead
	 */
	@Deprecated
	public <T> T getEntityRequired(Class<T> klass)
	{
		return getEntityManager().getEntities(klass)
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("No element of type: " + klass + "."));
	}

	/**
	 * Like {@link #getEntities(Class)}, but returns just the first entity satisfying the predicate.
	 *
	 * @param klass     the entity class
	 * @param predicate a predicate to filter entities
	 * @param <T>       the entity type
	 *
	 * @return an optional containing an entity
	 *
	 * @deprecated use {@link EntityManager} instead
	 */
	@Deprecated
	public <T> Optional<T> getEntity(Class<T> klass, Predicate<? super T> predicate)
	{
		return getEntityManager().getEntities(klass)
				.filter(predicate)
				.findFirst();
	}

	/**/

	@Override
	public Optional<AUHost> findHost(String name)
	{

		return (this.getEntities(HostSystem.class)

				.map(x -> Pair.of(x.getName(), x)) // potential RPC calls to vSphere; expensive

				.filter(nx -> matchesAgainstCandidate(nx.getLeft(), name))

				.map(nx -> AUHost_from(nx.getRight(), nx.getLeft()))

				.findFirst()
		);
	}

	@Override
	public List<AUHost> findMatchingHosts(String nameRegexp)
	{

		final Pattern compiledNamePattern = (nameRegexp == null) ? null : Pattern.compile(nameRegexp);

		return (this.getEntities(HostSystem.class)

				.map(x -> Pair.of(x.getName(), x))
				//^-- potential RPC calls to vSphere; expensive

				.filter(nx -> matchesAgainstCandidate(nx.getLeft(), compiledNamePattern))

				.map(nx -> AUHost_from(nx.getRight(), nx.getLeft()))

				.collect(Collectors.toCollection(() -> new LinkedList<>()))
				//^-- collect results in encounter order
		);
	}

	/**/

	@Override
	public Optional<AUNetwork> findNetwork(String name)
	{

		return (this.getEntities(Network.class)

				.map(x -> Pair.of(x.getName(), x)) // potential RPC calls to vSphere; expensive

				.filter(nx -> matchesAgainstCandidate(nx.getLeft(), name))

				.map(nx -> AUNetwork_from(nx.getRight(), nx.getLeft(), null, null))

				.findFirst()
		);
	}

	@Override
	public List<AUNetwork> findMatchingNetworks(String nameRegexp)
	{

		final Pattern compiledNamePattern = (nameRegexp == null) ? null : Pattern.compile(nameRegexp);

		return (this.getEntities(Network.class)

				.map(x -> Pair.of(x.getName(), x))
				//^-- potential RPC calls to vSphere; expensive

				.filter(nx -> matchesAgainstCandidate(nx.getLeft(), compiledNamePattern))

				.map(nx -> AUNetwork_from(nx.getRight(), nx.getLeft(), null, null))

				.collect(Collectors.toCollection(() -> new LinkedList<>()))
				//^-- collect results in encounter order
		);
	}

	/**/

	@Override
	public Optional<AUDistributedVirtualSwitch>
	findDistributedVirtualSwitch(
			String/*                     */ name
	)
	{
		LinkedList<Pair<HostSystem, LinkedList<HostVirtualSwitch>>> matchingHostVirtualSwitchListPairs = (
				this.getEntities(HostSystem.class)

						.map(x -> Pair.of(x, matchingVirtualSwitchesAttachedToHost(x, name)))
						//^-- potential RPC calls to vSphere; expensive

						.filter(xl -> xl.getRight().size() > 0)

						.collect(Collectors.toCollection(() -> new LinkedList<>()))
						//^-- collect results in encounter order
		);

		/**/

		TreeMap<String, LinkedList<AUHost>> matchingVirtualSwitchAttachedHostListMap = (
				newMatchingVirtualSwitchAttachedHostListMap(matchingHostVirtualSwitchListPairs)
		);
		matchingHostVirtualSwitchListPairs = null;

		return (matchingVirtualSwitchAttachedHostListMap.entrySet().stream()

				.map(x -> AUDistributedVirtualSwitch_from(
						null, x.getKey(), x.getValue(), null))

				.findFirst()
		);
	}

	@Override
	public List<AUDistributedVirtualSwitch>
	findMatchingDistributedVirtualSwitches(
			String/*                     */ nameRegexp
	)
	{
		final Pattern compiledNamePattern = (nameRegexp == null) ? null : Pattern.compile(nameRegexp);

		LinkedList<Pair<HostSystem, LinkedList<HostVirtualSwitch>>> matchingHostVirtualSwitchListPairs = (
				this.getEntities(HostSystem.class)

						.map(x -> Pair.of(x, matchingVirtualSwitchesAttachedToHost(x, compiledNamePattern)))
						//^-- potential RPC calls to vSphere; expensive

						.filter(xl -> xl.getRight().size() > 0)

						.collect(Collectors.toCollection(() -> new LinkedList<>()))
						//^-- collect results in encounter order
		);

		/**/

		TreeMap<String, LinkedList<AUHost>> matchingVirtualSwitchAttachedHostListMap = (
				newMatchingVirtualSwitchAttachedHostListMap(matchingHostVirtualSwitchListPairs)
		);
		matchingHostVirtualSwitchListPairs = null;

		return (matchingVirtualSwitchAttachedHostListMap.entrySet().stream()

				.map(x -> AUDistributedVirtualSwitch_from(
						null, x.getKey(), x.getValue(), null))

				.collect(Collectors.toCollection(() -> new LinkedList<>()))
				//^-- collect results in encounter order
		);
	}

	protected static LinkedList<HostVirtualSwitch>
	matchingVirtualSwitchesAttachedToHost(HostSystem host, String name)
	{
		HostNetworkSystem host_networkSystem = runRemoteAction(
				() -> host.getHostNetworkSystem(),
				() -> "while getting network system of host"
		);
		HostVirtualSwitch[] host_virtualSwitches =
				host_networkSystem.getNetworkInfo().getVswitch();

		return (Stream.of(host_virtualSwitches)

				.filter(x -> matchesAgainstCandidate(x.getName(), name))

				.collect(Collectors.toCollection(() -> new LinkedList<>()))
				//^-- collect results in encounter order
		);
	}

	protected static LinkedList<HostVirtualSwitch>
	matchingVirtualSwitchesAttachedToHost(HostSystem host, Pattern compiledNamePattern)
	{
		HostNetworkSystem host_networkSystem = runRemoteAction(
				() -> host.getHostNetworkSystem(),
				() -> "while getting network system of host"
		);
		HostVirtualSwitch[] host_virtualSwitches =
				host_networkSystem.getNetworkInfo().getVswitch();

		return (Stream.of(host_virtualSwitches)

				.filter(x -> matchesAgainstCandidate(x.getName(), compiledNamePattern))

				.collect(Collectors.toCollection(() -> new LinkedList<>()))
				//^-- collect results in encounter order
		);
	}

	protected TreeMap<String, LinkedList<AUHost>>
	newMatchingVirtualSwitchAttachedHostListMap(
			LinkedList<Pair<HostSystem, LinkedList<HostVirtualSwitch>>> matchingHostVirtualSwitchListPairs
	)
	{
		TreeMap<String, LinkedList<AUHost>> result = new TreeMap<>();

		while (! matchingHostVirtualSwitchListPairs.isEmpty()) {

			Pair<HostSystem, LinkedList<HostVirtualSwitch>> p =
					matchingHostVirtualSwitchListPairs.remove();

			HostSystem attachedHost_delegate = p.getLeft();
			LinkedList<HostVirtualSwitch> matchingVirtualSwitchList = p.getRight();

			String attachedHost_name = attachedHost_delegate.getName();
			//^-- potential RPC calls to vSphere; expensive

			AUHost attachedHost = AUHost_from(attachedHost_delegate, attachedHost_name);

			while (! matchingVirtualSwitchList.isEmpty()) {

				HostVirtualSwitch matchingVirtualSwitch_delegate =
						matchingVirtualSwitchList.remove();

				String matchingVirtualSwitch_name = matchingVirtualSwitch_delegate.getName();
				//^-- potential RPC calls to vSphere; expensive

				LinkedList<AUHost> attachedHostList = result
						.computeIfAbsent(matchingVirtualSwitch_name, (k) -> new LinkedList<AUHost>());

				attachedHostList.add(attachedHost);
			}

			matchingVirtualSwitchList = null;
		}

		return result;
	}

	public AUDistributedVirtualSwitch createDistributedVirtualSwitch(
			String/*                     */ virtualSwitchName,
			List<AUHost>/*               */ attachedHosts,
			String[]/*                   */ nicList,
			boolean/*                    */ allowPromiscuous
	)
	{
		if (nicList == null) {
			nicList = new String[0];
		}

		for (AUHost host : attachedHosts) {

			HostVirtualSwitchConfig[] virtualSwitchConfigs;
			Map<String, HostVirtualSwitchConfig> virtualSwitchConfigMap;

			/*
			 * First, create/obtain all the virtual switches on the host network system.
			 */
			virtualSwitchConfigs =
					host.getNetworkSystem().getVirtualSwitchConfigs();

			virtualSwitchConfigMap = (Stream.of(virtualSwitchConfigs)
					.collect(Collectors.toMap(HostVirtualSwitchConfig::getName, Function.identity()))
			);

			if (virtualSwitchConfigMap.containsKey(virtualSwitchName)) {

				logger.trace("Switch named \"{}\" already exists; will attempt to reuse.", virtualSwitchName);
			}
			else {
				HostVirtualSwitchSpec virtualSwitchSpec_useDefault = null;

				host.getNetworkSystem().addVirtualSwitch(virtualSwitchName, virtualSwitchSpec_useDefault);

				logger.trace("Created switch \"{}\".", virtualSwitchName);

				virtualSwitchConfigs =
						host.getNetworkSystem().getVirtualSwitchConfigs();

				virtualSwitchConfigMap = (Stream.of(virtualSwitchConfigs)
						.collect(Collectors.toMap(HostVirtualSwitchConfig::getName, Function.identity()))
				);
			}

			/*
			 * Build the virtual switch spec and commit it.
			 */
			HostVirtualSwitchSpec virtualSwitchSpec_actual = virtualSwitchConfigMap.get(virtualSwitchName).getSpec();

			virtualSwitchSpec_actual.getPolicy().getSecurity().setAllowPromiscuous(allowPromiscuous);

			if (nicList.length != 0) {

				logger.debug("NIC specification for virtual switch \"{}\": {}.",
						virtualSwitchName, Arrays.deepToString(nicList)
				);
				HostVirtualSwitchBondBridge bridge = new HostVirtualSwitchBondBridge();
				LinkDiscoveryProtocolConfig ldpc = new LinkDiscoveryProtocolConfig();

				ldpc.setOperation("listen"); // FIXME: STRING: srogers
				ldpc.setProtocol("cdp"); // FIXME: STRING: srogers

				bridge.setLinkDiscoveryProtocolConfig(ldpc);
				bridge.setNicDevice(nicList);

				virtualSwitchSpec_actual.setBridge(bridge);
				virtualSwitchSpec_actual.getPolicy().getNicTeaming().getNicOrder().setActiveNic(nicList);
			}

			host.getNetworkSystem().updateVirtualSwitch(virtualSwitchName, virtualSwitchSpec_actual);
		}

		return AUDistributedVirtualSwitch_from(null, virtualSwitchName, attachedHosts, null);
	}

	public void deleteDistributedVirtualSwitch(
			AUDistributedVirtualSwitch/* */ virtualSwitch
	)
	{
		String virtualSwitch_name = virtualSwitch.getName();

		for (AUHost host : virtualSwitch.getAttachedHostList()) {

			long matchingVirtualSwitchesOnHost_count = (Arrays.stream(host.getNetworkSystem().getVirtualSwitches())

					.filter(x -> matchesAgainstCandidate(x.getName(), virtualSwitch_name))

					.count()
			);
			if (matchingVirtualSwitchesOnHost_count > 0) {

				host.getNetworkSystem().removeVirtualSwitch(virtualSwitch_name);

				logger.trace("removed virtual switch {}", virtualSwitch_name);
			}
		}
	}

	/**/

	@Override
	public AUDistributedVirtualPortGroup createDistributedVirtualPortGroup(
			String/*                     */ portGroupName,
			AUDistributedVirtualSwitch/* */ virtualSwitch,
			int/*                        */ vlanId,
			boolean/*                    */ allowPromiscuous
	)
	{
		String virtualSwitchName = virtualSwitch.getName();

		HostPortGroupSpec portGroupSpec = new HostPortGroupSpec();
		portGroupSpec.setName(portGroupName);
		portGroupSpec.setVlanId(vlanId);
		portGroupSpec.setVswitchName(virtualSwitchName);
		portGroupSpec.setPolicy(new HostNetworkPolicy());
		portGroupSpec.getPolicy().setSecurity(new HostNetworkSecurityPolicy());
		portGroupSpec.getPolicy().getSecurity().setAllowPromiscuous(allowPromiscuous);

		for (AUHost host : virtualSwitch.getAttachedHostList()) {

			HostPortGroupConfig[] portGroupConfigs = host.getNetworkSystem().getPortGroupConfigs();

			Optional<HostPortGroupConfig> portGroupConfig_maybe = (Stream.of(portGroupConfigs)

					.filter(portGroupConfig -> Objects.equals(portGroupName, portGroupConfig.getSpec().getName()))

					.findFirst()
			);

			if (portGroupConfig_maybe.isPresent()) {

				logger.debug("Port group {} already exists; reconfiguring it.", portGroupName);

				host.getNetworkSystem().updatePortGroup(portGroupName, portGroupSpec);
			}
			else {
				logger.debug("Port group {} does not exist); creating it.", portGroupName);

				host.getNetworkSystem().addPortGroup(portGroupSpec);
			}
		}

		AUDistributedVirtualPortGroup result = AUDistributedVirtualPortGroup_from(
				null, portGroupName, virtualSwitch
		);
		return result;
	}

	@Override
	public void deleteDistributedVirtualPortGroup(
			AUDistributedVirtualPortGroup/* */ portGroup
	)
	{
		String portGroup_name = portGroup.getName();

		for (AUHost host : portGroup.getOwner().getAttachedHostList()) {

			HostPortGroup[] portGroupsOnHost = host.getNetworkSystem().getPortGroups();

			Set<String> portGroupNamesOnHost = (Stream.of(portGroupsOnHost)
					.map(HostPortGroup::getSpec)
					.map(HostPortGroupSpec::getName)
					.collect(Collectors.toSet())
			);
			if (portGroupNamesOnHost.contains(portGroup_name)) {

				host.getNetworkSystem().removePortGroup(portGroup_name);

				logger.trace("Removed port group: {}.", portGroup_name);
			}
		}
	}

	/**/

	@Override
	public Optional<AUVirtualMachine> findVirtualMachine(String name)
	{

		return (this.getEntities(VirtualMachine.class)

				.map(x -> Pair.of(x.getName(), x)) // potential RPC calls to vSphere; expensive

				.filter(nx -> matchesAgainstCandidate(nx.getLeft(), name))

				.map(nx -> AUVirtualMachine_from(nx.getRight(), nx.getLeft(),
						null, null, null, null
						)
				)
				.findFirst()
		);
	}

	@Override
	public List<AUVirtualMachine> findMatchingVirtualMachines(String nameRegexp)
	{

		final Pattern compiledNamePattern = (nameRegexp == null) ? null : Pattern.compile(nameRegexp);

		return (this.getEntities(VirtualMachine.class)

				.map(x -> Pair.of(x.getName(), x))
				//^-- potential RPC calls to vSphere; expensive

				.filter(nx -> matchesAgainstCandidate(nx.getLeft(), compiledNamePattern))

				.map(nx -> AUVirtualMachine_from(nx.getRight(), nx.getLeft(),
						null, null, null, null
						)
				)
				.collect(Collectors.toCollection(() -> new LinkedList<>()))
				//^-- collect results in encounter order
		);
	}

	@Override
	public void configureNetworkAdapterForVirtualMachine(
			AUVirtualMachine/*         */ virtualMachine,
			String/*                   */ networkAdapterName,
			String/*                   */ networkName
	) {
		AUNetwork network = (findNetwork(networkName).orElseThrow(() ->
				new NoSuchElementException("while getting network: " + networkName))
		);
		//^-- side effect: fail unless named network exists

		VirtualEthernetCardNetworkBackingInfo nicNetworkBackingInfo = new VirtualEthernetCardNetworkBackingInfo();
		nicNetworkBackingInfo.setDeviceName(networkName);

		VirtualEthernetCard nic = Stream.of(virtualMachine.getHardwareDevices())
				.filter(VirtualEthernetCard.class::isInstance).map(VirtualEthernetCard.class::cast)
				.filter(theNic -> networkAdapterName.equals(theNic.getDeviceInfo().getLabel()))
				.findFirst().orElseThrow(() ->
						new NoSuchElementException("No adapter (" + networkAdapterName + ") on virtual machine."));

		nic.setBacking(nicNetworkBackingInfo);

		VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
		virtualDeviceConfigSpec.setDevice(nic);
		virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.edit);

		VirtualMachineConfigSpec virtualMachineConfigChange = new VirtualMachineConfigSpec();
		virtualMachineConfigChange.setDeviceChange(new VirtualDeviceConfigSpec[] {virtualDeviceConfigSpec});

		reconfigureVirtualMachine(
				virtualMachine, virtualMachineConfigChange
		);
	}

	public void reconfigureVirtualMachine(AUVirtualMachine virtualMachine,
			VirtualMachineConfigSpec virtualMachineConfigChange
	)
	{
		VSphereTestbedUtil.reconfigureVirtualMachine(virtualMachine, virtualMachineConfigChange);
	}

	@Override
	public void ensurePowerStateOfVirtualMachine(AUVirtualMachine machine,
			AUVirtualMachine.PowerState powerState
	)
	{
		VSphereAPIUtil.ensurePowerStateOfVirtualMachine(underlying(machine), powerState);
	}

	@Override
	public AUVirtualMachine createLinkedClone(AUVirtualMachine donor,
			String cloneName, String creatorUUID, AUResourcePool resourcePool,
			PasswordAuthentication cloneAdminCredentials
	)
	{
		return VSphereTestbedUtil.createLinkedClone(
				donor, cloneName, creatorUUID, resourcePool, this, cloneAdminCredentials
		);
	}

	@Override
	public Stream<Supplier<Pair<String, AUVirtualMachine>>> createLinkedClones(
			AUVirtualMachine donor,
			Collection<String> cloneNames, String creatorUUID, AUResourcePool resourcePool,
			PasswordAuthentication cloneAdminCredentials
	)
	{
		return VSphereTestbedUtil.createLinkedClones(
				donor, cloneNames, creatorUUID, resourcePool, this, cloneAdminCredentials
		);
	}

	@Override
	public void deleteLinkedClone(AUVirtualMachine clone)
	{
		VSphereTestbedUtil.deleteLinkedClone(clone);
	}

	@Override
	public void deleteLinkedClones(Stream<AUVirtualMachine> clones)
	{
		VSphereTestbedUtil.deleteLinkedClones(clones);
	}

	/**/

	@Override
	public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
			AUDistributedVirtualPortGroup.Delegate/* */ portGroup
	)
	{
		return mom.AUDistributedVirtualPortGroup_from(portGroup);
	}

	@Override
	public AUDistributedVirtualPortGroup AUDistributedVirtualPortGroup_from(
			AUDistributedVirtualPortGroup.Delegate/* */ portGroup,
			String/*                                 */ name_expected,
			AUDistributedVirtualSwitch/*             */ owner_expected
	)
	{
		return mom.AUDistributedVirtualPortGroup_from(portGroup, name_expected, owner_expected);
	}

	/**/

	@Override
	public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(
			AUDistributedVirtualSwitch.Delegate/*    */ virtualSwitch
	)
	{
		return mom.AUDistributedVirtualSwitch_from(virtualSwitch);
	}

	@Override
	public AUDistributedVirtualSwitch AUDistributedVirtualSwitch_from(
			AUDistributedVirtualSwitch.Delegate/*    */ virtualSwitch,
			String/*                                 */ name_expected,
			List<AUHost>/*                           */ attachedHostList_expected,
			List<AUVirtualMachine>/*                 */ attachedVMList_expected
	)
	{
		return mom.AUDistributedVirtualSwitch_from(
				virtualSwitch, name_expected, attachedHostList_expected, attachedVMList_expected
		);
	}

	/**/

	@Override
	public AUHost AUHost_from(HostSystem system)
	{
		return mom.AUHost_from(system);
	}

	@Override
	public AUHost AUHost_from(HostSystem system, String name_expected)
	{
		return mom.AUHost_from(system, name_expected);
	}

	/**/

	@Override
	public AUHostNetworkSystem AUHostNetworkSystem_from(
			HostNetworkSystem system
	)
	{
		return mom.AUHostNetworkSystem_from(system);
	}

	@Override
	public AUHostNetworkSystem AUHostNetworkSystem_from(
			HostNetworkSystem system, String name_expected, AUHost owner_expected
	)
	{
		return mom.AUHostNetworkSystem_from(system, name_expected, owner_expected);
	}

	/**/

	@Override
	public AUNetwork AUNetwork_from(Network network)
	{
		return mom.AUNetwork_from(network);
	}

	@Override
	public AUNetwork AUNetwork_from(Network network, String name_expected,
			List<AUHost> attachedHostList_expected, List<AUVirtualMachine> attachedVMList_expected
	)
	{
		return mom.AUNetwork_from(network, name_expected, attachedHostList_expected, attachedVMList_expected);
	}

	/**/

	@Override
	public AUResourcePool AUResourcePool_from(ResourcePool pool)
	{
		return mom.AUResourcePool_from(pool);
	}

	@Override
	public AUResourcePool AUResourcePool_from(ResourcePool pool, String name_expected, AUResourcePool parent_expected)
	{
		return mom.AUResourcePool_from(pool, name_expected, parent_expected);
	}

	/**/

	@Override
	public AUVirtualMachine AUVirtualMachine_from(VirtualMachine machine)
	{
		return mom.AUVirtualMachine_from(machine);
	}

	@Override
	public AUVirtualMachine AUVirtualMachine_from(VirtualMachine machine, String name_expected,
			GuestFamily family_expected, PasswordAuthentication adminCredentials_expected,
			AUVirtualMachine donor_expected, AUResourcePool resourcePool_expected)
	{
		return mom.AUVirtualMachine_from(machine, name_expected,
				family_expected, adminCredentials_expected, donor_expected, resourcePool_expected
		);
	}

	/**/

	/**
	 * Assembles a file URL from a given filePath and
	 * datastore name and the login information provided in this
	 * VSphereAPIUtil's login information.
	 * <p>
	 * <strong>Note:</strong> The URL assembled by this method contains
	 * the current username and <em>the username's password</em>.  This
	 * may be visible to anyone watching HTTP traffic on the network,
	 * depending on whether the carryin connection is encrypted.  This
	 * may appear in HTTP logs, etc.  Additionally, this URL should not
	 * be displayed in logging messages, etc.
	 *
	 * @param filePath the file path
	 * @param datastoreName the datastore name
	 * @return the file retrieval URL
	 */

	/**
	 * Creates an authenticated HTTP request for the given file path on the specified datastore. The request factory is used
	 * to produce the initial HttpBaseRequest.  The URI of the request is generated from the filepath and the datastore
	 * name, as well as the host of this VSphereAPIUtil.  Authentication information is provided as a Basic authentication
	 * header with the login information from this VSphereAPIUtil.
	 *
	 * @param requestFactory a supplier of HttpRequestBase objects
	 * @param filePath       the file path on the datastore
	 * @param datastore      the name of the datastore
	 * @param datacenter     the name of the datacenter
	 * @param <T>            the type of the request factory
	 *
	 * @return an HttpRequestBase
	 */
	private <T extends HttpRequestBase> T createDatastoreFileHttpRequest(Supplier<T> requestFactory, String filePath,
			String datastore, String datacenter)
	{
		String server = getLoginInfo().getServer();
		URI uri = URI.create(String.format(
				"https://%s/folder/%s?dcPath=%s&dsName=%s",
				server,
				encodeUtf8(filePath),
				datacenter,
				datastore
		));
		T req = requestFactory.get();
		req.setURI(uri);
		req.addHeader(createBasicAuthenticationHeader(getLoginInfo().getUsername(), getLoginInfo().getPassword(), req));
		return req;
	}

	/**
	 * Returns the encoding of a string in UTF-8.
	 *
	 * @param string the string to encode
	 *
	 * @return the UTF-8 encoding of the string
	 */
	private final String encodeUtf8(String string)
	{
		try {
			return URLEncoder.encode(string, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new AssertionError("Could not UTF-8 encode '" + string + "'.", e);
		}
	}

	/**
	 * Like {@link #createBasicAuthenticationHeader(String, String, HttpRequest, HttpContext)}, but passes <code>null</code>
	 * as the {@link HttpContext} argument.
	 *
	 * @param username the username
	 * @param password the password
	 * @param request  the HTTP request
	 *
	 * @return a header
	 */
	private static Header createBasicAuthenticationHeader(String username, String password, HttpRequest request)
	{
		HttpContext context = null;
		return createBasicAuthenticationHeader(username, password, request, context);
	}

	/**
	 * Returns a new header specifying basic scheme authentication for the specified username, password, request, and
	 * context.  This is a thin wrapper around {@link BasicScheme#authenticate(org.apache.http.auth.Credentials,
	 * HttpRequest, HttpContext)}; this catches any {@link AuthenticationException} and wraps it in an assertion error,
	 * because such an exception should never be thrown for basic authentication.
	 *
	 * @param username the username
	 * @param password the password
	 * @param request  the HTTP request
	 * @param context  the HTTP context, or null
	 *
	 * @return a header
	 */
	private static Header createBasicAuthenticationHeader(String username, String password, HttpRequest request,
			HttpContext context)
	{
		try {
			return new BasicScheme().authenticate(new UsernamePasswordCredentials(username, password), request, context);
		}
		catch (AuthenticationException e) {
			// Basic authentication should never throw an exception.
			throw new AssertionError("Could not create basic authentication header.", e);
		}
	}

	/**
	 * Uploads a file to the server.  This method uses the HTTP interface to the remote server, by constructing a HTTP PUT
	 * request that is executed with the specified content as the entity.
	 *
	 * @param filePath   the path of the file on the server
	 * @param content    the content of the file
	 * @param datastore  the name of the datastore
	 * @param datacenter the name of the datacenter
	 */
	public void uploadFile(String filePath, byte[] content, String datastore, String datacenter)
	{
		HttpPut put = createDatastoreFileHttpRequest(HttpPut::new, filePath, datastore, datacenter);
		logger.trace("uploadFile(filePath={},datastore={}), url={}", filePath, datastore, put.getURI());
		put.setEntity(new ByteArrayEntity(content));
		StatusLine line;
		try (
				CloseableHttpClient client = getTrustingClient();
				CloseableHttpResponse response = client.execute(put)
		) {
			line = response.getStatusLine();
		}
		catch (IOException e) {
			throw new VSphereTestbedException("Unable to upload file, " + filePath, e);
		}
		logger.trace("* response: {}", line);
		isTrue(
				HttpStatusCodes.isSuccess(line.getStatusCode()),
				"HTTP error while uploading file: %s.",
				line.getReasonPhrase()
		);
	}

	/**
	 * Retrieves a file from the server, returning its content as an array of bytes.
	 *
	 * @param filePath   the path of the file on the server
	 * @param datastore  the name of the datastore
	 * @param datacenter the name of the datacenter
	 *
	 * @return the content of the file
	 */
	public byte[] downloadFile(String filePath, String datastore, String datacenter)
	{
		HttpGet get = createDatastoreFileHttpRequest(HttpGet::new, filePath, datastore, datacenter);
		logger.trace("downloadFile(filePath={},datastore={}), url={}", filePath, datastore, get.getURI());
		try (
				CloseableHttpClient client = getTrustingClient();
				CloseableHttpResponse response = client.execute(get);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()
		) {
			StatusLine statusLine = response.getStatusLine();
			isTrue(
					HttpStatus.SC_OK == statusLine.getStatusCode(),
					"HTTP error while downloading file, %s: %s.",
					filePath,
					statusLine.getReasonPhrase()
			);
			HttpEntity entity = response.getEntity();
			IOUtils.copy(entity.getContent(), baos);
			return baos.toByteArray();
		}
		catch (IOException e) {
			throw new VSphereTestbedException("Error while downloading file from server.", e);
		}
	}

	/**
	 * Returns a string like URL, but with `*` replaced by the actual hostname of the server.
	 *
	 * @param url a url for file transfer
	 *
	 * @return url, with <code>*</code> replaced by host
	 *
	 * @see <a href="https://pubs.vmware.com/vsphere-60/topic/com.vmware.wssdk.apiref.doc/vim.vm.guest.FileManager.html#initiateFileTransferToGuest">FileManager.initiateFileTransferToGuest</a>
	 */
	public String replaceStarInTransferUrl(String url)
	{
		String host = getServiceInstance().getServerConnection().getUrl().getHost();
		return url.replaceAll("\\*", host);
	}

	/**
	 * Returns guest information for a virtual machine.
	 *
	 * @param vm the virtual machine
	 *
	 * @return the virtual machines guest info
	 */
	public static GuestInfo getGuest(VirtualMachine vm)
	{
		return runRemoteAction(vm::getGuest, () -> "Could not get guest info.");
	}

	/**
	 * Initiates a file transfer to a guest virtual machine.
	 *
	 * @param guestFileManager    the guest file manager
	 * @param guestAuthentication the authentication information
	 * @param guestFilePath       the path in the guest
	 *
	 * @return file transfer information
	 */
	public static FileTransferInformation initiateFileTransferFromGuest(
			GuestFileManager guestFileManager,
			GuestAuthentication guestAuthentication,
			String guestFilePath)
	{
		return runRemoteAction(
				() -> guestFileManager.initiateFileTransferFromGuest(guestAuthentication, guestFilePath),
				() -> "Could not transfer file " + guestFilePath + " from " + guestFileManager.getVM()
		);
	}

	/**
	 * Returns if guest operations are available on a virtual machine, else throws a {@link VSphereTestbedException}.
	 *
	 * @param vm the virtual machine
	 */
	public static void ensureGuestOperationsAvailable(VirtualMachine vm)
	{
		if (! getGuest(vm).getGuestOperationsReady()) {
			throw new VSphereTestbedException("guest operations are unavailable on " + vm);
		}
	}

	/**
	 * Returns whether guest tools are running on a virtual machine.
	 *
	 * @param vm the virtual machine
	 *
	 * @return whether the guest tools are running
	 */
	public static boolean isToolsRunning(VirtualMachine vm)
	{
		VirtualMachineToolsVersionStatus versionStatus = toolsVersionStatus(vm);
		isTrue(
				VirtualMachineToolsVersionStatus.guestToolsNotInstalled != versionStatus,
				"Guest tools not installed on virtual machine."
		);
		switch (toolsRunningStatus(vm)) {
		case guestToolsRunning:
			return true;
		case guestToolsExecutingScripts:
		case guestToolsNotRunning:
		default:
			return false;
		}
	}

	/**
	 * Returns the version status of the guest tools on a virtual machine.
	 *
	 * @param vm the virtual machine
	 *
	 * @return the tools version status
	 */
	public static VirtualMachineToolsVersionStatus toolsVersionStatus(VirtualMachine vm)
	{
		String toolsVersionStatus = getGuest(vm).getToolsVersionStatus2();
		return VirtualMachineToolsVersionStatus.valueOf(toolsVersionStatus);
	}

	/**
	 * Returns the status of the guest tools on a virtual machine.
	 *
	 * @param vm the virtual machine
	 *
	 * @return the running status
	 */
	public static VirtualMachineToolsRunningStatus toolsRunningStatus(VirtualMachine vm)
	{
		String runningStatus = getGuest(vm).getToolsRunningStatus();
		return VirtualMachineToolsRunningStatus.valueOf(runningStatus);
	}

	/**
	 * Suspends a virtual machine.
	 *
	 * @param vm the virtual machine
	 */
	public static void suspendVm(VirtualMachine vm)
	{
		runRemoteTask(
				vm::suspendVM_Task,
				() -> "Could not suspend virtual machine."
		);
	}

	/**
	 * Returns the runtime information of a virtual machine.
	 *
	 * @param vm the virtual machine
	 *
	 * @return the runtime
	 */
	public static VirtualMachineRuntimeInfo getRuntime(VirtualMachine vm)
	{
		return runRemoteAction(
				vm::getRuntime,
				() -> "Could not get runtime information."
		);
	}

	/**
	 * Returns the guest file manager for a virtual machine.
	 *
	 * @param vm the virtual machine
	 *
	 * @return the guest file manager for the virtual machine
	 */
	public static GuestFileManager getGuestFileManager(VirtualMachine vm)
	{
		ServiceInstance si = vm.getServerConnection().getServiceInstance();
		GuestOperationsManager gom = runRemoteAction(
				si::getGuestOperationsManager, () -> "Could not get guest operations manager.");
		if (gom == null) {
			throw new VSphereTestbedException("Guest operations manager was null.");
		}
		return runRemoteAction(() -> gom.getFileManager(vm), () -> "Could not get guest file manager.");
	}

	/**
	 * Ensures that a virtual machine has a requested power state. If the virtual machine already is in the requested power
	 * state, returns immediately.  Otherwise, powers on, powers off, or suspends the virtual machine as appropriate.
	 *
	 * @param vm             the virtual machine
	 * @param requestedState the requested power state
	 */
	public static void ensurePowerStateOfVirtualMachine(VirtualMachine vm, AUVirtualMachine.PowerState requestedState)
	{
		logger.trace("ensurePowerStateOfVirtualMachine(vm={},requestedState={})", vm, requestedState);

		VirtualMachinePowerState currentState = getRuntime(vm).getPowerState();
		if (Objects.equals(AUVirtualMachine.PowerState.from(currentState), requestedState)) {
			return;
		}

		switch (requestedState) {
		case OFF:
			powerOffVm(vm);
			return;
		case ON:
			powerOnVm(vm);
			return;
		case SUSPENDED:
			suspendVm(vm);
			return;
		default:
			throw new AssertionError(requestedState + " is not a legal enum value.");
		}
	}

	/**
	 * Starts a program in the guest virtual machine.
	 *
	 * @param vm             the virtual machine
	 * @param authentication authentication information
	 * @param spec           the guest program specification
	 *
	 * @return the pid of the process
	 */
	public static long startProgramInGuest(VirtualMachine vm, GuestAuthentication authentication, GuestProgramSpec spec)
	{
		ServiceInstance si = vm.getServerConnection().getServiceInstance();
		GuestOperationsManager gom = runRemoteAction(
				si::getGuestOperationsManager, () -> "Could not get guest operations maanager.");
		GuestProcessManager manager = runRemoteAction(
				() -> gom.getProcessManager(vm), () -> "Could not get guest process manager.");
		return runRemoteAction(
				() -> manager.startProgramInGuest(authentication, spec),
				() -> "Could not start program " + spec.getProgramPath() + " in vm, " + vm
		);
	}

	/**
	 * Deletes a file from a datacenter, with {@link FileManager#deleteDatastoreFile_Task(String, Datacenter)}.
	 *
	 * @param fm         the FileManager
	 * @param name       the name of the file
	 * @param datacenter a Datacenter, or null
	 */
	public static void deleteDatastoreFile(FileManager fm, String name, Datacenter datacenter)
	{
		runRemoteTask(
				() -> fm.deleteDatastoreFile_Task(name, datacenter),
				() -> "Could not delete datastore file, " + name
		);
	}

	/**
	 * Powers on a virtual machine.
	 *
	 * @param vm the virtual machine
	 */
	public static void powerOnVm(VirtualMachine vm)
	{
		runRemoteTask(
				() -> vm.powerOnVM_Task(null),
				() -> "Could not power on vm, " + vm + "."
		);
	}

	/**
	 * Powers off a virtual machine.
	 *
	 * @param vm the virtual machine
	 */
	public static void powerOffVm(VirtualMachine vm)
	{
		runRemoteTask(
				vm::powerOffVM_Task,
				() -> "Could not power off virtual machine."
		);
	}

	/**
	 * Registers a virtual machine with {@link Folder#registerVM_Task(String, String, boolean, ResourcePool, HostSystem)}.
	 *
	 * @param vmFolder the folder in which the virtual machine is registered
	 * @param path     the path to the VMX file
	 * @param name     the name of the virtual machine
	 * @param pool     the resource pool for the virtual machine, or null
	 * @param host     the host for the virtual machine, or null
	 *
	 * @return the virtual machine
	 */
	public static VirtualMachine registerVM(
			Folder vmFolder,
			String path,
			String name,
			ResourcePool pool,
			HostSystem host)
	{
		boolean asTemplate = false;
		ManagedObjectReference vmMOR = runRemoteTask(
				() -> vmFolder.registerVM_Task(path, name, asTemplate, pool, host),
				() -> "Could not register virtual machine, " + name,
				ManagedObjectReference.class
		);
		return new VirtualMachine(vmFolder.getServerConnection(), vmMOR);
	}

	/**
	 * Copies a file from one location to another, possibly between datacenters.
	 *
	 * @param fileManager           the file manager to use
	 * @param sourceName            the source path
	 * @param sourceDatacenter      the source datacenter, or null
	 * @param destinationName       the destination path
	 * @param destinationDatacenter the destination datacenter, or null
	 * @param force                 whether to force the copy, in the event that the destination already exists
	 */
	public static void copyDatastoreFile(
			FileManager fileManager,
			String sourceName,
			Datacenter sourceDatacenter,
			String destinationName,
			Datacenter destinationDatacenter,
			boolean force)
	{
		runRemoteTask(
				() -> fileManager
						.copyDatastoreFile_Task(sourceName, sourceDatacenter, destinationName, destinationDatacenter,
								force
						),
				() -> "Could not copy file '" + sourceName + "' to '" + destinationName + "'."
		);
	}

	/**
	 * Creates a directory in a datacenter.
	 *
	 * @param fileManager             the file manager
	 * @param directoryName           the path of the directory to create
	 * @param datacenter              the datacenter in which to create the directory
	 * @param createParentDirectories whether to create parent directories if they do not already exist
	 */
	public static void makeDirectory(FileManager fileManager, String directoryName, Datacenter datacenter,
			boolean createParentDirectories)
	{
		// TODO: remove eventually (added for debugging at lab)
		String datacenterName = null;
		if (datacenter != null) {
			try {
				datacenterName = runRemoteAction(datacenter::getName, () -> "Could not get datacenter name.");
			}
			catch (Exception e) {
				logger.warn("Unable to get name for datacenter, {}.", datacenter, e);
			}
		}
		logger.info("makeDirectory(fileManager={}, directoryName={}, datacenter={} (name={}), createParentDirectories={})",
				fileManager, directoryName, datacenter, datacenterName, createParentDirectories
		);
		runRemoteAction(
				() -> fileManager.makeDirectory(directoryName, datacenter, createParentDirectories),
				() -> "Could not create directory, " + directoryName
		);
	}

	/**
	 * @return the root resource pool of this util
	 */
	@Override
	public AUResourcePool getRootResourcePool()
	{

		/*
		 * The root resource pool is always the first one returned by the entity manager. //<-- FIXME: REVIEW: srogers
		 */
		return AUResourcePool_from(runRemoteAction(
				() -> this.getEntityRequired(ResourcePool.class),
				() -> "Could not get root resource pool."
		));
	}

	@Override
	public Optional<AUResourcePool> findResourcePool(String name)
	{

		return (this.getEntities(ResourcePool.class)

				.map(x -> Pair.of(x.getName(), x))
				//^-- potential RPC calls to vSphere; expensive

				.filter(nx -> matchesAgainstCandidate(nx.getLeft(), name))

				.map(nx -> AUResourcePool_from(nx.getRight(), nx.getLeft(), null))
				//^-- FIXME: DESIGN: REVIEW: srogers: provide parent by walking tree top to bottom

				.findFirst()
		);
	}

	@Override
	public List<AUResourcePool> findMatchingResourcePools(String nameRegexp)
	{

		final Pattern compiledNamePattern = (nameRegexp == null) ? null : Pattern.compile(nameRegexp);

		return (this.getEntities(ResourcePool.class)

				.map(x -> Pair.of(x.getName(), x))
				//^-- potential RPC calls to vSphere; expensive

				.filter(nx -> matchesAgainstCandidate(nx.getLeft(), compiledNamePattern))

				.map(nx -> mom.AUResourcePool_from(nx.getRight(), nx.getLeft(), null))
				//^-- FIXME: DESIGN: REVIEW: srogers: provide parent by walking tree top to bottom

				.collect(Collectors.toCollection(() -> new LinkedList<>()))
				//^-- collect results in encounter order
		);

	}

	@Override
	public AUResourcePool createResourcePool(
			String name,
			AUResourcePool parent,
			AUResourcePool.AllocationInfo cpuAllocationInfo,
			AUResourcePool.AllocationInfo ramAllocationInfo
	)
	{
		ResourceAllocationInfo cpuAllocationInfo_vSphere =
				cpuAllocationInfo.toVSphereResourceAllocationInfo();

		ResourceAllocationInfo ramAllocationInfo_vSphere =
				ramAllocationInfo.toVSphereResourceAllocationInfo();

		SharesInfo sharesInfo = new SharesInfo();
		sharesInfo.setLevel(SharesLevel.normal);

		cpuAllocationInfo_vSphere.setShares(sharesInfo);
		ramAllocationInfo_vSphere.setShares(sharesInfo);

		ResourceConfigSpec resourceConfigSpec = new ResourceConfigSpec();

		resourceConfigSpec.setCpuAllocation(cpuAllocationInfo_vSphere);
		resourceConfigSpec.setMemoryAllocation(ramAllocationInfo_vSphere);

		AUResourcePool parent_final = (parent != null) ? parent : getRootResourcePool();

		return AUResourcePool_from(runRemoteAction(
				() -> underlying(parent_final).createResourcePool(name, resourceConfigSpec),
				() -> "Could not create resource pool, " + name
		));
	}

	@Override
	public void deleteResourcePool(AUResourcePool pool)
	{
		runRemoteTask(
				underlying(pool)::destroy_Task,
				() -> "Could not destroy resource pool."
		);
	}

	/**
	 * Runs a remote task with {@link #runRemoteTask(Callable, Supplier)} runRemoteTask}, but ignores the result.
	 *
	 * @param remoteTask      the remote task
	 * @param messageSupplier the message supplier
	 *
	 * @return the result from the task
	 */
	public static Object runRemoteTask(
			Callable<Task> remoteTask,
			Supplier<String> messageSupplier)
	{
		return runRemoteTask(remoteTask, messageSupplier, Object.class);
	}

	/**
	 * Runs the remote task and waits for the task to complete.
	 *
	 * @param remoteTask      the remote task
	 * @param messageSupplier the exception message supplier
	 * @param klass           the return type
	 * @param <T>             result type
	 *
	 * @return the result of the task, cast to klass
	 */
	public static <T> T runRemoteTask(
			Callable<Task> remoteTask,
			Supplier<String> messageSupplier,
			Class<T> klass)
	{
		int tries = Config.getConfiguration().getInt("cqf.vsphere.task.tries"); // FIXME: STRING: srogers
		int runningDelay = Config.getConfiguration().getInt("cqf.vsphere.task.runningDelay"); // FIXME: STRING: srogers
		int queueDelay = Config.getConfiguration().getInt("cqf.vsphere.task.queueDelay"); // FIXME: STRING: srogers
		int missingDelay = Config.getConfiguration().getInt("cqf.vsphere.task.missingDelay"); // FIXME: STRING: srogers
		String timeUnit = Config.getConfiguration().getString("cqf.vsphere.task.timeUnit"); // FIXME: STRING: srogers
		TimeUnit unit = TimeUnit.valueOf(timeUnit.toUpperCase());
		boolean cancel = true;

		Task task = runRemoteAction(remoteTask, messageSupplier);
		return klass.cast(waitForTask(task, tries, runningDelay, queueDelay, missingDelay, unit, cancel));
	}

	/**
	 * Returns an HTTP client that trusts self signed SSL certificates.  This is <em>not</em> not secure.
	 *
	 * @return a trusting HTTP client
	 */
	public static CloseableHttpClient getTrustingClient()
	{
		return HttpClients.custom().setSSLSocketFactory(TRUST_SELF_SIGNED_AND_ALLOW_ALL_HOSTNAMES_SOCKET_FACTORY).build();
	}

	/**
	 * Polls a task until it completes, and returns the result, or until a number of tries is exhausted, and throws an
	 * exception. If the task "completes" with an error.
	 *
	 * @param task         the task
	 * @param nTries       the number of times to poll the task
	 * @param runningDelay the delay before the next poll, when the task is running
	 * @param queueDelay   the delay before the next poll, when the task is queued
	 * @param missingDelay delay between tries when task info is null
	 * @param unit         the time unit of the delay
	 * @param cancel       whether to cancel the task if it does not complete within the allotted time
	 *
	 * @return the result of the task
	 */
	public static Object waitForTask(
			Task task,
			int nTries,
			long runningDelay,
			long queueDelay,
			long missingDelay,
			TimeUnit unit,
			boolean cancel)
	{
		boolean cancellable = false;
		for (int retries = 0; retries < nTries; retries++) {
			TaskInfo info = runRemoteAction(task::getTaskInfo, () -> "Could not get task info.");
			long timeout;
			if (info == null) {
				timeout = missingDelay;
			}
			else {
				cancellable = info.isCancelable();
				TaskInfoState state = info.getState();
				switch (state) {
				case error:
					throw new VSphereTestbedException(info.getError().getFault());
				case success:
					return info.getResult();
				case running:
					timeout = runningDelay;
					break;
				case queued:
					timeout = queueDelay;
					break;
				default:
					throw new AssertionError(state + " is not a TaskInfoState.");
				}
			}
			try {
				unit.sleep(timeout);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new VSphereTestbedException(e);
			}
		}

		try {
			String message = "ExecutionTask did not complete with the allocated time, will attempt to cancel. " + task;
			logger.error(message);
			throw new VSphereTestbedException(message);
		}
		finally {
			if (cancel && cancellable) {
				runRemoteAction(task::cancelTask, () -> "Could not cancel timed out task.");
			}
		}
	}

}
