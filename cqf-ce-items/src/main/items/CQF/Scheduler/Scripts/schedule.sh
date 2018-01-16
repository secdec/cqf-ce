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

# Set the working directory.  This ensure that commands with relative
# pathnames, e.g., `echo " ..." >> cqf.log` write the log file
# `/cqf/cqf.log`, and not some other working directory.
#
cd /cqf

# Install the CQF scheduler.  This script is run by CQF
# with six command line arguments, all integers.  These are
# <seconds> <minute> <hour> <day-in-month> <month> <year>
#
ls /cqf/ >> cqf.log

second=$((10#$1))
minute=$((10#$2))
hour=$((10#$3))
day=$((10#$4))
month=$((10#$5))
year=$((10#$6))

echo "# ${year}-${month}-${day}T${hour}:${minute}:${second} -- Installing CQF test scheduler" >> cqf.log

touch mycron

if [ `cat /proc/version | grep -c "Debian"` != "0" ];then
cp /cqf/onreboot.sh /etc/init.d
update-rc.d onreboot.sh defaults 100
elif [ `cat /proc/version | grep -c "Ubuntu"` != "0" ];then
cp /cqf/onreboot.sh /etc/init.d
update-rc.d onreboot.sh defaults 100
elif [ `cat /proc/version | grep -c "Red Hat"` != "0" ];then
echo "@reboot /cqf/launch.sh onreboot" >> mycron
fi

#write the job
M=$((($minute+$((10#CQF_${INITIALIZE_MINUTE}_CQF)))%60))
H=$((($hour+$((10#0)))%24 + ($minute+$((10#CQF_${INITIALIZE_MINUTE}_CQF)))/60))
echo "$M $H * * * /cqf/launch.sh initialize" >> mycron

M=$((($minute+$((10#CQF_${SETUP_MINUTE}_CQF)))%60))
H=$((($hour+$((10#0)))%24 + ($minute+$((10#CQF_${SETUP_MINUTE}_CQF)))/60))
echo "$M $H * * * /cqf/launch.sh setup" >> mycron

M=$((($minute+$((10#CQF_${STARTSENSORS_MINUTE}_CQF)))%60))
H=$((($hour+$((10#0)))%24 + ($minute+$((10#CQF_${STARTSENSORS_MINUTE}_CQF)))/60))
echo "$M $H * * * /cqf/launch.sh startsensors" >> mycron

M=$((($minute+$((10#CQF_${MAIN_MINUTE}_CQF)))%60))
H=$((($hour+$((10#0)))%24 + ($minute+$((10#CQF_${MAIN_MINUTE}_CQF)))/60))
echo "$M $H * * * /cqf/launch.sh main" >> mycron

M=$((($minute+$((10#CQF_${STOPSENSORS_MINUTE}_CQF)))%60))
H=$((($hour+$((10#0)))%24 + ($minute+$((10#CQF_${STOPSENSORS_MINUTE}_CQF)))/60))
echo "$M $H * * * /cqf/launch.sh stopsensors" >> mycron

M=$((($minute+$((10#CQF_${CLEANUP_MINUTE}_CQF)))%60))
H=$((($hour+$((10#0)))%24 + ($minute+$((10#CQF_${CLEANUP_MINUTE}_CQF)))/60))
echo "$M $H * * * /cqf/launch.sh cleanup" >> mycron

echo "" >> mycron
#write the job
crontab mycron
rm mycron
crontab -l >> cqf.log
