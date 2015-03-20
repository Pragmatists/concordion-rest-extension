package pl.pragmatists.concordion.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import test.concordion.FixtureExtension;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPattern;

@RunWith(ConcordionRunner.class)
@Extensions(FixtureExtension.class)
public class JsonResponseCommandFixture {

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
    
    public void respondOkFor(String url) {
        respondWith(200, url);
    }
    
    public void respondWith(Integer code, String url){
        http.resetMappings();
        http.givenThat(
                get(urlMatching(url)).
                willReturn(aResponse().withStatus(code))
               );
    }
    
    public void respondWithBody(String url, String body) {
        http.resetMappings();
        http.givenThat(
                get(urlMatching(url)).
                willReturn(aResponse().withStatus(200).withBody(body))
               );
    }
    
    public boolean urlHasBeenRequestedWithGET(String url) {
        RequestPattern request = getRequestedFor(urlMatching(url)).build();
        return http.findRequestsMatching(request).getRequests().size() == 1;
    }
}
