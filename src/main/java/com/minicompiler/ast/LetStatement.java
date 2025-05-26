package com.minicompiler.ast;

import com.minicompiler.lexer.Token;
import java.util.Objects;

public class LetStatement implements Statement {
    private final Token token; // The TokenType.LET token
    private final Identifier name;
    private final Expression value;

    public LetStatement(Token token, Identifier name, Expression value) {
        this.token = token;
        this.name = name;
        this.value = value;
    }

    public Token getToken() {
        return token;
    }

    public Identifier getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tokenLiteral()).append(" ");
        sb.append(name.toString());
        sb.append(" = ");
        if (value != null) {
            sb.append(value.toString());
        }
        sb.append(";");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LetStatement that = (LetStatement) o;
        return Objects.equals(token, that.token) &&
               Objects.equals(name, that.name) &&
               Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, name, value);
    }
}
