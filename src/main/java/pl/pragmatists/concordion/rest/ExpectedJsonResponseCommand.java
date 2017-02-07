package pl.pragmatists.concordion.rest;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import pl.pragmatists.concordion.rest.util.Comparator;
import pl.pragmatists.concordion.rest.util.Comparator.Replacement;
import pl.pragmatists.concordion.rest.util.JsonPrettyPrinter;

public class ExpectedJsonResponseCommand extends AbstractCommand {

    private static class EqualsJsonComparator implements JsonComparator {

        @Override
        public boolean assertEqualsJson(String actual, String expected) {

            JsonElement actualJson = parse(actual);
            JsonElement expectedJson = parse(expected);

            return actualJson.equals(expectedJson);
        }

        @Override
        public List<Replacement> getReplacements() {
            return Collections.emptyList();
        }

    }

    private static class IncludesJsonComparator implements JsonComparator {

        private List<Replacement> replacements = new ArrayList<Replacement>();
        
        @Override
        public List<Replacement> getReplacements() {
            return replacements;
        }
        
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

            String expected = expectedJson.toString();
            
            Matcher matcher = Pattern.compile("^\"(\\$.*)\"$").matcher(expected);
            if(matcher.matches()){
                expected = matcher.group(1);
            }
            
            Comparator comparator = new Comparator(expected);
            boolean areEqual = comparator.compareTo(actualJson.toString());
            
            if(areEqual){
                replacements.addAll(comparator.replacements());
                return true;
            } else {
                return false;
            }
            
        }

    }

    private interface JsonComparator {

        boolean assertEqualsJson(String actual, String expected);

        List<Replacement> getReplacements();
        
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

        RequestExecutor request = RequestExecutor.fromEvaluator(evaluator);

        Element element = commandCall.getElement();
        element.addStyleClass("json");
        
        JsonPrettyPrinter printer = new JsonPrettyPrinter();
        
        String expected = printer.prettyPrint(request.resolve(element.getText(), evaluator));
        
        String mode = modeFrom(element);
        
        String path = element.getAttributeValue("path");
        String actual;
        if(path == null){
            actual = request.getBody();
        } else {
            actual = request.getBody(path);
        }
        
        String prettyActual = printer.prettyPrint(actual);

        if (StringUtils.isEmpty(actual)){
            jsonDoesNotEqual(resultRecorder, element, "(not set)", expected);
            return;
        }
        
        try {
            
            JsonComparator comparator = comparator(mode, evaluator);
            if (comparator.assertEqualsJson(prettyActual, expected)) {
                jsonEquals(resultRecorder, element);
                
                for (Replacement r : comparator.getReplacements()) {
                    evaluator.setVariable("#" + r.getVariable(), r.getValue());
                    expected = r.replaceIn(expected);
                }
                
                element.moveChildrenTo(new Element("tmp"));
                element.appendText(expected);

            } else {

                element.moveChildrenTo(new Element("tmp"));
                element.appendText(expected);

                jsonDoesNotEqual(resultRecorder, element, prettyActual, expected);
            }


        } catch (Exception e) {
            e.printStackTrace();
            jsonError(resultRecorder, element, prettyActual, expected);
        }
        
    }

    private JsonComparator comparator(String mode, Evaluator evaluator) {
        
        if("includes".equals(mode)){
            return new IncludesJsonComparator();
        }

        if("equals".equals(mode)){
            return new EqualsJsonComparator();
        }
        
        throw new IllegalArgumentException("Invalid comparition mode: " + mode);
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
