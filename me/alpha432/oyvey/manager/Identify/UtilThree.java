package me.alpha432.oyvey.manager.Identify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UtilThree {
  private static final Gson gson = new Gson();
  
  private static final Gson PRETTY_PRINTING = (new GsonBuilder()).setPrettyPrinting().create();
  
  public String toJson() {
    return gson.toJson(this);
  }
  
  public String toJson(boolean prettyPrinting) {
    return prettyPrinting ? PRETTY_PRINTING.toJson(this) : gson.toJson(this);
  }
}
