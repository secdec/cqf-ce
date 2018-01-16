/**
 *  Copyright (c) 2016 Siege Technologies.
 */
package com.siegetechnologies.cqf.core.util;

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

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

/**
 * Class with static methods for loading the CQF configuration and providing
 * access to configuration values during runtime.
 * 
 * @author taylorj
 */
public class Config {
	
	private static final Logger logger = LoggerFactory.getLogger(Config.class); 

    private static final String CONFIGURATION_DEFAULTS_RESOURCE = "/cqf.defaults.xml"; // FIXME: STRING: srogers
    protected static final String DEFAULT_CONFIGURATION_RESOURCE = "/cqf.xml"; // FIXME: STRING: srogers
    protected static final String CONFIGURATION_FILE_PROPERTY = "cqf.configuration"; // FIXME: STRING: srogers
    
    private static CompositeConfiguration configuration = null;

    private Config() {}
    
    /**
     * Loads the initial configuration.  This is essentially the same as 
     * {@link #loadConfiguration()}, but catches the exceptions and wraps
     * them in {@link UncheckedIOException}s.
     * 
     * @return the initial configuration
     * 
     * @see #loadConfiguration()
     */
    private static CompositeConfiguration initConfiguration() {
    	try {
    		return loadConfiguration();
		}
    	catch (ConfigurationException e) {
    		throw new UncheckedIOException(new IOException(e));
    	}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
    }
    
    /**
     * Loads and returns configuration data for the CQF.  This is used by {@link Config} to
     * populate the produce the value later returned by calls to {@link Config#getConfiguration()}.
     * This method first checks whether a system property with the name given by 
     * {@link #CONFIGURATION_FILE_PROPERTY} is defined.  If it is, then it is interpreted as a
     * a filename, and loaded as the configuration.  Otherwise, the classpath is checked for 
     * a resource named by {@link #DEFAULT_CONFIGURATION_RESOURCE}.  It it is present, then
     * it is loaded as the configuration.  If it is not, then the configuration is an empty
     * configuration.  In either of the cases that a configuration is available (either 
     * as a file, or a classpath resource), if loading the configuration throws a
     * {@link ConfigurationException}, then an {@link Error} wrapping the exception is 
     * thrown.
     * 
     * @return the CQF configuration
     * @throws ConfigurationException if a configuration exception occurs
     * @throws IOException  if an I/O error occurs
     */
    protected static CompositeConfiguration loadConfiguration() throws ConfigurationException, IOException {
        Configuration userConfiguration = loadUserConfiguration();
        Configuration configurationDefaults = loadConfigurationDefaults();
        CompositeConfiguration composite = new CompositeConfiguration(Arrays.asList(userConfiguration,configurationDefaults));
        show(composite);
        return composite;
    }

    /**
     * Logs the keys and values from the configuration.
     * 
     * @param configuration the configuration
     */
    private static void show(AbstractConfiguration configuration) {
		if (logger.isDebugEnabled()) {
			Iterator<String> keys = configuration.getKeys(); 
			while (keys.hasNext()) {
				String key = keys.next();
				logger.debug("{}\t{}", key, configuration.getString(key));
			}
		}
	}
    
    /**
	 * Loads the user configuration, either from the location specified by the
	 * {@link #CONFIGURATION_FILE_PROPERTY}, if it is non-null, or else from the
	 * {@link #DEFAULT_CONFIGURATION_RESOURCE}.
	 * 
	 * @return the user configuration
	 * 
	 * @throws ConfigurationException if an configuration error occurs
	 * @throws IOException if an I/O error occurs
	 */
    private static Configuration loadUserConfiguration() throws ConfigurationException, IOException {
        Properties properties = System.getProperties();
        String configFile = properties.getProperty(CONFIGURATION_FILE_PROPERTY);
        
        // Load user specified values from classpath or file
        XMLConfiguration cqfXmlConfiguration = new XMLConfiguration();
        cqfXmlConfiguration.setValidating(false);
        
        if (configFile != null) {
        	logger.info("Using user-specified configuration path: '{}'...", configFile);
            try {
            	Configuration config = new FileBasedConfigurationBuilder<>(XMLConfiguration.class).configure(new Parameters().xml()
						.setValidating(false)
						.setFileName(configFile))
						.getConfiguration();
                logger.info("Loaded user-specified configuration path: {}.", configFile);
                return config;
            } catch (ConfigurationException e) {
                logger.error("Unable to load configuration file, {}.", configFile, e);
                throw e;
            }
        }
        else {
            URL url = Config.class.getResource(DEFAULT_CONFIGURATION_RESOURCE);
            if (url == null) {
                logger.warn("No default configuration resource, {}, found on classpath.",DEFAULT_CONFIGURATION_RESOURCE);
                logger.warn("The CQF will be mostly unusable without configuration. Please initialize the CQF configuration.");
            }
            else {
                try {
                	logger.info("Using default configuration url: '{}'...", url);
                	Configuration config = new FileBasedConfigurationBuilder<>(XMLConfiguration.class).configure(new Parameters().xml()
    						.setValidating(false)
    						.setURL(url))
    						.getConfiguration();
                	logger.info("Loaded default configuration url: '{}'.", url);
                	return config;
                } catch ( RuntimeException e) {
                    logger.error("Unable to load default configuration resource, {}.",DEFAULT_CONFIGURATION_RESOURCE, e);
                    throw e;
                }
            }
        }
        return cqfXmlConfiguration;
    }

    /**
     * Load configuration defaults from the classpath.
     * 
     * @return a configuration containing the default values
     * 
     * @throws IOException if an I/O error occurs
     * @throws ConfigurationException if a configuration error occurs
     */
    static Configuration loadConfigurationDefaults() throws ConfigurationException {
        URL defaultsUrl = Config.class.getResource(CONFIGURATION_DEFAULTS_RESOURCE);
		return new FileBasedConfigurationBuilder<>(XMLConfiguration.class).configure(new Parameters().xml()
				.setURL(defaultsUrl)
				.setValidating(false))
				.getConfiguration();
    }

	/**
	 * Resets the CQF configuration.
	 *
	 * Used only for testing.
	 */
	public static void resetConfiguration() {

		setConfiguration(null);
	}
	//^-- FIXME: BUG: srogers: not thread safe

	/**
	 * Sets the CQF configuration.
	 *
	 * Used only for testing.
	 */
	public static void setConfiguration(CompositeConfiguration value) {

		configuration = value;
	}
	//^-- FIXME: BUG: srogers: not thread safe

	/**
     * Returns the CQF configuration.  
     * 
     * @see #loadConfiguration()
     * 
     * @return the CQF configuration
     */
    public static CompositeConfiguration getConfiguration() {
    	if (configuration == null) {
    		configuration = initConfiguration();
    	}

    	return configuration;
    }
	//^-- FIXME: BUG: srogers: not thread safe

}
