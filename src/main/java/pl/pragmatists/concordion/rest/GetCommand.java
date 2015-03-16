package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class GetCommand extends AbstractCommand {

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        String url = commandCall.getElement().getText();
        Response response = RestAssured.get(url).andReturn();
        evaluator.setVariable("#response", response);
        
    }
    
}
