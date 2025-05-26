package com.minicompiler.lexer;

import java.util.Objects;

public class Token {
    private final TokenType type;
    private final String literal;
    private final int line;
    private final int column;

    public Token(TokenType type, String literal, int line, int column) {
        this.type = type;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Token{" +
               "type=" + type +
               ", literal='" + literal + '\'' +
               ", line=" + line +
               ", column=" + column +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return line == token.line &&
               column == token.column &&
               type == token.type &&
               Objects.equals(literal, token.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, literal, line, column);
    }
}
