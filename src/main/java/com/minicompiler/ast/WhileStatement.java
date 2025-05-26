package com.minicompiler.ast;

import com.minicompiler.lexer.Token;
import java.util.Objects;

public class WhileStatement implements Statement {
    private final Token token; // The TokenType.WHILE token
    private final Expression condition;
    private final BlockStatement body;

    public WhileStatement(Token token, Expression condition, BlockStatement body) {
        this.token = token;
        this.condition = condition;
        this.body = body;
    }

    public Token getToken() {
        return token;
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockStatement getBody() {
        return body;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("while");
        sb.append(condition.toString());
        sb.append(" ");
        sb.append(body.toString());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhileStatement that = (WhileStatement) o;
        return Objects.equals(token, that.token) &&
               Objects.equals(condition, that.condition) &&
               Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, condition, body);
    }
}
