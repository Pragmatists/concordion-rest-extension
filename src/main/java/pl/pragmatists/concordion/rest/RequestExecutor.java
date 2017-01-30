package pl.pragmatists.concordion.rest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.Evaluator;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import pl.pragmatists.concordion.rest.RestExtension.Config;

public class RequestExecutor {
    
    private RequestSpecification request;
    private Response response;

    private String method;
    private String url;
    private String body;
    private Map<String, String> headers = new HashMap<String, String>();

    protected static final String REQUEST_EXECUTOR_VARIABLE = "#request";
    
    private Config config;

    protected RequestExecutor(Config config) {
        request = RestAssured.given()
                .urlEncodingEnabled(false)
                .port(config.port)
                .baseUri("http://" + config.host)
                .log()
                .all(true);
        this.config = config;
    }

    public RequestExecutor method(String method) {
        this.method = method;
        return this;
    }

    public RequestExecutor url(String url) {
        this.url = url;
        return this;
    }
    
    void resolvePlaceholders(Evaluator evaluator){
        
        evaluator.setVariable("#host", config.host);
        evaluator.setVariable("#port", config.port);
        
        url = resolve(url, evaluator);

        for(String header : headers.keySet()) {
            String placeholder = headers.get(header);
            headers.put(header, resolve(placeholder, evaluator));
        }
        
        body = resolve(body, evaluator);
    }
    
    public void execute(){

        for (String header : headers.keySet()) {
            request.header(header, headers.get(header));
        }

        if(body != null){
            request.body(body);
        }
        
        if("GET".equals(method)){
            response = request.get(url);
            return;
        }
        if("POST".equals(method)){
            response = request.post(url);
            return;
        }
        if("PUT".equals(method)){
            response = request.put(url);
            return;
        }
        if("DELETE".equals(method)){
            response = request.delete(url);
            return;
        }
        if("PATCH".equals(method)){
            response = request.patch(url);
            return;
        }
         
    }

    String resolve(String text, Evaluator evaluator) {
        
        if(text == null){
            return null;
        }
        
        Pattern placeholder = Pattern.compile("\\{\\s*([a-zA-Z_#][^}]+)\\}");
        
        Matcher matcher = placeholder.matcher(text);
        while(matcher.find()){
            String group = matcher.group(0);
            String expression = matcher.group(1);
            String result = "" + evaluator.evaluate(expression);
            
            System.err.println(String.format("Replacing %s with %s", group, result));
            
            text = text.replace(group, result);
        }
        
        return text;
    }

    
    public RequestExecutor header(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
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
        return this;
    }

    public String getBody() {
        return response.body().asString();
    }

    public InputStream getBodyAsInputStream() {
        return response.asInputStream();
    }

    public static RequestExecutor fromEvaluator(Evaluator evaluator) {
        return (RequestExecutor) evaluator.getVariable(REQUEST_EXECUTOR_VARIABLE);
    }
    
    public static RequestExecutor newExecutor(Evaluator evaluator, Config config) {
        
        RequestExecutor variable = new RequestExecutor(config);
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

    public int getStatusCode() {
        return response.getStatusCode();
    }
}