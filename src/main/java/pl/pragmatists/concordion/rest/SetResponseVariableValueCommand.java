package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SetResponseVariableValueCommand extends AbstractCommand {

  @Override
  public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    Element element = commandCall.getElement();
    String property = element.getAttributeValue("property");

    String response = RequestExecutor.fromEvaluator(evaluator).getBody();
    JsonParser jsonParser = new JsonParser();
    JsonObject body = (JsonObject)jsonParser.parse(response);

    String propertyValue = ((JsonObject)body.get("properties")).get(property).getAsString();
    evaluator.setVariable("#" + property, propertyValue);
  }
}
