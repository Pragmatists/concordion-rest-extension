package pl.pragmatists.concordion.rest;

import java.io.StringReader;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.util.Announcer;

import pl.pragmatists.concordion.rest.util.JsonPrettyPrinter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ExpectedJsonResponseCommand extends AbstractCommand {

    private static class EqualsJsonComparator implements JsonComparator {

        @Override
        public boolean assertEqualsJson(String actual, String expected) {

            JsonElement actualJson = parse(actual);
            JsonElement expectedJson = parse(expected);

            return Objects.equals(actualJson, expectedJson);
        }

    }

    private static class IncludesJsonComparator implements JsonComparator {

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

    }

    private interface JsonComparator {

        boolean assertEqualsJson(String actual, String expected);
    }

    private static JsonElement parse(String input) {
        JsonReader reader = new JsonReader(new StringReader(input));
        reader.setLenient(true);
        return new JsonParser().parse(reader);
    }

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    
    public ExpectedJsonResponseCommand() {
        listeners.addListener(new RestResultRenderer());
    }

    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        Element element = commandCall.getElement();
        element.addStyleClass("json");
        
        JsonPrettyPrinter printer = new JsonPrettyPrinter();
        
        String expected = printer.prettyPrint(element.getText());
        element.moveChildrenTo(new Element("tmp"));
        element.appendText(expected);
        
        String mode = modeFrom(element);
        String actual = RequestExecutor.fromEvaluator(evaluator).getBody();
        String prettyActual = printer.prettyPrint(actual);

        if (StringUtils.isEmpty(actual)){
            jsonDoesNotEqual(resultRecorder, element, "(not set)", expected);
            return;
        }
        
        try {
            if (comparator(mode).assertEqualsJson(prettyActual, expected)) {
                jsonEquals(resultRecorder, element);
            } else {
                jsonDoesNotEqual(resultRecorder, element, prettyActual, expected);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonError(resultRecorder, element, prettyActual, expected);
        }

    }

    private JsonComparator comparator(String mode) {
        switch (mode) {
        case "includes": return new IncludesJsonComparator();
        case "equals": return new EqualsJsonComparator();
        default : throw new IllegalArgumentException();
        }
    }

    private String modeFrom(Element element) {
        String mode = element.getAttributeValue("mode");
        return mode == null ? "includes" : mode;
    }
    
    protected void jsonError(ResultRecorder resultRecorder, Element element, String actual, String expected) {
        resultRecorder.record(Result.FAILURE);
        announceFailure(element, expected, actual);
    }

    protected void jsonDoesNotEqual(ResultRecorder resultRecorder, Element element, String actual, String expected) {
        resultRecorder.record(Result.FAILURE);
        announceFailure(element, expected, actual);
    }

    protected void jsonEquals(ResultRecorder resultRecorder, Element element) {
        resultRecorder.record(Result.SUCCESS);
        announceSuccess(element);
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }

    
}
