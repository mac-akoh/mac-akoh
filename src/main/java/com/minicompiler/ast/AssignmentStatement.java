package com.minicompiler.ast;

import com.minicompiler.lexer.Token; // Assuming Token is in lexer package
import java.util.Objects;

public class AssignmentStatement implements Statement {
    private final Token token; // The IDENTIFIER token (name of variable being assigned)
    private final Identifier name;
    private final Expression value;

    public AssignmentStatement(Token token, Identifier name, Expression value) {
        this.token = token; // Should be the token of the identifier
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
        return name.tokenLiteral(); // The literal of the identifier
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
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
        AssignmentStatement that = (AssignmentStatement) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
