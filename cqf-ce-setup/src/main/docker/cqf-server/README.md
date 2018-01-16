# ASTAM-CQF-CE Docker Image
An ASTAM CQF CE server primed with admin tools, packaged as a Docker image.

See the file "packages.needed.01.txt" for a (configurable) list of what else is installed.

### Build instructions:
Run the make command as root: `sudo make`. After the build process is complete, an image should be available in the `src/main/docker/cqf-server/target/` directory.

For installation after the docker image is built, follow the `INSTALL.md` instructions in the top level of this repository.