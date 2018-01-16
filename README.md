#CQF Community Edition

## Summary
The Cyber Quantification Framework (CQF) is a framework designed to abstract away the virtualization details of setting 
up an automated environment. This framework can be used for a variety of tasks, including continuous integration, 
administrating several redundant servers, or automating security testing.

In the case of security testing, isolation, repeatability, and fast turnaround are the most important traits. An attack
execution framework used to automate the security testing process should abstract away all virtualization details.
This allows the attack developer to solely focus on developing application security issues.

Working on a platform such as Docker, Vagrant, or ESXi may allow an attack developer to isolate and repeat attacks, but 
forces the developer to work at a lower level of abstraction. As a result, the developer may have to write their own 
layer of abstraction, or face a complex attack simulation model.

The CQF's goal is to cut out the work that would be put into developing an abstraction layer for automated security 
testing by providing the building blocks and process of execution.

## Architecture
The CQF is a Java framework that interacts with ESXi to execute pre-defined actions (such as running an attack 
tool on one machine to attempt to penetrate another machine) based on provided parameters. Included in this set of 
repositories is a REST server that allows a remote client to define and execute an "experiment" using the CQF.

The basic flow might look like the following:
1. The client defines experiment object models in memory. These include information such as what experiment elements 
make up an experiment and what parameters are sent to each of those elements.
2. The client sends these models to the REST API.
3. The REST API consumes the model and passes it to the CQF. The CQF then begins execution of the experiment by spinning
up virtual machines in ESXi and running tasks using parameters provided by the experiment.
4. The REST API returns back to the client.
5. The client waits for completion of the CQF experiment.
6. The client extracts the results of the CQF experiment from the REST API.

## The Conceptual Model of an Experiment
An experiment has a design, parameter bindings, an execution trace, and (eventually) results. The central aspect of an 
experiment is its design.

Structurally, an experiment design is a directed acyclic graph (DAG) of design elements—the building blocks of the design. Each element of the experiment design has its own actions, resources, parameter specs, execution specs, and result specs. To prepare an experiment for execution, the parameters of each element in the design are bound to specific values, and then the experiment is executed. Thus, the distinction between an experiment and an experiment design is that an experiment is executed with specific parameter values, whereas an experiment design is essentially a parameterized template that is used to instantiate an experiment.

Like its design, structurally, an experiment is a directed acyclic graph (DAG) of (bound) elements—the building 
blocks of the experiment. 

It follows that each element of an experiment has its own design, parameter bindings, execution trace, and results. Note the structural similarity between an experiment and an experiment element. In order to support composing experiments from other, smaller experiments, we will say that an experiment is itself an experiment element, and that an experiment design is itself an experiment design element; at which point we have come full circle, in terms of structure.

A design element is an item in a design catalog. The presence of a design catalog enables the construction of an experiment design by composition. One composes (or assembles) a design by selecting from items in the design catalog.
 
Thus, when discussing CQF experiments, the terms "design element" and "design item" (or just "item" for short) are 
synonymous. To facilitate organization of the catalog, and subsequent design element selection, each design element has 
a category and a one-sentence description. In addition, to facilitate correct design assembly, detailed documentation 
is provided for each design element.

Experiment archetypes are large-grained items in the design catalog. They are pre-designed, parameterized experiments 
that capture commonly used experiment design patterns.

## Setting up a test environment
### Pre-requisites
1. Java for maven - https://java.com/en/download/
2. Install maven - https://maven.apache.org/
3. ESXi - https://www.vmware.com/products/esxi-and-esx.html
4. Make for compiling the Docker image.
5. Docker for the server image.
6. Optionally, Python for the API client, if not using the Java version of the API client.

### Setup instructions
1. Build various CQF projects
	- When cloning, be sure to clone with line endings set to unix (Windows line endings will break the Docker build script)
	- CQF server image
		- Navigate to `astam-cqf-ce/`
		- Run `mvn clean install`
		- Navigate to `astam-cqf-ce-api/`
		- Run `mvn clean install`
		- Navigate to `astam-cqf-ce-base/`
		- Run `mvn clean install`
		- Navigate to `astam-cqf-ce-items/`
		- Run `mvn clean install`
		- The deployable server should be available in `astam-cqf-ce-api-server-java/modules/cqf-ce-api-server-java-webapp-jersey/target/`
		- Open `astam-cqf-ce-setup/src/main/resources/deployment/web-app/install-tomcat.extra.d/cqf-astam.xml`
		- Edit the user/pwd/server values in the `<esxi></esxi>` element to point to your own ESXi server
		- Navigate to `astam-cqf-ce-setup/src/main/docker/cqf-server/`
		- Run `sudo make`
		- A distributable docker image of the CQF server should now be available in `astam-cqf-ce-setup/src/main/docker/cqf-server/target/`, as well as a Docker image available in your Docker installation. Confirm by running `sudo docker images`
		- To run the image, use the following command: `sudo docker run --name astam-cqf-ce --publish 8080:8080 -d astam-cqf-ce`
		- To verify that the image is responsive, use the following command: `curl http://localhost:8080/cqf/api/v1/experiment_design_catalogs | less`
	- CQF client API
		- Navigate to `astam-cqf-ce/`
		- Run `mvn clean install`
		- Navigate to `astam-cqf-ce-api/`
		- Run `mvn clean install`
		- In `astam-cqf-ce-api/modules/` there will be two folders: `cqf-ce-api-client-java` and `cqf-ce-api-client-python`. Each folder will contain a `target/generated-sources/client/` folder which will contain a Swagger generated API documentation and API client for each language, as well as instructions for using each generated library.
2. Acquire ESXi server
	- Create a base Ubuntu image on ESXi, and give it a known name (to be used in the CQF API).
	- Copy the contents of `astam-cqf-ce-setup/src/main/resources/` to a folder of your choosing on the Ubuntu image (in this example we will use `/opt/`)
	- Run the following loop to install the provisions required: 
	```bash
	for f1 in /opt/deployment/provision.sh ; do
      chmod +rx "$f1" ; sudo "$f1" testbed
    done
	```

3. Send example experiment JSON via curl/postman.
	- There are example experiment files in `astam-cqf-ce-api-server-java/modules/cqf-ce-api-server-java-impl/src/test/resources/experiments/`.
	- These files can be sent using the `curl` command or using a REST tool such as Postman. An example is provided:
	```bash
	curl -X POST \
	  http://localhost:8080/cqf/api/v1/experiments \
	  -H 'cache-control: no-cache' \
	  -H 'content-type: application/json' \
	  -H 'postman-token: 765f08bc-47f7-6d96-3811-7acf9c0161e0' \
	  -d '{
		"id": null,
		"design": {
			"object_role": null,
			"object_key": "com.siegetechnologies.cqf.design.item.archetype.multinode"
		},
		"parameter_bindings": [
			{
				"name": "archetype_scenario",
				"value": "hello world",
				"codec": [
					"injected",
					"parameter_expansion"
				]
			}
		],
		"execution": null,
		"children": [
			{
				"object_role": "primary_vm",
				"object": {
					"id": null,
					"design": {
						"object_role": null,
						"object_key": "com.siegetechnologies.cqf.design.item.package.nil"
					},
					"parameter_bindings": [
						{
							"name": "primary_vm_parameter_01",
							"value": "FOO",
							"codec": [
								"injected",
								"parameter_expansion"
							]
						}
					],
					"execution": null
				}
			}
		]
	}'
```