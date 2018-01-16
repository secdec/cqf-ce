An experiment has a design, parameter bindings, an execution trace, and (eventually) a result.
The central aspect of an experiment is its design.

Structurally, an experiment design is an acyclic graph (DAG) of design elements — the building blocks of the design.
Each element of the experiment design has its own actions, resources, parameter specs, execution spec, and result spec.
To prepare an experiment for execution, the parameters of each element in the design are bound to specific values; then the experiment is executed.
Thus, the distinction between an experiment and an experiment design is that an experiment is executed with specific parameter values,
whereas an experiment design is essentially a parameterized template that is used to instantiate an experiment.

Like its design, structurally, an experiment is an acyclic graph (DAG) of (bound) elements — the building blocks of the experiment.
It follows that each element of an experiment has its own design, parameter bindings, execution trace, and result.
Note the structural similarity between an experiment and an experiment element.
In order to support composing experiments from other, smaller experiments, we will say that an experiment is itself an experiment element,
and that an experiment design is itself an experiment design element;
at which point we have come full circle, in terms of structure.

A design element is an item in a design catalog.
The presence of a design catalog enables the construction of an experiment design by composition.
One composes (or assembles) a design by selecting from items in the design catalog.
Thus, when discussing CQF experiments, the terms ‘design element’ and ‘design item’ are synonymous.
To facilitate organization of the catalog, and subsequent design element selection, each design element has a category and a one-sentence description.
In addition, to facilitate correct design assembly, detailed documentation is provided for each design element.

Experiment archetypes are large-grained items in the design catalog.
They are pre-designed, parameterized experiments that capture commonly used experiment design patterns.

