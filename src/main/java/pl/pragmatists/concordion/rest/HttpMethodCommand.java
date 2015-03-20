package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

public class HttpMethodCommand extends AbstractCommand {

    private String method;

    public HttpMethodCommand(String method) {
        this.method = method;
    }
    
    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        String url = commandCall.getElement().getText();
        RequestExecutor.fromEvaluator(evaluator)
            .method(method)
            .url(url);
    }
    
}
