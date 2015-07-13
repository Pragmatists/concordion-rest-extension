package test.concordion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.concordion.Concordion;
import org.concordion.api.Evaluator;
import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.api.Source;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.SimpleEvaluator;


public class TestRig implements EvaluatorFactory {

    private Object fixture = null;
    private StubSource stubSource = new StubSource();
    private SimpleEvaluator evaluator;
    private Source source = stubSource;
    private StubTarget stubTarget = new StubTarget();
    private String namespaceDeclaration = "xmlns:concordion='" + ConcordionBuilder.NAMESPACE_CONCORDION_2007 + "'";
    private List<ConcordionExtension> extensions = new ArrayList<ConcordionExtension>();

    public TestRig withFixture(Object fixture) {
        this.fixture = fixture;
        this.evaluator = new SimpleEvaluator(fixture);
        return this;
    }

    public TestRig withNamespaceDeclaration(String prefix, String namespace) {
        namespaceDeclaration += " " + String.format("xmlns:%s='%s'", prefix, namespace);
        return this;
    }
    
    public ProcessingResult processFragment(String fragment) {
        return process(wrapFragment(fragment), "/testrig");
    }

    public ProcessingResult processFragment(String fragment, String fixtureName) {
        return process(wrapFragment(fragment), fixtureName);
    }

    public ProcessingResult process(Resource resource) {
        EventRecorder eventRecorder = new EventRecorder();
        
        ConcordionBuilder builder = new ConcordionBuilder()
            .withAssertEqualsListener(eventRecorder)
            .withThrowableListener(eventRecorder)
            .withSource(source)
            .withEvaluatorFactory(this)
            .withTarget(stubTarget);
        
        for (ConcordionExtension e : extensions) {
            e.addTo(builder);
        }
        
        Concordion concordion = builder.build();
        
        try {
            ResultSummary resultSummary = concordion.process(resource, fixture);
            String xml = stubTarget.getWrittenString(resource);
            return new ProcessingResult(resultSummary, eventRecorder, xml);
        } catch (IOException e) {
            throw new RuntimeException("Test rig failed to process specification", e);
        } 
    }

    public ProcessingResult process(String html, String fullName) {
        Resource resource = new Resource(fullName);
        withResource(resource, html);
        return process(resource);
    }

    private String wrapFragment(String fragment) {
        fragment = "<body><fragment>" + fragment + "</fragment></body>";
        return wrapWithNamespaceDeclaration(fragment);
    }
    
    private String wrapWithNamespaceDeclaration(String fragment) {
        return "<html " + namespaceDeclaration + ">"
            + fragment
            + "</html>";
    }

    public TestRig withSourceFilter(String filterPrefix) {
        this.source = new FilterSource(source, filterPrefix);
        return this;
    }
    
    public TestRig withResource(Resource resource, String content) {
        stubSource.addResource(resource, content);
        return this;
    }
    
    public TestRig withOutputStreamer(OutputStreamer streamer) {
        stubTarget.setOutputStreamer(streamer);
        return this;
    }
    
    public boolean hasCopiedResource(Resource resource) {
        return stubTarget.hasCopiedResource(resource);
    }

    @Override
    public Evaluator createEvaluator(Object fixture) {       
        return evaluator;
    }

    public TestRig withExtension(ConcordionExtension extension) {
        extensions.add(extension);
        return this;
    }

}
