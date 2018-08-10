package metadata.element;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Column extends DatabaseElement{
  private Table parentTable;
  private String valueType;

  public Column(String columnName, Table ParentTable) {
    this.name = columnName;
    this.parentTable = ParentTable;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public Table getParentTable() {
    return parentTable;
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = super.toJson();
    json.addProperty("value_type", valueType);
    return json;
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public ArrayList<DatabaseElement> getInnerElements() {
    return new ArrayList<>();
  }

  @Override
  public ArrayList<Integer> getParentsIds() {
    ArrayList<Integer> ids = new ArrayList<>(2);
    ids.add(parentTable.getId());
    ids.add(parentTable.getParentSchema().getId());
    return ids;
  }
}
