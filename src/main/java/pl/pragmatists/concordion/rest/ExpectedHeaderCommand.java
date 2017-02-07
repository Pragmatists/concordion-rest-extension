package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.listener.AssertResultRenderer;
import org.concordion.internal.util.Announcer;

import pl.pragmatists.concordion.rest.util.Comparator;
import pl.pragmatists.concordion.rest.util.Comparator.Replacement;

public class ExpectedHeaderCommand extends AbstractCommand {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    
    public ExpectedHeaderCommand() {
        listeners.addListener(new AssertResultRenderer());
    }
    
    @Override
    public void verify(CommandCall commandCall, final Evaluator evaluator, ResultRecorder resultRecorder) {
        
        Element element = commandCall.getElement();
        element.addStyleClass("header");
        
        RequestExecutor response =  RequestExecutor.fromEvaluator(evaluator);
        String expectedHeader = response.resolve(element.getText().trim(), evaluator);
        String actualHeader = response.getHeader(element.getAttributeValue("name"));
        if (actualHeader == null)
            actualHeader = "(not set)";
        
        
        Comparator comparator = new Comparator(expectedHeader);
        boolean equal = comparator.compareTo(actualHeader); 
        
        if(equal){
            
            for(Replacement r: comparator.replacements()){
                evaluator.setVariable("#" + r.getVariable(), r.getValue());
                expectedHeader = r.replaceIn(expectedHeader);
            }

            element.moveChildrenTo(new Element("tmp"));
            element.appendText(expectedHeader);
            
            resultRecorder.record(Result.SUCCESS);
            announceSuccess(element);
        } else {
            
            element.moveChildrenTo(new Element("tmp"));
            element.appendText(expectedHeader);

            resultRecorder.record(Result.FAILURE);
            announceFailure(element, expectedHeader, actualHeader);
        }

    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
    
}
