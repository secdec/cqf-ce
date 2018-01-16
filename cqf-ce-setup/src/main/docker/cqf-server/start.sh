#!/usr/bin/env bash
## Entry point for the ASTAM CQF CE container.
##
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

set -e

debugging=false

xx() {
	echo "+" "$@"
	"$@"
}

printenv_sorted() {
	xx printenv | xx env LC_ALL=C sort
}

sync_directory_pair() { # d1 d2
	local d1="${1:?}"
	local d2="${2:?}"

	xx mkdir -p "$d1"
	xx mkdir -p "$d2"

	(xx cd "$d1" && xx cp -r . "$d2"/.)
	(xx cd "$d2" && xx cp -r . "$d1"/.)
}

##

docker_image_tomcat_user_name="${docker_image_tomcat_user_name:-root}"
docker_image_tomcat_group_name="${docker_image_tomcat_group_name:-root}"

docker_image_tomcat_root="${docker_image_tomcat_root:-/usr/local/tomcat}"
docker_image_tomcat_conf_root="${docker_image_tomcat_conf_root:-/usr/local/tomcat/conf}"
docker_image_tomcat_webapps_root="${docker_image_tomcat_webapps_root:-/usr/local/tomcat/webapps}"

docker_image_tomcat_conf_ref_root="${docker_image_tomcat_conf_ref_root:-/var/local/workspaces/tomcat.ref/conf}"
docker_image_tomcat_webapps_ref_root="${docker_image_tomcat_webapps_ref_root:-/var/local/workspaces/tomcat.ref/webapps}"

docker_image_tomcat_setup_root="${docker_image_tomcat_setup_root:-/var/local/workspaces/tomcat/setup}"

##

xx :
sync_directory_pair "${docker_image_tomcat_conf_ref_root}" "${docker_image_tomcat_conf_root}"

xx :
sync_directory_pair "${docker_image_tomcat_webapps_ref_root}" "${docker_image_tomcat_webapps_root}"

xx :
xx cp "${docker_image_tomcat_root}"/cqf-astam.xml "${docker_image_tomcat_root}"/cqf.xml
xx rm "${docker_image_tomcat_root}"/cqf-*

##

export TINI_SUBREAPER=
#^-- mere existence indicates 'true'

echo
echo "Environment variables:"
xx :
printenv_sorted

##

tomcat="${CATALINA_HOME}/bin/catalina.sh"
action=run

if ${debugging} ; then
	echo
	echo "Launching a shell..."
	xx :
	xx exec bash -l
else
	echo
	echo "Launching ASTAM CQF CE within Tomcat ${Tomcat_VERSION}..."
	xx :
	xx exec "${tomcat}" "${action}" "$@"
##	xx exec tini -- "${tomcat}" "${action}" "$@"
fi

##

