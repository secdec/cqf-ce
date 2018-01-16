# CQF-CE-API-SERVER-JAVA
Builds the API server that clients interface with to execute experiements on the CQF-CE platform. Run `mvn clean install` to build the server binaries (a deployable war file), which will be made available in `modules/cqf-ce-api-server-java-webapp-jersey/target/`.

## MODULES:
#### cqf-ce-api-server-java-base
Contains maven specific configuration.
#### cqf-ce-api-server-java-impl
Contains the REST API implementation, as well as example JSON experiments in `src/test/resources/experiments/`.
#### cqf-ce-api-server-java-integration-tests
Contains code that tests the REST layer integration with the CQF core.
#### cqf-ce-api-server-java-webapp-jersey
Contains Jersey (a Java REST framework) specific implemenatation and configuration.