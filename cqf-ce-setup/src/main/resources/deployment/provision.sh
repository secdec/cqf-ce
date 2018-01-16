#!/bin/bash
## Provision the CQF development VM.
##
## Usage:
##
##     sudo ./provision.sh [provisioning_set ...]
##
## Typical uses:
##
##     sudo ./provision.sh
##     sudo ./provision.sh web-app
##     sudo ./provision.sh web-app-be
##     sudo ./provision.sh web-app-fe
##     sudo ./provision.sh web-app-dev
##
## When provisioning set(s) are not specified, they default to:
##
##     web-app web-app-dev
##
## Supported platforms: RHEL and its derivatives (such as CentOS).
##
#	Copyright (C) 2016 - 2017 Applied Visions, Inc.
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

[ $# -gt 0 ] || exec "$0" web-app web-app-dev

set -e -o pipefail

cd "$(dirname "$0")"

xx() {
	echo 1>&2 "+" "$@"
	"$@"
}

xxq() {
	xx "$@"
}

xxv() {
	xx "$@" || echo 1>&2 "+" "FAILED: $?"
}

##

find . -name '*.sh' -exec chmod a+rx {} \;

for u1 in vagrant ; do
for d1 in "/home/${u1:?}/opt" ; do
	! [ -d "${d1:?}" ] || continue

	mkdir "${d1:?}"
	chown "${u1:?}.${u1:?}" "${d1:?}"
done
done

##
## Needed for web application provisioning:

case " $@ " in
*" web-app "*|*" web-app-be "*|*" web-app-fe "*)
	xxv :
	xxv yum install -y epel-release net-tools time wget
	;;
esac

##
## Needed for web application development:

case " $@ " in
*" web-app-dev "*)
	xxv :
	xxv yum install -y nmap

#!#	web-app-dev/install-dev-tools-for-js.sh

	web-app-dev/install-dev-tools-for-java.sh
	;;
esac

##
## Needed by the web application itself:

case " $@ " in
*" web-app "*|*" web-app-be "*)
	web-app/install-mysql.sh
	;;
esac

case " $@ " in
*" web-app "*|*" web-app-be "*|*" web-app-fe "*|*" web-app-dev "*)
	web-app/install-tomcat.sh "$@"

	web-app/start-services.sh
	;;
esac

case " $@ " in
*" web-app "*|*" web-app-be "*)
	web-app/create-mysql-database.sh
	;;
esac

##
## EOF
