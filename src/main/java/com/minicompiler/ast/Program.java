package com.minicompiler.ast;

import java.util.List;
import java.util.stream.Collectors;

public class Program implements Node {
    private final List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public String tokenLiteral() {
        if (statements.isEmpty()) {
            return "";
        }
        return statements.get(0).tokenLiteral();
    }

    @Override
    public String toString() {
        return statements.stream()
                         .map(Node::toString)
                         .collect(Collectors.joining("\n"));
    }
}
