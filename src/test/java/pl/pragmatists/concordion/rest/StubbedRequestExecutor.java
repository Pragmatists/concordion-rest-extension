package pl.pragmatists.concordion.rest;

import org.concordion.api.Evaluator;

import pl.pragmatists.concordion.rest.RestExtension.Config;

public class StubbedRequestExecutor extends RequestExecutor {

    public StubbedRequestExecutor() {
        super(new Config());
    }

    private String body; 
    
    public StubbedRequestExecutor stubBody(String body){
        this.body = body;
        return this;
    }
    
    @Override
    public String getBody() {
        return body;
    }

    public void install(Evaluator evaluator) {
        evaluator.setVariable(REQUEST_EXECUTOR_VARIABLE, this);
    }
    
}
