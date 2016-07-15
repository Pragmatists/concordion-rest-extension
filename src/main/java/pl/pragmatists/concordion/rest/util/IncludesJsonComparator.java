package pl.pragmatists.concordion.rest.util;

import java.io.StringReader;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class IncludesJsonComparator implements JsonComparator {

  @Override
  public boolean assertEqualsJson(String actual, String expected) {

    JsonElement actualJson = parse(actual);
    JsonElement expectedJson = parse(expected);

    return includesJson(actualJson, expectedJson);
  }

  private boolean includesJson(JsonElement actualJson, JsonElement expectedJson) {

    if (expectedJson.isJsonObject()) {

      if (!actualJson.isJsonObject()) {
        return false;
      }

      for (Entry<String, JsonElement> entry : expectedJson.getAsJsonObject().entrySet()) {

        String property = entry.getKey();
        JsonElement expectedValue = entry.getValue();

        if (!actualJson.getAsJsonObject().has(property)) {
          return false;
        }

        JsonElement actualValue = actualJson.getAsJsonObject().get(property);
        if (!includesJson(actualValue, expectedValue)) {
          return false;
        }
      }

      return true;
    }

    if (expectedJson.isJsonArray()) {

      if (!actualJson.isJsonArray()) {
        return false;
      }

      outer:
        for (JsonElement expectedItem : expectedJson.getAsJsonArray()) {
          for (JsonElement candidateItem : actualJson.getAsJsonArray()) {
            if (includesJson(candidateItem, expectedItem)) {
              continue outer;
            }
          }
          return false;
        }
      return true;
    }

    return actualJson.equals(expectedJson);

  }

  private static JsonElement parse(String input) {
    JsonReader reader = new JsonReader(new StringReader(input));
    reader.setLenient(true);
    return new JsonParser().parse(reader);
  }
}
