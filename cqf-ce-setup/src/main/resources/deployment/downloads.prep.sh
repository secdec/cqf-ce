#!/bin/bash
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

cd "$(dirname "$0")"
cd "$(basename "$0" .prep.sh)"

for f1_url in *.url ; do
	f1="${f1_url%.url}"
	! [ -e "${f1:?}" ] || continue

	(set -x ; curl --output "${f1:?}" "$(head -1 "${f1_url:?}")")
done
