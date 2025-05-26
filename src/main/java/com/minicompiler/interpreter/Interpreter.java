package com.minicompiler.interpreter;

import com.minicompiler.ast.*;
import com.minicompiler.lexer.Token; // For error reporting with location

import java.util.List;
import java.util.Optional;

public class Interpreter {
    private final List<String> errors;

    // Static instances for convenience
    public static final MCBoolean TRUE = MCBoolean.TRUE;
    public static final MCBoolean FALSE = MCBoolean.FALSE;
    public static final MCNull NULL = MCNull.INSTANCE;

    public Interpreter(List<String> errors) {
        this.errors = errors;
    }

    public MCObject eval(Node node, Environment environment) {
        if (node instanceof Program) {
            return evalProgram((Program) node, environment);
        } else if (node instanceof BlockStatement) {
            return evalBlockStatement((BlockStatement) node, environment);
        } else if (node instanceof ExpressionStatement) {
            return evalExpressionStatement((ExpressionStatement) node, environment);
        } else if (node instanceof LetStatement) {
            return evalLetStatement((LetStatement) node, environment);
        } else if (node instanceof AssignmentStatement) {
            return evalAssignmentStatement((AssignmentStatement) node, environment);
        } else if (node instanceof PrintStatement) {
            return evalPrintStatement((PrintStatement) node, environment);
        } else if (node instanceof IfStatement) {
            return evalIfStatement((IfStatement) node, environment);
        } else if (node instanceof WhileStatement) {
            return evalWhileStatement((WhileStatement) node, environment);
        } else if (node instanceof Identifier) {
            return evalIdentifier((Identifier) node, environment);
        } else if (node instanceof IntegerLiteral) {
            return evalIntegerLiteral((IntegerLiteral) node); // No env needed for literals
        } else if (node instanceof BinaryExpression) {
            return evalBinaryExpression((BinaryExpression) node, environment);
        }
        // Should not happen if all AST node types are covered and valid
        errors.add("Runtime Error: Unknown AST node type encountered: " + node.getClass().getSimpleName());
        return new MCError("Unknown AST node type: " + node.getClass().getSimpleName());
    }

    private MCObject evalProgram(Program program, Environment env) {
        MCObject result = NULL;
        for (Statement statement : program.getStatements()) {
            result = eval(statement, env);
            if (result instanceof MCError) { // If an error occurred, stop and return it
                return result;
            }
            // No MCReturnValue handling needed yet
        }
        return result; // Return the result of the last statement
    }

    private MCObject evalBlockStatement(BlockStatement block, Environment env) {
        MCObject result = NULL;
        for (Statement statement : block.getStatements()) {
            result = eval(statement, env);
            if (result instanceof MCError) {
                return result; // Propagate error
            }
            // No MCReturnValue handling needed yet
        }
        return result;
    }

    private MCObject evalExpressionStatement(ExpressionStatement stmt, Environment env) {
        if (stmt.getExpression() != null) {
            return eval(stmt.getExpression(), env);
        }
        return NULL;
    }

    private MCObject evalLetStatement(LetStatement stmt, Environment env) {
        MCObject value = eval(stmt.getValue(), env);
        if (isError(value)) {
            return value;
        }
        env.define(stmt.getName().getValue(), value);
        return NULL;
    }

    private MCObject evalAssignmentStatement(AssignmentStatement stmt, Environment env) {
        String varName = stmt.getName().getValue();
        MCObject value = eval(stmt.getValue(), env);
        if (isError(value)) {
            return value;
        }
        Optional<MCObject> assigned = env.assign(varName, value);
        if (assigned.isEmpty()) {
            return newError(stmt.getName().getToken(), "runtime error: variable '%s' not declared for assignment.", varName);
        }
        return NULL;
    }

    private MCObject evalPrintStatement(PrintStatement stmt, Environment env) {
        MCObject value = eval(stmt.getArgument(), env);
        if (isError(value)) {
            return value;
        }
        System.out.println(value.inspect());
        return NULL;
    }

    private MCObject evalIdentifier(Identifier identifier, Environment env) {
        Optional<MCObject> value = env.get(identifier.getValue());
        if (value.isEmpty()) {
            return newError(identifier.getToken(), "runtime error: identifier not found: %s", identifier.getValue());
        }
        return value.get();
    }

    private MCInteger evalIntegerLiteral(IntegerLiteral literal) {
        return new MCInteger(literal.getValue());
    }

    private MCObject evalBinaryExpression(BinaryExpression expr, Environment env) {
        MCObject left = eval(expr.getLeft(), env);
        if (isError(left)) {
            return left;
        }
        MCObject right = eval(expr.getRight(), env);
        if (isError(right)) {
            return right;
        }

        if (left.type() == ObjectType.INTEGER && right.type() == ObjectType.INTEGER) {
            return evalIntegerBinaryExpression(expr.getOperator(), (MCInteger) left, (MCInteger) right, expr.getToken());
        }
        // Add other type combinations if necessary (e.g. string concatenation)
        return newError(expr.getToken(), "runtime error: type mismatch for operator '%s'. Expected INTEGERs, got %s and %s.",
                expr.getOperator(), left.type(), right.type());
    }

    private MCObject evalIntegerBinaryExpression(String operator, MCInteger left, MCInteger right, Token opToken) {
        long leftVal = left.getValue();
        long rightVal = right.getValue();

        switch (operator) {
            case "+": return new MCInteger(leftVal + rightVal);
            case "-": return new MCInteger(leftVal - rightVal);
            case "*": return new MCInteger(leftVal * rightVal);
            case "/":
                if (rightVal == 0) {
                    return newError(opToken, "runtime error: division by zero.");
                }
                return new MCInteger(leftVal / rightVal);
            case "<": return nativeBoolToBooleanObject(leftVal < rightVal);
            case ">": return nativeBoolToBooleanObject(leftVal > rightVal);
            case "==": return nativeBoolToBooleanObject(leftVal == rightVal);
            case "!=": return nativeBoolToBooleanObject(leftVal != rightVal);
            case "<=": return nativeBoolToBooleanObject(leftVal <= rightVal);
            case ">=": return nativeBoolToBooleanObject(leftVal >= rightVal);
            default:
                return newError(opToken, "runtime error: unknown integer operator: %s", operator);
        }
    }

    private MCObject evalIfStatement(IfStatement stmt, Environment env) {
        MCObject condition = eval(stmt.getCondition(), env);
        if (isError(condition)) {
            return condition;
        }

        if (isTruthy(condition)) {
            Environment consequenceEnv = new Environment(env); // New scope for consequence
            return eval(stmt.getConsequence(), consequenceEnv);
        } else if (stmt.getAlternative() != null) {
            Environment alternativeEnv = new Environment(env); // New scope for alternative
            return eval(stmt.getAlternative(), alternativeEnv);
        } else {
            return NULL;
        }
    }

    private MCObject evalWhileStatement(WhileStatement stmt, Environment env) {
        MCObject lastEval = NULL;
        while (true) {
            MCObject condition = eval(stmt.getCondition(), env); // Condition evaluated in current/outer scope
            if (isError(condition)) {
                return condition;
            }
            if (!isTruthy(condition)) {
                break;
            }
            Environment bodyEnv = new Environment(env); // Body evaluated in its own scope for each iteration
            lastEval = eval(stmt.getBody(), bodyEnv);
            if (isError(lastEval)) {
                return lastEval;
            }
            // No break/continue support yet
        }
        return lastEval; // Or NULL if while loops shouldn't yield a value
    }

    // Helper methods
    private MCBoolean nativeBoolToBooleanObject(boolean input) {
        return input ? TRUE : FALSE;
    }

    private boolean isTruthy(MCObject obj) {
        if (obj == NULL) return false;
        if (obj == FALSE) return false; // MCBoolean.FALSE
        // All other objects, including MCInteger(0), are considered "truthy" in this language.
        // If MCInteger(0) should be falsy, add:
        // if (obj instanceof MCInteger && ((MCInteger) obj).getValue() == 0) return false;
        return true;
    }

    private MCError newError(Token token, String format, Object... args) {
        String message = String.format("Line %d, Col %d: %s", token.getLine(), token.getColumn(), String.format(format, args));
        errors.add(message); // Add to the main error list for reporting
        return new MCError(message); // Return an error object to halt execution flow
    }

    private boolean isError(MCObject obj) {
        return obj != null && obj.type() == ObjectType.ERROR;
    }
}
