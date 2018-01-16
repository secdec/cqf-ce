#!/bin/bash
## Provision the CQF development VM (always).
##
## Usage:
##
##     sudo ./provision.always.sh
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

for d1 in /etc/sysconfig/network-scripts ; do
	grep -E -l '^#VAGRANT-BEGIN' "$d1"/ifcfg-* 2>&- |
	while read -r f1 ; do
		grep -E -h '^DEVICE=' "$f1" 2>&- | sed -e 's/DEVICE=//' |
		while read -r f1_device ; do
			xx :
			xx ifup "$f1_device" || :
		done || :
	done || :
done

xx :
xx ifconfig

##
## EOF
