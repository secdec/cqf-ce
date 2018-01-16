package com.siegetechnologies.cqf.testbed.vsphere._v01.experiment.design.elements.util;

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

import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core._v01.experiment.design.ExperimentDesignElementSpec;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementIdResolver;

import java.util.NoSuchElementException;

/**
 * An enumeration of symbolic item dependencies. Each enumeration element provides access to an
 * ExperimentDesignElementSpec for the designed item.
 * 
 * @author taylorj
 */
public enum DesignElementSpecEnum {
  /** Result sensor */
  RESULT("Result", "Sensor"),
  
  /** Virtual switch */
  VIRTUAL_SWITCH("Virtual Switch", "Hardware"),

  /** Port group */
  PORT_GROUP("Port Group", "Hardware"),

  /** Clone VM */
  CLONE_VM("Clone VM", "Node"),

  /** CQF Scheduler */
  SCHEDULER("Scheduler", "CQF"),
  
  /** SSH server software */
  SSH("SSH", "Software"),

  /** Static IP */
  STATIC_IP("Static IP", "Hardware"),
  
  /** Run Command utility */
  RUN_COMMAND("Run Command", "Util"),

  /** Network interface card */
  NETWORK_INTERFACE_CARD("Network Interface Card", "Hardware"),

  /** Dot CMS Attack */
  DOT_CMS_ATTACK("ESM 7", "Attacker"),

  /** Dot CMS Server */
  DOT_CMS_SERVER("DotCMS", "Attackee");

  private final ExperimentDesignElementSpec spec;

  private DesignElementSpecEnum(String name, String category) {
    this.spec = ExperimentDesignElementSpec.of(name, category);
  }

  /**
   * Returns the item spec of the enumeration element.
   * 
   * @return the item spec
   */
  public ExperimentDesignElementSpec getItemSpec() {
    return this.spec;
  }

  /**
   * Returns an item from the resolver for the id for the spec.
   * 
   * @param resolver the resolver
   * @return the item
   */
  public ExperimentDesignElementImpl getItem(ExperimentDesignElementIdResolver<? extends ExperimentDesignElementImpl> resolver) {
    ExperimentDesignElementId id = ExperimentDesignElementId.of(getItemSpec());
    return resolver.resolve(id)
        .map(ExperimentDesignElementImpl.class::cast)
        .orElseThrow(() -> new NoSuchElementException("No item for id: " + id));
  }
}
