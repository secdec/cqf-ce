package com.siegetechnologies.cqf.vsphere.api.util.mo;

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

import com.siegetechnologies.cqf.vsphere.api.util.VSphereAPIException;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.ManagedObject;
import java.security.MessageDigest;

/**
 * A managed object synthesized on the client side (only).
 *
 * @author srogers
 */
public abstract class AUSyntheticManagedObject extends AUManagedObject
{
	public/*for_mocking_otherwise_protected*/ ManagedObject getDelegate()
	{
		return null;
	}

	protected ManagedObjectReference getDelegateMOR()
	{
		ManagedObject delegate = this.getDelegate();

		if (delegate != null) {

			return delegate.getMOR();
		}
		else {

			String type = this.getDelegateMOR_type();
			String value = this.getDelegateMOR_value();

			ManagedObjectReference result = new ManagedObjectReference();
			result.setType(type);
			result.setVal(value);

			return result;
		}
	}

	protected final String getDelegateMOR_type() {

		return this.getClass().getSimpleName().toString();
	}

	protected final String getDelegateMOR_value()
	{
		StringBuilder result = new StringBuilder();

		result.append(this.getName());
		result.append("-");
		result.append(this.getMessageDigestOfKeyProperties("SHA-1"));

		return result.toString();
	}

	protected MessageDigest getMessageDigest(String algorithm)
	{
		try {
			return MessageDigest.getInstance(algorithm);
		}
		catch (Exception xx) {

			String m = String.format("while getting message digest computation engine (%s)", algorithm);
			throw new VSphereAPIException(m, xx);
		}
	}

	abstract
	protected String getMessageDigestOfKeyProperties(String algorithm);

	/**/

}
