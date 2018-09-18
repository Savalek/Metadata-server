package rest;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaSettings {
  public static int RES_POOL_MIN_CONNECTIONS = 0;           // default
  public static int RES_POOL_MAX_CONNECTIONS = 40;          // default
  public static int RES_POOL_CONN_MAX_INACTIVITY_TIME = 15; // default
  public static int RES_POOL_CONN_TTL = 600;                // default
  public static int CACHE_TTL = 600;                        // default
  public static int JSTREE_SEARCH_LIMIT = 20;               // default
  public static int PARALLEL_UPDATE_DB_COUNT = 4;           // default

  public static String CONNECTIONS_CONFIG_FILE_PATH;

  @Value("${RES_POOL_MIN_CONNECTIONS}")
  public void setResPoolMinConnections(int resPoolMinConnections) {
    RES_POOL_MIN_CONNECTIONS = resPoolMinConnections;
  }

  @Value("${RES_POOL_MAX_CONNECTIONS}")
  public void setResPoolMaxConnections(int resPoolMaxConnections) {
    RES_POOL_MAX_CONNECTIONS = resPoolMaxConnections;
  }

  @Value("${RES_POOL_CONN_MAX_INACTIVITY_TIME}")
  public void setResPoolConnMaxInactivityTime(int resPoolConnMaxInactivityTime) {
    RES_POOL_CONN_MAX_INACTIVITY_TIME = resPoolConnMaxInactivityTime;
  }

  @Value("${RES_POOL_CONN_TTL}")
  public void setResPoolConnTTL(int resPoolConnTTL) {
    RES_POOL_CONN_TTL = resPoolConnTTL;
  }

  @Value("${CACHE_TTL}")
  public void setCacheTtl(int cacheTtl) {
    CACHE_TTL = cacheTtl;
  }

  @Value("${JSTREE_SEARCH_LIMIT}")
  public void setJstreeSearchLimit(int jstreeSearchLimit) {
    JSTREE_SEARCH_LIMIT = jstreeSearchLimit;
  }

  @Value("${PARALLEL_UPDATE_DB_COUNT}")
  public void setParallelUpdateDbCount(int parallelUpdateDbCount) {
    PARALLEL_UPDATE_DB_COUNT = parallelUpdateDbCount;
  }
}
