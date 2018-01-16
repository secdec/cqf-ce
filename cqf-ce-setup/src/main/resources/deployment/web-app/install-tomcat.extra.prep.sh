#!/bin/bash
## Prepare extra files for deployment to the CQF Tomcat instance.
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

set -e -o pipefail

cd "$(dirname "$0")"

this_script_stem="$(basename "${0}" .sh)"

xx() {
	echo 1>&2 "+" "$@"
	"$@"
}

##

dev_tomcat_extra_files_dpn="${this_script_stem%.prep}.d"

dev_tomcat_external_webapps_dpn="../downloads/webapps"

##

cqf_edition=community

if [ "${cqf_edition:?}" = "community" ] ; then
	sandbox_cqf_etc_root_dpn=

	sandbox_cqf_war_root_dpn="../../../../../../astam-cqf-ce-api-server-java"
	[ -d "${sandbox_cqf_war_root_dpn:?}" ] ||
	sandbox_cqf_war_root_dpn="../../../../../../cqf-ce-api-server-java"
else
	sandbox_cqf_etc_root_dpn="../../../../../../cqf/etc"

	sandbox_cqf_war_root_dpn="../../../../../../cqf"
fi

##

! true || ! [ -n "${sandbox_cqf_etc_root_dpn}" ] ||
for f1 in "${sandbox_cqf_etc_root_dpn:?}"/log*.properties ; do
for f2 in "${dev_tomcat_extra_files_dpn:?}/${f1##*/}" ; do
	[ -e "${f1:?}" ] || continue

	xx :
	xx mkdir -p "$(dirname "${f2}")"

	xx :
	xx rm -f "$f2"
	xx rsync -i -Lpt -u "${f1}" "${f2}"
done
done

##

! true || ! [ -n "${sandbox_cqf_etc_root_dpn}" ] ||
for f1 in "${sandbox_cqf_etc_root_dpn:?}"/set*.sh ; do
for f2 in "${dev_tomcat_extra_files_dpn:?}/bin/${f1##*/}" ; do
	[ -e "${f1:?}" ] || continue

	xx :
	xx mkdir -p "$(dirname "${f2}")"

	xx :
	xx rm -f "$f2"
	xx rsync -i -Lpt -u "${f1}" "${f2}"
done
done

##

for f2 in "${dev_tomcat_extra_files_dpn:?}"/cqf.xml ; do
	if [ "${cqf_edition:?}" = "community" ] ; then
		b1="cqf-astam.xml"
	else
		b1="cqf-1.2.xml"
	fi

	f1="$(dirname "$f2")/$b1"
	[ -e "${f1:?}" ] || continue

	xx :
	xx mkdir -p "$(dirname "$f2")"

	xx :
	xx rm -f "$f2"

	xx :
	xx ln -nf "$f1" "$f2"

	break
done

##

for f2_parent in "${dev_tomcat_external_webapps_dpn:?}" ; do
	xx :
	xx mkdir -p "${f2_parent:?}"

	for f2 in "${f2_parent:?}"/* ; do
		! [ -d "${f2:?}" ] || continue

		xx :
		xx rm -f "${f2:?}"
	done

	(
	cat <<-EOF
		*.war
		*/target/*.war
		modules/*/target/*.war
	EOF
	) |

	while read -r x1 ; do
		for f1 in "${sandbox_cqf_war_root_dpn:?}"/${x1} ; do
		for f2 in "${f2_parent:?}/${f1##*/}" ; do
			[ -e "${f1:?}" ] || continue

			xx :
			xx ln -nf "${f1}" "${f2}"
		done
		done
	done
done

