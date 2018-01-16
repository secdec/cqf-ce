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

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.siegetechnologies.cqf.core.util.Iterators;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

@RunWith(MockitoJUnitRunner.class)
public class EntityManagerTest {

	@Mock
	ServiceInstance serviceInstance;

	@Mock
	InventoryNavigator inventoryNavigator;
	
	EntityManager em;
	
	@Before
	public void init() {
		em = new EntityManager(serviceInstance, folder -> inventoryNavigator);
	}

	@Test
	public void testGetEntities() throws InvalidProperty, RuntimeFault, RemoteException {
		when(inventoryNavigator.searchManagedEntities("String")).thenReturn(new ManagedEntity[] {});
		assertEquals(0, em.getEntities(String.class)
				.count());
	}

	@Test(expected=VSphereAPIException.class)
	public void testGetEntities_throws() throws InvalidProperty, RuntimeFault, RemoteException {
		when(inventoryNavigator.searchManagedEntities("String")).thenThrow(new RemoteException());
		em.getEntities(String.class);
	}
	
	@Test
	public void testConstructor() {
		new EntityManager(serviceInstance);
	}
	
	@Test
	public void testConstructor2() {
		ManagedEntity me = mock(ManagedEntity.class, Mockito.RETURNS_DEEP_STUBS);
		new EntityManager(me);
		verify(me).getServerConnection();
		verify(me.getServerConnection()).getServiceInstance();
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGetAncestors_badNext() {
		ManagedEntity c = mock(ManagedEntity.class);
		Iterator<ManagedEntity> ancs = em.getAncestors(c);
		ancs.next();
		ancs.next();
	}
	
	
	@Test
	public void testGetAncestors() {
		ManagedEntity a = mock(ManagedEntity.class);
		ManagedEntity b = mock(ManagedEntity.class);
		ManagedEntity c = mock(ManagedEntity.class);
		
		when(c.getParent()).thenReturn(b);
		when(b.getParent()).thenReturn(a);
		when(a.getParent()).thenReturn(null);
		
		Iterator<ManagedEntity> ancs = em.getAncestors(c);
		assertEquals(Arrays.asList(c,b,a), Iterators.toStream(ancs).collect(toList()));
	}

	
	@Test
	public void testGetDatacenter() {
		ManagedEntity a = mock(ManagedEntity.class, Mockito.RETURNS_DEEP_STUBS);
		when(a.getMOR().getType()).thenReturn("NotDatacenter");

		ManagedEntity b = mock(ManagedEntity.class, Mockito.RETURNS_DEEP_STUBS);
		when(b.getMOR().getType()).thenReturn("Datacenter");
		
		ManagedEntity c = mock(ManagedEntity.class, Mockito.RETURNS_DEEP_STUBS);
		when(c.getMOR().getType()).thenReturn("NotDatacenter");
		
		when(c.getParent()).thenReturn(b);
		when(b.getParent()).thenReturn(a);
		when(a.getParent()).thenReturn(null);
		
		assertTrue(em.getDatacenter(c).isPresent());
		assertTrue(em.getDatacenter(b).isPresent());
		assertFalse(em.getDatacenter(a).isPresent());
	}

}
