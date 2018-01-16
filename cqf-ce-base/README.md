# CQF-CE-BASE
Build the CQF-CE framework: a framework that abstracts away many of the machine specific details and allows rapid development of automated tests. These automated tests can range from automating penetration testing to running self rolled continuous integration builds.
This core code interfaces directly with ESXi, and can run experiments on a variety of configurations.

##MODULES:
####cqf-ce-core
Contains the core functionality of CQF design and execution of experiments on remote machines.
####cqf-ce-testbed-base
Contains the functionality required to interact with a "testbed", also known as the machine actually performing the tests.
####cqf-ce-testbed-vsphere
Contains the functionality required to interact with a vsphere based testbed, run on a platform such as ESXi.