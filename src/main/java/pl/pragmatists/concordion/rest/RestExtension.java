package pl.pragmatists.concordion.rest;

import java.util.HashMap;
import java.util.Map;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.DocumentParsingListener;

import com.jayway.restassured.RestAssured;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import pl.pragmatists.concordion.rest.bootstrap.BootstrapExtension;
import pl.pragmatists.concordion.rest.codemirror.CodeMirrorExtension;

public class RestExtension implements ConcordionExtension {

    public static final String REST_EXTENSION_NS = "http://pragmatists.github.io/concordion-rest-extension";

    private boolean codeMirrorEnabled = false;
    private boolean includeBootstrap = false;
    private int port = 8080;
    private String host = "localhost";

    public RestExtension enableCodeMirror(){
        codeMirrorEnabled = true;
        return this;
    }

    public RestExtension includeBoostrap(){
        includeBootstrap = true;
        return this;
    }

    public RestExtension host(String host){
        RestAssured.baseURI = host;
        return this;
    }

    public RestExtension port(int port){
        RestAssured.port = port;
        return this;
    }

    @Override
    public void addTo(ConcordionExtender concordionExtender) {
        
        if(codeMirrorEnabled){
            new CodeMirrorExtension().addTo(concordionExtender);
        }
        if(includeBootstrap){
            new BootstrapExtension().addTo(concordionExtender);
        }
        
        concordionExtender.withCommand(REST_EXTENSION_NS, "request", new RequestCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "get", new HttpMethodCommand("GET"));
        concordionExtender.withCommand(REST_EXTENSION_NS, "post", new HttpMethodCommand("POST"));
        concordionExtender.withCommand(REST_EXTENSION_NS, "put", new HttpMethodCommand("PUT"));
        concordionExtender.withCommand(REST_EXTENSION_NS, "delete", new HttpMethodCommand("DELETE"));
        concordionExtender.withCommand(REST_EXTENSION_NS, "patch", new HttpMethodCommand("PATCH"));
        concordionExtender.withCommand(REST_EXTENSION_NS, "jsonBody", new JsonBodyCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "setHeader", new SetHeaderCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "status", new ExpectedStatusCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "success", new ExpectedSuccessCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "header", new ExpectedHeaderCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "jsonResponse", new ExpectedJsonResponseCommand());              
        concordionExtender.withCommand(REST_EXTENSION_NS, "xmlResponse", new ExpectedXmlResponseCommand());              
        concordionExtender.withCommand(REST_EXTENSION_NS, "attachment", new ExpectedAttachmentCommand(concordionExtender));
        
        concordionExtender.withDocumentParsingListener(new DocumentParsingListener() {
            
            private Map<String, String> tags = new HashMap<String, String>(){{
                put("request", "div");
                put("get", "code");
                put("post", "code");
                put("put", "code");
                put("delete", "code");
                put("patch", "code");
                put("jsonBody", "pre");
                put("setHeader", "code");
                put("status", "code");
                put("success", "span");
                put("header", "code");
                put("jsonResponse", "pre");
                put("xmlResponse", "pre");
                put("attachment", "code");
            }};

            @Override
            public void beforeParsing(Document document) {
           
                visit(document.getRootElement());
                
            }

            private void visit(Element elem) {
                
                Elements children = elem.getChildElements();
                for(int i=0; i<children.size(); i++){
                    visit(children.get(i));
                }
                
                if(RestExtension.REST_EXTENSION_NS.equals(elem.getNamespaceURI())){
                    Attribute attr = new Attribute(elem.getLocalName(), "");
                    attr.setNamespace("r", REST_EXTENSION_NS);
                    elem.addAttribute(attr);
                    elem.setNamespacePrefix("");
                    elem.setNamespaceURI(null);
                    elem.setLocalName(translateTag(elem.getLocalName()));
                }
            }

            private String translateTag(String localName) {
                String name = tags.get(localName);
                return name == null ? localName : name;
            }
        });
    }
    
}
