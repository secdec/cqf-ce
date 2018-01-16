CATALINA_OPTS="-Xms2g ${CATALINA_OPTS}"

CATALINA_OPTS="-Dlog4j.configuration=file://${CATALINA_BASE}/log4j.properties ${CATALINA_OPTS}"

CATALINA_OPTS="-Dcqf.configuration=file://${CATALINA_BASE}/cqf.xml ${CATALINA_OPTS}"

CATALINA_OPTS="-ea:com.secdec.astam.cqf.api... ${CATALINA_OPTS}"

## Copyright (C) 2016 - 2017 Applied Visions, Inc.
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