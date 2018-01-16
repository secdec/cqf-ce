#!/bin/bash

this_user="$(basename "$0" .sh)"
this_user="${this_user##*for-user-}"

for g1 in docker root ; do
	getent group "$g1" || continue
	sudo usermod --append --groups "$g1" "${this_user}"
done
