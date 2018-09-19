import metadata.element.Column;
import metadata.element.Schema;
import metadata.element.Table;

import java.util.concurrent.ConcurrentHashMap;

class TestUtils {

  static Schema getSchema(ConcurrentHashMap<String, Schema> allSchemas, String schemaName) {
    return allSchemas.get(schemaName);
  }

  static Table getTable(ConcurrentHashMap<String, Schema> allSchemas, String schemaName, String tableName) {
    return getSchema(allSchemas, schemaName).getTable(tableName);
  }

  static Column getColumn(ConcurrentHashMap<String, Schema> allSchemas, String schemaName, String tableName, String columnName) {
    return getTable(allSchemas, schemaName, tableName).getColumn(columnName);
  }

}
