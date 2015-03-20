package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

public class PostCommand extends AbstractCommand {

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        RequestExecutor request = RequestExecutor.fromEvaluator(evaluator);
        request.method("POST");
        request.url(commandCall.getElement().getText());
        
    }

    
}
