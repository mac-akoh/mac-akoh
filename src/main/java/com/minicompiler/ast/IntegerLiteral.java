package com.minicompiler.ast;

import com.minicompiler.lexer.Token;

import java.util.Objects;

public class IntegerLiteral implements Expression {
    private final Token token;
    private final long value;

    public IntegerLiteral(Token token, long value) {
        this.token = token;
        this.value = value;
    }

    public Token getToken() {
        return token;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        return token.getLiteral();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerLiteral that = (IntegerLiteral) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
