#!/bin/bash
##

set -e +o pipefail

cd "$(dirname "$0")"

##

find deployment deployment.*.sh -type f -name '*.sh' -exec chmod a+rx {} \;

##

for x1 in deployment/*.prep.sh ; do
	"$x1"
done
