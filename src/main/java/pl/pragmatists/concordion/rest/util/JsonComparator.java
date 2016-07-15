package pl.pragmatists.concordion.rest.util;

public interface JsonComparator {

  boolean assertEqualsJson(String actual, String expected);
}
