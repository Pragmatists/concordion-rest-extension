package pl.pragmatists.concordion.rest;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Resource;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Target;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.api.listener.ConcordionBuildEvent;
import org.concordion.api.listener.ConcordionBuildListener;
import org.concordion.internal.listener.AssertResultRenderer;
import org.concordion.internal.util.Announcer;

public class ExpectedAttachmentCommand extends AbstractCommand {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);

    private Target target;
    
    public ExpectedAttachmentCommand(ConcordionExtender concordionExtender) {
        concordionExtender.withBuildListener(new ConcordionBuildListener() {
            @Override
            public void concordionBuilt(ConcordionBuildEvent event) {
                target = event.getTarget();
            }
        });
        listeners.addListener(new AssertResultRenderer());
    }
    
    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        Element element = commandCall.getElement();
        String filename = element.getText();
        RequestExecutor response = RequestExecutor.fromEvaluator(evaluator);
        InputStream actualBody = response.getBodyAsInputStream();

        Resource resource = commandCall.getResource();
        System.err.println(resource);
        
        Resource destination = resource.getRelativeResource(filename);

        try {
            OutputStream output = target.getOutputStream(destination);
            int copied = IOUtils.copy(actualBody, output);
            if(copied <= 0){
                throw new IllegalStateException("No content copied from response body!");
            }
        } catch (Exception e) {
            resultRecorder.record(Result.FAILURE);
            announceFailure(element, filename, "(no attachment)");
            return;
        }
        
        resultRecorder.record(Result.SUCCESS);
        Element attachmentLink = new Element("a");
        element.moveChildrenTo(attachmentLink);
        attachmentLink.addAttribute("href", resource.getRelativePath(destination));
        element.appendChild(attachmentLink);
        announceSuccess(element);
        
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
    
}
