#!/bin/bash

this_delegate="${0%.sh}.gist.d/${0##*/}"

chmod a+rx "${this_delegate}"

exec "${this_delegate}" "$@"

