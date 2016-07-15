package pl.pragmatists.concordion.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.internal.listener.AssertResultRenderer;
import org.concordion.internal.util.Announcer;

public class FormBodyCommand extends AbstractCommand {

  private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);

  public FormBodyCommand() {
    listeners.addListener(new AssertResultRenderer());
  }

  @Override
  public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {        
    Element element = commandCall.getElement();
    element.addStyleClass("form-body");      

    String text = element.getText();
    String variable = element.getAttributeValue("variable");
    if (variable != null) {
      List<String> variables = Arrays.asList(variable.split(","));
      for(String var : variables)
        text = text.replace(var, (String) evaluator.getVariable(var));   
    }

    String[] lines = text.split("\n");
    List<String> resultingLines = new ArrayList<String>();
    for(String line : lines)
    {
      if (!line.trim().isEmpty())
      {
        resultingLines.add(line);
      }
    }

    element.moveChildrenTo(new Element("tmp"));
    element.appendText(prettyPrint(resultingLines));

    RequestExecutor request = RequestExecutor.fromEvaluator(evaluator);
    request.body(getBodyForm(resultingLines));
  }

  private String prettyPrint(List<String> lines)
  {
    return String.join("\n", lines).replaceAll("\t+", "\t");
  }

  private String getBodyForm(List<String> lines)
  {
    return String.join("&", lines).replaceAll("[\t\\s+]", "");
  }
}
