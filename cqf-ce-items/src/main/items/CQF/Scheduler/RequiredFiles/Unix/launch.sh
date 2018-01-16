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

if [ "$1" = "stop" ]; then
	exit
fi
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
mkdir -p /cqf
cd /cqf
timeslot=$1
echo "$(date) #################### $timeslot #################### begin" >> cqf.log
for filename in $timeslot-*.sh; do
	echo "$(date) ---------------- $filename ---------------- begin" >> cqf.log
	bash -x ./"$filename" &>> cqf.log
	echo "$(date) ---------------- $filename ---------------- end" >> cqf.log
done
echo "$(date) #################### $timeslot #################### end" >> cqf.log
if [ -f "$timeslot-reboot.txt" ];
then
    echo "end of $timeslot reboot" >> cqf.log
    reboot
fi
