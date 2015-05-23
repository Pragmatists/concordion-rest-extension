package pl.pragmatists.concordion.rest.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class JsonPrettyPrinter {

    public String prettyPrint(String json){
        
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement element = gson.fromJson(json, JsonElement.class);
            return gson.toJson(element);
            
        } catch (Exception e) {
            return json;
        }
    }
    
}
