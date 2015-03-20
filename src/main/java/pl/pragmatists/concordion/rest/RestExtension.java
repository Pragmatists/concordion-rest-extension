package pl.pragmatists.concordion.rest;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.DocumentParsingListener;

public class RestExtension implements ConcordionExtension{

    public static final String REST_EXTENSION_NS = "http://pragmatists.github.io/concordion-rest-extension";

    @Override
    public void addTo(ConcordionExtender concordionExtender) {
        
        concordionExtender.withCommand(REST_EXTENSION_NS, "request", new RequestCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "get", new GetCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "post", new PostCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "jsonBody", new JsonBodyCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "setHeader", new SetHeaderCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "status", new ExpectedStatusCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "header", new ExpectedHeaderCommand());
        concordionExtender.withCommand(REST_EXTENSION_NS, "jsonResponse", new ExpectedJsonResponseCommand());
        
        concordionExtender.withDocumentParsingListener(new DocumentParsingListener() {
            
            private Map<String, String> tags = new HashMap<String, String>(){{
                put("request", "div");
                put("get", "code");
                put("post", "code");
                put("jsonBody", "pre");
                put("setHeader", "code");
                put("status", "code");
                put("header", "code");
                put("jsonResponse", "pre");
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
                return tags.getOrDefault(localName, localName);
            }
        });
    }

    
    
}
