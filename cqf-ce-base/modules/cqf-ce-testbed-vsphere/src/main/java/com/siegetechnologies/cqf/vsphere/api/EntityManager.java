package com.siegetechnologies.cqf.vsphere.api;

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

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siegetechnologies.cqf.core.util.Iterators;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * Provides access to vCenter entities.
 *
 * @author taylorj
 */
public class EntityManager {

	private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

	/**
	 * The datancenter constructor as a managed entity factory.
	 */
	private static final ManagedEntityFactory<Datacenter> DATACENTER_FACTORY = Datacenter::new;

	@FunctionalInterface
	interface NavigatorFactory {
		/**
		 * Returns an inventory navigator for a folder.
		 *
		 * @param folder the folder
		 * @return the navigator
		 */
		InventoryNavigator get(Folder folder);
	}

	private final ServiceInstance serviceInstance;
	private final NavigatorFactory navigatorFactory;

	EntityManager(ServiceInstance serviceInstance, NavigatorFactory navigatorFactory) {
		this.serviceInstance = serviceInstance;
		this.navigatorFactory = navigatorFactory;
	}

	/**
	 * Creates a new instance using the service instance from a managed entity's
	 * server connection.
	 *
	 * @param managedEntity the managed entity
	 *
	 * @see #EntityManager(ServiceInstance)
	 */
	public EntityManager(ManagedEntity managedEntity) {
		this(managedEntity.getServerConnection()
				.getServiceInstance());
	}

	/**
	 * Creates a new instance.
	 *
	 * @param serviceInstance the service instance
	 */
	public EntityManager(ServiceInstance serviceInstance) {
		this(serviceInstance, InventoryNavigator::new);
	}

	private ManagedEntity[] getEntitiesArray(String type) {
		Folder rootFolder = serviceInstance.getRootFolder();
		InventoryNavigator in = this.navigatorFactory.get(rootFolder);
		try {
			return in.searchManagedEntities(type);
		}
		catch (RemoteException e) {
			String message = "Unable to retrieve entities of type " + type + ".";
			throw new VSphereAPIException(message, e);
		}
	}

	public Stream<ManagedEntity> getEntities(String type) {
		return Stream.of(getEntitiesArray(type));
	}

	public <T> Stream<T> getEntities(String type, Class<T> klass) {
		return getEntities(type).map(klass::cast);
	}

	/**
	 * Returns a stream of entities of a specified type. This is equivalent to
	 * {@link #getEntities(String, Class)} with the type argument being the
	 * {@link Class#getSimpleName() simple name} of the class.
	 *
	 * @param klass the type
	 * @return the stream
	 */
	public <T> Stream<T> getEntities(Class<T> klass) {
		return getEntities(klass.getSimpleName(), klass);
	}

	/**
	 * Returns an iterator over the ancestry induced by
	 * {@link ManagedEntity#getParent()}.
	 *
	 * @param managedEntity the managed entity
	 * @return the iterator
	 */
	Iterator<ManagedEntity> getAncestors(ManagedEntity managedEntity) {
		return new Iterator<ManagedEntity>() {
			ManagedEntity curr = managedEntity;

			@Override
			public boolean hasNext() {
				return curr != null;
			}

			@Override
			public ManagedEntity next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				ManagedEntity result = curr;
				curr = curr.getParent();
				return result;
			}
		};
	}

	/**
	 * Returns true if a managed entity is a datacenter. It is a datacenter if
	 * it's managed object reference has type "Datacenter".
	 *
	 * @param managedEntity the managed entity
	 * @return whether the managed entity is a datacenter
	 */
	private static boolean isDatacenter(ManagedEntity managedEntity) {
		final ManagedObjectReference mor = managedEntity.getMOR();
		final String type = mor.getType();
		return "Datacenter".equals(type);
	}

	/**
	 * Returns the datacenter containing a managed entity. This method is based
	 * on the assumption that the datacenter appears in the ancestry produced by
	 * repeated calls to {@link ManagedEntity#getParent()}, which should hold
	 * true for many managed entity types, such as virtual machines.
	 *
	 * @param managedEntity the managed entity
	 * @return the datacenter containing the managed entity
	 */
	public Optional<Datacenter> getDatacenter(final ManagedEntity managedEntity) {
		Optional<Datacenter> datacenter = Iterators.toStream(getAncestors(managedEntity))
				.filter(EntityManager::isDatacenter)
				.map(DATACENTER_FACTORY::create)
				.findFirst();
		logger.trace("getDatacenter(managedEntity={})", datacenter);
		return datacenter;
	}
}
