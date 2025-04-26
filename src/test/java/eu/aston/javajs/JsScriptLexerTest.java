package eu.aston.javajs;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsScriptLexerTest {

    @Test
    public void testTemplateStringLexing() {
        // Test basic template string
        String input = "`Hello, world!`";
        JsLexer lexer = new JsLexer(input);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Hello, world!`", tokens.getFirst().getValue());
        
        // Test template string with interpolation
        input = "`Hello, ${name}!`";
        lexer = new JsLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Hello, ${name}!`", tokens.getFirst().getValue());
        
        // Test template string with nested expressions
        input = "`Result: ${ a + b * (c / d) }`";
        lexer = new JsLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Result: ${ a + b * (c / d) }`", tokens.getFirst().getValue());
        
        // Test template string with nested template strings
        input = "`Outer ${ `Inner ${value}` }`";
        lexer = new JsLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Outer ${ `Inner ${value}` }`", tokens.getFirst().getValue());
        
        // Test multiline template string
        input = "`Line 1\nLine 2\nLine 3`";
        lexer = new JsLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Line 1\nLine 2\nLine 3`", tokens.getFirst().getValue());
        
        // Test complex template string with multiple interpolations
        input = "`User ${user.name} has ${user.messages.length} messages and ${user.isActive ? 'is' : 'is not'} active.`";
        lexer = new JsLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`User ${user.name} has ${user.messages.length} messages and ${user.isActive ? 'is' : 'is not'} active.`", tokens.getFirst().getValue());
    }
    
    @Test
    public void testRestIdentifierLexing() {
        // Test basic rest identifier
        String input = "...rest";
        JsLexer lexer = new JsLexer(input);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.REST_IDENTIFIER, tokens.getFirst().getType());
        assertEquals("...rest", tokens.getFirst().getValue());
        
        // Test rest identifier in an object destructuring
        input = "const { a, b, ...rest } = obj;";
        lexer = new JsLexer(input);
        tokens = lexer.tokenize();
        
        boolean hasRestIdentifier = false;
        for (Token token : tokens) {
            if (token.getType() == TokenType.REST_IDENTIFIER && token.getValue().equals("...rest")) {
                hasRestIdentifier = true;
                break;
            }
        }
        assertTrue(hasRestIdentifier, "Should contain a REST_IDENTIFIER token with value '...rest'");
        
        // Test rest identifier in an array destructuring
        input = "const [first, second, ...remaining] = array;";
        lexer = new JsLexer(input);
        tokens = lexer.tokenize();
        
        hasRestIdentifier = false;
        for (Token token : tokens) {
            if (token.getType() == TokenType.REST_IDENTIFIER && token.getValue().equals("...remaining")) {
                hasRestIdentifier = true;
                break;
            }
        }
        assertTrue(hasRestIdentifier, "Should contain a REST_IDENTIFIER token with value '...remaining'");
        
        // Test spread operator (not rest identifier) with array
        input = "const newArray = [...array1, ...array2];";
        lexer = new JsLexer(input);
        tokens = lexer.tokenize();
        
        // In this case, we should have two REST_IDENTIFIER tokens
        int restIdentifierCount = 0;
        for (Token token : tokens) {
            if (token.getType() == TokenType.REST_IDENTIFIER) {
                restIdentifierCount++;
            }
        }
        assertEquals(2, restIdentifierCount, "Should contain two REST_IDENTIFIER tokens");
    }
} 