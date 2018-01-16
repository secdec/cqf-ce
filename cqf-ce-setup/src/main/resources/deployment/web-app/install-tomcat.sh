#!/bin/bash
## Install Tomcat from a .rpm and/or a .tgz.
## By Stephen D. Rogers <steve-rogers.com>, 2016-10. Use at own risk.
##
## Usage:
##
##     sudo ./install-tomcat.sh [provisioning_set ...]
##
## Typical uses:
##
##     sudo ./install-tomcat.sh
##     sudo ./install-tomcat.sh web-app
##     sudo ./install-tomcat.sh web-app-be
##     sudo ./install-tomcat.sh web-app-fe
##     sudo ./install-tomcat.sh web-app-dev
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

this_script_stem="$(basename "$0" .sh)"

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

source "${this_script_stem:?}".conf

##

install_tomcat_rpm() {
#	Install system-recommended version of Tomcat.
#	Ensure all prerequisites are installed.
#
#	Create system's primary Tomcat instance.
#
	xx :
	xx yum install -y tomcat

	xx :
	service_sys_tomcat stop
	chkconfig_sys_tomcat off
}

install_tomcat_tgz() {
#	Install latest and greatest version of Tomcat.
#
#	Create a private Tomcat instance for development.
#
	if [ ! -f "${dev_tomcat_tgz_fpn:?}" ] ; then
		xxv :
		[ -d "$(dirname "${dev_tomcat_tgz_fpn:?}")" ] || xxv mkdir "$(dirname "${dev_tomcat_tgz_fpn:?}")"
		xxv wget -q -O "${dev_tomcat_tgz_fpn:?}" "${dev_tomcat_tgz_url:?}"
	fi

	if [ ! -d "${dev_tomcat_base_dpn:?}" ] ; then
		xxv :
		[ -d "${dev_tomcat_base_parent_dpn:?}" ] || xxv mkdir "${dev_tomcat_base_parent_dpn:?}"
		xxv tar xf "${dev_tomcat_tgz_fpn:?}" -C "${dev_tomcat_base_parent_dpn:?}"

		xxv :
		xxv ln -snf "$(basename "${dev_tomcat_base_dpn:?}")" "${dev_tomcat_base_parent_dpn:?}"/tomcat

		xxv :
		xxv chown -R "${dev_tomcat_user:?}.${dev_tomcat_group:?}" "${dev_tomcat_base_dpn:?}"

		xxv :
		xxv ls -l "${dev_tomcat_base_dpn:?}"
	fi

	xxv :
	service_dev_tomcat stop

	for d1 in "${this_script_stem:?}".extra.d ; do
	for d2 in "${dev_tomcat_base_dpn:?}" ; do
		xxv :
		xxv rsync -i -lpt -u --backup --suffix "~" -r --stats --filter=": .rsync-filter.deployment" "${d1:?}"/ "${d2:?}"/

		xxv :
		xxv chown -R "${dev_tomcat_user:?}.${dev_tomcat_group:?}" "${d2:?}"
	done
	done
}

##

install_tomcat_rpm

install_tomcat_tgz

##

for x1 in ./"${this_script_stem:?}".post-install.sh ; do
	! [ -e "$x1" ] || "$x1" "$@"
done

##
