package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

public class HttpMethodCommand extends AbstractCommand {

    private String method;

  public HttpMethodCommand(String method) {
    this.method = method;
  }

  @Override
  public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

    Element element = commandCall.getElement();
    element.addStyleClass(method.toLowerCase());
    
    String url = element.getText();
    String variable = element.getAttributeValue("variable");
    if (variable != null)
    {
      url = url.replaceAll(variable, (String) evaluator.getVariable(variable));
    }

    element.moveChildrenTo(new Element("tmp"));
    element.appendText(url);
    
    RequestExecutor.fromEvaluator(evaluator)
    .method(method)
    .url(url);
  }

}
