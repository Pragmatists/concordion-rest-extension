package pl.pragmatists.concordion.rest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPrettyPrinter {

    public String prettyPrint(String json){
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode tree = mapper.readTree(json);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
        } catch (Exception e) {
            return json;
        }
    }
    
}
