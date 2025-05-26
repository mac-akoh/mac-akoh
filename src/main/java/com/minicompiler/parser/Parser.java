package com.minicompiler.parser;

import com.minicompiler.ast.*;
import com.minicompiler.lexer.Lexer;
import com.minicompiler.lexer.Token;
import com.minicompiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;
    private Token peekToken;
    private final List<String> errors;

    // Operator precedence levels
    private static final int LOWEST = 1;
    private static final int EQUALS = 2;      // ==
    private static final int LESSGREATER = 3; // > or < or <= or >=
    private static final int SUM = 4;         // +
    private static final int PRODUCT = 5;     // *
    private static final int PREFIX = 6;      // -X or !X
    // private static final int CALL = 7;        // myFunction(X) // For future use

    private static final Map<TokenType, Integer> precedences = new HashMap<>();

    static {
        precedences.put(TokenType.EQ, EQUALS);
        precedences.put(TokenType.NEQ, EQUALS);
        precedences.put(TokenType.LT, LESSGREATER);
        precedences.put(TokenType.GT, LESSGREATER);
        precedences.put(TokenType.LTE, LESSGREATER);
        precedences.put(TokenType.GTE, LESSGREATER);
        precedences.put(TokenType.PLUS, SUM);
        precedences.put(TokenType.MINUS, SUM);
        precedences.put(TokenType.MULTIPLY, PRODUCT);
        precedences.put(TokenType.DIVIDE, PRODUCT);
    }

    // Interfaces for Pratt parser
    @FunctionalInterface
    private interface PrefixParseFn {
        Expression parse();
    }

    @FunctionalInterface
    private interface InfixParseFn {
        Expression parse(Expression left);
    }

    private final Map<TokenType, PrefixParseFn> prefixParseFns;
    private final Map<TokenType, InfixParseFn> infixParseFns;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();

        this.prefixParseFns = new HashMap<>();
        registerPrefix(TokenType.IDENTIFIER, this::parseIdentifier);
        registerPrefix(TokenType.INTEGER, this::parseIntegerLiteral);
        registerPrefix(TokenType.LPAREN, this::parseGroupedExpression);
        // Add prefix operators like MINUS, BANG (NOT) if implementing them
        // registerPrefix(TokenType.MINUS, this::parsePrefixExpression);

        this.infixParseFns = new HashMap<>();
        registerInfix(TokenType.PLUS, this::parseInfixExpression);
        registerInfix(TokenType.MINUS, this::parseInfixExpression);
        registerInfix(TokenType.MULTIPLY, this::parseInfixExpression);
        registerInfix(TokenType.DIVIDE, this::parseInfixExpression);
        registerInfix(TokenType.EQ, this::parseInfixExpression);
        registerInfix(TokenType.NEQ, this::parseInfixExpression);
        registerInfix(TokenType.LT, this::parseInfixExpression);
        registerInfix(TokenType.GT, this::parseInfixExpression);
        registerInfix(TokenType.LTE, this::parseInfixExpression);
        registerInfix(TokenType.GTE, this::parseInfixExpression);

        // Read two tokens, so currentToken and peekToken are both set
        nextToken();
        nextToken();
    }

    public void nextToken() {
        currentToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public List<String> getErrors() {
        return errors;
    }

    private void peekError(TokenType type) {
        String msg = String.format("expected next token to be %s, got %s instead (line %d, col %d)",
                type, peekToken.getType(), peekToken.getLine(), peekToken.getColumn());
        errors.add(msg);
    }
    
    private void noPrefixParseFnError(TokenType t) {
        String msg = String.format("no prefix parse function for %s found (line %d, col %d)", 
                t, currentToken.getLine(), currentToken.getColumn());
        errors.add(msg);
    }

    public Program parseProgram() {
        Program program = new Program(new ArrayList<>());

        while (currentToken.getType() != TokenType.EOF) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                program.getStatements().add(stmt);
            }
            nextToken();
        }
        return program;
    }

    private Statement parseStatement() {
        switch (currentToken.getType()) {
            case LET:
                return parseLetStatement();
            case IDENTIFIER: // Could be an assignment or an expression statement
                if (peekToken.getType() == TokenType.ASSIGN) {
                     return parseAssignmentStatement();
                }
                return parseExpressionStatement(); // Default to expression statement if not assign
            case PRINT:
                return parsePrintStatement();
            case LBRACE:
                return parseBlockStatement();
            case IF:
                return parseIfStatement();
            case WHILE:
                return parseWhileStatement();
            default:
                // If it's not a recognized statement keyword, try parsing as an expression statement
                // This allows for lines like "5 + 5;" which are valid expressions
                // but might not be useful statements unless part of a print or assignment.
                // However, the grammar allows expression statements.
                return parseExpressionStatement();
        }
    }

    private LetStatement parseLetStatement() {
        Token letToken = currentToken; // The 'let' token

        if (!expectPeek(TokenType.IDENTIFIER)) {
            return null;
        }

        Identifier name = new Identifier(currentToken, currentToken.getLiteral());

        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }
        nextToken(); // Consume the '=' token

        Expression value = parseExpression(LOWEST);

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return new LetStatement(letToken, name, value);
    }
    
    private AssignmentStatement parseAssignmentStatement() {
        Identifier name = new Identifier(currentToken, currentToken.getLiteral());

        if (!expectPeek(TokenType.ASSIGN)) { // currentToken is IDENTIFIER, peek should be ASSIGN
            // This case should ideally not be reached if called correctly from parseStatement
            return null; 
        }
        
        Token assignToken = peekToken; // The '=' token
        nextToken(); // Consume IDENTIFIER, currentToken is now '='
        nextToken(); // Consume '=', currentToken is now the start of the expression

        Expression value = parseExpression(LOWEST);

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        // The token for AssignmentStatement is typically the identifier's token or the assign token.
        // Let's use the identifier's token for consistency with how LetStatement might be structured.
        return new AssignmentStatement(name.getToken(), name, value);
    }

    private PrintStatement parsePrintStatement() {
        Token printToken = currentToken; // The 'print' token
        
        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        nextToken(); // Consume '('

        Expression argument = parseExpression(LOWEST);

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return new PrintStatement(printToken, argument);
    }

    private BlockStatement parseBlockStatement() {
        Token lbraceToken = currentToken; // The '{' token
        List<Statement> statements = new ArrayList<>();

        nextToken(); // Consume '{'

        while (!currentTokenIs(TokenType.RBRACE) && !currentTokenIs(TokenType.EOF)) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            nextToken(); // Crucial: advance after parsing a statement within the block
        }

        if (!currentTokenIs(TokenType.RBRACE)) {
            // Error: unclosed block
            errors.add(String.format("expected '}' but got %s (line %d, col %d)", 
                    currentToken.getType(), currentToken.getLine(), currentToken.getColumn()));
            return null; // Or handle more gracefully
        }
        
        return new BlockStatement(lbraceToken, statements);
    }

    private IfStatement parseIfStatement() {
        Token ifToken = currentToken; // The 'if' token

        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        nextToken(); // Consume '('
        Expression condition = parseExpression(LOWEST);

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        BlockStatement consequence = parseBlockStatement(); // parseBlockStatement starts with currentToken as LBRACE

        BlockStatement alternative = null;
        if (peekTokenIs(TokenType.ELSE)) {
            nextToken(); // Consume 'else'
            if (!expectPeek(TokenType.LBRACE)) {
                return null;
            }
            alternative = parseBlockStatement(); // parseBlockStatement starts with currentToken as LBRACE
        }
        return new IfStatement(ifToken, condition, consequence, alternative);
    }
    
    private WhileStatement parseWhileStatement() {
        Token whileToken = currentToken; // The 'while' token

        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        nextToken(); // Consume '('
        Expression condition = parseExpression(LOWEST);

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        BlockStatement body = parseBlockStatement(); // parseBlockStatement starts with currentToken as LBRACE

        return new WhileStatement(whileToken, condition, body);
    }


    private ExpressionStatement parseExpressionStatement() {
        Token currentExpressionToken = currentToken;
        Expression expression = parseExpression(LOWEST);

        // Optional semicolon for expression statements
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return new ExpressionStatement(currentExpressionToken, expression);
    }

    public Expression parseExpression(int precedence) {
        PrefixParseFn prefix = prefixParseFns.get(currentToken.getType());
        if (prefix == null) {
            noPrefixParseFnError(currentToken.getType());
            return null;
        }
        Expression leftExp = prefix.parse();

        while (!peekTokenIs(TokenType.SEMICOLON) && precedence < peekPrecedence()) {
            InfixParseFn infix = infixParseFns.get(peekToken.getType());
            if (infix == null) {
                return leftExp;
            }
            nextToken(); // Consume the operator
            leftExp = infix.parse(leftExp);
        }
        return leftExp;
    }

    private Expression parseIdentifier() {
        return new Identifier(currentToken, currentToken.getLiteral());
    }

    private Expression parseIntegerLiteral() {
        Token token = currentToken;
        try {
            long value = Long.parseLong(token.getLiteral());
            return new IntegerLiteral(token, value);
        } catch (NumberFormatException e) {
            String msg = String.format("could not parse %s as integer (line %d, col %d)", 
                    token.getLiteral(), token.getLine(), token.getColumn());
            errors.add(msg);
            return null;
        }
    }
    
    private Expression parseGroupedExpression() {
        nextToken(); // Consume '('
        Expression exp = parseExpression(LOWEST);
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        return exp;
    }
    
    // private Expression parsePrefixExpression() { // Example for future use
    //     Token operatorToken = currentToken;
    //     nextToken();
    //     Expression right = parseExpression(PREFIX);
    //     // return new PrefixExpression(operatorToken, operatorToken.getLiteral(), right);
    //     return null; // Placeholder
    // }

    private Expression parseInfixExpression(Expression left) {
        Token operatorToken = currentToken;
        String operator = currentToken.getLiteral();
        int currentPrecedence = currentPrecedence();
        nextToken(); // Consume operator
        Expression right = parseExpression(currentPrecedence);
        return new BinaryExpression(operatorToken, left, operator, right);
    }


    // Helper methods for Pratt parser
    private void registerPrefix(TokenType type, PrefixParseFn fn) {
        prefixParseFns.put(type, fn);
    }

    private void registerInfix(TokenType type, InfixParseFn fn) {
        infixParseFns.put(type, fn);
    }

    private int peekPrecedence() {
        return precedences.getOrDefault(peekToken.getType(), LOWEST);
    }

    private int currentPrecedence() {
        return precedences.getOrDefault(currentToken.getType(), LOWEST);
    }

    // Helper methods for checking token types
    private boolean currentTokenIs(TokenType type) {
        return currentToken.getType() == type;
    }

    private boolean peekTokenIs(TokenType type) {
        return peekToken.getType() == type;
    }

    private boolean expectPeek(TokenType type) {
        if (peekTokenIs(type)) {
            nextToken();
            return true;
        } else {
            peekError(type);
            return false;
        }
    }
}
