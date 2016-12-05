package pl.pragmatists.concordion.rest;

import org.apache.commons.lang3.StringUtils;
import org.concordion.api.*;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.listener.AssertResultRenderer;
import org.concordion.internal.util.Announcer;

public class ExpectedStatusCodeCommand extends AbstractCommand {
    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);

    public ExpectedStatusCodeCommand() {
        listeners.addListener(new AssertResultRenderer());
    }

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        Integer expectedStatus = Integer.parseInt(StringUtils.trim(commandCall.getElement().getText()));
        RequestExecutor response = RequestExecutor.fromEvaluator(evaluator);
        int actualStatus = response.getStatusCode();

        System.err.println("STATUS(Expected): " + expectedStatus);
        System.err.println("STATUS(Actual): " + actualStatus);

        if (expectedStatus.equals(actualStatus)) {
            resultRecorder.record(Result.SUCCESS);
            announceSuccess(commandCall.getElement());
        } else {
            resultRecorder.record(Result.FAILURE);
            announceFailure(commandCall.getElement(), "" + expectedStatus, actualStatus);
        }

    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
}