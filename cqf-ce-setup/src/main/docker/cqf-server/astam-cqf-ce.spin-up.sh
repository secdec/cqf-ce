#!/bin/bash
## Spin up a ASTAM CQF CE server as a Docker container.
## By Stephen D. Rogers <inbox.c7r@steve-rogers.com>, 2017-07.
##
## Usage:
##
##     astam-cqf-ce.spin-up [--interactive|-i] [--restart policy] [--tty|-t] [listening_port]
##
## The ASTAM CQF CE listening port (on the container host) defaults to 8080.
##
## See also:
##
##     docker run --help
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

umask 0002

set -e -o pipefail

no_worries() {
        echo 1>&2 "No worries; continuing."
}

qq() {
	printf "%q" "$@"
}

xx() {
	echo 1>&2 "+" "$@"
	"$@"
}

##

run_options=
while [ $# -gt 0 ] ; do
case "$1" in 
--interactive|-i)
	run_options+="${run_options:+ }${1}"
	shift 1 ; continue
	;;
--restart)
	run_options+="${run_options:+ }${1} $(qq "${2}")"
	shift 2 ; continue
	;;
--tty|-t)
	run_options+="${run_options:+ }${1}"
	shift 1 ; continue
	;;
--)
	shift 1 ; break
	;;
-*)
	echo 1>&2 "unrecognized option: ${1}"
	exit 2
	;;
*)
	break
	;;
esac
done

container_name=astam-cqf-ce
container_image=${container_name}:latest

p1h="${1:-8080}" # tomcat listening port on the host
p1c="8080"       # tomcat listening port in the container

for d1h in /var/local/workspaces/tomcat.ref/conf ; do # tomcat initial reference configuration directory on the host
for d1c in /usr/local/workspaces/tomcat.ref/conf ; do # tomcat initial reference configuration directory in the container
for d2h in /var/local/workspaces/tomcat.ref/webapps ; do # tomcat initial reference webapps directory on the host
for d2c in /usr/local/workspaces/tomcat.ref/webapps ; do # tomcat initial reference webapps directory in the container

	for dxh in "$d1h" "$d2h" ; do
	# The container determines owner uid/gid for "$dxh" and below;
	# seal off access to that subtree to just the superuser (root).
	for dxh_parent in "$(dirname "$(dirname "$dxh")")" ; do
		xx sudo mkdir -p "$dxh_parent"
		xx sudo chown root:root "$dxh_parent"
		xx sudo chmod 0770 "$dxh_parent"
		xx sudo chmod g+s "$dxh_parent"
	done;done

	if false ; then
	xx :
	xx docker pull "$container_image"
	fi

	xx :
        xx docker stop "$container_name" || no_worries
	xx docker rm --force "$container_name" || no_worries

	xx :
	xx eval "docker run --name $(qq "$container_name") -d \
		-v $(qq "$d1h":"$d1c") -v $(qq "$d2h":"$d2c") \
		-p $(qq "$p1h":"$p1c") ${run_options} $(qq "$container_image")"

done;done
done;done

