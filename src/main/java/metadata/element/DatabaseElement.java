package metadata.element;
import com.google.gson.JsonObject;

import java.util.ArrayList;

abstract public class DatabaseElement {

  private static int idCounter = 0;

  protected int id = idCounter++;
  protected String name;
  protected String description;
  private boolean isRelevant = true;

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("id", String.valueOf(id));
    json.addProperty("text", name);
    json.addProperty("type", this.getClass().getSimpleName().toLowerCase());
    if (description != null) {
      json.addProperty("description", description);
    }
    return json;
  }

  public boolean isRelevant() {
    return isRelevant;
  }

  public void setRelevant(boolean relevant) {
    isRelevant = relevant;
  }

  abstract public ArrayList<DatabaseElement> getInnerElements();

  abstract public ArrayList<Integer> getParentsIds();

  public int getId() {
    return id;
  }
}
