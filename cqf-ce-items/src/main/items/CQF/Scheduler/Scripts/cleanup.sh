#!/bin/bash

###
# #%L
# astam-cqf-ce-items
# %%
# Copyright (C) 2009 - 2017 Siege Technologies, LLC
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###

cp /cqf/cqf.log /cqf/cqf.log-tmp

for VARIABLE in initialize setup startsensors main stopsensors
do
	grep "$VARIABLE #################### begin" "/cqf/cqf.log"
	if [ $? -eq 0 ]
	then
		echo '{"CQF_NOERROR":"0"}' > /cqf/cqferror.json
		echo "CQF_${cqf.name}_CQF: Phase did not start: $VARIABLE" >> /cqf/cqferrors.txt
	fi
	
	grep "$VARIABLE #################### end" "/cqf/cqf.log"
	if [ $? -eq 0 ]
	then
		echo '{"CQF_NOERROR":"0"}' > /cqf/cqferror.json
		echo "CQF_${cqf.name}_CQF: Phase did not end: $VARIABLE" >> /cqf/cqferrors.txt
	fi
done
