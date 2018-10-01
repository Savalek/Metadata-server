# Database metadata server
Stores metadata about different databases.

## Info
server settings in `conf\application.properties`  
database connection settings in `conf\connections-config.json`  
(examples in `connections-config.json.template`)

To package project run in console `mvn package`  
To run server from console use `bin/metadata-server.sh`.  
Available commands: `start`, `stop`, `reload`  
Example: `./metadata-server.sh start`

To run in your IDE set run parameters:  
spring.config.location `./conf/`  
connections.config `./conf/connections-config.json`  

<p align="center">
  <img src="https://github.com/Savalek/Metadata-server/blob/master/docs/img/inv_var.png">
</p>  

### application.properties :
`server.port` - port to connect to the server  
`RES_POOL_MIN_CONNECTIONS` - minimum amount of live connection to the resource pool  
`RES_POOL_MAX_CONNECTIONS` - maximum amount of live connection to the resource pool  
`RES_POOL_CONN_MAX_INACTIVITY_TIME` - time through which an unused connection will be deleted (in seconds)  
`RES_POOL_CONN_TTL` - max lifetime of active connection (in seconds)  
`CACHE_TTL` - interval of full metadata update (in seconds)  
`JSTREE_SEARCH_LIMIT` - the maximum number of elements to display when searching  
`PARALLEL_UPDATE_DB_COUNT` - the maximum number of simultaneously updated databases   
`REQUEST_FOR_EVERY_FILTER` - true: do request for every filter item when database cache updating. false - do only one request when updating. (Recommended `false` only if you have many filters)
