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

#####
# cqfscript.sh
#
# This file serves as a set of utility functions and scripts to make
# installing and using setup scripts on CQF much easier.  It should
# make it much easier and more concise to define prerequisites for
# scripts, do initial installations and whatever other setup is
# needed.
#####

#####
# VARIABLES
#####
declare -A CQF_PACMAN_CMD
declare CQF_DEBUG=1
declare OS_VERSION=None

CQF_PACMAN_CMD[Debian]="DEBIAN_FRONTEND=noninteractive apt-get -y --fix-missing install"
CQF_PACMAN_CMD[RedHat]="yum install -y"
#####
# FUNCTIONS
#####

##
# debug
# Echos all the arguments passed if CQF_DEBUG is set.
##
function debug() {
    ((CQF_DEBUG)) && echo "### $*";
}

##
# determine_os
# Determines the currently running OS and stores it into OS_VERSION.
##
function determine_os() {
    debug "Checking Linux Distribution..."
    OSVERSION=None

    if [ `cat /proc/version | grep -c "Debian"` != "0" ];then
	OSVERSION=Debian
    fi

    if [ `cat /proc/version | grep -c "Ubuntu"` != "0" ];then
	OSVERSION=Debian
    fi

    if [ `cat /proc/version | grep -c "Red Hat"` != "0" ];then
	OSVERSION=RedHat
    fi
}

##
# in_array
# Checks if $2 is contained in the array passed in $1
##
function in_array {
    local haystack=${1}[@]
    local needle=${2}

    for i in ${!haystack}; do
	if [[ ${i} == ${needle} ]]; then
	    return 0
	fi
    done
    return 1
}
##
# require_valid_os
# Makes sure that a known OS has been found.  If not, then the script
# will terminate.
##
function require_valid_os()
{
    determine_os
    if [ "$OSVERSION" = "None" ]; then
	debug "No compatible OS detected"
	exit 1
    fi
}

##
# require_os
# Validates that the running os is contained in the SUPPORTED_OS array
# set by the caller.
##
function require_os()
{
    determine_os
    in_array SUPPORTED_OS ${OSVERSION} && return
    debug "No compatible OS detected"
    exit 1
}

##
# install_packages
#
# Installs all the packages specified in the associated array
# CQF_PACKAGES.  This array maps from OS Name to a space separated
# list of package names.
##
function install_packages()
{
    if [[ -z ${CQF_PACKAGES[@]} ]]; then
	debug "CQF_PACKAGES variable not set"
	exit
    fi
    local PACKAGE_LIST=${CQF_PACKAGES[${OSVERSION}]}
    debug "Installing required packages: ${PACKAGE_LIST}"
	apt-get update
	yum makecache fast
	ifconfig
	#yum check-update
    eval ${CQF_PACMAN_CMD[${OSVERSION}]} ${PACKAGE_LIST} || cqf_error "Error installing ${PACKAGE_LIST}"
}

##
# set_config_entry
#
# Sets a configuration entry, for configurations taking foo=bar style
# configuration options.  If the entry is not set then it will append
# it to the file.
#
# Arguments: @file @key @value @sep
#
# @file - Configuration file to update
# @key - Configuration key being updated
# @value - New value to assign to the key
# @sep - Separator character for sed, in case key or value has a '/' in it
# @assign - Configuration assignment delimeter, "=" by default, use "" for no delim
##
function set_config_entry()
{
	local cfile="$1"
	local key="$2"
	local val="$3"
	local sep="${4-/}"
	local assign="${5-=}"

	if grep -q "^${key}\s*${assign}" "$cfile"; then
		sed -i "s${sep}\(^${key}\s*${assign}\s*\).*${sep}\1${val}${sep}" "$cfile"
	else
		echo "${key} ${assign} ${val}" >> "$cfile"
	fi
}

##
# disable_config_entry
#
# Comments out the specified configuration key directive
# Arguments: @file @key @sep
#
# @file - Configuration file to update
# @key - Configuration key being updated
# @sep - Separator character for sed, in case key or value has a '/' in it
##
function disable_config_entry()
{
    local cfile="$1"
    local key="$2"
    local sep="${3-/}"

    if grep -q "^${key}\s*=" "$cfile"; then
	sed -i "s${sep}\(^${key}\s*=\)${sep}#\1${sep}" "$cfile"
    fi
    #Don't need to disable nonexistent config entries
}

##
# enable_config_entry
#
# Uncomments a commented out configuration key directive
# Arguments: @file @key @sep
#
# @file - Configuration file to update
# @key - Configuration key being updated
# @sep - Separator character for sed, in case key or value has a '/' in it
##
function enable_config_entry()
{
    local cfile="$1"
    local key="$2"
    local sep="${3-/}"

    if grep -q "^#${key}\s*=" "$cfile"; then
	sed -i "s${sep}^#\(${key}\s*=\)${sep}\1${sep}" "$cfile"
    fi
    #Don't need to disable nonexistent config entries
}

##
# cqf_error
#
# Outputs an error.
# Arguments: @text
##
function cqf_error() {
	echo '{"CQF_NOERROR":"0"}' > cqferror.json
	echo "[$(date)] $1" >> cqferrors.txt
}
