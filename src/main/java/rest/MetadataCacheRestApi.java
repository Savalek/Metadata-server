package rest;

import metadata.MetadataCacheServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@CrossOrigin
public class MetadataCacheRestApi {


  private static final Logger LOGGER = LoggerFactory.getLogger(MetadataCacheRestApi.class);

  private MetadataCacheServer server = new MetadataCacheServer();
  private ExecutorService forceRefreshService = Executors.newCachedThreadPool();


  /**
   * @return name's list of all available database
   */
  @RequestMapping(value = "/jstree/databases_list", produces = "application/json")
  public ArrayList<String> getAllDatabases() {
    return server.getAllDatabasesNames();
  }

  /**
   * @return json list of elementId's children
   */
  @RequestMapping(value = "/jstree/get_children", produces = "application/json")
  public String jstreeGetChildren(@RequestParam("database") String databaseName,
                                  @RequestParam("id") String elementId,
                                  @RequestParam(value = "type", required = false) String type,
                                  @RequestParam(value = "schemaId", required = false) String schemaId) {
    try {
      if (elementId.equals("#")) {
        return server.jstreeGetRootElements(databaseName).toString();
      } else {
        Integer iElementId = getValue(elementId);
        Integer iSchemaId = getValue(schemaId);
        return server.jstreeGetChildren(databaseName, iElementId, type, iSchemaId).toString();
      }
    } catch (IllegalArgumentException e) {
      LOGGER.error("Invalid parameters!", e);
      return null;
    }
  }

  /**
   * @return json list of elementId's children
   */
  @RequestMapping(value = "/jstree/refresh_element", produces = "application/json")
  public boolean jstreeRefreshElement(@RequestParam("database") String databaseName,
                                      @RequestParam("id") String elementId,
                                      @RequestParam(value = "schemaId", required = false) String schemaId,
                                      @RequestParam(value = "recursively", required = false) Boolean isRecursively) {

    try {
      boolean refreshRecursively = isRecursively == null ? false : isRecursively;
      forceRefreshService.submit(
              () -> server.jstreeRefreshElement(databaseName, getValue(elementId), getValue(schemaId), refreshRecursively)
      );

      return true;
    } catch (IllegalArgumentException e) {
      LOGGER.error("Invalid parameters!", e);
      return false;
    }
  }

  /**
   * Massload method for JsTree
   */
  @RequestMapping(value = "/jstree/massload", produces = "application/json")
  public String jstreeMassload(@RequestParam("database") String databaseName,
                               @RequestParam("ids") String elementIds) {

    String[] strIds = elementIds.split(",");
    ArrayList<Integer> ids = new ArrayList<>(strIds.length);
    for (String strId : strIds) {
      Integer id = getValue(strId);
      if (id != null) {
        ids.add(id);
      }
    }
    try {
      return server.jstreeMassload(databaseName, ids).toString();
    } catch (IllegalArgumentException e) {
      LOGGER.error("Invalid parameters!", e);
      return null;
    }
  }

  /**
   * Search method for JsTree
   */
  @RequestMapping(value = "/jstree/search", produces = "application/json")
  public HashSet<Integer> jstreeSearch(@RequestParam("database") String databaseName,
                                       @RequestParam("str") String searchString) {

    try {
      CompletableFuture<HashSet<Integer>> future = CompletableFuture.supplyAsync(() -> server.jstreeSearch(databaseName, searchString));
      return server.jstreeSearch(databaseName, searchString);
    } catch (IllegalArgumentException e) {
      LOGGER.error("Invalid parameters!", e);
      return null;
    }
  }

  /**
   * Search method for JsTree
   */
  @RequestMapping(value = "/jstree/get_search_limit", produces = "application/json")
  public int jstreeSearchLimit() {
    return MetaSettings.JSTREE_SEARCH_LIMIT;
  }


  private Integer getValue(String str) {
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException e) {
      return null;
    }
  }

}
