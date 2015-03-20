package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

public class GetCommand extends AbstractCommand {

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        RequestExecutor request = (RequestExecutor) evaluator.getVariable("#request");
        request.method("GET");
        request.url(commandCall.getElement().getText());
        
    }

    
}
