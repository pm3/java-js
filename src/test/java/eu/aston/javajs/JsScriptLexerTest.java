package eu.aston.javajs;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsScriptLexerTest {

    @Test
    public void testTemplateStringLexing() {
        // Test basic template string
        String input = "`Hello, world!`";
        JsScriptLexer lexer = new JsScriptLexer(input);
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Hello, world!`", tokens.getFirst().getValue());
        
        // Test template string with interpolation
        input = "`Hello, ${name}!`";
        lexer = new JsScriptLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Hello, ${name}!`", tokens.getFirst().getValue());
        
        // Test template string with nested expressions
        input = "`Result: ${ a + b * (c / d) }`";
        lexer = new JsScriptLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Result: ${ a + b * (c / d) }`", tokens.getFirst().getValue());
        
        // Test template string with nested template strings
        input = "`Outer ${ `Inner ${value}` }`";
        lexer = new JsScriptLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Outer ${ `Inner ${value}` }`", tokens.getFirst().getValue());
        
        // Test multiline template string
        input = "`Line 1\nLine 2\nLine 3`";
        lexer = new JsScriptLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`Line 1\nLine 2\nLine 3`", tokens.getFirst().getValue());
        
        // Test complex template string with multiple interpolations
        input = "`User ${user.name} has ${user.messages.length} messages and ${user.isActive ? 'is' : 'is not'} active.`";
        lexer = new JsScriptLexer(input);
        tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_TEMPLATE, tokens.getFirst().getType());
        assertEquals("`User ${user.name} has ${user.messages.length} messages and ${user.isActive ? 'is' : 'is not'} active.`", tokens.getFirst().getValue());
    }
} 