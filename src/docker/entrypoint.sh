#!/bin/bash

export PORT0=${PORT0:-"8443"}

export JAVA_ARGS="--server.port=${PORT0}"

HEAP_PERCENTAGE=${HEAP_PERCENTAGE:-"80"}
JAVA_TOOL_OPTIONS=${JAVA_TOOL_OPTIONS:-"-XX:+UseG1GC -XX:MaxRAMPercentage=${HEAP_PERCENTAGE} -XshowSettings:vm"}

JAVA_CMD="java ${JAVA_TOOL_OPTIONS} -jar /data/app.jar ${JAVA_ARGS}"

echo "Starting Spring Boot Service!"

${JAVA_CMD}