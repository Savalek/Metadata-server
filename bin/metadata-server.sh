#!/bin/bash
USAGE="-e Command is not recognized! Available:\n\t
start   - start metadata server\n\t
stop    - stop metadata server\n\t
reload  - stop and start metadata server "

ROOT_DIR=$(dirname $0)/..

METASERVER_PID_FILE="./${ROOT_DIR}/bin/${HOSTNAME}.pid"
export LOGS_DIR=${ROOT_DIR}/logs

METASERVER_PID=$(cat ${METASERVER_PID_FILE})

function start_server {

 if kill -0 ${METASERVER_PID} >/dev/null 2>&1; then
   echo "!!! Metaserver is already running"
   return 0;
 fi

 echo "### Start metadata server"
 java -jar ./${ROOT_DIR}/lib/metaserver-1.0.0.jar --spring.config.location=file:./${ROOT_DIR}/conf/ --connections.config=./${ROOT_DIR}/conf/connections-config.json > /dev/null &
 echo $! >${METASERVER_PID_FILE}
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