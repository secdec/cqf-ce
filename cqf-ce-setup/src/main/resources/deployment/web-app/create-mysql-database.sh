#!/bin/bash
## Create a MySQL database.
## By Stephen D. Rogers <steve-rogers.com>, 2016-10. Use at own risk.
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

source "${this_script_stem:?}".conf

##

probe_mysql_database() {
	xx :
	xx mysql --user="${mysql_database_user:?}" -e "status" "${mysql_database_name:?}"
}

create_mysql_database() {
	if ! probe_mysql_database ; then
		xx :
		xx mysql --user="${mysql_database_user:?}" -e "create database ${mysql_database_name:?}"

		probe_mysql_database
	fi
}

##

create_mysql_database

