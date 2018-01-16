FROM sdr01810/tomcat

# Copyright (C) 2016 - 2017 Applied Visions, Inc.
#
#	Licensed under the Apache License, Version 2.0 (the "License");
#	you may not use this file except in compliance with the License.
#	You may obtain a copy of the License at
#
#		http://www.apache.org/licenses/LICENSE-2.0
#
#	Unless required by applicable law or agreed to in writing, software
#	distributed under the License is distributed on an "AS IS" BASIS,
#	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#	See the License for the specific language governing permissions and
#	limitations under the License.

STOPSIGNAL SIGTERM

ENV docker_image_tomcat_root=/usr/local/tomcat
ENV docker_image_tomcat_conf_root=/usr/local/tomcat/conf
ENV docker_image_tomcat_webapps_root=/usr/local/tomcat/webapps

ENV docker_image_tomcat_conf_ref_root=/var/local/workspaces/tomcat.ref/conf
ENV docker_image_tomcat_webapps_ref_root=/var/local/workspaces/tomcat.ref/webapps

ENV docker_image_tomcat_setup_root=/var/local/workspaces/tomcat/setup

VOLUME [ "$docker_image_tomcat_conf_ref_root" ]
VOLUME [ "$docker_image_tomcat_webapps_ref_root" ]

#^-- specified by the base image

##

USER    root
WORKDIR "${docker_image_tomcat_setup_root}"

COPY packages.needed.01.txt .
RUN  egrep -v '^\s*#' packages.needed.01.txt > packages.needed.01.filtered.txt

RUN apt-get update && apt-get install -y apt-utils && \
	apt-get install -y $(cat packages.needed.01.filtered.txt) && \
	rm -rf /var/lib/apt/lists/* ;

# tini is a zombie process reaper
# <https://github.com/krallin/tini>
ARG command_tini=/bin/tini
ARG command_tini_version=0.14.0
ARG command_tini_sha256sum=6c41ec7d33e857d4779f14d9c74924cab0c7973485d2972419a3b7c7620ff5fd
ARG command_tini_url=https://github.com/krallin/tini/releases/download/v${command_tini_version}/tini-static-amd64

RUN curl -fsSL -o "${command_tini}" "${command_tini_url}" && \
	(echo "$command_tini_sha256sum  ${command_tini}" | shasum -a 256 -c -) && \
	chmod +x "${command_tini}" ;

##

USER    root
WORKDIR "${docker_image_tomcat_root}"

COPY target/deployment/web-app/install-tomcat.extra.d .
COPY target/deployment/downloads/webapps webapps

##

USER    root
WORKDIR "${docker_image_tomcat_root}"

EXPOSE 8080

COPY "start.sh" .

ENTRYPOINT ["sh", "start.sh"]

##

