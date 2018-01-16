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

#!/bin/bash

. /cqf/cqfscript.sh

SUPPORTED_OS=(Debian RedHat)

require_os

if [ "CQF_${INITIALIZE_NETWORK}_CQF" = "true" ];then

case $OSVERSION in
    "Debian")
        echo "auto lo
        iface lo inet loopback
        " > /etc/network/interfaces
        ;;
esac

fi

IPADDRESS=CQF_${IPADDRESS}_CQF
NETMASK=CQF_${NETMASK}_CQF
GATEWAY=CQF_${GATEWAY}_CQF
ADAPTERNUM=CQF_${ADAPTERNUM}_CQF

#####################################################
## determine the version of the operating system (debian vs redhat)
#####################################################
echo "checking linux distribution...."
OSVERSION=None

if [ `cat /proc/version | grep -c "Red Hat"` != "0" ];then
OSVERSION=RedHat
else
OSVERSION=Debian
fi

#####################################################
## determine the version of the operating system (debian vs redhat)
#####################################################


#####################################################
## determine adapter name
#####################################################



case $OSVERSION in
	"Debian")
		## bring up all adapters first

		for i in $( ifconfig -s -a | awk '{print $1}' | tail -n +2 | head -n -1 ); do
			ifconfig $i up
		done
		ADAPTERNAME=`ifconfig -s -a | awk '{print $1}' | tail -n +2 | head -n -1 | sed -n "$ADAPTERNUM p"`
		;;
	"RedHat")
		ADAPTERNUM=$((${ADAPTERNUM}+1))
		for i in $( ip -o link show | awk -F': ' '{print $2}'); do
			ip link set ${i} up;
		done

		ADAPTERNAME=`ip -o link show | awk -F': ' '{print $2}' | sed -n "$ADAPTERNUM p"`
		;;
esac


echo "adapter name: $ADAPTERNAME"

#####################################################
## determine adapter name
#####################################################


#####################################################
## set static ip addresses
#####################################################

if [ "$OSVERSION" = "Debian" ];then

echo "auto $ADAPTERNAME
iface $ADAPTERNAME inet static
address $IPADDRESS
netmask $NETMASK
gateway $GATEWAY" >> /etc/network/interfaces

cat /etc/network/interfaces

elif [ "$OSVERSION" = "RedHat" ];then

HWADDR=`cat /sys/class/net/$ADAPTERNAME/address`

echo "DEVICE=\"$ADAPTERNAME\"
BOOTPROTO=\"static\"
HWADDR=\"$HWADDR\"
NM_CONTROLLED=\"yes\"
ONBOOT=\"yes\"
TYPE=\"Ethernet\"
IPADDR=$IPADDRESS
NETMASK=$NETMASK
DEFROUTE=\"no\"
GATEWAY=$GATEWAY" > /etc/sysconfig/network-scripts/ifcfg-$ADAPTERNAME

fi



#ifdown $ADAPTERNAME
#ifup $ADAPTERNAME

#ifconfig

#####################################################
## set static ip addresses
#####################################################


echo "reboot for static" >> initialize-reboot.txt

