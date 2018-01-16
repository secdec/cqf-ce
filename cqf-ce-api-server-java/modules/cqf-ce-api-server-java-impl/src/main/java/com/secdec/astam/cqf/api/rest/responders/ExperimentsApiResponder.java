package com.secdec.astam.cqf.api.rest.responders;

import static com.siegetechnologies.cqf.core.util.Strings.matchesAgainstCandidate;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status;

import com.secdec.astam.cqf.api.models.ExperimentElement;
import com.secdec.astam.cqf.api.rest.ExperimentsApi;
import com.secdec.astam.cqf.api.rest.app.CQFApplication;
import com.secdec.astam.cqf.api.rest.app.CQFResourceManager;
import com.secdec.astam.cqf.api.rest.responders.util.ResponderToolkit;
import com.siegetechnologies.cqf.core.experiment.ExperimentId;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.ws.rs.core.Response;

/*-
 * #%L
 * cqf-ce-api-server-java-impl
 * %%
 * Copyright (C) 2016 - 2017 Applied Visions, Inc.
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
 * Responds to REST API requests pertaining to Experiment resources.
 *
 * @author srogers
 * @author taylorj
 */
public class ExperimentsApiResponder extends ExperimentsApi {

	private static final ResponderToolkit toolkit = new ResponderToolkit();

	private static final Logger logger = Logger.getLogger(ExperimentDesignCatalogsApiResponder.class.getName());

	protected final CQFResourceManager resourceManager; // injected

	/**
	 * Creates a new responder that uses the default (singleton) resource manager.
	 */
	public ExperimentsApiResponder() {

		this(CQFApplication.theInstance().getResourceManager());
	}

	/**
	 * Creates a new responder that uses the specified resource manager.
	 *
	 * @param resourceManager the resource manager
	 */
	public ExperimentsApiResponder(CQFResourceManager resourceManager) {

		logger.log(Level.FINE, "creating: {0}", this);

		this.resourceManager = resourceManager;
	}

	@Override
	public Response createAndExecuteExperiment(ExperimentElement experimentRoot) {

		ExperimentImpl delegate = toolkit.newExperimentImplFromModel(experimentRoot, resourceManager);
		try {

			resourceManager.getExperimentManager().execute(delegate);
		}
		catch (Throwable xx) {
			logger.log(Level.SEVERE, "execution of experiment failed", xx);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		com.secdec.astam.cqf.api.models.ExperimentElement result =
				toolkit.newExperimentElementFromDelegate(delegate.getRoot(), resourceManager);

		Response response = Response.ok().entity(result).build();
		assert result == response.getEntity();

		return response;
	}

	@Override
	public Response getExperiment(String id_value) {

		if (id_value == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		ExperimentId id = new ExperimentId(id_value);
		Optional<ExperimentImpl> delegate_maybe = resourceManager.getExperimentManager().resolve(id);
		//^-- FIXME: REVIEW: srogers: should this be in a try/catch block, or will passing through an exception do the right thing?
		if (! delegate_maybe.isPresent()) {
			return Response.status(Status.NOT_FOUND).build();
		}

		ExperimentElement result = toolkit.newExperimentElementFromDelegate(
				delegate_maybe.get().getRoot(), resourceManager
		);
		return Response.ok().entity(result).build();
	}

	@Override
	public Response getExperiments(String executionPhaseRegexp) {

		final Pattern compiledExecutionPhasePattern;
		try {
			compiledExecutionPhasePattern =
					(executionPhaseRegexp == null) ? null : Pattern.compile(executionPhaseRegexp);
		}
		catch (PatternSyntaxException xx) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		List<?> resultList = (resourceManager.getExperimentManager().getExperiments().stream()

				.filter(delegate -> matchesAgainstExecutionPhase(
						delegate.getCurrentPhase(), compiledExecutionPhasePattern))

				.map(delegate -> toolkit.newExperimentElementFromDelegate(
						delegate.getRoot(), resourceManager))

				.collect(toList())
		);

		Response response;
		switch (resultList.size()) {
		case 0:
			response = Response.status(Status.NOT_FOUND).build();
			break;

		case 1:
		default:
			response = Response.ok().entity(resultList).build();
			assert resultList == response.getEntity();
			break;
		}

		return response;
	}

	protected static boolean matchesAgainstExecutionPhase(ExecutionPhase candidate, Pattern pattern) {

		return matchesAgainstCandidate(candidate.getValue(), pattern);
	}

}

