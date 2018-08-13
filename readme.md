# Database metadata server
Stores metadata about different databases.

## Info
server settings in `src\main\resources\application.properties`  
database connection settings in `src\main\resources\connections-config.json`  
(examples in `connections-config.json.template`)

To run server execute in the console `sh run.sh`


### application.properties :
`server.port` - port to connect to the server  
`RES_POOL_MIN_CONNECTIONS` - minimum amount of live connection to the resource pool  
`RES_POOL_MAX_CONNECTIONS` - maximum amount of live connection to the resource pool  
`RES_POOL_CONNECTION_TTL` - time through which an unused connection will be deleted (in seconds)  
`CACHE_TTL` - interval of full metadata update (in seconds)  
`JSTREE_SEARCH_LIMIT` - the maximum number of elements to display when searching  
`PARALLEL_UPDATE_DB_COUNT` - the maximum number of simultaneously updated databases   