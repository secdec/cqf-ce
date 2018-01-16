package com.siegetechnologies.cqf.core.experiment;

/*-
 * #%L
 * cqf-ce-core
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Unique ID of an experiment element.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentElementId implements Comparable<ExperimentElementId>
{
	private final String value;

	/**/
	
	public ExperimentElementId()
	{
		this(null);
	}

	@JsonCreator
	public ExperimentElementId(@JsonProperty("value") String value)
	{
		if (value == null) {
			value = generateUniqueValue();
		}
		this.value = value;
	}

	private static String generateUniqueValue() {

		long uniquifier = experimentElementSerialNumber.getAndIncrement();

		String result = String.format("cqf-experiment-element-%06d", uniquifier); // FIXME: STRING: srogers
		return result; // must be unique during the lifetime of the CQF server
	}

	private static final AtomicLong experimentElementSerialNumber = new AtomicLong(1);

	/**/

	@Override
	public int hashCode() {

		return this.value.hashCode();
	}

	@Override
	public int compareTo(ExperimentElementId that) {

		return this.value.compareTo(that.value);
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (o instanceof ExperimentElementId) {

			ExperimentElementId that = (ExperimentElementId) o;

			return this.value.equals(that.value);
		}
		return false;
	}

	@Override
	public String toString() {

		return this.value.toString();
	}

	/**/
	
	public String value() {

		return value;
	}

	/**/

}
