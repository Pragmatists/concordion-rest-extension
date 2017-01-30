package pl.pragmatists.concordion.rest;

import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;

import java.util.List;

import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import test.concordion.FixtureExtension;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
@Extensions(FixtureExtension.class)
public class CustomizingHostAndPortFixture {
    
    public RestExtension extension = new RestExtension();

    @Rule
    public WireMockRule http = new WireMockRule(9876);
    
    @Before 
    public void uninstallExtension() {
        System.clearProperty("concordion.extensions");
    }
    
    public void setHost(String host){
        extension = extension.host(host);
    }

    public void setPort(Integer port){
        extension = extension.port(port);
    }
    
    public ProcessingResult process(String html) {
       
        return new TestRig()
            .withFixture(this)
            .withExtension(extension)
            .withNamespaceDeclaration("rest", RestExtension.REST_EXTENSION_NS)
            .processFragment(html);
    }
    
    public LoggedRequest requestSentTo(String method, String url) {
        RequestPattern pattern = newRequestPattern(RequestMethod.fromString(method), WireMock.urlEqualTo(url)).build();
        List<LoggedRequest> requests = http.findRequestsMatching(pattern).getRequests();
        return requests.get(0);
    }
    
}
