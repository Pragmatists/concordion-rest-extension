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
        
        RequestExecutor.fromEvaluator(evaluator)
            .method(method)
            .url(url);
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Element element = commandCall.getElement();
        String url = RequestExecutor.fromEvaluator(evaluator).getRequestUrl();
        element.moveChildrenTo(new Element("span"));
        element.appendText(url);
    }
    
}
