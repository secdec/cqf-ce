#!/bin/bash
## Update each of the gists in bin that provides us with a script.
## By Stephen D. Rogers <inbox.c7r@steve-rogers.com>, 2017-06.
## 

set -e -o pipefail

cd "$(dirname "$0")"

for d1 in *.gist.d ; do
(
	cd "$d1" 2>&-

	echo ; pwd ; set -x

	git reset --hard

	git pull
)
done

