package pl.pragmatists.concordion.rest;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import test.concordion.FixtureExtension;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import java.util.List;

@RunWith(ConcordionRunner.class)
@Extensions(FixtureExtension.class)
public class EnableURLEncodingFixture {
    
    public RestExtension extension = new RestExtension().host("127.0.0.1").port(9876).disableUrlEncoding();

    @Rule
    public WireMockRule http = new WireMockRule(9876);
    
    @Before 
    public void uninstallExtension() {
        System.clearProperty("concordion.extensions");
    }
    
    public ProcessingResult process(String html) {
       
        return new TestRig()
            .withFixture(this)
            .withExtension(extension)
            .withNamespaceDeclaration("rest", RestExtension.REST_EXTENSION_NS)
            .processFragment(html);
    }
    
    public boolean requestSentTo(String method, String url) {
        List<LoggedRequest> requests = http.findRequestsMatching(new RequestPattern(RequestMethod.fromString(method), url)).getRequests();
        return requests.size() == 1;
    }

    public void enableUrlEncoding(){
        extension.enableUrlEncoding();
    }
}
