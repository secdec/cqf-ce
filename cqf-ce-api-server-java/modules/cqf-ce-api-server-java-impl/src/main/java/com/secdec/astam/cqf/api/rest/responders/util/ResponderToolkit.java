package com.secdec.astam.cqf.api.rest.responders.util;

/*-
 * #%L
 * astam-cqf-ce-api-server-java-impl
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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.secdec.astam.cqf.api.models.ExecutionState;
import com.secdec.astam.cqf.api.models.ExecutionTraceRecord;
import com.secdec.astam.cqf.api.models.ExperimentDesignCatalog;
import com.secdec.astam.cqf.api.models.ExperimentDesignElement;
import com.secdec.astam.cqf.api.models.ExperimentDesignElementRef;
import com.secdec.astam.cqf.api.models.ExperimentElement;
import com.secdec.astam.cqf.api.models.ExperimentElementRef;
import com.secdec.astam.cqf.api.models.Parameter;
import com.secdec.astam.cqf.api.models.ParameterBinding;
import com.secdec.astam.cqf.api.rest.app.CQFResourceManager;
import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentElementImpl;
import com.siegetechnologies.cqf.core.experiment.ExperimentId;
import com.siegetechnologies.cqf.core.experiment.ExperimentImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementId;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.catalog.ExperimentDesignCatalogImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.DocumentationImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ParameterImpl;
import com.siegetechnologies.cqf.core.experiment.design.util.ResultFileImpl;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionTraceRecordImpl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A back of tricks helpful to implementors of responders for the CQF REST API.
 * Mostly they are model-to-delegate converters, and vice versa.
 *
 * @author srogers
 */
public class ResponderToolkit
{
	private static final Logger logger = LoggerFactory.getLogger(ResponderToolkit.class);
	
	public ResponderToolkit() {}

	/**/
	
	public ExperimentImpl newExperimentImplFromModel(
			ExperimentElement rootElement,
			CQFResourceManager resourceManager
	) {
		ExperimentElementImpl result_root = newExperimentElementImplFromModel(
				rootElement, null, resourceManager
		);
		String result_name = rootElement.getId();
		
		ExperimentImpl result = new ExperimentImpl(result_root, result_name);
		return result;
	}

	/**/
	
	public ExperimentDesignCatalog newExperimentDesignCatalogFromDelegate(ExperimentDesignCatalogImpl delegate) {
		
		List<ExperimentDesignElementRef> items = (delegate.getItems().stream()
				
				.map(id -> this.newExperimentDesignElementRefFromDelegate(id, 0))
				
				.collect(Collectors.toList())
		);
		
		com.secdec.astam.cqf.api.models.ExperimentDesignCatalog result = new ExperimentDesignCatalog();
		result.name("main").description("")
				.items(items); // FIXME: srogers: add property "name" to ExperimentDesignCatalog(?)
		
		return result;
	}

	/**/
	
	public ExperimentDesignElement newExperimentDesignElementFromDelegate(ExperimentDesignElementImpl delegate) {
		
		DocumentationImpl delegate_documentation = delegate.getDocumentation();
		
		String model_description = delegate_documentation.getInfo();
		String model_documentation = newDocumentationFromDelegate(delegate_documentation);
		
		List<Parameter> model_parameters = newParameterListFromDelegates(delegate.getParameters(), delegate_documentation);
		List<ParameterBinding> model_parameterBindings = newDefaultParameterBindingListFromDelegates(
				delegate.getParameters());
		
		ExperimentDesignElement result = (new ExperimentDesignElement()
				.name(delegate.getId().value())
				.category(delegate.getCategory())
				.parameters(model_parameters)
				.defaultParameterBindings(model_parameterBindings)
				.description(model_description)
				.documentation(model_documentation)
				.subtype("unknown_subtype") // FIXME: srogers: do we still need subtype??
				.children(Collections.emptyList())
		);
		return result;
	}
	
	public ExperimentDesignElementRef newExperimentDesignElementRefFromDelegate(ExperimentDesignElementId id, int depth) {
		if (depth != 0) {
			throw new IllegalArgumentException("only a depth of 0 is currently supported");
		}
		//^-- FIXME: srogers: add support for ExperimentDesignElementRef depths +1, -1
		
		ExperimentDesignElementRef result = new ExperimentDesignElementRef();
		result.objectKey(id.value()); // FIXME: srogers: should be design element dotted-name, not ID
		result.object(null);
		
		return result;
	}

	/**/
	
	public ExperimentElementImpl newExperimentElementImplFromModel(
			ExperimentElement modelObject,
			ExperimentElementImpl delegateParent,
			CQFResourceManager resourceManager
	) {
		assert modelObject != null;
		assert resourceManager != null;
		
		String delegateDesignName = modelObject.getDesign().getObjectKey();
		ExperimentDesignElementId delegateDesignId = new ExperimentDesignElementId(delegateDesignName);
		
		Optional<ExperimentDesignElementImpl> optionalDelegateDesignItem =
				resourceManager.getMainDesignCatalog().resolve(delegateDesignId);
		
		ExperimentElementImpl delegate = new ExperimentElementImpl(optionalDelegateDesignItem.get(), delegateParent);
		
		Map<String, String> delegate_parameters = delegate.getParameterValueMap();
		modelObject.getParameterBindings().forEach(
				pb -> delegate_parameters.put(pb.getName(), pb.getValue())
		);
		
		List<ExperimentElementImpl> delegate_children = delegate.getChildren();
		modelObject.getChildren().stream()
				
				.map(modelObject_childRef -> resolveExperimentElementImplFromModelRef(
						modelObject_childRef, delegate, resourceManager))
				
				.forEach(delegate_children::add);
		
		return delegate;
	}
	
	public ExperimentElement newExperimentElementFromDelegate(
			ExperimentElementImpl delegate,
			CQFResourceManager resourceManager
	) {
		ExperimentElementImpl delegate_root = findRootStartingFromExperimentElement(delegate);
		ExperimentId delegate_experimentId = new ExperimentId(delegate_root.getId().value());
		
		ExperimentDesignElementRef design = (new ExperimentDesignElementRef()
				.objectKey(delegate.getDesign().getId().value())
				.object(null)
		);
		List<ParameterBinding> parameterBindings = (delegate.getParameterValueMap().entrySet().stream()
				
				.map(nv -> new ParameterBinding().name(nv.getKey()).value(nv.getValue()))
				
				.collect(toList())
		);
		List<ExperimentElementRef> children = (delegate.getChildren().stream()
				
				.map(child_delegate -> newExperimentElementRefFromDelegate(child_delegate, resourceManager))
				
				.collect(toList())
		);
		ExecutionState executionState = (resourceManager.getExperimentManager()
				.resolve(delegate_experimentId)
				.map(this::newExecutionStateFromDelegate)
				.orElse(new ExecutionState())
		);
		ExperimentElement result = (new ExperimentElement()
				.id(delegate.getId().value())
				.design(design)
				.parameterBindings(parameterBindings)
				.execution(executionState)
				.children(children)
		);
		return result;
	}
	
	public ExperimentElementImpl findRootStartingFromExperimentElement(ExperimentElementImpl delegate) {
		
		Optional<ExperimentElementImpl> delegate_parent = delegate.getParent();
		
		while (delegate_parent.isPresent()) {
			
			delegate = delegate_parent.get();
			delegate_parent = delegate.getParent();
		}
		
		return delegate;
	}
	
	public ExperimentElementRef newExperimentElementRefFromDelegate(ExperimentElementImpl delegate,
			CQFResourceManager resourceManager
	) {
		
		ExperimentElement modelObject;
		if (resourceManager != null) {
			modelObject = newExperimentElementFromDelegate(delegate, resourceManager);
		}
		else {
			modelObject = null;
		}
		
		ExperimentElementRef result = (new ExperimentElementRef()
				.objectKey(delegate.getId().value())
				.object(modelObject)
		);
		return result;
	}

	/**/
	
	public ExperimentElementImpl resolveExperimentElementImplFromModelRef(ExperimentElementRef modelObjectRef,
			ExperimentElementImpl delegateParent,
			CQFResourceManager resourceManager
	) {
		ExperimentElement x = resolveExperimentElementFromModelRef(modelObjectRef, resourceManager);
		
		return newExperimentElementImplFromModel(x, delegateParent, resourceManager);
	}
	
	public ExperimentElement resolveExperimentElementFromModelRef(ExperimentElementRef modelObjectRef,
			CQFResourceManager resourceManager
	) {
		if (modelObjectRef.getObject() == null) {
			
			if (resourceManager == null) {
				throw new NullPointerException("while resolving ExperimentElementRef");
			}
			else {
				throw new UnsupportedOperationException("while resolving ExperimentElementRef");
			}
		}
		
		return (ExperimentElement) modelObjectRef.getObject();
	}

	/**/
	
	public String newDocumentationFromDelegate(DocumentationImpl documentation) {
		
		StringBuilder result = new StringBuilder();
		
		result.append(documentation.getInfo()).append('\n');
		
		documentation.getParams().entrySet().stream()
				
				.map(e -> e.getKey() + ": " + e.getValue())
				
				.collect(collectingAndThen(joining("\n"), result::append)
				);
		
		return result.toString();
	}

	/**/
	
	public List<Parameter> newParameterListFromDelegates(List<ParameterImpl> parameters,
			DocumentationImpl documentation
	) {
		List<Parameter> result = (parameters.stream()
				
				.map(p -> newParameterFromDelegate(p, documentation))
				
				.collect(toList())
		);
		return result;
	}
	
	public Parameter newParameterFromDelegate(ParameterImpl parameter, DocumentationImpl documentation) {
		
		ParameterBinding defaultBinding = (new ParameterBinding()
				.name(parameter.getName())
				.value(parameter.getDefaultValue())
				.codec(Arrays.asList("variable_substitution"))
		);
		//^-- FIXME: DESIGN: REVIEW: REQUIRED: srogers: add support for parameter-binding codecs
		
		String description = (documentation.getParams().get(parameter.getName()));
		
		Parameter result = (new com.secdec.astam.cqf.api.models.Parameter()
				.name(parameter.getName())
				.type(parameter.getType())
				.description(description)
				.required(parameter.isRequired())
				.defaultBinding(defaultBinding)
		);
		return result;
	}
	
	public List<ParameterBinding> newDefaultParameterBindingListFromDelegates(List<ParameterImpl> parameters) {
		
		List<ParameterBinding> result = (parameters.stream()
				
				.filter(ParameterImpl::hasDefaultValue)
				
				.map(ip -> newDefaultParameterBindingFromDelegate(ip))
				
				.collect(toList())
		);
		return result;
	}
	
	public ParameterBinding newDefaultParameterBindingFromDelegate(ParameterImpl parameter) {
		
		ParameterBinding result = (new ParameterBinding()
				.name(parameter.getName())
				.value(parameter.getDefaultValue())
		);
		return result;
	}
	
	public ExecutionState newExecutionStateFromDelegate(ExperimentImpl experiment) {
		
		assert experiment != null;
		assert experiment.getCurrentPhase() != null;
		assert experiment.getExecutionTrace() != null;
		
		String executionPhase =
				experiment.getCurrentPhase().getValue().toLowerCase();
		
		List<ExecutionTraceRecord> executionTrace =
				newExecutionTraceRecordListFromDelegate(experiment.getExecutionTrace());
		
		String executionResult = experiment.getResult()
				.map(this::newExecutionResultFromDelegate)
				.orElse(null);
		
		ExecutionState result = new ExecutionState()
				.phase(executionPhase)
				.trace(executionTrace)
				.results(executionResult);
		
		return result;
	}
	
	public List<ExecutionTraceRecord> newExecutionTraceRecordListFromDelegate
			(
					List<ExecutionTraceRecordImpl> executionTraceRecordImplList
			) {
		Optional<List<ExecutionTraceRecordImpl>> entries = Optional.ofNullable(executionTraceRecordImplList);
		if (! entries.isPresent() || executionTraceRecordImplList.isEmpty()) {
			return Collections.emptyList();
		}
		
		return executionTraceRecordImplList.stream()
				.filter(Objects::nonNull)
				.map(x -> new ExecutionTraceRecord().value(x.toString()))
				.collect(Collectors.toList());
	}
	
	public String newExecutionResultFromDelegate(ExecutionResultImpl executionResult) {
		
		final String activityDescription =
				"While extracting execution result file from structured execution result";
		
		ResultFileImpl executionResultFile;
		try {
			executionResultFile = findExecutionResultFile(executionResult);
		}
		catch (IllegalArgumentException e) {
			
			logger.warn(activityDescription, e);
			return null;
		}
		
		String result;
		try (
				InputStream in = executionResultFile.getContentInputStream();
				ByteArrayOutputStream out = new ByteArrayOutputStream()
		) {
			
			IOUtils.copy(in, out);
			result = Base64.getEncoder().encodeToString(out.toByteArray());
		}
		catch (IOException e) {
			
			logger.error(activityDescription, e);
			return null;
		}
		return result;
	}
	
	protected ResultFileImpl findExecutionResultFile(ExecutionResultImpl executionResult) {
		
		return executionResult.getEntries()
				.filter(this::isExecutionResult)
				.flatMap(ExecutionResultImpl.Entry::getFiles)
				.collect(collectingAndThen(toList(), this::getOnlyElementFromCollection));
	}
	
	protected boolean isExecutionResult(ExecutionResultImpl.Entry executionResultEntry) {
		
		boolean result = (executionResultEntry.getDepth() == 0); // root entry
		
		assert ! (result) || (executionResultEntry.getParent() == null);
		assert ! (executionResultEntry.getParent() == null) || (result);
		
		return result;
	}
	
	protected <T> T getOnlyElementFromCollection(Collection<T> collection) {
		
		if (collection.size() != 1) {
			throw new IllegalStateException("Expected collection to have exactly one element: " + collection);
		}
		return collection.iterator().next();
	}
	
}
