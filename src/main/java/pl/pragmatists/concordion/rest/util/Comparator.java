package pl.pragmatists.concordion.rest.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comparator {
    
    public class Replacement {

        private String variable;
        private String original;

        public Replacement(String variable, String original) {
            this.variable = variable;
            this.original = original;
        }

        public String getVariable() {
            return variable;
        }

        public String getValue() {
            return matcher.group(variable);
        }

        public String replaceIn(String text) {
            return text.replaceAll(Pattern.quote(original), getValue());
        }

    }

    private Pattern pattern;
    private List<Replacement> replacements = new ArrayList<Replacement>();
    private Matcher matcher;
    
    public Comparator(String text){
        
        Pattern placeholder = Pattern.compile("(?:\\{\\s*)?\\$([a-zA-Z]+)(?::([^}]+))?\\}?");
        
        Matcher matcher = placeholder.matcher(text);
        while(matcher.find()){
            String group = matcher.group(0);
            String variable = matcher.group(1);
            String regexp = matcher.group(2);
            if(regexp == null){
                regexp = ".*?";
            }
            registerReplacement(variable, group);
            String replacement = "\\E(?<" + variable + ">" + regexp + ")\\Q"; 
            text = text.replace(group, replacement);
        }
        
        this.pattern = Pattern.compile("^\\Q" + text + "\\E$".replaceAll("\\\\Q\\\\E", ""));
    }
    
    private void registerReplacement(String variable, String group) {
        replacements.add(new Replacement(variable, group));
    }

    public boolean compareTo(String value){
        
        matcher = pattern.matcher(value);
        return matcher.matches();
    }
    
    public List<Replacement> replacements() {
        
        return replacements;
    }
    
}