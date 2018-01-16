#!/usr/bin/perl
####
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
#
# Convert to naming convention 0 from naming convention 1+.
#

$/ = undef;

while( <> ) {

	s</var/cqf/execution>               </cqf>g;

	s<cqf[.]error[.]log>                <cqferrors.txt>g;
	s<cqf[.]error[.]json>               <cqferror.json>g;
	s<cqf[.]functions[.]sh>             <cqfscript.sh>g;

	s<my-crontab>                       <mycron>g;

	s<(on)(_)(reboot)>                  <${1}${3}>g;
	s<(start|stop)(_)(sensors)>         <${1}${3}>g;

	s<(admin|user)(_)(login_name)>      <${1}username>g;
	s<(admin|user)(_)(login_password)>  <${1}password>g;

	##
	
	s<\b(duration) (_in_minutes_of_run_slot_)     (\w+)\b>  <unix_${3}_${1}>gx;

	s<\b(offset)   (_in_minutes_of_run_slot_)     (\w+)\b>  <\U${3}_minute\E>gx;

	s<\b(command)  (_to_execute_during_run_slot_) (\w+)\b>  <\U${3}${1}\E>gx;

	##
	
	s<INITIALIZECOMMAND>  <INITCOMMAND>g;

	##

	print;
}
