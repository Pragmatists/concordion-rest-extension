package pl.pragmatists.concordion.rest;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.Before;
import org.junit.Rule;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;

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

        RequestPatternBuilder pattern = newRequestPattern(RequestMethod.fromString(method), urlEqualTo(url));
        List<LoggedRequest> requests = http.findRequestsMatching(pattern.build()).getRequests();
        return requests.size() == 1;
    }

}