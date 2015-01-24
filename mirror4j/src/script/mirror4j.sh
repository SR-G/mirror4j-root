#!/bin/sh
SCRIPT_NAME=$0
CURRENT_PATH=`dirname "${SCRIPT_NAME}"`
LIB_PATH="${CURRENT_PATH}/lib/"
MAIN_JAR=`ls -1 "${LIB_PATH}" 2>/dev/null | grep -v "plugin" | grep "mirror4j"`
java -Xms92M -Xmx128M -jar "${LIB_PATH}/${MAIN_JAR}" --pid ${CURRENT_PATH}/mirror4j.pid -c ${CURRENT_PATH}/mirror4j.xml $*