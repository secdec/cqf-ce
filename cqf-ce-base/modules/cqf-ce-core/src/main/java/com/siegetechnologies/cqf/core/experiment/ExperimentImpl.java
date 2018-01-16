package com.siegetechnologies.cqf.core.experiment;

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


import com.siegetechnologies.cqf.core._v01.experiment.execution.result.ExecutionResultImpl;
import com.siegetechnologies.cqf.core.experiment.design.ExperimentDesignElementImpl;
import com.siegetechnologies.cqf.core.experiment.design.elements.WorkspaceDesignElement;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionPhase;
import com.siegetechnologies.cqf.core.experiment.execution.util.ExecutionTraceRecordImpl;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines an experiment that can be executed by CQF.  An experiment consists of a Root
 * ExperimentElement that contains all item instances needed by the experiment to run.
 */
public class ExperimentImpl implements Comparable<ExperimentImpl>
{

    private static final Logger logger = LoggerFactory.getLogger(ExperimentImpl.class);

    private static final String CONTEXT_NAME = "astam"; // FIXME: STRING: srogers

	private final String uniquifier; // FIXME: REVIEW: srogers: replace uniquifier with read-only property 'id'

	private final List<ExecutionTraceRecordImpl> executionTrace = new ArrayList<>();
    private ExecutionPhase currentPhase = null;
	private ExecutionResultImpl result = null;

	private final ExperimentElementImpl root;
    private final String name;

    /**
     * Construct a new Experiment
     * @param root Workspace of this experiment
     * @param name Name of this experiment.
     */
    public ExperimentImpl(ExperimentElementImpl root, String name ) {

	    this.uniquifier = uniquifierFromRoot(root);

	    this.root = root;
        this.name = name;
    }

    protected static String uniquifierFromRoot(ExperimentElementImpl root) {

    	if (root == null) {
    		throw new IllegalArgumentException("root cannot be null");
	    }
	    String result = root.getId().toString().replaceAll("-element-", "-");
	    return result;
    }

    /**/

	@Override
	public int hashCode() {

		return this.uniquifier.hashCode();
	}

	@Override
	public int compareTo(ExperimentImpl that) {

		int result = this.uniquifier.compareTo(that.uniquifier);
		assert result != 0 || this == that;
		//^-- every experiment is unique
		return result;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (o instanceof ExperimentImpl) {

			ExperimentImpl that = (ExperimentImpl) o;

			boolean result = this.uniquifier.equals(that.uniquifier);
			assert result != true || this == that;
			//^-- every experiment is unique
			return result;
		}
		return false;
	}

	@Override
	public String toString() {

		String result = this.uniquifier.toString().replaceAll("-element-", "-");
		return result;
	}

	/**/
	
	/**
	 * Returns the unique ID of this experiment.
	 *
	 * @return the unique ID of this experiment
	 */
	public final ExperimentId getId() {
		
		if (this.root == null) {
			throw new IllegalStateException("root element has not been set");
		}
		return new ExperimentId(this.root.getId().value());
	}
	
	/**/
	
	/**
	 * Returns true if experiment is well formed, false otherwise.
	 * 
	 * @return true if experiment is well formed, false otherwise
	 */
    public boolean validate() {
    	ExperimentDesignElementImpl rootDesignElement = getRoot().getDesign();
        if( !WorkspaceDesignElement.matches(rootDesignElement) ) {
            logger.info(
            		"Unexpected top-level design element; category: {}; name: {}",
		            rootDesignElement.getCategory(), rootDesignElement.getName()
            );
            return false;
        }

        return true;
    }

	/**
	 * Returns context name for this experiment.
	 * 
	 * @return context name for this experiment
	 */
	public String getContextName() {
        return CONTEXT_NAME;
    }

    /**
     * Returns workspace for this experiment.
     * 
     * @return workspace for this experiment
     */
    public ExperimentElementImpl getRoot() {
        return root;
    }

    /**
     * Returns name of this experiment.
     * 
     * @return name of this experiment
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the current experiment result.
     * 
     * @return the current experiment result
     */
    public Optional<ExecutionResultImpl> getResult() {
        return Optional.ofNullable(this.result);
    }

	/**
     * Set the result of this experiment
     * @param value
     */
    public void setResult(ExecutionResultImpl value) {
        this.result = value;
    }

	public List<ExecutionTraceRecordImpl> getExecutionTrace() {
        return this.executionTrace;
    }
	
	/**
	 * Adds and sets the current phase the active experiment 
	 * @param phase
	 * @param experimentElement
	 * @param data
	 */
	public void addToExecutionTrace(ExecutionPhase phase, ExperimentElementImpl experimentElement, Object data) {
        this.executionTrace.add(new ExecutionTraceRecordImpl(Instant.now(), phase, experimentElement, data));
        this.currentPhase = phase;
    }

    public ExecutionPhase getCurrentPhase() {
        return this.currentPhase;
    }

	/**
	 * Returns the elements of this experiment element (top to bottom), as a stream.
	 *
	 * @return the stream
	 */
    public Stream<ExperimentElementImpl> stream() {
        return getRoot().stream();
    }

	/**
	 * Walks the elements of this experiment (top to bottom), applying <code>visitor</code> to each in turn.
	 *
	 * @param visitor
	 */
    public void walk(Consumer<ExperimentElementImpl> visitor) {
    	getRoot().walk(visitor);
    }

}
