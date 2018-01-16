package com.secdec.astam.cqf.api.rest.app;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

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

/**
 * JAX-RS application entry point for CQF (basic behavior).
 * <p/>
 * <em>Implementation note:</em><br>
 * All resources are actually managed internally by a resource manager. In the
 * JAX-RS scenario, a single resource manager is created when the application is
 * is created, and all resource management services are started at that time.
 * However, we also support testing scenarios in which the resource manager is
 * created directly, without being associated with a corresponding JAX-RS application.
 * In those scenarios, a resource manager can be created, started, and shutdown,
 * all during a single test case.
 *
 * @author srogers
 */
public abstract class CQFApplication extends Application {

	private static final Logger logger = Logger.getLogger(CQFApplication.class.getName());

	static {
		logger.log(Level.FINEST, "loading: {0}", CQFApplication.class);
	}

	/**/
	
	/**
	 * Returns the distinguished instance.
	 * <p/>
	 * <em>Implementation note:</em><br>
	 * In testing scenarios, and in some web containers,
	 * it is possible to have more than once instance of CQFApplication
	 * alive at the same time. In that case, the first instance created will be 'the' instance.
	 * This will always be the instance that is created when the CQFApplication class is loaded
	 * by the JVM.
	 *
	 * @return the distinguished instance
	 */
	public static CQFApplication theInstance() {

		CQFApplication result = theInstance.get();
		return result;
	}

	public static boolean theInstance(CQFApplication proposedValue) {

		return theInstance(proposedValue, false);
	}


	public static boolean theInstance(CQFApplication proposedValue, boolean must_succeed) {

		boolean did_set_value = theInstance.compareAndSet(null, proposedValue);
		if (must_succeed && ! did_set_value) {
			throw new IllegalStateException("failed to set the distinguished CQFApplication instance");
		}

		return did_set_value;
	}


	public static void theInstance_reset() {

		theInstance.set(null);
	}

	private static final AtomicReference<CQFApplication> theInstance = new AtomicReference<>();

	/**
	 * Creates a (normally singleton) object of this type.
	 * During creation, a corresponding resource manager is created (and started) automatically.
	 * <p/>
	 * <em>Implementation note:</em><br>
	 * In testing scenarios, multiple objects of this type can exist at the same time.
	 */
	protected CQFApplication() {

		this(CQFResourceManager.theInstance());
	}

	protected CQFApplication(CQFResourceManager resourceManager) {
		
		logger.log(Level.FINE, "creating: {0}", this);
		
		if (CQFApplication.theInstance(this, false)) {
			
			// NB: theInstance() interlocks upstream: none; downstream: CQFResourceManager
			
			if (CQFResourceManager.theInstance() != resourceManager) {

				throw new IllegalStateException("mismatched application and resource manager");
			}
			
		}
		
		this.resourceManager = resourceManager;
		startupResourceManager();
	}

	/**/
	
	protected void startupResourceManager() {

		try {
			this.getResourceManager().startup(null);
		}
		catch (IOException xx) {
			throw new IllegalStateException("while starting resource manager", xx);
		}
	}
	
	protected void shutdownResourceManager() {
		
		this.resourceManager.shutdown();
	}
	
	/**
	 * Returns this application's resource manager.
	 *
	 * @return this application's resource manager.
	 */
	public final CQFResourceManager getResourceManager() {
		
		if (this.resourceManager == null) {
			throw new IllegalStateException("resource manager has not been set");
		}
		return this.resourceManager;
	}

	protected CQFResourceManager resourceManager;

	/**/
	
	@Override
	public Set<Class<?>> getClasses() {

		logger.log(Level.FINE, "invoked on: {0}", this);

		return resourceManager.getRootResourceAndProviderAndFeatureClasses();
	}

	@Override
	public Set<Object> getSingletons() {

		logger.log(Level.FINE, "invoked on: {0}", this);

		return resourceManager.getRootResourceAndProviderAndFeatureSingletons();
	}

	@Override
	public Map<String, Object> getProperties() {

		logger.log(Level.FINE, "invoked on: {0}", this);

		return resourceManager.getProperties();
	}

	/**/
	
}

