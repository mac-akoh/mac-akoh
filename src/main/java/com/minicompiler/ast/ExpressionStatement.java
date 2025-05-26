package com.minicompiler.ast;

import com.minicompiler.lexer.Token;
import java.util.Objects;

public class ExpressionStatement implements Statement {
    private final Token token; // The first token of the expression
    private final Expression expression;

    public ExpressionStatement(Token token, Expression expression) {
        this.token = token;
        this.expression = expression;
    }

    public Token getToken() {
        return token;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        if (expression != null) {
            return expression.toString();
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionStatement that = (ExpressionStatement) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
