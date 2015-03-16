package test.concordion;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.DocumentParsingListener;

public class FixtureExtension implements ConcordionExtension{

    public static final String FIXTURE_EXTENSION_NS = "http://pragmatists.github.io/concordion-rest-extension";

    @Override
    public void addTo(ConcordionExtender concordionExtender) {
        
        concordionExtender.withCommand(FIXTURE_EXTENSION_NS, "assertHtmlIncludes", new IncludesHtmlCommand());
        concordionExtender.withLinkedCSS("/css/fixture-extension.css", new Resource("/css/fixture-extension.css"));
        concordionExtender.withDocumentParsingListener(new DocumentParsingListener() {

            @Override
            public void beforeParsing(Document document) {
           
                replaceFixturesWithEscapedHtml(document.getRootElement());
            }

            private void replaceFixturesWithEscapedHtml(Element elem) {

                Nodes fixtures = elem.query(String.format("//*[local-name()='%s' and namespace-uri()='%s']", "fixture", FIXTURE_EXTENSION_NS));
                
                for(int i=0; i<fixtures.size(); i++){
                    Element fixture = (Element)fixtures.get(0);
                    
                    StringBuilder xml = new StringBuilder();
                    
                    for(int c=0; c<fixture.getChildCount(); c++){
                        xml.append(fixture.getChild(c).toXML());
                    }
                    
                    fixture.removeChildren();
                    fixture.appendChild(xml.toString());
                    fixture.setLocalName("pre");
                    fixture.setNamespacePrefix(null);
                }
                
            }
        });
    }

    
    
}
