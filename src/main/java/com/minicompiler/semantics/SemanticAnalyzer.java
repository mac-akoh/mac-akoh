package com.minicompiler.semantics;

import com.minicompiler.ast.*;
import com.minicompiler.lexer.Token;

import java.util.List;
import java.util.Optional;

public class SemanticAnalyzer {
    private final List<String> errors;

    public SemanticAnalyzer(List<String> errors) {
        this.errors = errors;
    }

    public void analyze(Node node, SymbolTable table) {
        if (node instanceof Program) {
            visit((Program) node, table);
        } else if (node instanceof LetStatement) {
            visit((LetStatement) node, table);
        } else if (node instanceof AssignmentStatement) {
            visit((AssignmentStatement) node, table);
        } else if (node instanceof Identifier) {
            visit((Identifier) node, table);
        } else if (node instanceof IntegerLiteral) {
            visit((IntegerLiteral) node, table);
        } else if (node instanceof BinaryExpression) {
            visit((BinaryExpression) node, table);
        } else if (node instanceof IfStatement) {
            visit((IfStatement) node, table);
        } else if (node instanceof WhileStatement) {
            visit((WhileStatement) node, table);
        } else if (node instanceof PrintStatement) {
            visit((PrintStatement) node, table);
        } else if (node instanceof BlockStatement) {
            visit((BlockStatement) node, table);
        } else if (node instanceof ExpressionStatement) {
            visit((ExpressionStatement) node, table);
        }
        // Other node types can be added here
    }

    private void visit(Program program, SymbolTable table) {
        for (Statement stmt : program.getStatements()) {
            analyze(stmt, table);
        }
    }

    private void visit(LetStatement stmt, SymbolTable table) {
        String varName = stmt.getName().getValue();
        // Check if already defined in the current scope of the table passed.
        // For simple global scope, this is fine. For nested scopes, table.isDefinedInCurrentScope(varName) would be better.
        if (table.isDefinedInCurrentScope(varName)) {
            Token token = stmt.getName().getToken();
            errors.add(String.format("Error: Variable '%s' already declared in this scope (line %d, col %d)",
                    varName, token.getLine(), token.getColumn()));
        } else {
            table.define(varName); // Define in the current table
        }
        analyze(stmt.getValue(), table); // Analyze the expression part
    }

    private void visit(AssignmentStatement stmt, SymbolTable table) {
        String varName = stmt.getName().getValue();
        Optional<Symbol> symbol = table.resolve(varName);
        if (symbol.isEmpty()) {
            Token token = stmt.getName().getToken();
            errors.add(String.format("Error: Variable '%s' not declared (line %d, col %d)",
                    varName, token.getLine(), token.getColumn()));
        }
        analyze(stmt.getValue(), table); // Analyze the expression part
    }

    private void visit(Identifier identifier, SymbolTable table) {
        String varName = identifier.getValue();
        Optional<Symbol> symbol = table.resolve(varName);
        if (symbol.isEmpty()) {
            Token token = identifier.getToken();
            errors.add(String.format("Error: Variable '%s' not declared (line %d, col %d)",
                    varName, token.getLine(), token.getColumn()));
        }
        // Type checking could happen here if types were more complex:
        // symbol.ifPresent(s -> { /* check type */ });
    }

    private void visit(IntegerLiteral expr, SymbolTable table) {
        // Integers are always semantically valid by themselves.
        // Type checking for operations involving them happens in BinaryExpression.
    }

    private void visit(BinaryExpression expr, SymbolTable table) {
        analyze(expr.getLeft(), table);
        analyze(expr.getRight(), table);
        // For now, all variables and literals are integers, so type checking on operands
        // is implicitly okay. If other types were introduced, we'd check here
        // that left and right expressions are compatible with the operator.
        // e.g. for +, -, *, /, <, >, ==, !=, <=, >=, both sides should be INTEGER.
    }

    private void visit(IfStatement stmt, SymbolTable table) {
        analyze(stmt.getCondition(), table); // Condition should result in a comparable value

        // Consequence block
        SymbolTable consequenceScope = new SymbolTable(table); // New scope for consequence
        analyze(stmt.getConsequence(), consequenceScope);

        // Alternative block (optional)
        if (stmt.getAlternative() != null) {
            SymbolTable alternativeScope = new SymbolTable(table); // New scope for alternative
            analyze(stmt.getAlternative(), alternativeScope);
        }
    }

    private void visit(WhileStatement stmt, SymbolTable table) {
        analyze(stmt.getCondition(), table); // Condition

        // Body block
        SymbolTable bodyScope = new SymbolTable(table); // New scope for while body
        analyze(stmt.getBody(), bodyScope);
    }

    private void visit(PrintStatement stmt, SymbolTable table) {
        analyze(stmt.getArgument(), table);
    }

    private void visit(BlockStatement stmt, SymbolTable table) {
        // If a BlockStatement is directly analyzed (not as part of if/while),
        // it gets its own scope. If it's part of if/while, they already created a scope.
        // For simplicity, let's assume if/while/func will create scopes.
        // If a standalone block is allowed by grammar and needs its own scope:
        // SymbolTable blockScope = new SymbolTable(table);
        // for (Statement s : stmt.getStatements()) {
        //    analyze(s, blockScope);
        // }
        // However, current visit(If/While) already creates a new scope for the block.
        // So, if this visit is called, it's likely the SymbolTable 'table' is already the correct new scope.
        for (Statement s : stmt.getStatements()) {
            analyze(s, table); // Analyze statements within the provided scope
        }
    }

    private void visit(ExpressionStatement stmt, SymbolTable table) {
        if (stmt.getExpression() != null) {
            analyze(stmt.getExpression(), table);
        }
    }
}
