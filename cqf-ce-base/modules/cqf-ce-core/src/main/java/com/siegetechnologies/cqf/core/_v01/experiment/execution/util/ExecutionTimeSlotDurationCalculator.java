package com.siegetechnologies.cqf.core._v01.experiment.execution.util;

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


import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Sets up time-slot-based scheduling for an experiment's nodes.
 */
public class ExecutionTimeSlotDurationCalculator {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeSlotDurationCalculator.class);

    // FIXME: cbancroft: Remove hardcoded defaults when we can do substitution parsing here
    private static final Map<ExecutionTimeSlot, Integer> DEFAULT_SLOT_TIMINGS = new HashMap<>();
    static {
        DEFAULT_SLOT_TIMINGS.put( ExecutionTimeSlot.INITIALIZE, 120 );
        DEFAULT_SLOT_TIMINGS.put( ExecutionTimeSlot.SETUP, 180 );
        DEFAULT_SLOT_TIMINGS.put( ExecutionTimeSlot.STARTSENSORS, 60 );
        DEFAULT_SLOT_TIMINGS.put( ExecutionTimeSlot.MAIN, 300 );
        DEFAULT_SLOT_TIMINGS.put( ExecutionTimeSlot.STOPSENSORS, 60 );
        DEFAULT_SLOT_TIMINGS.put( ExecutionTimeSlot.CLEANUP, 60 );
    }

    public void apply(ExperimentImpl experiment) {
        Objects.requireNonNull(experiment, "Cannot calculate scheduling information for null experiment");

        Instant start = Instant.now();

        List<ExperimentElementImpl> nodes = experiment.getRoot().getChildren();
        EnumMap<ExecutionTimeSlot, Integer> maxDurationMap = nodes.stream()
                .map(this::calculateNodeDuration)
                .peek(em -> logger.trace("Map before processing: {}", em))
                //Split apart each node mapping
                .map(EnumMap::entrySet)

                //Stream it
                .flatMap(Collection::stream)

                //Pull out the max of each and construct a new map.
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::max,
                        () -> new EnumMap<>(ExecutionTimeSlot.class)));
        Instant end = Instant.now();


        EnumSet.allOf(ExecutionTimeSlot.class).forEach(ts -> maxDurationMap.computeIfPresent(ts, (executionTimeSlot, sec) -> (sec + 59) / 60));
        maxDurationMap.computeIfAbsent(ExecutionTimeSlot.MAIN, (executionTimeSlot) -> (DEFAULT_SLOT_TIMINGS.get(executionTimeSlot) + 59)/60 );
        logger.info("Duration Minutes: {}", maxDurationMap);

        experiment.getRoot().walk(i -> setMaxDuration(i, maxDurationMap));

        Integer totalRunDuration = maxDurationMap.values().stream().mapToInt(Integer::intValue).sum() + 3;
        experiment.getRoot().getParameterValueMap().put("DURATION", String.valueOf(totalRunDuration)); // FIXME: STRING: srogers

        logger.warn("Schedule calculated in: {}", Duration.between(start, end));
    }

    /**
     * Returns the duration for a time slot, if specified, or else a provided
     * default duration. The platform and the time slot's lower-cased name are
     * used to generate a parameter name of the form "[platform]_[executionTimeSlot
     * name]_duration". If the experimentElement has no parameter with that name, then
     * the default value is returned. Otherwise, the value is extracted and
     * parsed with {@link Integer#valueOf(String)}. If the parsing is
     * successful, then the value is returned. Otherwise, if a
     * {@link NumberFormatException} occurs (which can happen, for experimentElement, if
     * the value of the parameter is the empty string), then the default
     * duration is returned instead.
     *
     * @param experimentElement        the experimentElement
     * @param platform        the platform
     * @param executionTimeSlot        the time slot
     * @param defaultDuration the default duration
     * @return the duration for the time slot
     */
    private int getTimeSlotDuration(ExperimentElementImpl experimentElement, String platform, ExecutionTimeSlot executionTimeSlot, int defaultDuration) {
        String slotName = executionTimeSlot.name();
        String key = String.format("%s_%s_duration", platform.toLowerCase(), slotName.toLowerCase()); // FIXME: STRING: srogers
        return experimentElement.getParameter(key, Integer::parseInt).orElse(defaultDuration);
    }

    private void setMaxDuration(ExperimentElementImpl experimentElement, EnumMap<ExecutionTimeSlot, Integer> maxDuration) {
        if (ExperimentDesignElementId.of(ExperimentDesignElementSpec.of("Scheduler", "CQF")).equals(experimentElement.getDesign().getId())) {
            Integer offset = 2;//This value may need to be increased with scale (only leaves a one minute buffer of time for run commands to get to all targets)
            Map<String, String> params = experimentElement.getParameterValueMap();

            for (ExecutionTimeSlot ts : EnumSet.allOf(ExecutionTimeSlot.class)) {
                Integer tsOffset = maxDuration.getOrDefault(ts, 0);
                params.put(ts.name() + "_MINUTE", String.valueOf(tsOffset.equals(0) ? 0 : offset));
                offset += tsOffset;
            }
        }
    }

    /**
     * Calculates the experimentElement duration for each timeslot for this particular item.  If it is a 'One of' type item,
     * then the max duration for a given timeslot is used.
     *
     * @param experimentElement ExperimentElement to calculate durations for
     * @param platform Platform used by the experimentElement
     * @return Map from timeslot to duration
     */
    private EnumMap<ExecutionTimeSlot, Integer> calculateInstanceDuration(ExperimentElementImpl experimentElement, String platform) {
        EnumMap<ExecutionTimeSlot, Integer> em = EnumSet.allOf(ExecutionTimeSlot.class)
                .stream()
                .filter(ts -> {
                    try {
                        return experimentElement.getDesign()
                        .getScriptFiles().stream()
                        .anyMatch(script -> script.toUpperCase()
                                .startsWith(ts.name()));
                    } catch(IOException ioe ) {
                        logger.error("IOException while retrieving script files", ioe );
                    }
                    return false;
                })
                .collect(Collectors.toMap(Function.identity(),
                        ts -> getTimeSlotDuration(experimentElement, platform, ts, DEFAULT_SLOT_TIMINGS.getOrDefault(ts, 10 )),
                        Integer::sum,
                        () -> new EnumMap<>(ExecutionTimeSlot.class)));
        logger.trace("ExperimentDesignElement Map for {}: {}", experimentElement.getDesign(), em);
        return em;
    }

    /**
     * Calculates the durations of all time slots in a given node.  Durations for each slot are the sum of all
     * child instance durations at that particular slot.
     *
     * @param node Node to calculate durations for
     * @return Map from timeslot to duration .
     */
    private EnumMap<ExecutionTimeSlot, Integer> calculateNodeDuration(ExperimentElementImpl node) {
        String platform = node.getParameterValueMap().getOrDefault("platform", "").toLowerCase(); // FIXME: STRING: srogers

        //Gather all items in this node
        List<ExperimentElementImpl> items = new ArrayList<>();
        node.walk(items::add);
        items.remove(node);

        return items.stream()
                //Each item gets transformed into a Map<ExecutionTimeSlot,Duration> that
                //contains the duration of each slot in this item.
                .map(item -> calculateInstanceDuration(item, platform))
                .peek(m -> logger.info("ExperimentDesignElement Map: {}", m))
                //Split the map into individual Slot->Duration entries
                .flatMap(timeSlotIntegerEnumMap -> timeSlotIntegerEnumMap.entrySet().stream())
                //Sum up all the entries for each item in the node.
                .collect(Collectors.toMap(
                        Map.Entry<ExecutionTimeSlot, Integer>::getKey,
                        Map.Entry<ExecutionTimeSlot, Integer>::getValue,
                        Integer::sum,
                        () -> new EnumMap<>(ExecutionTimeSlot.class)
                ));
    }
}
