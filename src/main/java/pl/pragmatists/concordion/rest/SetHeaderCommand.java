package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.internal.listener.AssertResultRenderer;
import org.concordion.internal.util.Announcer;

public class SetHeaderCommand extends AbstractCommand {

  private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);

  public SetHeaderCommand() {
    listeners.addListener(new AssertResultRenderer());
  }

  @Override
  public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

    Element element = commandCall.getElement();
    element.addStyleClass("set-header");

    String headerValue = element.getText();
    String variable = element.getAttributeValue("variable");
    if (variable != null)
    {
      headerValue = headerValue.replaceAll(variable, (String) evaluator.getVariable(variable));
    }
    
    element.moveChildrenTo(new Element("tmp"));
    element.appendText(headerValue);    
    
    RequestExecutor request = RequestExecutor.fromEvaluator(evaluator);
    String headerName = element.getAttributeValue("name");
    request.header(headerName, headerValue);
  }

}
