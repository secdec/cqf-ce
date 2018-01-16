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

/**
 * Utility methods for dealing with HTTP status codes.
 * 
 * @author taylorj
 */
public class HttpStatusCodes {
	
	private HttpStatusCodes() {}
	
    /**
     * Returns true if the code is in the 2xx range.
     * 
     * @param status the status code
     * 
     * @return whether the code is in the 2xx range
     */
    public static boolean isSuccess(int status) {
        return 200 <= status && status < 300;
    }
}
