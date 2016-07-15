package pl.pragmatists.concordion.rest.util;

import java.io.StringReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class EqualsJsonComparator implements JsonComparator {

  @Override
  public boolean assertEqualsJson(String actual, String expected) {

    JsonElement actualJson = parse(actual);
    JsonElement expectedJson = parse(expected);

    return actualJson.equals(expectedJson);
  }

  private static JsonElement parse(String input) {
    JsonReader reader = new JsonReader(new StringReader(input));
    reader.setLenient(true);
    return new JsonParser().parse(reader);
  }
}
