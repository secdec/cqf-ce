#!/bin/bash
## Update content files in the CQF app.
##
## Usage:
##
##     sudo ./install-tomcat.post-install.sh [provisioning_set ...]
##
## Typical uses:
##
##     sudo ./install-tomcat.post-install.sh
##     sudo ./install-tomcat.post-install.sh web-app
##     sudo ./install-tomcat.post-install.sh web-app-be
##     sudo ./install-tomcat.post-install.sh web-app-fe
##     sudo ./install-tomcat.post-install.sh web-app-dev
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

##

source "install-tomcat.conf"

source "${this_script_stem:?}".conf

this_script_extra_files_dpn="${this_script_stem%.post-install}".extra.d

this_script_downloads_dpn="../downloads"

##

case " $@ " in
*" web-app "*|*" web-app-be "*|*" web-app-fe "*)
	for d0 in "${this_script_extra_files_dpn:?}" ; do
	for d1 in "${this_script_downloads_dpn:?}" ; do
	for d2 in "${dev_tomcat_base_dpn:?}" ; do
		xx :
		for f1 in "${d0:?}"/cqf*.xml{,.orig} ; do
			[ -e "${f1:?}" ] || continue

			xx rsync -i -Lpt -u --backup --suffix "~" "${f1:?}" "${d2:?}"/.
		done

		xx :
		for f1 in "${d0:?}/webapps"/*.war "${d1:?}/webapps"/*.war ; do
			[ -e "${f1:?}" ] || continue

			case "$(basename "${f1:?}")" in *-portal.war) continue ;; *) : ;; esac

			xx rm -rf "${d2:?}/webapps/$(basename "${f1:?}" .war)" # force redeployment

			xx rsync -i -Lpt -u --backup --suffix "~" "${f1:?}" "${d2:?}/webapps"/.
		done

		xx :
		xx chown -R "${dev_tomcat_user:?}.${dev_tomcat_group:?}" "${d2:?}"
	done;done
	done
	;;
esac

case " $@ " in
*" web-app "*|*" web-app-fe "*)
	for d0 in "${this_script_extra_files_dpn:?}" ; do
	for d1 in "${this_script_downloads_dpn:?}" ; do
	for d2 in "${dev_tomcat_base_dpn:?}" ; do
		xx :
		for f1 in "${d0:?}/webapps"/*.war "${d1:?}/webapps"/*.war ; do
			[ -e "${f1:?}" ] || continue

			case "$(basename "${f1:?}")" in *-portal.war) : ;; *) continue ;; esac

			xx rm -rf "${d2:?}/webapps/$(basename "${f1:?}" .war)" # force redeployment

			xx rsync -i -Lpt -u --backup --suffix "~" "${f1:?}" "${d2:?}/webapps"/.
		done

		xx :
		xx chown -R "${dev_tomcat_user:?}.${dev_tomcat_group:?}" "${d2:?}"
	done;done
	done
	;;
esac

##

case " $@ " in
*" web-app "*|*" web-app-be "*)
	for d1_parent in /vagrant/{etc,*/src/main/webapp,deployment/extras/*/etc} ; do
	for d2_parent_parent in "${cqf_home_dpn:?}" ; do
	for d2_parent        in "${d2_parent_parent:?}"/etc ; do
	for x1 in items parse ; do
		d1="${d1_parent}/${x1}"
		[ -d "${d1}" ] || continue

		case "${x1}" in
		items) ;; *) d2_parent="${d2_parent}/items" ;;
		esac
		d2="${d2_parent}/${x1}"

		xx :
		[ -d "${d2_parent_parent}" ] || xx mkdir "${d2_parent_parent}"
		[ -d "${d2_parent}"        ] || xx mkdir "${d2_parent}"
		xx rsync -i -Lpt -u -r --stats --delete --filter=": .rsync-filter.deployment" "${d1}"/ "${d2}"/

		xx :
		xx chown -R "${dev_tomcat_user:?}.${dev_tomcat_group:?}" "${d2_parent_parent}"
	done;done
	done;done
	;;
esac

##

