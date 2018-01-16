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

echo "Configuring dotCMS to use MySQL"
echo 'create database dotcms default character set = utf8 default collate = utf8_general_ci;' | mysql --password=root

echo "starting dotCMS"
/downloadedApps/dotcms-3.3.1/bin/shutdown.sh
/downloadedApps/dotcms-3.3.1/bin/startup.sh

echo ''
echo 'IN A FEW MINUTES, dotCMS will be accessible at http://localhost:8080/ (from your host)'
echo '===== Application Credentials ====='
echo ''
echo '=== Admin ==='
echo '     URL: http://localhost:8080/admin'
echo 'username: admin@dotcms.com'
echo 'password: admin'
echo ''
echo '=== Intranet User ==='
echo 'username: bill@dotcms.com'
echo 'password: bill'
echo ''
echo '=== Limited User ==='
echo 'username: joe@dotcms.com'
echo 'password: joe'
echo '===== Application Credentials ====='
echo 'Script finished.'
