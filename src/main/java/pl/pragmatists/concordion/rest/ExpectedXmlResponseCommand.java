package pl.pragmatists.concordion.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Serializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.internal.util.Announcer;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

public class ExpectedXmlResponseCommand extends AbstractCommand {

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);

    public ExpectedXmlResponseCommand() {
        listeners.addListener(new RestResultRenderer());
    }
    
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        Element element = commandCall.getElement();
        element.addStyleClass("xml");
        StringBuilder xml = new StringBuilder();
        
        Element[] child = element.getChildElements();
        for(int c=0; c<child.length; c++){
            xml.append(child[c].toXML());
        }
        element.moveChildrenTo(new Element("tmp"));
        
        String actual = RequestExecutor.fromEvaluator(evaluator).getBody();
        String expected = prettyPrint(xml.toString());
        element.appendText(expected);

        if(StringUtils.isBlank(actual)){
            xmlDoesNotEqual(resultRecorder, element, "(not set)", expected);
            return;
        }
        
        String prettyPrintedActual = prettyPrint(actual);
        System.err.println(String.format("Comparing: \n%s vs. \n %s", prettyPrintedActual, expected));

        try {
            if (assertEqualsXml(prettyPrintedActual, expected))
                xmlEquals(resultRecorder, element);
            else
                xmlDoesNotEqual(resultRecorder, element, prettyPrintedActual, expected);
        } catch (Exception e) {
            e.printStackTrace();
            xmlError(resultRecorder, element, prettyPrintedActual, expected);
        }
    }

    private String prettyPrint(String xml) {
        
        try {
    
            Builder parser = new Builder();
            Document document = parser.build(IOUtils.toInputStream(xml));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Serializer serializer = new Serializer(out, "UTF-8");
            serializer.setIndent(4);
            serializer.write(document);
            String pretty = out.toString();
            return pretty.substring(pretty.indexOf('\n') + 1); // replace first line
            
        } catch (Exception e) {
            throw new RuntimeException("invlaid xml", e);
        }
    }

    private boolean assertEqualsXml(String actual, String expected) throws TransformerException, SAXException, IOException {

        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        Diff diff = XMLUnit.compareXML(actual, expected);
        if (diff.identical())
            return true;
        throw new RuntimeException(diff.toString());
    }

    protected void xmlEquals(ResultRecorder resultRecorder, Element element) {
        resultRecorder.record(Result.SUCCESS);
        announceSuccess(element);
    }

    protected void xmlDoesNotEqual(ResultRecorder resultRecorder, Element element, String actual, String expected) {
        resultRecorder.record(Result.FAILURE);
        announceFailure(element, expected, actual);
    }

    protected void xmlError(ResultRecorder resultRecorder, Element element, String actual, String expected) {
        resultRecorder.record(Result.FAILURE);
        announceFailure(element, expected, actual);
    }

    protected void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertSuccessEvent(element));
    }

    protected void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertFailureEvent(element, expected, actual));
    }
}
