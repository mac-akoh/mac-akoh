package com.minicompiler.ast;

import com.minicompiler.lexer.Token;
import java.util.Objects;

public class PrintStatement implements Statement {
    private final Token token; // The TokenType.PRINT token
    private final Expression argument;

    public PrintStatement(Token token, Expression argument) {
        this.token = token;
        this.argument = argument;
    }

    public Token getToken() {
        return token;
    }

    public Expression getArgument() {
        return argument;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tokenLiteral()).append("(");
        if (argument != null) {
            sb.append(argument.toString());
        }
        sb.append(");");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintStatement that = (PrintStatement) o;
        return Objects.equals(token, that.token) &&
               Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, argument);
    }
}
