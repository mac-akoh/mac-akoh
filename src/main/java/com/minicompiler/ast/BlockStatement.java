package com.minicompiler.ast;

import com.minicompiler.lexer.Token;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlockStatement implements Statement {
    private final Token token; // The { token
    private final List<Statement> statements;

    public BlockStatement(Token token, List<Statement> statements) {
        this.token = token;
        this.statements = statements;
    }

    public Token getToken() {
        return token;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Statement s : statements) {
            sb.append(s.toString()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockStatement that = (BlockStatement) o;
        return Objects.equals(token, that.token) &&
               Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, statements);
    }
}
