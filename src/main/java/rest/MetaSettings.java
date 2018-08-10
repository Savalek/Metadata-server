package rest;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaSettings {
  public static int RES_POOL_MIN_CONNECTIONS = 0;
  public static int RES_POOL_MAX_CONNECTIONS = 40;
  public static int RES_POOL_CONNECTION_TTL = 15;
  public static int CACHE_TTL = 600;
  public static int JSTREE_SEARCH_LIMIT = 20;
  public static int PARALLEL_UPDATE_DB_COUNT = 4;
  public static int PERSON;

  @Value("${RES_POOL_MIN_CONNECTIONS}")
  public void setResPoolMinConnections(int resPoolMinConnections) {
    RES_POOL_MIN_CONNECTIONS = resPoolMinConnections;
  }

  @Value("${RES_POOL_MAX_CONNECTIONS}")
  public void setResPoolMaxConnections(int resPoolMaxConnections) {
    RES_POOL_MAX_CONNECTIONS = resPoolMaxConnections;
  }

  @Value("${RES_POOL_CONNECTION_TTL}")
  public void setResPoolConnectionTtl(int resPoolConnectionTtl) {
    RES_POOL_CONNECTION_TTL = resPoolConnectionTtl;
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
