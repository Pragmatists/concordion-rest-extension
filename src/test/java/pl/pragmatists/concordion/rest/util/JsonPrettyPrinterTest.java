package pl.pragmatists.concordion.rest.util;

import org.junit.Before;

import static org.assertj.core.api.Assertions.*;

import org.junit.After;
import org.junit.Test;

public class JsonPrettyPrinterTest {

    private JsonPrettyPrinter printer;

    private String acualJson;
    private String expectedJson;
    
    @Before
    public void setUp() {
        printer = new JsonPrettyPrinter();
    }
    
    @After
    public void tearDown(){
        assertThat(printer.prettyPrint(acualJson)).isEqualTo(expectedJson);
    }
    
    @Test
    public void shouldHandleNull() throws Exception {
        acualJson = null;
        expectedJson = "";
    }

    @Test
    public void shouldHandleEmptyString() throws Exception {
        acualJson = "";
        expectedJson = "";
    }

    @Test
    public void shouldHandleSimpleString() throws Exception {
        acualJson = "string";
        expectedJson = "string";
    }
    
    @Test
    public void shouldTrimString() throws Exception {
        acualJson = "  string  ";
        expectedJson = "string";
    }

    @Test
    public void shouldHandleQuotedString() throws Exception {
        acualJson = "\" string \"";
        expectedJson = "\" string \"";
    }

    @Test
    public void shouldHandleDoubleQuotedStringWithEscapedDoubleQuote() throws Exception {
        acualJson = "\" string\\\" {  }\"";
        expectedJson = "\" string\\\" {  }\"";
    }

    @Test
    public void shouldHandleSingleQuotedString() throws Exception {
        acualJson = "' string '";
        expectedJson = "' string '";
    }

    @Test
    public void shouldHandleSingleQuotedStringWithEscapedSingleQuote() throws Exception {
        acualJson = "' string\\' {  }'";
        expectedJson = "' string\\' {  }'";
    }

    @Test
    public void shouldHandleEmptyObjectLiteral() throws Exception {
        acualJson = "{}";
        expectedJson = "{}";
    }

    @Test
    public void shouldHandleEmptyObjectLiteralInsideObject() throws Exception {
        acualJson = "{\"test\": {} }";
        expectedJson = "{\n  \"test\": {}\n}";
    }

    @Test
    public void shouldHandleEmptyObjectLiteralWithEmptySpaces() throws Exception {
        acualJson = " \t { \n\t}";
        expectedJson = "{}";
    }
    
    @Test
    public void shouldPrettyPrintSimpleLiteral() throws Exception {
        acualJson = "{\"property\":123}";
        expectedJson = "{\n  \"property\": 123\n}";
    }

    @Test
    public void shouldPrettyPrintSimpleLiteralWithoutQuotationMarks() throws Exception {
        acualJson = "{property:123}";
        expectedJson = "{\n  property: 123\n}";
    }

    @Test
    public void shouldPrettyPrintSimpleLiteralWithSingleQuotationMarks() throws Exception {
        acualJson = "{'property':123}";
        expectedJson = "{\n  'property': 123\n}";
    }
    
    @Test
    public void shouldHandleStringProperty() throws Exception {
        acualJson = "{\"property\":\"123\"}";
        expectedJson = "{\n  \"property\": \"123\"\n}";
    }

    @Test
    public void shouldHandleNumberProperty() throws Exception {
        acualJson = "{\"property\":123.456}";
        expectedJson = "{\n  \"property\": 123.456\n}";
    }
    
    @Test
    public void shouldHandleBooleanProperty() throws Exception {
        acualJson = "{\"property\":false}";
        expectedJson = "{\n  \"property\": false\n}";
    }

    @Test
    public void shouldHandleNullProperty() throws Exception {
        acualJson = "{\"property\":null}";
        expectedJson = "{\n  \"property\": null\n}";
    }
    
    @Test
    public void shouldHandleInvalidProperty() throws Exception {
        acualJson = "{\"property\":invalid}";
        expectedJson = "{\n  \"property\": invalid\n}";
    }

    @Test
    public void shouldIgnoreWhiteSpaces() throws Exception {
        acualJson = "{\"property\" \n:  \t\n false}";
        expectedJson = "{\n  \"property\": false\n}";
    }    

    @Test
    public void shouldHandleEmptyArray() throws Exception {
        acualJson = "[]";
        expectedJson = "[]";
    }

    @Test
    public void shouldHandleNumberArray() throws Exception {
        acualJson = "[1,2,3]";
        expectedJson = "[1, 2, 3]";
    }

    @Test
    public void shouldHandleStringArray() throws Exception {
        acualJson = "[\"1\",\"2\",\"3\"]";
        expectedJson = "[\"1\", \"2\", \"3\"]";
    }
    
    @Test
    public void shouldHandleObjectWithMultipleProperties() throws Exception {
        acualJson = "{\"a\":124,\"b\":true}";
        expectedJson = "{\n  \"a\": 124,\n  \"b\": true\n}";
    }

    @Test
    public void shouldHandleObjectWithRedudantComma() throws Exception {
        acualJson = "{\"a\":124,}";
        expectedJson = "{\n  \"a\": 124,\n}";
    }

    @Test
    public void shouldHandleObjectWithNestedObject() throws Exception {
        acualJson = "{\"a\":{\"b\":123}}";
        expectedJson = "{\n  \"a\": {\n    \"b\": 123\n  }\n}";
    }
    
    @Test
    public void shouldHandleComplexScenario() throws Exception {

        acualJson = "   {\"name\":\"John\"\n\n\n,   \"age\":unknown, children:[\"Jane\",{\"lastname \":\"Doe\"},],}} ";
        expectedJson = "{\n  \"name\": \"John\",\n  \"age\": unknown,\n  children: [\"Jane\", {\n    \"lastname \": \"Doe\"\n  }, ],\n}}";
    }
    
}
