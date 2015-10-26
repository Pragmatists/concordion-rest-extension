package pl.pragmatists.concordion.rest.util;

import java.util.Scanner;
import java.util.Stack;

public class JsonPrettyPrinter {

    enum State {
        UKNOWN, OBJECT, ARRAY;
    }
    
    public String prettyPrint(String json){

        if(json == null){
            return "";
        }
        
        return new Parser(json).parse();
    }
    
    private class Parser {
        
        private Scanner scanner;
        private StringBuilder result;
        private int indent = 0;
        private boolean newLine = false;
        private Stack<State> state = new Stack<State>();
        private String token;

        private Parser(String json) {
            scanner = new Scanner(json);
            scanner.useDelimiter("");
            result = new StringBuilder();
            enterState(State.UKNOWN);
        }

        String parse() {
        
            try{
                
                while(hasToken()){
                    
                    if(nextTokenIs("{")){
        
                        consumeToken();
                        if(!nextTokenIs("}")){
                            growIndent();
                            append("{");
                            enterState(State.OBJECT);
                        } else {
                            skipUnitl("}");
                            append("{}");
                            consumeToken();
                        }
                        continue;
                    }
                    if(nextTokenIs("}")){
                        shrinkIndent();
                    }
                    if(nextTokenIs("[")){
                        enterState(State.ARRAY);
                    }
                    if(nextTokenIs("]")){
                        leaveState();
                    }                        
                    if(nextTokenIs(",")){
                        append(",");
                        if(inState(State.OBJECT)){
                            newLine = true;
                        } else if(inState(State.ARRAY)) {
                            append(" ");
                        }
                        consumeToken();
                        continue;
                    }
                    if(nextTokenIs(":")){
                        append(": ");
                        consumeToken();
                        continue;
                    }
        
                    if(newLine){
                        append("\n");
                        indent();
                        newLine = false;
                    }
                    
                    if(nextTokenIs("\"")){
                        append("\"");
                        eatUnitl("\"");
                        consumeToken();
                        continue;
                    }
                    
                    if(nextTokenIs("'")){
                        append("'");
                        eatUnitl("'");
                        consumeToken();
                        continue;
                    }
                    
                    append(token);
                    consumeToken();
                }
                
                return result.toString();
                
            } finally {
                scanner.close();
            }
        }

        private boolean inState(State testState) {
            return state.peek() == testState;
        }

        private State leaveState() {
            if(!state.isEmpty()) {
                return state.pop();
            }
            return State.UKNOWN;
        }

        private void enterState(State newState) {
            state.push(newState);
        }

        private void indent() {
            for(int i=0;i<indent;i++){
                append("  ");
            }
        }

        private void append(String value) {
            result.append(value);
        }

        private boolean nextTokenIs(String testToken) {
            return hasToken() && testToken.equals(token);
        }

        private void eatUnitl(String desiredToken) {
            while(scanner.hasNext()){
                String x = scanner.next();
                result.append(x);
                if(x.equals(desiredToken)) break;
            }
        }

        private void skipUnitl(String desiredToken) {
            while(scanner.hasNext()){
                String x = scanner.next();
                if(x.equals(desiredToken)) break;
            }
        }

        private void shrinkIndent() {
            indent--;
            if(leaveState() == State.OBJECT){
                newLine = true;
            }
        }

        private void growIndent() {
            indent++;
            newLine = true;
        }

        private boolean hasToken() {
            
            if(token != null){
                return true;
            }
            
            while (scanner.hasNext()){
                token = scanner.next().trim();
                if(token.isEmpty()){
                    continue;
                } else {
                    return true;
                }
            }
            return false;
        }
        
        private void consumeToken(){
            token = null;
        }
    }
    
}
