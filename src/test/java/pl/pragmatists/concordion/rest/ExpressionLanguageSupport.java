package pl.pragmatists.concordion.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import test.concordion.FixtureExtension;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;


@RunWith(ConcordionRunner.class)
@Extensions(FixtureExtension.class)
public class ExpressionLanguageSupport extends SetupHttpMethodCommandFixture{

    public ProcessingResult process(String html){
        
        return new TestRig()
            .withFixture(this)
            .withNamespaceDeclaration("c", "http://www.concordion.org/2007/concordion")
            .withNamespaceDeclaration("rest", RestExtension.REST_EXTENSION_NS)
            .processFragment(html);
            
    }
    
    public String echo(String text){
        return text;
    }
    
    public String requestHeaderValueFor(String url, String headerName) {
        RequestPatternBuilder pattern = newRequestPattern().withUrl(url);
        List<LoggedRequest> requests = http.findRequestsMatching(pattern.build()).getRequests();
        assertThat(requests).hasSize(1);
        return requests.get(0).getHeader(headerName);
    }

    public String requestBodyFor(String url) {
        RequestPatternBuilder pattern = newRequestPattern().withUrl(url);
        List<LoggedRequest> requests = http.findRequestsMatching(pattern.build()).getRequests();
        assertThat(requests).hasSize(1);
        return requests.get(0).getBodyAsString();
    }
    
    public void respondWithHeader(String url, String headerName, String headerValue) {
        http.resetMappings();
        http.givenThat(
                get(urlMatching(url)).
                willReturn(aResponse().withStatus(200).withHeader(headerName, headerValue))
               );
    }    

    public void respondOkToAnyRequest() {
        http.resetMappings();
        http.givenThat(
                any(urlMatching("/.*")).
                willReturn(aResponse().withStatus(200))
               );
    }
    
    public void respondWithBody(String url, String body) {
        http.resetMappings();
        http.givenThat(
                get(urlMatching(url)).
                willReturn(aResponse().withStatus(200).withBody(body))
               );
    }

}
