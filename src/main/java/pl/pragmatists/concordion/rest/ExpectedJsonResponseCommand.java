package pl.pragmatists.concordion.rest;

import java.util.Arrays;

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

import pl.pragmatists.concordion.rest.util.EqualsJsonComparator;
import pl.pragmatists.concordion.rest.util.IgnoreValuesJsonComparator;
import pl.pragmatists.concordion.rest.util.IncludesJsonComparator;
import pl.pragmatists.concordion.rest.util.JsonComparator;
import pl.pragmatists.concordion.rest.util.JsonPrettyPrinter;

public class ExpectedJsonResponseCommand extends AbstractCommand {

  private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);

  public ExpectedJsonResponseCommand() {
    listeners.addListener(new RestResultRenderer());
  }

  @Override
  public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

    Element element = commandCall.getElement();
    element.addStyleClass("json");

    JsonPrettyPrinter printer = new JsonPrettyPrinter();

    String expected = element.getText();
    expected = replaceVariableValues(evaluator, element, expected);  

    expected = printer.prettyPrint(expected);
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
      if (comparator(mode, element).assertEqualsJson(prettyActual, expected)) {
        actual = printer.prettyPrint(actual);
        element.moveChildrenTo(new Element("tmp"));
        element.appendText(actual);
        jsonEquals(resultRecorder, element);
      } else {
        jsonDoesNotEqual(resultRecorder, element, prettyActual, expected);
      }
    } catch (Exception e) {
      e.printStackTrace();
      jsonError(resultRecorder, element, prettyActual, expected);
    }

  }

  private String replaceVariableValues(Evaluator evaluator, Element element, String expected) {
    String variable = element.getAttributeValue("variable");
    if (variable != null)
    {
      String variableValue = (String) evaluator.getVariable(variable);
      expected = expected.replaceAll(variable, variableValue);
    }
    return expected;
  }

  private JsonComparator comparator(String mode, Element element) {

    if("includes".equals(mode)){
      return new IncludesJsonComparator();
    }

    if("equals".equals(mode)){
      return new EqualsJsonComparator();
    }

    if ("ignoreValues".equals(mode))
    {
      String propertyValuesAttribute = element.getAttributeValue("propertyValuesIgnored");
      if (propertyValuesAttribute != null)
      {
        return new IgnoreValuesJsonComparator(Arrays.asList(propertyValuesAttribute.split(",")));
      }
      throw new IllegalArgumentException("Ignore property values comparator need at least one property name to ignore its value");

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
