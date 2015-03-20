package pl.pragmatists.concordion.rest;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

public class SetupHttpMethodCommandFixture {

    @Rule
    public WireMockRule http = new WireMockRule();

    public SetupHttpMethodCommandFixture() {
        super();
    }

    @Before
    public void installExtension() {
        System.setProperty("concordion.extensions", RestExtension.class.getName());
    }

    public ProcessingResult process(String html) {
        
        return new TestRig()
            .withFixture(this)
            .withNamespaceDeclaration("rest", RestExtension.REST_EXTENSION_NS)
            .processFragment(html);
            
    }

    public boolean requestSent(String method, String url) {
        List<LoggedRequest> requests = http.findRequestsMatching(new RequestPattern(RequestMethod.fromString(method), url)).getRequests();
        return requests.size() == 1;
    }

}