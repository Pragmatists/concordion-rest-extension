package pl.pragmatists.concordion.rest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.concordion.api.Evaluator;

import pl.pragmatists.concordion.rest.RestExtension.Config;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class RequestExecutor {
    
    private RequestSpecification request;
    private Response response;

    private String method;
    private String url;
    private String body;
    private Map<String, String> headers = new HashMap<String, String>();

    protected static final String REQUEST_EXECUTOR_VARIABLE = "#request";
    
    private Config config;

  public RequestExecutor(Config config) {
    request = RestAssured.given()
        .urlEncodingEnabled(false)
        .port(config.port)
        .baseUri(config.host)
        .log()
        .all(true);

    if (config.host.startsWith("https"))
      request.relaxedHTTPSValidation();

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
    
    public void execute(){

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

    public RequestExecutor header(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        request.header(headerName, headerValue);
        return this;
    }

    public String getHeader(String attributeValue) {
        return replacePlaceholdersIfNeeded(response.getHeader(attributeValue));
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
        return replacePlaceholdersIfNeeded(response.body().asString());
    }

    private String replacePlaceholdersIfNeeded(String string) {
        
        if(string == null){
            return null;
        }
        
        if(config.enablePlaceholders){

            string = replaceWith(string, "https://" + config.host + ":" + config.port, "https://{host:port}");
            string = replaceWith(string, "https://" + config.host, "https://{host}");

            string = replaceWith(string, "http://" + config.host + ":" + config.port, "http://{host:port}");
            string = replaceWith(string, "http://" + config.host, "http://{host}");

            if(response.getSessionId() != null){
                string = replaceWith(string, response.getSessionId(), "{sessionId}");
            }
        }
        
        return string;
    }

    private String replaceWith(String string, String replace, String replacement) {
        return string.replaceAll(replace, replacement);
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

}