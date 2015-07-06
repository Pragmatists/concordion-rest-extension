package pl.pragmatists.concordion.rest;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.CommandCallList;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.ExecuteEvent;
import org.concordion.api.listener.ExecuteListener;
import org.concordion.internal.util.Announcer;

public class RequestCommand extends AbstractCommand {

    private Announcer<ExecuteListener> listeners = Announcer.to(ExecuteListener.class);

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        
        RequestExecutor request = RequestExecutor.newExecutor(evaluator);

        CommandCallList childCommands = commandCall.getChildren();
        childCommands.setUp(evaluator, resultRecorder);
        
        childCommands.execute(evaluator, resultRecorder);

        request.execute();
        
        announceExecuteCompleted(commandCall.getElement());
        childCommands.verify(evaluator, resultRecorder);
    }

    private void announceExecuteCompleted(Element element) {
        listeners.announce().executeCompleted(new ExecuteEvent(element));
    }
    
}
