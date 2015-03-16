package test.concordion;

import java.lang.reflect.Field;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.BrowserStyleWhitespaceComparator;
import org.concordion.internal.util.Announcer;

import com.google.common.base.Objects;

public class IncludesHtmlCommand extends AbstractCommand implements AssertEqualsListener {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);

    public IncludesHtmlCommand() {
        addAssertEqualsListener(this);
    }
    
    public void addAssertEqualsListener(AssertEqualsListener listener) {
        listeners.addListener(listener);
    }

    public void removeAssertEqualsListener(AssertEqualsListener listener) {
        listeners.removeListener(listener);
    }

    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        org.concordion.api.Element element = commandCall.getElement();

        ProcessingResult result = (ProcessingResult) evaluator.evaluate(commandCall.getExpression());
        
        org.concordion.api.Element actual = result.getRootElement().getFirstDescendantNamed("fragment");
        org.concordion.api.Element expected = element;
        
        try {
            if (assertIncludesHtml(actual, expected)) {
                htmlIncludes(resultRecorder, element, actual, expected);
            } else {
                htmlDoesNotInclude(resultRecorder, element, actual, expected);
            }
        } catch (Exception e) {
            e.printStackTrace();
            htmlError(resultRecorder, element, actual, expected);
        }

    }

    protected void htmlError(ResultRecorder resultRecorder, org.concordion.api.Element element, org.concordion.api.Element actual, org.concordion.api.Element expected) {
        resultRecorder.record(Result.FAILURE);
        announceFailure(element, expected.toString(), actual);
    }

    protected void htmlDoesNotInclude(ResultRecorder resultRecorder, org.concordion.api.Element element, 
            org.concordion.api.Element actual, org.concordion.api.Element expected) throws Exception {
        resultRecorder.record(Result.FAILURE);
        announceFailure(element, expected.toString(), actual);
    }

    protected void htmlIncludes(ResultRecorder resultRecorder, org.concordion.api.Element element, org.concordion.api.Element actual, org.concordion.api.Element expected) {
        resultRecorder.record(Result.SUCCESS);
        announceSuccess(element);
    }

    private boolean assertIncludesHtml(org.concordion.api.Element actual, org.concordion.api.Element expected) throws Exception {

        return includesHtml(actual, expected);
    }

    private boolean includesHtml(Attribute actualHtml, Attribute expectedHtml) {

        if(expectedHtml != null && actualHtml == null){
            System.err.println("Attribute does not match: " + expectedHtml.getLocalName());
            return false;
        }
        
        if(!Objects.equal(actualHtml.getLocalName(), expectedHtml.getLocalName())){
            System.err.println("Attribute (local-name) does not match: " + actualHtml.getLocalName());
            return false;
        }
        
        if(!Objects.equal(actualHtml.getNamespacePrefix(), expectedHtml.getNamespacePrefix())){
            System.err.println("Attribute (namespace-prefix) does not match: " + actualHtml.getLocalName());
            return false;
        }
        if(!Objects.equal(actualHtml.getNamespaceURI(), expectedHtml.getNamespaceURI())){
            System.err.println("Attribute (namespace-uri) does not match: " + actualHtml.getLocalName());
            return false;
        }
        
        if ("class".equals(expectedHtml.getLocalName())){

            
            String[] expectedClasses = expectedHtml.getValue().split(" ");
            for (String expectedClass : expectedClasses) {
                if(!actualHtml.getValue().contains(expectedClass)){
                    System.err.println("Attribute (class:" + expectedClass + ") does not match: " + actualHtml.getLocalName());
                    return false;
                }
            }
            
            return true;
            
        } else {
            boolean areEqual = Objects.equal(actualHtml.getValue(), expectedHtml.getValue());
            if(!areEqual) {
                System.err.println("Attribute (value) does not match: " + actualHtml.getLocalName());
            }
            return areEqual; 
        }
    }
    
    private boolean includesHtml(org.concordion.api.Element actual, org.concordion.api.Element expected) {

        return compareChildren(unwrap(actual).getChildElements(), unwrap(expected).getChildElements());
    }
    
    private Element unwrap(org.concordion.api.Element actual) {
        
        // XXX: drity fix for now :(
        try {
            Field privateField = org.concordion.api.Element.class.getDeclaredField("xomElement");
            privateField.setAccessible(true);
            return (Element) privateField.get(actual);
        } catch (Exception e) {
            throw new RuntimeException("This is bad!", e);
        }
    }

    private boolean includesHtml(Element actualHtml, Element expectedHtml) {
        
        if(!Objects.equal(actualHtml.getLocalName(), expectedHtml.getLocalName())){
            System.err.println("Element (local-name) does not match: " + actualHtml.getLocalName());
            return false;
        }
        if(!Objects.equal(actualHtml.getNamespacePrefix(), expectedHtml.getNamespacePrefix())){
            System.err.println("Element (namespace-prefix) does not match: " + actualHtml.getLocalName());
            return false;
        }
        if(!Objects.equal(actualHtml.getNamespaceURI(), expectedHtml.getNamespaceURI())){
            System.err.println("Element (namespace-uri) does not match: " + actualHtml.getLocalName());
            return false;
        }

        return compareContent(actualHtml, expectedHtml);
    }

    private boolean compareContent(Element actualHtml, Element expectedHtml) {
       
        if(!compareChildren(actualHtml.getChildElements(), expectedHtml.getChildElements())){
            return false;
        }

        // compare attributes
        for(int i=0; i < expectedHtml.getAttributeCount(); i++){
            
            Attribute expectedAttribute = expectedHtml.getAttribute(i);
            Attribute actualAttribute = actualHtml.getAttribute(expectedAttribute.getLocalName());

            if(!includesHtml(actualAttribute, expectedAttribute)){
                return false;
            }
            
        }
        
        boolean areEqual = new BrowserStyleWhitespaceComparator().compare(actualHtml.getValue(), expectedHtml.getValue()) == 0;
        
        if(!areEqual){
            System.err.println(String.format("Element text (%s) does not match:\n%s\n\tvs.\n%s\n", actualHtml.getLocalName(), actualHtml.getValue(), expectedHtml.getValue()));
        }
        
        return areEqual;
    }

    private boolean compareChildren(Elements actualHtml, Elements expectedHtml) {
        // compare child elements
        for(int i=0; i < expectedHtml.size(); i++){
            Element expectedChild = expectedHtml.get(i);
            Element actualChild = actualHtml.get(i);
            
            if(!includesHtml(actualChild, expectedChild)){
                return false;
            }
        }
        
        return true;
    }
    
    protected void announceSuccess(org.concordion.api.Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    protected void announceFailure(org.concordion.api.Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }

    public void failureReported(AssertFailureEvent event) {
        org.concordion.api.Element element = event.getElement();
        element.addStyleClass("custom-failure");
        element.addAttribute("style", "overflow: hidden;");
        
        org.concordion.api.Element spanExpected = new org.concordion.api.Element("div");
        spanExpected.addStyleClass("output-expected");
        element.moveChildrenTo(spanExpected);
        element.appendChild(spanExpected);
        spanExpected.appendNonBreakingSpaceIfBlank();
        
        org.concordion.api.Element spanActual = new org.concordion.api.Element("div");
        spanActual.addStyleClass("output-actual");
        org.concordion.api.Element actualElement = (org.concordion.api.Element) event.getActual();
        actualElement.moveChildrenTo(spanActual);
        spanActual.appendNonBreakingSpaceIfBlank();
        
        element.appendText("\n");
        element.appendChild(spanActual);        
    }

    public void successReported(AssertSuccessEvent event) {
        event.getElement()
            .addStyleClass("output-success")
            .appendNonBreakingSpaceIfBlank();
    }
    
}
