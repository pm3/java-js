package eu.aston.javajs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsLexer {

    // All ECMAScript 3.1 keywords
    private static final Map<String, TokenType> RESERVED_WORDS = new HashMap<>();
    static {
        String[] keywords = {
                "break", "case", "catch", "continue", "const", "default",
                "do", "else", "finally", "for", "function", "if", "in",
                "let", "of", "return", "switch", "this", "throw", "try", "typeof", "var", "while"
        };

        for (String keyword : keywords) {
            RESERVED_WORDS.put(keyword, TokenType.KEYWORD);
        }
        RESERVED_WORDS.put("true", TokenType.BOOLEAN);
        RESERVED_WORDS.put("false", TokenType.BOOLEAN);
        RESERVED_WORDS.put("null", TokenType.NULL);
        RESERVED_WORDS.put("undefined", TokenType.UNDEFINED);
        RESERVED_WORDS.put("NaN", TokenType.NUMBER);
        RESERVED_WORDS.put("Infinity", TokenType.NUMBER);
    }

    private final String input;
    private int position;
    private int line;
    private int column;

    public JsLexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }

    public JsLexer(String input, int line, int column){
        this.input = input;
        this.position = 0;
        this.line = line;
        this.column = column;
    }

    // Main method to tokenize the input and build a token tree
    public List<Token> tokenize() {
        // Root token to hold all top-level tokens
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            Token token = getNextToken();
            if (token.getType() != TokenType.WHITESPACE && token.getType() != TokenType.COMMENT) {
                tokens.add(token);
            }
        }

        return tokens;
    }

    private Token getNextToken() {
        if (position >= input.length()) {
            return new Token(TokenType.EOF, "", line, column);
        }

        char ch = input.charAt(position);

        // Whitespace
        if (isWhitespace(ch)) {
            return scanWhitespace();
        }

        // Comments
        if (ch == '/' && position + 1 < input.length()) {
            char nextCh = input.charAt(position + 1);
            if (nextCh == '/') {
                return scanLineComment();
            } else if (nextCh == '*') {
                return scanBlockComment();
            }
        }

        // Number
        if (isDigit(ch) || (ch == '.' && position + 1 < input.length() && isDigit(input.charAt(position + 1)))) {
            return scanNumber();
        }

        // Template string (backtick)
        if (ch == '`') {
            return scanTemplateString();
        }

        // String
        if (ch == '"' || ch == '\'') {
            return scanString();
        }

        // Identifier or keyword
        if (isIdentifierStart(ch)) {
            return scanIdentifier();
        }

        // Operators and punctuation
        return scanOperatorOrPunctuation();
    }

    private boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == '\f';
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private boolean isHexDigit(char ch) {
        return isDigit(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }

    private boolean isIdentifierStart(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_' || ch == '$';
    }

    private boolean isIdentifierPart(char ch) {
        return isIdentifierStart(ch) || isDigit(ch);
    }

    private Token scanWhitespace() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        while (position < input.length() && isWhitespace(input.charAt(position))) {
            char ch = input.charAt(position);
            sb.append(ch);

            if (ch == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }

            position++;
        }

        return new Token(TokenType.WHITESPACE, sb.toString(), startLine, startColumn);
    }

    private Token scanLineComment() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        // Skip the //
        sb.append("//");
        position += 2;
        column += 2;

        while (position < input.length() && input.charAt(position) != '\n') {
            sb.append(input.charAt(position));
            position++;
            column++;
        }

        return new Token(TokenType.COMMENT, sb.toString(), startLine, startColumn);
    }

    private Token scanBlockComment() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        // Skip the /*
        sb.append("/*");
        position += 2;
        column += 2;

        boolean foundEnd = false;
        while (position < input.length() && !foundEnd) {
            char ch = input.charAt(position);
            sb.append(ch);

            if (ch == '*' && position + 1 < input.length() && input.charAt(position + 1) == '/') {
                sb.append('/');
                position += 2;
                column += 2;
                foundEnd = true;
            } else {
                if (ch == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
                position++;
            }
        }

        return new Token(TokenType.COMMENT, sb.toString(), startLine, startColumn);
    }

    private Token scanNumber() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        // Check for hexadecimal
        if (position + 1 < input.length() && input.charAt(position) == '0' &&
                (input.charAt(position + 1) == 'x' || input.charAt(position + 1) == 'X')) {
            sb.append("0");
            sb.append(input.charAt(position + 1));
            position += 2;
            column += 2;

            while (position < input.length() && isHexDigit(input.charAt(position))) {
                sb.append(input.charAt(position));
                position++;
                column++;
            }

            return new Token(TokenType.NUMBER, sb.toString(), startLine, startColumn);
        }

        // Check for octal (starts with 0)
        if (position < input.length() && input.charAt(position) == '0' && position + 1 < input.length() &&
                isDigit(input.charAt(position + 1))) {
            sb.append('0');
            position++;
            column++;

            while (position < input.length() && isDigit(input.charAt(position))) {
                sb.append(input.charAt(position));
                position++;
                column++;
            }

            return new Token(TokenType.NUMBER, sb.toString(), startLine, startColumn);
        }

        // Decimal number
        boolean hasDecimalPoint = false;
        while (position < input.length() &&
                (isDigit(input.charAt(position)) ||
                        (input.charAt(position) == '.' && !hasDecimalPoint))) {

            if (input.charAt(position) == '.') {
                hasDecimalPoint = true;
            }

            sb.append(input.charAt(position));
            position++;
            column++;
        }

        // Check for exponent
        if (position < input.length() && (input.charAt(position) == 'e' || input.charAt(position) == 'E')) {
            sb.append(input.charAt(position));
            position++;
            column++;

            // Check for sign
            if (position < input.length() && (input.charAt(position) == '+' || input.charAt(position) == '-')) {
                sb.append(input.charAt(position));
                position++;
                column++;
            }

            // Exponent digits
            while (position < input.length() && isDigit(input.charAt(position))) {
                sb.append(input.charAt(position));
                position++;
                column++;
            }
        }

        return new Token(TokenType.NUMBER, sb.toString(), startLine, startColumn);
    }

    private Token scanString() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        char quote = input.charAt(position);
        sb.append(quote);
        position++;
        column++;

        boolean escaped = false;
        while (position < input.length()) {
            char ch = input.charAt(position);

            if (escaped) {
                sb.append(ch);
                escaped = false;
            } else if (ch == '\\') {
                sb.append(ch);
                escaped = true;
            } else if (ch == quote) {
                sb.append(ch);
                position++;
                column++;
                break;
            } else if (ch == '\n') {
                // Handle line breaks in strings
                sb.append(ch);
                line++;
                column = 1;
            } else {
                sb.append(ch);
            }

            position++;
            if (ch != '\n') {
                column++;
            }
        }

        return new Token(TokenType.STRING, sb.toString(), startLine, startColumn);
    }

    private Token scanIdentifier() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        while (position < input.length() && isIdentifierPart(input.charAt(position))) {
            sb.append(input.charAt(position));
            position++;
            column++;
        }

        String identifier = sb.toString();
        TokenType type = RESERVED_WORDS.getOrDefault(identifier, TokenType.IDENTIFIER);

        return new Token(type, identifier, startLine, startColumn);
    }

    private Token scanOperatorOrPunctuation() {
        int startLine = line;
        int startColumn = column;
        char ch = input.charAt(position);

        // Handle multi-character operators
        if (position + 1 < input.length()) {
            String twoChars = input.substring(position, position + 2);

            if (position + 2 < input.length()) {
                String threeChars = input.substring(position, position + 3);

                // Three-character operators
                switch (threeChars) {
                    case "===":
                    case "!==":
                        position += 3;
                        column += 3;
                        return new Token(TokenType.OPERATOR, threeChars, startLine, startColumn);
                }
            }

            // Two-character operators
            switch (twoChars) {
                case "==":
                case "!=":
                case "<=":
                case ">=":
                case "++":
                case "--":
                case "+=":
                case "-=":
                case "*=":
                case "/=":
                case "%=":
                case "&&":
                case "||":
                case "=>":
                case "??":
                case "?.":
                case "**":
                    position += 2;
                    column += 2;
                    return new Token(TokenType.OPERATOR, twoChars, startLine, startColumn);
            }
        }

        // Single character operators
        switch (ch) {
            case '+':
            case '-':
            case '*':
            case '/':
            case '%':
            case '!':
            case '<':
            case '>':
            case '=':
                position++;
                column++;
                return new Token(TokenType.OPERATOR, String.valueOf(ch), startLine, startColumn);

            // Punctuation
            case ';':
            case ',':
            case '.':
            case ':':
            case '(':
            case ')':
            case '[':
            case ']':
            case '{':
            case '}':
            case '?':
                position++;
                column++;
                return new Token(TokenType.PUNCTUATION, String.valueOf(ch), startLine, startColumn);

            default:
                // Unrecognized character
                position++;
                column++;
                return new Token(TokenType.OPERATOR, String.valueOf(ch), startLine, startColumn);
        }
    }

    /**
     * Scans a template string (enclosed in backticks), handling interpolation expressions.
     * Template strings can span multiple lines and contain interpolation with ${expression}.
     */
    private Token scanTemplateString() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        List<Token> subtokens = new ArrayList<>();
        int lastDelimiterPos = position+1;
        int lastDelimiterLine = line;
        int lastDelimiterColumn = column+1;

        // Add opening backtick
        sb.append('`');
        position++;
        column++;

        boolean escaped = false;
        while (position < input.length()) {
            char ch = input.charAt(position);

            if (escaped) {
                // Handle escape sequence
                sb.append(ch);
                escaped = false;
                position++;
                column++;
            } else if (ch == '\\') {
                // Start of escape sequence
                sb.append(ch);
                escaped = true;
                position++;
                column++;
            } else if (ch == '`') {
                // End of template string
                sb.append(ch);
                position++;
                column++;
                break;
            } else if (ch == '$' && position + 1 < input.length() && input.charAt(position + 1) == '{') {
                // Handle interpolation start
                subtokens.add(new Token(TokenType.STRING, input.substring(lastDelimiterPos, position), lastDelimiterLine, lastDelimiterColumn));
                lastDelimiterPos = position;
                lastDelimiterLine = line;
                lastDelimiterColumn = column;
                sb.append("${");
                position += 2;
                column += 2;
                
                // Track nested curly braces to correctly handle complex expressions
                int nestedBraces = 1;
                while (position < input.length() && nestedBraces > 0) {
                    ch = input.charAt(position);
                    
                    if (ch == '{') {
                        nestedBraces++;
                        sb.append(ch);
                        position++;
                        column++;
                    } else if (ch == '}') {
                        nestedBraces--;
                        sb.append(ch);
                        position++;
                        column++;
                        // If this is the closing brace of the interpolation, stop here
                        if (nestedBraces == 0) {
                            break;
                        }
                    } else if (ch == '\n') {
                        sb.append(ch);
                        line++;
                        column = 1;
                        position++;
                    } else if (ch == '\\' && position + 1 < input.length() && 
                              (input.charAt(position + 1) == '"' || input.charAt(position + 1) == '\'' || 
                               input.charAt(position + 1) == '`')) {
                        // Handle escaped quotes in interpolation
                        sb.append(ch);
                        sb.append(input.charAt(position + 1));
                        position += 2;
                        column += 2;
                    } else if (ch == '"' || ch == '\'' || ch == '`') {
                        // Handle quoted strings in interpolation to prevent closing on a brace within a string
                        char quote = ch;
                        sb.append(quote);
                        position++;
                        column++;
                        
                        boolean stringEscaped = false;
                        while (position < input.length()) {
                            ch = input.charAt(position);
                            
                            if (stringEscaped) {
                                sb.append(ch);
                                stringEscaped = false;
                            } else if (ch == '\\') {
                                sb.append(ch);
                                stringEscaped = true;
                            } else if (ch == quote) {
                                sb.append(ch);
                                break;
                            } else if (ch == '\n') {
                                sb.append(ch);
                                line++;
                                column = 1;
                                position++;
                                continue;
                            }
                            
                            sb.append(ch);
                            position++;
                            column++;
                        }
                        
                        position++;
                        column++;
                    } else {
                        sb.append(ch);
                        position++;
                        column++;
                    }
                }
                subtokens.add(new Token(TokenType.STRING, input.substring(lastDelimiterPos, position), lastDelimiterLine, lastDelimiterColumn));
                lastDelimiterPos = position;
                lastDelimiterLine = line;
                lastDelimiterColumn = column;
                // Note: we continue the outer loop here without incrementing position further
                // This allows processing to continue with the next character after the closing brace
                // continue;
            } else if (ch == '\n') {
                // Handle line breaks in template strings (allowed in ES6+)
                sb.append(ch);
                line++;
                column = 1;
                position++;
            } else {
                sb.append(ch);
                column++;
                position++;
            }
        }
        subtokens.add(new Token(TokenType.STRING, input.substring(lastDelimiterPos, position-1), lastDelimiterLine, lastDelimiterColumn));
        return new Token(TokenType.STRING_TEMPLATE, sb.toString(), startLine, startColumn, subtokens);
    }
}