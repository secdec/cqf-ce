# CQF-CE-API
Builds Java and Python api clients from yaml using Swagger code generation.

### MODULES:
#### cqf-ce-api-client-java 
Run `mvn clean compile` in this directory to generate java client sources in `target/generated-sources/`. 
To install the client library in the local maven repository, run `mvn clean install`.

#### cqf-ce-api-client-python 
Run `mvn clean compile` in this directory to generate python client sources in `target/generated-sources/`.
Follow the installation instructions in the generated `target/generated-sources/client/README.md`.

#### cqf-ce-api-spec 
Contains source yaml and documentation markdown from which above client libraries are generated.