package pl.pragmatists.concordion.rest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.concordion.api.Evaluator;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class RequestExecutor {
    
    private RequestSpecification request;
    private Response response;

    private String method;
    private String url;
    private String body;
    private Map<String, String> headers = new HashMap<>();

    protected static final String REQUEST_EXECUTOR_VARIABLE = "#request";

    public RequestExecutor() {
        request = RestAssured.given().log().all(true);
    }
    
    public RequestExecutor method(String method) {
        this.method = method;
        return this;
    }

    public RequestExecutor url(String url) {
        this.url = url;
        return this;
    }
    
    public void execute(){

        switch (method) {
        case "GET":
            response = request.get(url);
            break;
        case "POST":
            response = request.post(url);
            break;
        case "PUT":
            response = request.put(url);
            break;
        case "DELETE":
            response = request.delete(url);
            break;
        case "PATCH":
            response = request.patch(url);
            break;
        default:
            break;
        }
        

    }

    public RequestExecutor header(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        request.header(headerName, headerValue);
        return this;
    }

    public String getHeader(String attributeValue) {
        return response.getHeader(attributeValue);
    }

    public String getStatusLine() {
        return response.getStatusLine();
    }

    public RequestExecutor body(String body) {
        this.body = body;
        request.body(body);
        return this;
    }

    public String getBody() {
        return response.body().asString();
    }

    public InputStream getBodyAsInputStream() {
        return response.asInputStream();
    }

    public static RequestExecutor fromEvaluator(Evaluator evaluator) {
        
        RequestExecutor variable = (RequestExecutor) evaluator.getVariable(REQUEST_EXECUTOR_VARIABLE);
        if(variable == null){
            variable = newExecutor(evaluator);
        }
        return variable;
    }

    public static RequestExecutor newExecutor(Evaluator evaluator) {
        RequestExecutor variable = new RequestExecutor();
        evaluator.setVariable(REQUEST_EXECUTOR_VARIABLE, variable);
        return variable;
    }

    public boolean wasSuccessfull() {
        int statusCode = response.getStatusCode();
        return statusCode >= 200 && statusCode < 400;
    }

    public String getRequestUrl(){
        return url;
    }

    public String getRequestMethod(){
        return method;
    }

    public String getRequestBody(){
        return body;
    }

    public String getRequestHeader(String header){
        return headers.get(header);
    }

}