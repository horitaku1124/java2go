#!/bin/bash -e

if [[ $# -eq 0 ]]
  then
    echo "Please specify file."
    exit 1
fi

java -cp build/libs/java2go-1.0-SNAPSHOT.jar com.github.horitaku1124.Compile $1