#!/bin/sh


usage() {
	    echo "entrypoint.sh [-h] [-l]"
	    echo "script to start container for everest analytics"
}
echo "-$#-"
for arg in "$@"
do
    #echo "arg -$1-"
    case "$1" in
    "")
        shift
        ;;
	-h)
	    usage
	    exit 0
	    ;;
	*)
	    echo "Parameter error -$1-"
	    shift
        usage
        exit 1
	    ;;
    esac
done

echo "Start the container entry point"
echo "Everest Tools is DONE and READY..."

tail -f /dev/null