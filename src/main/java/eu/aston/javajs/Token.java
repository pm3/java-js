package eu.aston.javajs;

import java.util.List;

// Token class to represent each token
public class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;
    private final List<Token> subtokens;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
        this.subtokens = null;
    }

    public Token(TokenType type, String value, int line, int column, List<Token> subtokens) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
        this.subtokens = subtokens;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public List<Token> getSubtokens() {
        return subtokens;
    }

    public TokenPos tokenPos() {
        return new TokenPos(line, column);
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + ", value='" + value + '\'' + ", line=" + line + ", column=" + column + '}';
    }
}
