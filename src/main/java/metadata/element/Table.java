package metadata.element;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Table extends DatabaseElement {
  private Schema parentSchema;
  private ArrayList<Column> columns = new ArrayList<>();

  public Table(String name, Schema parent) {
    this.name = name;
    this.parentSchema = parent;
  }

  public Schema getParentSchema() {
    return parentSchema;
  }

  public void addColumn(Column column) {
    columns.add(column);
  }

  @Override
  public String toString() {
    return this.name + this.columns;
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = super.toJson();
    if (columns.size() > 0) {
      json.addProperty("children", true);
    }
    return json;
  }

  @Override
  public ArrayList<DatabaseElement> getInnerElements() {
    return new ArrayList<>(columns);
  }

  @Override
  public ArrayList<Integer> getParentsIds() {
    ArrayList<Integer> ids = new ArrayList<>(1);
    ids.add(parentSchema.getId());
    return ids;
  }

  public ArrayList<Column> getAllColumns() {
    return columns;
  }

  public Column getColumn(String columnName) {
    for (Column column : columns) {
      if (column.getName().equals(columnName)) {
        return column;
      }
    }
    return null;
  }
}
