#!/bin/sh

javac -classpath `hadoop classpath` -d /tmp/labs/MaxTemp /tmp/labs/MaxTemp/src/*.java 
jar -cvf /tmp/labs/MaxTemp/MaxTemp.jar -C /tmp/labs/MaxTemp/ .