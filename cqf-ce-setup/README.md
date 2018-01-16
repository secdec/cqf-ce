# CQF-CE-SETUP
This repository contains the code necessary to build a docker image containing a runnable instance of tomcat hosting the CQF-CE framework.

### Building the Docker Image
The scripts available in `src/main/docker/cqf-server` expect project folders to be prefixed with `astam-`. For example: `astam-cqf-ce-api`.
If the image builds on but cannot successfully start, double check that the file newlines are unix based newlines when cloning the repository.
See `src/main/docker/cqf-server/README.md` for more detailed instructions on building the docker image.
See `INSTALL.md` for usage of the portable Docker archive that is built from the above `README.md`.