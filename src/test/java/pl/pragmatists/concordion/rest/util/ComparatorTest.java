package pl.pragmatists.concordion.rest.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import pl.pragmatists.concordion.rest.util.Comparator.Replacement;

public class ComparatorTest {

    @Test
    public void shouldParseSimpleExpression() throws Exception {
        
        // given:
        Comparator comparator = new Comparator("Hello {$who}!");
        
        // when:
        boolean result = comparator.compareTo("Hello World!");
        
        // when:
        assertThat(result).isTrue();
        assertThat(extractVariables(comparator))
            .hasSize(1)
            .containsEntry("who", "World");        
    }

    @Test
    public void shouldParseUrlPattern() throws Exception {
        
        // given:
        Comparator comparator = new Comparator("/api/resource/{$id}");
        
        // when:
        boolean result = comparator.compareTo("/api/resource/abc123");
        
        // when:
        assertThat(result).isTrue();
        assertThat(extractVariables(comparator))
            .hasSize(1)
            .containsEntry("id", "abc123");        
    }

    @Test
    @Ignore("waiting for implementation")
    public void shouldParseValueWith$() throws Exception {
        
        // given:
        Comparator comparator = new Comparator("5\\$usd");
        
        // when:
        boolean result = comparator.compareTo("5$usd");
        
        // when:
        assertThat(result).isTrue();
        assertThat(extractVariables(comparator))
            .hasSize(0);
            
                
    }
    
    @Test
    public void shouldParseCustomRegexp() throws Exception {
        
        // given:
        Comparator comparator = new Comparator("/api/resource/$id:\\d+");
        
        // when:
        boolean result = comparator.compareTo("/api/resource/abc123");
        boolean result2 = comparator.compareTo("/api/resource/123");
        
        // when:
        assertThat(result).isFalse();
        assertThat(result2).isTrue();
    }

    @Test
    public void shouldParseCustomRegexpWithSuffix() throws Exception {
        
        // given:
        Comparator comparator = new Comparator("/api/resource/{$id:\\d+}/OK");
        
        // when:
        boolean result = comparator.compareTo("/api/resource/123/OK");
        
        // when:
        assertThat(result).isTrue();
        assertThat(extractVariables(comparator))
            .hasSize(1)
            .containsEntry("id", "123");        

    }
    
    @Test
    public void shouldParseStringWithMultiplePlaceholders() throws Exception {
        
        // given:
        Comparator comparator = new Comparator("/api/resource/$id?ts=$timestamp");
        
        // when:
        boolean result = comparator.compareTo("/api/resource/abc123?ts=9299388821");
        
        // when:
        assertThat(result).isTrue();
        assertThat(extractVariables(comparator))
            .hasSize(2)
            .containsEntry("id", "abc123")
            .containsEntry("timestamp", "9299388821");
    }

    @Test
    public void shouldParseJson() throws Exception {
        
        // given:
        Comparator comparator = new Comparator("{\"a\": \"{$x}\"}");
        
        // when:
        boolean result = comparator.compareTo("{\"a\": \"x\"}");
        
        // when:
        assertThat(result).isTrue();
        assertThat(extractVariables(comparator))
            .hasSize(1)
            .containsEntry("x", "x");
    }

    @Test
    public void shouldParseJsonWithoutBraces() throws Exception {
        
        // given:
        Comparator comparator = new Comparator("Hello, $x!");
        
        // when:
        boolean result = comparator.compareTo("Hello, World!");
        
        // when:
        assertThat(result).isTrue();
        assertThat(extractVariables(comparator))
        .hasSize(1)
        .containsEntry("x", "World");
    }

    
    //--
    
    private Map<String, String> extractVariables(Comparator comparator) {
        final Map<String, String> map = new HashMap<String, String>();
        for (Replacement r: comparator.replacements()) {
            map.put(r.getVariable(), r.getValue());
        }
        return map;
    }
    
}
