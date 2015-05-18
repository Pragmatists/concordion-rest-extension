package pl.pragmatists.concordion.rest;

import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import test.concordion.FixtureExtension;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(ConcordionRunner.class)
@Extensions(FixtureExtension.class)
public class JsonPrettyPrintingFixture {

    @Rule
    public WireMockRule http = new WireMockRule();
    
    private StubbedRequestExecutor stubbedRequest = new StubbedRequestExecutor();
   
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", RestExtension.class.getName());
    }
    
    public void setResponseBody(String responseBody){
        stubbedRequest.stubBody(responseBody);
    }
    
    public ProcessingResult process(String html){
        
        TestRig testRig = new TestRig()
            .withFixture(this)
            .withNamespaceDeclaration("rest", RestExtension.REST_EXTENSION_NS);
        
        stubbedRequest.install(testRig.createEvaluator(this));
        
        return testRig            
            .processFragment(html);
            
    }
    
}
