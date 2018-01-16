package com.siegetechnologies.cqf.core.experiment.execution.util;

/*-
 * #%L
 * astam-cqf-ce-core
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


import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;

import java.time.Instant;
import java.util.Objects;

public class ExecutionTraceRecordImpl {

	private Instant timestamp;
	private ExecutionPhase phase;
	private ExperimentElementImpl element;
	private Object payload;

	/**
	 * Creates a new object of this type.
	 */
	ExecutionTraceRecordImpl() {
		this(null, null, null, null);
	}

	/**
	 * Creates a new object of this type.
	 *
	 * @param timestamp the time at which the event described by this record occurred
	 * @param phase the execution phase during which the event described by this record occurred
	 * @param element the experiment element being executed when the event described by this record occurred
	 * @param payload further data about the event described by this record
	 */
	public ExecutionTraceRecordImpl
	(
			Instant timestamp,
			ExecutionPhase phase,
			ExperimentElementImpl element,
			Object payload
	) {
		this.timestamp = timestamp;
		this.phase = phase;
		this.element = element;
		this.payload = payload;
	}

	@Override
	public String toString() {
		return (
				"[" + phase.getValue() + "] " +
						timestamp.toString() + " - " +
						element.getId() + " - " +
						payload.toString()
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ExecutionTraceRecordImpl other = (ExecutionTraceRecordImpl) o;
		return Objects.equals(timestamp, other.timestamp) &&
				Objects.equals(phase, other.phase) &&
				Objects.equals(element, other.element) &&
				Objects.equals(payload, other.payload);
	}

	@Override
	public int hashCode() {
		return Objects.hash(timestamp, phase, element, payload);
	}

	/**/

	/**
	 * Returns the time at which the event described by this record occurred.
	 *
	 * @return time at which the event described by this record occurred
	 */
	public Instant getTimestamp() { return timestamp; }

	public void setTimestamp(Instant value) { this.timestamp = value; }

	/**
	 * Returns the execution phase during which the event described by this record occurred.
	 *
	 * @return execution phase during which the event described by this record occurred
	 */
	public ExecutionPhase getPhase() { return phase; }

	public void setPhase(ExecutionPhase value) { this.phase = value; }

	/**
	 * Returns the experiment element being executed when the event described by this record occurred.
	 *
	 * @return experiment element being executed when the event described by this record occurred
	 */
	public ExperimentElementImpl getElement() { return element; }

	public void setElement(ExperimentElementImpl value) { this.element = value; }

	/**
	 * Returns further data about the event described by this record.
	 *
	 * @return further data about the event described by this record
	 */
	public Object getPayload() { return payload; }

	public void setPayload(Object value) { this.payload = value; }

	public <T> T getPayloadAs(Class<T> klass) { return klass.cast(payload); }

	/**/

	public static class Builder {

		private final ExecutionTraceRecordImpl result;

		public Builder() {
			this.result = new ExecutionTraceRecordImpl();
		}

		public ExecutionTraceRecordImpl build() {
			return result;
		}

		/**/

		public Builder setTimeStamp(Instant value) {
			result.setTimestamp(value);
			return this;
		}

		public Builder setPhase(ExecutionPhase value) {
			result.setPhase(value);
			return this;
		}

		public Builder setElement(ExperimentElementImpl value) {
			result.setElement(value);
			return this;
		}

		public Builder setPayload(Object value) {
			result.setPayload(value);
			return this;
		}

	}

	/**/

	/**
	 * Updates this object with non-null property values from each successive other object.
	 *
	 * @param others other object(s)
	 * @return this object
	 */
	public ExecutionTraceRecordImpl updateFrom(ExecutionTraceRecordImpl... others) {
		for (ExecutionTraceRecordImpl other : others) {
			if (other == null) {
				continue;
			}
			if (other.timestamp != null) {
				this.timestamp = other.timestamp;
			}
			if (other.phase != null) {
				this.phase = other.phase;
			}
			if (other.element != null) {
				this.element = other.element;
			}
			if (other.payload != null) {
				this.payload = other.payload;
			}
		}
		return this;
	}

	/**
	 * Returns a new object like this one, but with its null property values
	 * replaced with the corresponding values of another object.
	 *
	 * @param other another object
	 * @return the new object
	 */
	public ExecutionTraceRecordImpl copyAndUpdateWithDefaultsFrom(ExecutionTraceRecordImpl other) {
		ExecutionTraceRecordImpl result = new ExecutionTraceRecordImpl();
		return result.updateFrom(other, this);
	}

}
