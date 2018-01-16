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

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;

/**
 * Utility class for generating random identifiers, ala UUIDs, but with
 * methods for generating shorter identifiers.
 *
 * @author taylorj
 */
public class Identifiers {
	
	private static final SecureRandom random = new SecureRandom();
    private static final Encoder encoder = Base64.getUrlEncoder();

    private Identifiers() {}

	/**
     * Returns a randomly generated identifer based on a number
     * of bytes.  Note that the number of bytes is not the same
     * as the length of the resulting identifier; the identifier
     * is a URL-safe Base64 encoding of the bytes.  In general,
     * the length of the identifier will be greater than the
     * number of bytes.
     *
     * @param numberOfBytes the number of bytes to use
     * @return the identifier
     */
    public static String randomIdentifier(int numberOfBytes) {
        byte[] bytes = new byte[numberOfBytes];
        random.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }
}
