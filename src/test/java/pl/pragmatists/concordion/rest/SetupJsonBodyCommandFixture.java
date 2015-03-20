package pl.pragmatists.concordion.rest;

import static com.github.tomakehurst.wiremock.matching.RequestPattern.everything;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import test.concordion.FixtureExtension;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

@RunWith(ConcordionRunner.class)
@Extensions(FixtureExtension.class)
public class SetupJsonBodyCommandFixture {

    @Rule
    public WireMockRule http = new WireMockRule();
   
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", RestExtension.class.getName());
    }
    
    public ProcessingResult process(String html){
        
        return new TestRig()
            .withFixture(this)
            .withNamespaceDeclaration("rest", RestExtension.REST_EXTENSION_NS)
            .processFragment(html);
            
    }
    
    public String requestBodyFor(String url) {
        List<LoggedRequest> requests = http.findRequestsMatching(everything()).getRequests();
        assertThat(requests).hasSize(1);
        return requests.get(0).getBodyAsString();
    }
}
