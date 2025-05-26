package com.minicompiler.ast;

import com.minicompiler.lexer.Token;
import java.util.Objects;

public class IfStatement implements Statement {
    private final Token token; // The TokenType.IF token
    private final Expression condition;
    private final BlockStatement consequence;
    private final BlockStatement alternative; // Can be null if there's no else block

    public IfStatement(Token token, Expression condition, BlockStatement consequence, BlockStatement alternative) {
        this.token = token;
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
    }

    public Token getToken() {
        return token;
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockStatement getConsequence() {
        return consequence;
    }

    public BlockStatement getAlternative() {
        return alternative;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("if");
        sb.append(condition.toString());
        sb.append(" ");
        sb.append(consequence.toString());
        if (alternative != null) {
            sb.append("else ");
            sb.append(alternative.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IfStatement that = (IfStatement) o;
        return Objects.equals(token, that.token) &&
               Objects.equals(condition, that.condition) &&
               Objects.equals(consequence, that.consequence) &&
               Objects.equals(alternative, that.alternative);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, condition, consequence, alternative);
    }
}
