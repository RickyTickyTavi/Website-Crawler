#!/bin/sh
arg1=$1
arg2=$2
arg3=$3
arg4=$4
arg5=$5
arg6=$6

##directory where jar file is located
dir=/directory-path/to/jar-file

##jar file name
jar_name = our jar name

## Perform validation
if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Missing arguments, exiting.."
    echo "Usage : $0 arg1 arg2"
    exit 1
fi

java -jar $dir/$jar_name arg1 arg2 arg3 arg4 arg5 arg6
