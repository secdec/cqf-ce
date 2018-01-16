package com.siegetechnologies.cqf.core._v01.experiment.execution.util.impl;

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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siegetechnologies.cqf.core._v01.experiment.execution.util.ExecutionQuantifierConfiguration;
import com.siegetechnologies.cqf.core.util.Config;

/**
 * The standard quantification configuration is based on values from
 * the {@link Config global configuration}.
 *
 * @author taylorj
 */
public class DefaultExecutionQuantifierConfiguration implements ExecutionQuantifierConfiguration {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(DefaultExecutionQuantifierConfiguration.class);

	/**
	 * The map from test phass to phase information.
	 */
	private Map<ExecutionPhase, PhaseInfo> phaseInfo;

	/**
	 * A PhaseInfo encapsulates the information needed for each phase:
	 * the number of tries, and the delay measure and unit.
	 *
	 * @author taylorj
	 */
	interface PhaseInfo {
		/**
		 * Returns the number of times to attempt the phase.
		 * 
		 * @return the number of times to attempt the phase
		 */
		int getTries();
		
		/**
		 * Returns the duration of the delay between attempts.
		 * 
		 * @return the duration of the delay between attempts
		 */
		long getDelay();
		
		/**
		 * Returns the time unit of the delay between attempts
		 * 
		 * @return the time unit of the delay between attempts
		 */
		TimeUnit getUnit();

		/**
		 * Returns a new PhaseInfo object with specified fields.
		 * 
		 * @param tries the number of times to attempt the phase
		 * @param delay the duration of the delay between attempts
		 * @param unit the time unit of the delay between attempts
		 * @return the phase info
		 */
		static PhaseInfo of(int tries, long delay, TimeUnit unit) {
			return new PhaseInfo() {
				@Override
				public TimeUnit getUnit() { return unit; }

				@Override
				public int getTries() { return tries; }

				@Override
				public long getDelay() { return delay; }
			};
		}
	}

	/*
	 * System default values.
	 */

	static final int DEFAULT_TRIES = 1;
	static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;
	static final long DEFAULT_DELAY = 3;
	static final PhaseInfo DEFAULT_INFO = PhaseInfo.of(DEFAULT_TRIES, DEFAULT_DELAY, DEFAULT_UNIT);

	/**
	 * Creates a new {@link DefaultExecutionQuantifierConfiguration} that will
	 * read values from the provided configuration.
	 *
	 * @param configuration the configuration
	 */
	public DefaultExecutionQuantifierConfiguration(Configuration configuration) {
		phaseInfo = getPhaseInfoMap(configuration);
	}

	/**
	 * Returns a {@link PhaseInfo} based on values read from a configuration and
	 * some default information.  This method uses the keys obtained by
	 * concatenating "tries", "delay", and "unit" to the provided prefix to obtain
	 * the values for for the number of tries, the measure of the delay, and a
	 * string indicating the time unit, respectively.  For each of these keys,
	 * if the configuration does not have a value for the key, then the value
	 * from <code>defaults</code> is used instead.
	 *
	 * @param configuration the configuration
	 * @param prefix the prefix
	 * @param defaults the default information
	 * @return the phase info
	 */
	private static PhaseInfo getPhaseInfo(Configuration configuration, String prefix, PhaseInfo defaults) {
		int tries = configuration.getInt(prefix+"tries", defaults.getTries()); // FIXME: srogers: extract string construction into a dedicated method
		long delay = configuration.getLong(prefix+"delay", defaults.getDelay()); // FIXME: srogers: extract string construction into a dedicated method
		TimeUnit unit;
		if (configuration.containsKey(prefix+"unit")) { // FIXME: srogers: extract string construction into a dedicated method
			String dUnitStr = configuration.getString(prefix+"unit").toUpperCase(); // FIXME: srogers: extract string construction into a dedicated method
			unit = TimeUnit.valueOf(dUnitStr);
		}
		else {
			unit = defaults.getUnit();
		}
		return PhaseInfo.of(tries, delay, unit);
	}

	/**
	 * Returns a map from test phases to phase info.  First, configuration default information
	 * is read, which can override the {@link #DEFAULT_INFO}.  Then, values for each phase are
	 * read, using the configuration default information as defaults.
	 *
	 * @param configuration the configuration
	 * @return the map
	 */
	static Map<ExecutionPhase, PhaseInfo> getPhaseInfoMap(Configuration configuration) {
		EnumMap<ExecutionPhase, PhaseInfo> map = new EnumMap<>(ExecutionPhase.class);

		PhaseInfo configDefault = getPhaseInfo(configuration, "cqf.quantify.default.", DEFAULT_INFO); // FIXME: STRING: srogers

		for (ExecutionPhase phase : EnumSet.of(ExecutionPhase.INITIALIZE, ExecutionPhase.RUN, ExecutionPhase.RETRIEVE_DATA, ExecutionPhase.CLEANUP)) {
			String prefix = "cqf.quantify."+phase.name().toLowerCase()+"."; // FIXME: STRING: srogers
			map.put(phase, getPhaseInfo(configuration, prefix, configDefault));
		}
		return map;
	}

	@Override
	public int getNumberOfTries(ExecutionPhase phase) {
		return phaseInfo.get(phase).getTries();
	}

	@Override
	public TimeUnit getDelayTimeUnit(ExecutionPhase phase) {
		return phaseInfo.get(phase).getUnit();
	}

	@Override
	public long getDelayMeasure(ExecutionPhase phase) {
		return phaseInfo.get(phase).getDelay();
	}
}
