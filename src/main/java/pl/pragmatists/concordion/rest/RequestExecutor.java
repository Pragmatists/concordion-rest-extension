package pl.pragmatists.concordion.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class RequestExecutor {
    
    private RequestSpecification request;
    private Response response;
    
    private String url;
    private String method;

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
}