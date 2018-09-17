#!/bin/bash
USAGE="-e Command is not recognized! Available:\n\t
start   - start metadata server\n\t
stop    - close metadata server\n\t
reload  - close metadata server "

ROOT_DIR=$(dirname $0)/..

METASERVER_PID_FILE="${HOSTNAME}.pid"
METASERVER_PID=$(cat ${METASERVER_PID_FILE})

function start_server {

 if kill -0 ${METASERVER_PID} >/dev/null 2>&1; then
   echo "!!! Metaserver is already running"
   return 0;
 fi

 echo "### Start metadata server"
 java -jar ./${ROOT_DIR}/lib/metaserver-1.0.0.jar --spring.config.name=application,conf --spring.config.location=file:./${ROOT_DIR}/conf/ > /dev/null &
 echo $! >${HOSTNAME}.pid
 echo "### Metadata server start (PID: $(cat ${METASERVER_PID_FILE}))"
}

function stop_server {
 if kill -0 ${METASERVER_PID} >/dev/null 2>&1
 then
   echo "### Stop metadata server"
   kill ${METASERVER_PID}
   echo "### Metadata server stop (PID: ${METASERVER_PID})"
 else
   echo "!!! Metaserver is not running"
 fi
}

case $1 in
start)  start_server ;;
stop)   stop_server;;
reload) stop_server; sleep 2;start_server;;
*)      echo $USAGE;;
esac