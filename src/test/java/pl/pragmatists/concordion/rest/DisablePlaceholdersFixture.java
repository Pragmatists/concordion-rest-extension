package pl.pragmatists.concordion.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import java.util.List;

import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import test.concordion.FixtureExtension;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

@RunWith(ConcordionRunner.class)
@Extensions(FixtureExtension.class)
public class DisablePlaceholdersFixture {
    
    public RestExtension extension = new RestExtension();

    @Rule
    public WireMockRule http = new WireMockRule();

    @Before 
    public void uninstallExtension() {
        System.clearProperty("concordion.extensions");
    }
    
    public void stub(String endpoint, String body, String sessionId){
        http.resetMappings();
        http.givenThat(
                get(urlMatching(endpoint)).
                willReturn(aResponse().withStatus(200)
                        .withHeader("Set-Cookie", "JSESSIONID=" + sessionId)
                        .withBody(body))
               );

    }
    
    public void disablePlaceholders(){
        extension = extension.disablePlaceholders();
    }
    
    public ProcessingResult process(String html) {
       
        return new TestRig()
            .withFixture(this)
            .withExtension(extension)
            .withNamespaceDeclaration("rest", RestExtension.REST_EXTENSION_NS)
            .processFragment(html);
    }
    
    public LoggedRequest requestSentTo(String method, String url) {
        List<LoggedRequest> requests = http.findRequestsMatching(new RequestPattern(RequestMethod.fromString(method), url)).getRequests();
        return requests.get(0);
    }
    
}
