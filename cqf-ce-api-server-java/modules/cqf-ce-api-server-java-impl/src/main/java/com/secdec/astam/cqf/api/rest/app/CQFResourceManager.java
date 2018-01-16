package com.secdec.astam.cqf.api.rest.app;

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

import com.secdec.astam.cqf.api.rest.app.aor.ConfigurationProvider;
import com.secdec.astam.cqf.api.rest.app.aor.DesignCatalogMapProvider;
import com.secdec.astam.cqf.api.rest.app.aor.ExecutionHandlerRegistrar;
import com.secdec.astam.cqf.api.rest.app.aor.ExecutionPlatformManager;
import com.secdec.astam.cqf.api.rest.app.aor.ExperimentManager;
import com.secdec.astam.cqf.api.rest.app.aor.SessionManager;
import com.secdec.astam.cqf.api.rest.app.aor.impl.ResourceManagementServiceBase;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignCatalogImpl;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration2.ImmutableConfiguration;

/**
 * Manages all active resources for a CQFApplication (basic behavior).
 */
public abstract class CQFResourceManager extends ResourceManagementServiceBase
{
	private static final Logger logger = Logger.getLogger(CQFResourceManager.class.getName());

	static {
		logger.log(Level.FINEST, "loading: {0}", CQFResourceManager.class);
	}

	ConfigurationProvider/*     */ configurationProvider;
	DesignCatalogMapProvider/*  */ designCatalogMapProvider;
	ExecutionHandlerRegistrar/* */ executionHandlerRegistrar;
	ExecutionPlatformManager/*  */ executionPlatformManager;

	/**/

	/**
	 * Returns the singleton instance.
	 * <p/>
	 * <em>Implementation note:</em><br/> In testing scenarios, it is possible to have more than once instance of
	 * ResourceManager alive at the same time. In that case, the first instance created will be 'the' instance. This will
	 * always be the instance that is created when the ResourceManager class is loaded by the JVM.
	 *
	 * @return the singleton instance
	 */
	public static CQFResourceManager theInstance()
	{

		CQFResourceManager result = theInstance.get();
		return result;
	}

	public static boolean theInstance(CQFResourceManager proposedValue)
	{

		return theInstance(proposedValue, false);
	}

	public static boolean theInstance(CQFResourceManager proposedValue, boolean must_succeed)
	{

		boolean did_set_value = theInstance.compareAndSet(null, proposedValue);
		if (must_succeed && ! did_set_value) {
			throw new IllegalStateException("failed to set the singleton CQFResourceManager instance");
		}

		return did_set_value;
	}

	public static void theInstance_reset()
	{

		theInstance.set(null);
	}

	private static final AtomicReference<CQFResourceManager> theInstance = new AtomicReference<>();

	/**
	 * Creates a (normally singleton) object of this type.
	 * <p/>
	 * <em>Implementation note:</em><br/> In testing scenarios, multiple objects of this type can exist at the same time.
	 */
	protected CQFResourceManager()
	{

		logger.log(Level.FINE, "creating: {0}", this);

		if (CQFResourceManager.theInstance(this, false)) {

			// NB: theInstance() interlocks upstream: CQFApplication; downstream: none
		}

		// For JAX framework:
		initRootResourceAndProviderAndFeatureClasses();
		initRootResourceAndProviderAndFeatureSingletons();
		initProperties();

		// For CQF framework:
		initConfigurationProvider();
		initDesignCatalogMapProvider();
		initExecutionHandlerRegistrar();
		initExecutionPlatformManager();
	}

	/**/

	protected void initRootResourceAndProviderAndFeatureClasses()
	{

		rootResourceAndProviderAndFeatureClasses.addAll(Arrays.asList(
				com.secdec.astam.cqf.api.rest.responders.ExperimentDesignCatalogsApiResponder.class,
				com.secdec.astam.cqf.api.rest.responders.ExperimentDesignElementsApiResponder.class,
				com.secdec.astam.cqf.api.rest.responders.ExperimentDesignsApiResponder.class,
				com.secdec.astam.cqf.api.rest.responders.ExperimentElementsApiResponder.class,
				com.secdec.astam.cqf.api.rest.responders.ExperimentsApiResponder.class,

				com.secdec.astam.cqf.api.rest.app.aor.JSONObjectMapperProvider.class
		));
	}

	protected void initRootResourceAndProviderAndFeatureSingletons()
	{

      	/**/
	}

	protected void initProperties()
	{

      	/**/
	}

	protected Set<Class<?>> getRootResourceAndProviderAndFeatureClasses()
	{

		logger.log(Level.FINE, "invoked on: {0}", this);
		assert isRunning();

		return Collections.unmodifiableSet(rootResourceAndProviderAndFeatureClasses);
	}

	protected Set<Object> getRootResourceAndProviderAndFeatureSingletons()
	{

		logger.log(Level.FINE, "invoked on: {0}", this);
		assert isRunning();

		return Collections.unmodifiableSet(rootResourceAndProviderAndFeatureSingletons);
	}

	protected Map<String, Object> getProperties()
	{

		logger.log(Level.FINE, "invoked on: {0}", this);
		assert isRunning();

		return Collections.unmodifiableMap(properties);
	}

	protected final Set<Class<?>>/*       */ rootResourceAndProviderAndFeatureClasses/*    */ = new HashSet<>();
	protected final Set<Object>/*         */ rootResourceAndProviderAndFeatureSingletons/* */ = new HashSet<>();
	protected final Map<String, Object>/* */ properties/*                                  */ = new HashMap<>();

	/**/

	protected void initConfigurationProvider()
	{

		this.configurationProvider = new ConfigurationProvider();
	}

	protected synchronized void loadConfiguration()
			throws IOException
	{

		this.configurationProvider.load(null);

		this.configuration = this.configurationProvider.getConfiguration();
		//^-- stash the (immutable) configuration
	}

	protected synchronized void unloadConfiguration()
	{

		this.configuration = null;

		this.configurationProvider.unload();
	}

	/**
	 * Returns the (immutable) configuration. It is only available while the resource manager {@link #isRunning is
	 * running}.
	 * <p/>
	 * <em>Implementation note:</em><br/> In testing scenarios (only), it is possible to load/unload a ResourceManager's
	 * configuration multiple times, even while the ResourceManager {@link #isRunning is not running}.
	 *
	 * @return the (immutable) configuration
	 */
	@Override
	public final synchronized ImmutableConfiguration getConfiguration()
	{

		if (this.configuration == null) {
			throw new IllegalStateException("immutable configuration has not been set");
		}
		return this.configuration;
	}

	/**/

	protected void initDesignCatalogMapProvider()
	{

		this.designCatalogMapProvider = new DesignCatalogMapProvider();
	}

	protected synchronized void loadDesignCatalogs()
			throws IOException
	{

		this.designCatalogMapProvider.load(getConfiguration());
	}

	protected synchronized void unloadDesignCatalogs()
	{

		this.designCatalogMapProvider.unload();
	}

	/**
	 * Returns all available design catalogs, mapped by name. They are only available while the resource manager {@link
	 * #isRunning is running}.
	 * <p/>
	 * The main design catalog will always be a member of the collection.
	 * <p/>
	 * <em>Implementation note:</em><br/> In testing scenarios, it is possible to load/unload a ResourceManager's design
	 * catalog map multiple times, even while the ResourceManager {@link #isRunning is not running}.
	 *
	 * @return all available design catalogs, mapped by name
	 */
	public synchronized NavigableMap<String, ExperimentDesignCatalogImpl> getDesignCatalogs()
	{

		if (this.designCatalogMapProvider == null) {
			throw new IllegalStateException("design catalog map provider has not been set");
		}
		return Collections.unmodifiableNavigableMap(this.designCatalogMapProvider.getDesignCatalogMap());
	}

	/**
	 * Returns the main design catalog. It is only available while the resource manager {@link #isRunning is running}.
	 * <p/>
	 * <em>Implementation note:</em><br/> In testing scenarios, it is possible to load/unload a ResourceManager's main
	 * design catalog multiple times, even while the ResourceManager {@link #isRunning is not running}.
	 *
	 * @return the main design catalog
	 *
	 * @see #getDesignCatalogs
	 */
	public synchronized ExperimentDesignCatalogImpl getMainDesignCatalog()
	{

		if (this.designCatalogMapProvider == null) {
			throw new IllegalStateException("design catalog map provider has not been set");
		}
		return this.designCatalogMapProvider.getMainDesignCatalog();
	}

	/**/

	protected void initExecutionHandlerRegistrar()
	{

		this.executionHandlerRegistrar = new ExecutionHandlerRegistrar();
	}

	protected synchronized void registerExecutionHandlers()
	{

		this.executionHandlerRegistrar.registerExecutionHandlers(getConfiguration());
	}

	protected synchronized void withdrawExecutionHandlers()
	{

		this.executionHandlerRegistrar.withdrawExecutionHandlers();
	}

	/**/

	protected void initExecutionPlatformManager()
	{

		this.executionPlatformManager = new ExecutionPlatformManager(null);
	}

	protected synchronized void startupExecutionPlatformManager()
			throws IOException
	{

		this.executionPlatformManager.startup(getConfiguration());
	}

	protected synchronized void shutdownExecutionPlatformManager()
	{

		this.executionPlatformManager.shutdown();
	}

	/**
	 * Returns the execution platform manager. It is only available while the resource manager {@link #isRunning is
	 * running}.
	 *
	 * @return the manager reference
	 */
	public ExecutionPlatformManager getExecutionPlatformManager()
	{

		if (this.executionPlatformManager == null) {
			throw new IllegalStateException("execution platform manager has not been set");
		}
		return this.executionPlatformManager;
	}

	/**/

	@Override
	protected void startup_internal()
			throws IOException
	{

		if (this.configuration != null) {
			throw new IllegalArgumentException("resource manager does not accept a configuration at startup (yet)");
			//^-- TODO: FUTURE: srogers: use specified configuration as the resource manager's starting point??
		}

		loadConfiguration();
		loadDesignCatalogs();
		registerExecutionHandlers();
		startupExecutionPlatformManager();
	}

	@Override
	protected void shutdown_internal()
	{

		shutdownExecutionPlatformManager();
		withdrawExecutionHandlers();
		unloadDesignCatalogs();
		unloadConfiguration();
	}

	/**/

	/**
	 * Returns the session manager, as provided by the execution platform manager. This is a convenience method.
	 *
	 * @return the session manager, as provided by the execution platform manager
	 *
	 * @see #getExecutionPlatformManager
	 */
	public final SessionManager getSessionManager()
	{

		return this.executionPlatformManager.getSessionManager();
	}

	/**
	 * Returns the experiment manager, as provided by the execution platform manager. This is a convenience method.
	 *
	 * @return the experiment manager, as provided by the execution platform manager
	 *
	 * @see #getExecutionPlatformManager
	 */
	public final ExperimentManager getExperimentManager()
	{

		return this.executionPlatformManager.getExperimentManager();
	}

	/**/

}
