package pl.pragmatists.concordion.rest;

import org.concordion.api.Evaluator;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class RequestExecutor {
    
    private RequestSpecification request;
    private Response response;
    
    private String url;
    private String method;
    private static final String REQUEST_EXECUTOR_VARIABLE = "#request";

    public RequestExecutor() {
        request = RestAssured.given();
    }
    
    public void method(String method) {
        this.method = method;
    }

    public void url(String url) {
        this.url = url;
    }
    
    public void execute(){

        switch (method) {
        case "GET":
            response = request.get(url);
            break;
        case "POST":
            response = request.post(url);
            break;
        default:
            break;
        }
        
        
    }

    public void header(String headerName, String headerValue) {
        request.header(headerName, headerValue);
    }

    public String getHeader(String attributeValue) {
        return response.getHeader(attributeValue);
    }

    public String getStatusLine() {
        return response.getStatusLine();
    }

    public void body(String body) {
        request.body(body);
    }

    public String getBody() {
        return response.body().asString();
    }

    public static RequestExecutor fromEvaluator(Evaluator evaluator) {
        
        RequestExecutor variable = (RequestExecutor) evaluator.getVariable(REQUEST_EXECUTOR_VARIABLE);
        if(variable == null){
            variable = new RequestExecutor();
            evaluator.setVariable(REQUEST_EXECUTOR_VARIABLE, variable);
        }
        return variable;
    }
}