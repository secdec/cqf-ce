## Copyright (C) 2016 - 2017 Applied Visions, Inc.
##
##	Licensed under the Apache License, Version 2.0 (the "License");
##	you may not use this file except in compliance with the License.
##	You may obtain a copy of the License at
##
##		http://www.apache.org/licenses/LICENSE-2.0
##
##	Unless required by applicable law or agreed to in writing, software
##	distributed under the License is distributed on an "AS IS" BASIS,
##	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
##	See the License for the specific language governing permissions and
##	limitations under the License.

container_name           = astam-cqf-ce
container_version        = 1.2.0-SNAPSHOT

docker_hub_user          = $(or ${DOCKER_HUB_USER},)
#^-- override this on the make(1) command line or in the environment

ifneq (,$(strip ${docker_hub_user}))
image_tag                = ${docker_hub_user}/${container_name}
else
image_tag                = ${container_name}
endif
docker_build_args_extra  = # --no-cache

##

docker                   = docker

rsync                    = rsync -rlt -i --delete

##

all :: build
all :: push
all :: distribution

build :: import-cqf-ce-build-products
build :: build-docker-image

push ::
#!#	${docker} push "${image_tag}"

clean ::

clobber ::

##

import-cqf-ce-build-products ::
	for d2 in target/deployment ; do \
	for d1 in ../../resources/deployment ; do \
		mkdir -p "$$d2" && \
		${rsync} "$$d1"/ "$$d2"/ ; \
	done;done
	for d2 in target/deployment/downloads/webapps ; do \
	for d1 in ../../../../../cqf-ce-api-server-java/modules/cqf-ce-api-server-java-webapp-jersey/target ; do \
		mkdir -p "$$d2" && \
		rm -f "$$d2/cqf.war" && \
		${rsync} "$$d1/cqf.war" "$$d2/cqf.war" ; \
	done;done

##

DOCKER_IMAGE_SOURCE_FILES = $(strip \
	Dockerfile \
	start.sh \
)

build-docker-image :: target/${container_name}.docker-image.stamp

clobber ::
	rm -f target/${container_name}.docker-image.stamp

target/${container_name}.docker-image.stamp : ${DOCKER_IMAGE_SOURCE_FILES}
	${docker} build ${docker_build_args_extra} --tag "${image_tag}" .
	@touch "$(@)"

##

DISTRIBUTION_TEXT_FILES = $(strip \
	target/dist/LICENSE.txt \
	target/dist/README.md \
	target/dist/INSTALL.md \
	target/dist/Dockerfile \
)

DISTRIBUTION_BINARY_FILES = $(strip \
	target/dist/${image_tag}.docker-image.tar \
)

DISTRIBUTION_FILES = $(strip \
	${DISTRIBUTION_TEXT_FILES} \
	${DISTRIBUTION_BINARY_FILES} \
)

DISTRIBUTION.tar.gz = $(strip \
	target/astam-cqf-ce-${container_version}-dist.tar.gz \
)

distribution :: ${DISTRIBUTION_FILES} ${DISTRIBUTION.tar.gz}

clean ::
	rm -f ${DISTRIBUTION_FILES}
	rm -f ${DISTRIBUTION.tar.gz}

target/dist/% : %
	@mkdir -p "$(@D)"
	cp "$(<)" "$(@)"

target/dist/INSTALL.md : ../../../../INSTALL.md
	@mkdir -p "$(@D)"
	cp "$(<)" "$(@)"

target/dist/${image_tag}.docker-image.tar : 
	@mkdir -p "$(@D)"
	${docker} image save --output "$(@)" "${image_tag}"

target/astam-cqf-ce-${container_version}-dist.tar.gz :
	@mkdir -p "$(@D)"
	tar czf "$(@)" -C target/dist .

##

clobber ::
	rm -rf target

#^-- must be last

##

