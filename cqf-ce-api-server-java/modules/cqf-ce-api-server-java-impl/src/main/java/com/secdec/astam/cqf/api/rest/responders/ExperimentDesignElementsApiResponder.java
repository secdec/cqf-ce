package com.secdec.astam.cqf.api.rest.responders;

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

import static com.siegetechnologies.cqf.core.util.Strings.matchesAgainstCandidate;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status;

import com.secdec.astam.cqf.api.rest.ExperimentDesignElementsApi;
import com.secdec.astam.cqf.api.rest.app.CQFApplication;
import com.secdec.astam.cqf.api.rest.app.CQFResourceManager;
import com.secdec.astam.cqf.api.rest.responders.util.ResponderToolkit;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignCatalogImpl;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.ws.rs.core.Response;

/**
 * Responds to REST API requests pertaining to ExperimentDesignElement resources.
 *
 * @author srogers
 *
 */
public class ExperimentDesignElementsApiResponder extends ExperimentDesignElementsApi {

	private static final ResponderToolkit toolkit = new ResponderToolkit();

	private static final Logger logger = Logger.getLogger(ExperimentDesignElementsApiResponder.class.getName());

	protected final CQFResourceManager resourceManager; // injected

	public ExperimentDesignElementsApiResponder() {

		this(CQFApplication.theInstance().getResourceManager());
	}

	public ExperimentDesignElementsApiResponder(CQFResourceManager resourceManager) {

		logger.log(Level.FINE, "creating: {0}", this);

		this.resourceManager = resourceManager;
	}

	@Override
	public Response getExperimentDesignElement(String name) {

		if (name == null) {

			return Response.status(Status.BAD_REQUEST).build();
		}

		ExperimentDesignCatalogImpl delegateCatalog = resourceManager.getMainDesignCatalog();
		assert ! delegateCatalog.getItems().isEmpty();

		List<?> resultList = (delegateCatalog.getItems().stream()

				.filter(delegateId -> matchesAgainstCandidate(delegateId.value(), name))

				.map(delegateId -> getDelegateFrom(delegateCatalog, delegateId))

				.map(delegate -> toolkit.newExperimentDesignElementFromDelegate(delegate))

				.collect(toList())
		);

		switch (resultList.size()) {
		case 0:
			return Response.status(Status.NOT_FOUND).build();

		case 1:
			return Response.ok().entity(resultList.get(0)).build();

		case 2:
		default:
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public Response getExperimentDesignElements(String subtypeRegexp, String nameRegexp) {

		final Pattern compiledSubtypePattern;
		try {
			compiledSubtypePattern =
					(subtypeRegexp == null) ? null : Pattern.compile(subtypeRegexp);

		} catch (PatternSyntaxException xx) {

			return Response.status(Status.BAD_REQUEST).build();
		}

		final Pattern compiledNamePattern;
		try {
			compiledNamePattern =
					(nameRegexp == null) ? null : Pattern.compile(nameRegexp);

		} catch (PatternSyntaxException xx) {

			return Response.status(Status.BAD_REQUEST).build();
		}

		ExperimentDesignCatalogImpl delegateCatalog = resourceManager.getMainDesignCatalog();
		assert ! delegateCatalog.getItems().isEmpty();

		List<?> resultList = (delegateCatalog.getItems().stream()

				.filter(delegateId -> matchesAgainstCandidate(delegateId.value(), compiledNamePattern))

				.map(delegateId -> getDelegateFrom(delegateCatalog, delegateId))

				.map(delegate -> toolkit.newExperimentDesignElementFromDelegate(delegate))

				.filter(model -> matchesAgainstCandidate(model.getSubtype(), compiledSubtypePattern))

				.collect(toList())
		);

		switch (resultList.size()) {
		case 0:
			return Response.status(Status.NOT_FOUND).build();

		case 1:
		default:
			return Response.ok().entity(resultList).build();
		}
	}

	protected ExperimentDesignElementImpl getDelegateFrom(ExperimentDesignCatalogImpl catalog, ExperimentDesignElementId delegateId) {

		Optional<ExperimentDesignElementImpl> optionalDelegate = catalog.resolve(delegateId);
		if (! optionalDelegate.isPresent()) {
			throw new IllegalStateException("a design catalog returned a reference to an item that it cannot fetch");
		}

		return optionalDelegate.get();
	}

}
