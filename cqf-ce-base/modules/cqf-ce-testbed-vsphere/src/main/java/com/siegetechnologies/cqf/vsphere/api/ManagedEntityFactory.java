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

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServerConnection;

/**
 * Interface for creating ManagedEntity subtypes. Many of the managed entity
 * subtypes define a constructor that can be cast to this type. For
 * instance, {@link Datacenter} defines a constructor
 * {@link Datacenter#Datacenter(ServerConnection, ManagedObjectReference)}.
 * The default methods in this interface provide some convenience wrappers
 * around those constructors, such as the ability to create an instance from
 * a managed entity directly, by extracting its server connection and
 * managed object reference.
 * 
 * @author taylorj
 *
 * @param <T> the subtype of managed entity
 */
@FunctionalInterface interface ManagedEntityFactory<T extends ManagedEntity> {
	/**
	 * Creates an instance of the managed entity sub-type.
	 * 
	 * @param serverConnection a server connection
	 * @param managedObjectReference a managed object reference
	 * @return the managed entity sub-type instance
	 */
	T create(ServerConnection serverConnection, ManagedObjectReference managedObjectReference);

	/**
	 * Creates an instance of the managed entity sub-type by calling
	 * {@link #create(ServerConnection, ManagedObjectReference)} with the
	 * entity's server connection and managed object reference.
	 * 
	 * @param me the managed entity
	 * @return the instance
	 */
	default T create(ManagedEntity me) {
		return create(me.getServerConnection(), me.getMOR());
	}
}
