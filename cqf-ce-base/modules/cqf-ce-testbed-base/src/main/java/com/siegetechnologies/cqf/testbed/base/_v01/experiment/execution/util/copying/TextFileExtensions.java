package com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.util.copying;

/*-
 * #%L
 * cqf-ce-testbed-base
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
 * An enumeration of extensions of files considered 
 * text files by the CQF.
 * 
 * @author taylorj
 */
enum TextFileExtensions {
	/**
	 * Unix shell scripts (typically sh or a variant (bash, dash, etc.)) 
	 */
	SH,
	
	/**
	 * Windows batch scripts.
	 */
	BAT,
	
	/**
	 * Text files 
	 */
	TXT,
	
	/**
	 * Configuration files 
	 */
	CONF,
	
	/**
	 * Script file for Windows Scripting Host
	 */
	SCT
}
