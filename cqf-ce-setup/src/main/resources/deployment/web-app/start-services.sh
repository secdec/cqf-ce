#!/bin/bash
## Start all services for the CQF app.
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

this_script_stem="$(basename "$0" .sh)"

xx() {
echo 1>&2 "+" "$@"
"$@"
}

##

source "install-mysql.conf"
source "install-tomcat.conf"

source "${this_script_stem:?}".conf

##

start_web_app_services() {
	service_mysql start

	xx :
	case "${debugging_tomcat_p}" in
	f|false|'')
		service_dev_tomcat start
		;;
	t|true)
		service_dev_tomcat jpda start
		;;
	*)
		echo 1>&2 "unrecognized value for debugging_tomcat_p: ${debugging_tomcat_p}"
		return 2
		;;
	esac
}

##

xx :
start_web_app_services

