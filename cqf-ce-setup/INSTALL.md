## Importing the testbed VM template

The CQF server automatically creates VM(s) for each experiment; 
these VMs exist only until the experiment has completed.
Each such VM is known as a testbed, and is created on demand by cloning a pre-configured template.
To import the testbed template into your VMware hypervisor, follows these steps:

1. Using the VMware GUI, import "ASTAM Ubuntu 64-bit.ova".

2. Note: this VM is a cloning master (a template); do not start it.


## Setting up the CQF server

The CQF server is distributed as a Docker container; 
it can run on any system that supports the Docker client tools and the program curl(1). 
As a reference platform, we deploy the CQF server container on an Ubuntu 16.04 system. 

To get the CQF server running on your system, follow these steps:

1. Download or build "astam-cqf-ce-1.2.0-dist.tar.gz".

2. Unpack it in the usual fashion:

```
	tar xzf astam-cqf-ce-1.2.0-dist.tar.gz
```

3. Import the container image into docker:

```
	docker image load --input astam-cqf-ce.docker-image.tar
```

4. Run the docker container so that it listens on the host port of your choice:

```	
	host_port=8080

	docker rm -f astam-cqf-ce || :

	docker run --detach --name astam-cqf-ce --publish ${host_port}:8080 astam-cqf-ce
```

5. The CQF server is deployed as a .war file
   in a Tomcat instance located at /usr/local/tomcat/ within the container.
   To work with the Tomcat instance, you can execute a shell in the container like this:

```
	docker exec -i -t astam-cqf-ce bash -l
```

6. It takes roughly 30 seconds for the CQF server to start-up within the container. To 
   confirm that it is running, examine the tail end of the Tomcat logs, like this:

```
	docker logs astam-cqf-ce | tail
```

7. Once the CQF server is up and running, you can confirm it is operating correctly by
   issuing a simple REST API request from the command line, using curl(1):

```
	curl http://localhost:${host_port}/cqf/api/v1/experiment_design_catalogs
```


## Changing the vSphere credentials used by CQF

For security reasons, the CQF server ships with a bogus set of credentials. 
In order to connect to your vSphere server, you will need to edit those credentials 
so that they match the login name and password for a valid vSphere account. You will
also need to change the URL of the vSphere server.
To do so, follow these steps:

1. Start a shell within the container:

```
	docker exec -i -t astam-cqf-ce bash -l
```

2. Within that shell, edit the configuration file; then exit the shell itself:

```	
	vim +/esxi/ /usr/local/tomcat/cqf.xml

	exit
```

3. And finally, restart the docker container:

```	
	docker stop astam-cqf-ce

	docker start astam-cqf-ce
```
