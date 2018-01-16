package com.siegetechnologies.cqf.core.experiment.design.util;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Documentation for an item.
 * 
 * @author taylorj
 */
public class DocumentationImpl {
	public static final Logger logger = LoggerFactory.getLogger(DocumentationImpl.class);

	/**
	 * Enumerated documentation line types.
	 *
	 * @author taylorj
	 */
	public enum Tag {
		/**
		 * The "@info" tag
		 */
		INFO("info", false), // FIXME: STRING: srogers

		/**
		 * The "@param" tag
		 */
		PARAM("param", true), // FIXME: STRING: srogers

		/**
		 * Any "@something" tag not otherwise specified
		 */
		UNKNOWN("unknown", true); // FIXME: STRING: srogers

		private final String tagName;
		private final boolean hasParam;

		Tag(String name, boolean hasParam) {
			this.tagName = name;
			this.hasParam = hasParam;
		}

		/**
		 * Returns whether the documentation tag accepts a parameter. If a tag
		 * accepts a parameter, then it is used as "@tag param value...". If it
		 * does not, then it is used as "@tag value...".
		 *
		 * @return whether the tag accepts a parameter
		 */
		public boolean hasParam() {
			return this.hasParam;
		}

		/**
		 * Returns the name of this tag.
		 *
		 * @return the name of this tag
		 */
		@SuppressWarnings("unused")
		public String getTagName() {
			return this.tagName;
		}
	}

	private String info;
	private Map<String, String> params;
	private Map<String, String> unknowns;

	/**
	 * Returns item documentation read from an input stream.
	 *
	 * @param in the input stream
	 * @return the item documentation
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public static DocumentationImpl fromInputStream(InputStream in) throws IOException {
		try (InputStreamReader r = new InputStreamReader(in)) {
			return fromReader(r);
		}
	}

	/**
	 * Returns new item documentation read from a reader.
	 *
	 * @param r the reader
	 * @return the item documentation
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public static DocumentationImpl fromReader(Reader r) throws IOException {
		DocumentationImpl ret = new DocumentationImpl();
		StreamTokenizer st = new StreamTokenizer(r);
		st.resetSyntax();
		st.whitespaceChars(0x00, 0x20);
		st.wordChars(0x21, 0xFF);
		st.quoteChar('"');
		StringBuilder sb = new StringBuilder();
		Tag current = Tag.INFO;
		String param = "";
		boolean gettingParam = false;
		String text = "";
		while (st.nextToken() != StreamTokenizer.TT_EOF) {
			if (st.sval == null) {
				continue;
			}
			if (st.sval.startsWith("@")) {
				if (gettingParam) {
					param = sb.toString()
							.trim();
				}
				else {
					text = sb.toString()
							.trim();
				}
				ret.set(current, param, text);
				sb = new StringBuilder();
				try {
					current = Tag.valueOf(st.sval.substring(1).toUpperCase());
				}
				catch (IllegalArgumentException e) {
					logger.warn("Encountered illegal tag value ({}), treating as unknown.", st.sval, e);
					current = Tag.UNKNOWN;
					param = st.sval;
				}

				if (current.hasParam()) {
					gettingParam = true;
					if (st.nextToken() != StreamTokenizer.TT_EOF) {
						sb.append(st.sval)
								.append(" ");
					}
				}
			}
			else if (gettingParam && "-".equals(st.sval)) {
				gettingParam = false;
				param = sb.toString()
						.trim();
				sb = new StringBuilder();
			}
			else {
				sb.append(st.sval)
						.append(" ");
			}
		}

		if (gettingParam) {
			param = sb.toString()
					.trim();
		}
		else {
			text = sb.toString()
					.trim();
		}

		ret.set(current, param, text);

		return ret;
	}

	/**
	 * Default constructor.
	 */
	public DocumentationImpl() {
		this(null, null, null);
	}

	/**
	 * Returns a new item (unparented) documentation object with provided fields.
	 *
	 * @param info the information string
	 * @param params the item parameter documentation
	 * @param unknowns the unknown documentation
	 * @return the item documentation
	 */
	public DocumentationImpl(String info, Map<String, String> params, Map<String, String> unknowns) {
		if (info == null) {
			info = "";
		}
		if (params == null) {
			params = new HashMap<>();
		}
		if (unknowns == null) {
			unknowns = new HashMap<>();
		}

		this.info = info;
		this.params = params;
		this.unknowns = unknowns;
	}

	/**
	 * Sets a parameter value for a tag on this documentation.
	 *
	 * @param tag the tag
	 * @param parameter the parameter
	 * @param value the value
	 */
	public void set(Tag tag, String parameter, String value) {
		switch (tag) {
		case INFO:
			this.setInfo(value);
			break;
		case PARAM:
			this.setParam(parameter, value);
			break;
		case UNKNOWN:
			this.setUnknown(parameter, value);
			break;
		default:
			throw new AssertionError("Unrecognized tag: "+tag+".");
		}
	}
	
	/**
	 * sets experiment documentation information 
	 * @param info
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
	/**
	 * Returns documentation information.
	 * 
	 * @return documentation information
	 */
	public String getInfo() {
		return this.info;
	}
	
 	/**
	 * Sets documentation parameter tags 
	 * @param paramName
	 * @param paramValue
	 */
	public void setParam(String paramName, String paramValue) {
		this.params.put(paramName, paramValue);
	}
	
	/**
	 * Returns map of documentation tag parameters.
	 * 
	 * @return map of documentation tag parameters
	 */
	public Map<String, String> getParams() {
		return this.params;
	}

	public void setUnknown(String unknownTag, String unknownValue) {
		this.unknowns.put(unknownTag, unknownValue);
	}

	public Map<String, String> getUnknowns() {
		return this.unknowns;
	}

	public String toString() {
		return String.format("Documentation(info=%s, params=%s, unknowns=%s)",
				getInfo(),
				getParams(),
				getUnknowns());
	}
	
	/**
	 * The builder for documentation
	 */
	public static class Builder {

		static class BuilderImplementation implements Paramer {

			private final String info;
			private final Map<String,String> params;

			BuilderImplementation(String info) {
				this.info = info;
				this.params = new TreeMap<>();
			}

			@Override
			public Paramer param(String name, String value) {
				if (params.containsKey(name)) {
					throw new IllegalStateException("Parameter documenation has already been specified for "+name+".");
				}
				params.put(name, value);
				return this;
			}

			@Override
			public DocumentationImpl build() {
				return new DocumentationImpl(info, params, null);
			}

		}

		public static Paramer info(String info) {
			return new BuilderImplementation(info);
		}
		
		/**
		 * 
		 */
		public interface Paramer {
			Paramer param(String name, String value);

			DocumentationImpl build();
		}

	}
}
