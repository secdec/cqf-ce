package com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.handlers;

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

import static org.apache.commons.lang3.Validate.isTrue;

import com.siegetechnologies.cqf.core._v01.experiment.ExperimentElementSpec;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionContext;
import com.siegetechnologies.cqf.core._v01.experiment.execution.ExperimentElementExecutionHandler;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultBuilder;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;
import com.siegetechnologies.cqf.core.util.Exceptions;
import com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.ExperimentExecutionToolkitForTestbedBase;
import com.siegetechnologies.cqf.testbed.base._v01.experiment.execution.util.copying.FileCopier;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.TestbedMachine;
import com.siegetechnologies.cqf.testbed.base.experiment.execution.util.OperatingSystemFamily;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The most basic element handler, responsible for copying item files to testbed machines
 * and retrieving result files from testbed machines.
 *
 * @param <R_before>
 * @param <R_main>
 *
 * @author taylorj
 */
public class BasicExecutionHandlerForTestbedBase<R_before, R_main> implements ExperimentElementExecutionHandler<R_before, R_main>
{
	private static final Logger logger = LoggerFactory.getLogger(BasicExecutionHandlerForTestbedBase.class);

	/**
	 * Map of execution-result builders, keyed by (root) execution context.
	 */
	private static final
	Map<ExperimentElementExecutionContext, ExecutionResultBuilder> executionResultBuilderMap = new HashMap<>();

	//^-- FIXME: REVIEW: MEMORY: COMPLEXITY: srogers: don't stash execution-result builders; create on demand and discard after use

	/**
	 * Content to use in place of files that cannot be retrieved.
	 */
	private static final byte[] ERROR_FILE_CONTENT = new byte[] {};

	/**/

	protected ExperimentExecutionToolkitForTestbedBase base_toolkit_of(ExperimentElementExecutionContext context) {

		return context.getExecutionToolkitAs(ExperimentExecutionToolkitForTestbedBase.class);
	}

	/**/

	@Override
	public String getDesignName() {
		return "";
	}

	@Override
	public String getDesignCategory() {
		return "";
	}

	/**/

	@Override
	public R_before initialize_beforeHook(ExperimentElementExecutionContext context) {

		logger.trace("BasicExecutionHandlerForTestbedBase.initialize_beforeHook({}, result={})", context, context.getResult());

		ExperimentDesignElementImpl designElement = context.getExperimentElement().getDesign();

		try {
			List<String> requiredFiles = designElement.getRequiredFiles();
			List<String> scriptFiles = designElement.getScriptFiles();

			if (! requiredFiles.isEmpty() || ! scriptFiles.isEmpty()) {

				FileCopier.copyFilesToTestbed(context);
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException("Unable to copy required and script files to guest.", e);
		}
		return null;
	}

	/**/

	/**
	 * Create a new execution-result builder for the root context if one doesn't already exist.
	 * Whichever element of the handler gets to an element from this tree first will
	 * create the builder for the root context, even if the current context isn't the
	 * root context.
	 */
	@Override
	public R_before retrieveData_beforeHook(ExperimentElementExecutionContext context) {

		logger.trace("BasicExecutionHandlerForTestbedBase.retrieveData_beforeHook({})", context);
		ExperimentElementExecutionContext rootContext = context.getRootContext();

		synchronized (executionResultBuilderMap) {
			executionResultBuilderMap.computeIfAbsent(rootContext, x -> {
				ExperimentElementSpec experimentElementSpec;
				try {
					experimentElementSpec = ExperimentElementSpec.from(context.getExperimentElement());
				}
				catch (IOException e) {
					throw new UncheckedIOException("Could not get element spec from element.", e);
				}
				return new ExecutionResultBuilder(experimentElementSpec);
			});
		}
		return null;
	}

	/**/

	/**
	 * Get the zip file from the enclosing context, if one exists,
	 * retrieve the result files specified by this element's design,
	 * and put them into the zip file.
	 *
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Override
	public R_main retrieveData(ExperimentElementExecutionContext context, R_before result_before) {

		logger.trace("BasicExecutionHandlerForTestbedBase.retrieveData({},{})", context, result_before);
		ExperimentElementExecutionContext root = context.getRootContext();

		ExecutionResultBuilder builder =
			Objects.requireNonNull(executionResultBuilderMap.get(root), "builder must not be null");

		ExperimentElementImpl experimentElement = context.getExperimentElement();
		String experimentElementId = experimentElement.getId().value();

		ExperimentElementSpec rootElementSpec = builder.getConfiguration();
		ExperimentElementSpec experimentElementSpec = findElementSpecById(experimentElementId, rootElementSpec);

		updateSpecification(experimentElementSpec, context);

		Set<ResultFileImpl> resultFiles = context.getExperimentElement().getDesign().getResultFiles();

		if (! resultFiles.isEmpty()) {

			TestbedMachine testbedMachine = context.getResult(TestbedMachine.class);
			OperatingSystemFamily testbedMachine_osFamily = testbedMachine.getFamily();

			logger.trace("* resultFiles: {}", context.getExperimentElement().getDesign().getResultFiles());

			for (ResultFileImpl resultFile : resultFiles) {

				OperatingSystemFamily resultFile_osFamily = getOperatingSystemFamilyForResultFile(resultFile);

				if (resultFile_osFamily != null && resultFile_osFamily != testbedMachine_osFamily) {

					logger.trace("Skipping result file {}({}) on {}.",
						resultFile, resultFile_osFamily, testbedMachine_osFamily
					);
					continue;
				}

				logger.trace("Including result file {}({}) on {}.",
					resultFile, resultFile_osFamily, testbedMachine_osFamily
				);
				String hostPathname = testbedMachine.getFamily().separatorsToFamily(resultFile.getHostPath());

				byte[] content;
				try {
					CompletableFuture<byte[]> download = testbedMachine.download(hostPathname);
					content = download.get();
				}
				catch (Exception e) {
					logger.debug("Exception while downloading {}, result file will be empty.[{}]",
						hostPathname, Exceptions.getSummary(e)
					);
					content = ERROR_FILE_CONTENT;
				}

				resultFile.setContent(content);

				builder.addFileForElement(experimentElementId, resultFile);
				//^-- FIXME: MEMORY: srogers: save content to temporary file on disk
			}
		}

		R_main result = ExperimentElementExecutionHandler.super.retrieveData(context, result_before);
		return result;
	}

	@Override
	public ExecutionResultImpl retrieveData_afterHook(ExperimentElementExecutionContext context, R_main result_main) {

		if (context != context.getRootContext()) {
			return null;
		}

		ExecutionResultBuilder executionResultBuilder = executionResultBuilderMap.get(context);
		Objects.requireNonNull(executionResultBuilder, "builder must not be null");

		ExecutionResultImpl result = executionResultBuilder.build();
		logger.trace("Result:\n{}", result);
		return result;
	}

	/**/

	/**
	 * Returns the element specification with a provided ID inside a root
	 * element specification. The found specification may be the same as the
	 * root specification.
	 *
	 * @param id
	 * 		the ID
	 * @param rootElementSpec
	 * 		the root spec
	 *
	 * @return the element spec with the specified ID
	 *
	 * @throws NoSuchElementException
	 * 		if there is no element specification with the provided ID
	 */
	protected ExperimentElementSpec findElementSpecById(String id, ExperimentElementSpec rootElementSpec) {

		return (getElementSpecs(rootElementSpec)
			.filter(spec -> Objects.equals(id, spec.getId()))
			.findFirst()
			.orElseThrow(() ->
				new NoSuchElementException("No element spec with ID " + id + " in tree."))
		);
	}

	/**
	 * Returns a stream of all the element specifications within a root
	 * element specification, including the root element specification. The
	 * element specifications are provided in pre-order traversal. That is, the
	 * root is first, then its first child, then the first child's child, and so
	 * on.
	 *
	 * @param root
	 * 		the root specification
	 *
	 * @return the stream
	 */
	protected Stream<ExperimentElementSpec> getElementSpecs(ExperimentElementSpec root) {

		return Stream.concat(Stream.of(root), root.getChildren().stream().flatMap(this::getElementSpecs));
	}

	/**/

	/**
	 * Iterate through the parameters of an element specification, updating
	 * each one with the corresponding value retrieved from an element context.
	 * This has the effect of setting the child specification's parameters to
	 * the runtime values, which may replace substitution parameters with
	 * concrete values.
	 *
	 * @param specification
	 * 		the specification
	 * @param context
	 * 		the element context corresponding to the specification
	 *
	 * @see ExperimentElementExecutionContext#getInstanceParameter(String)
	 */
	protected void updateSpecification(ExperimentElementSpec specification, ExperimentElementExecutionContext context) {

		String specId = specification.getId();
		String contextId = context.getExperimentElement().getId().value();

		isTrue(Objects.equals(specId, contextId),
			"ID of element specification and context's element should be the same, "
				+ "but were %s (for specification) and %s (for context).", specId, contextId
		);
		for (Map.Entry<String, String> e : specification.getParameters().entrySet()) {

			e.setValue(context.getInstanceParameter(e.getKey()));
		}
	}

	/**/

	private static OperatingSystemFamily getOperatingSystemFamilyForResultFile(ResultFileImpl resultFile) {

		switch (resultFile.getPlatform().toUpperCase()) {
		case "WINDOWS":
			return OperatingSystemFamily.WINDOWS;
		case "UNIX":
			return OperatingSystemFamily.UNIX;
		case "NULL":
			return null;
		default:
			throw new AssertionError("Unknown platform: \"" + resultFile.getPlatform() + "\".");
		}

	}

	//^-- TODO: REFACTOR: srogers: extract as OperatingSystemFamily.parse(String value)

}
