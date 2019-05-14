#!/bin/sh

loop=10
proc="python"

for arg in "$@"
do
    case "$1" in
        -n)
            shift
			loop=$1
            shift
            ;;
        -p)
			shift
            proc="$1"
            shift
            ;;
    esac
done

while [ $loop -gt 0 ]
do
	ps x -o rss,vsz,command | grep $proc | grep -v grep
	loop=`expr $loop - 1`
	sleep 1
done
