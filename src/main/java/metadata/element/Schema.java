package metadata.element;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Schema extends DatabaseElement {
  private ArrayList<Table> tables = new ArrayList<>();

  public Schema(String name) {
    this.name = name;
  }

  public void addTable(Table table) {
    tables.add(table);
  }

  @Override
  public String toString() {
    return this.name + this.tables;
  }

  public ArrayList<Table> getAllTables() {
    return tables;
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = super.toJson();
    if (tables.size() > 0) {
      json.addProperty("children", true);
    }
    return json;
  }

  @Override
  public ArrayList<DatabaseElement> getInnerElements() {
    return new ArrayList<>(tables);
  }

  @Override
  public ArrayList<Integer> getParentsIds() {
    return new ArrayList<>(0);
  }


  public Table getTable(String tableName) {
    for (Table table : tables) {
      if (table.name.equals(tableName)) {
        return table;
      }
    }
    return null;
  }

  public Table getTableById(long id) {
    for (Table table : tables) {
      if (table.getId() == id) {
        return table;
      }
    }
    return null;
  }
}