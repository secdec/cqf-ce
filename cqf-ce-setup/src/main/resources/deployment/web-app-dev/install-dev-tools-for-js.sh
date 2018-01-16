#!/bin/bash +x
## Install tools for JavaScript development.
## By Stephen D. Rogers, 2016-05.
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

##

xx :

xx yum install -y --enablerepo=epel nodejs npm

xx npm install -g bower grunt-cli

##

xx :

xx yum install -y ruby ruby-devel rubygems

xx gem install compass

##

