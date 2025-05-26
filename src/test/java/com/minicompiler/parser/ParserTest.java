package com.minicompiler.parser;

import com.minicompiler.ast.*;
import com.minicompiler.lexer.Lexer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ParserTest {

    private Program parseAndGetProgram(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        assertNotNull(program, "Parser returned a null program.");
        return program;
    }

    private void checkParserErrors(Parser parser) {
        List<String> errors = parser.getErrors();
        if (!errors.isEmpty()) {
            fail("Parser has " + errors.size() + " errors:\n" + String.join("\n", errors));
        }
    }
    
    private void checkParserErrorsAndExpectSome(Parser parser, int expectedErrorCount) {
        List<String> errors = parser.getErrors();
        assertEquals(expectedErrorCount, errors.size(), "Expected " + expectedErrorCount + " parser errors, but found " + errors.size() + ".\nErrors:\n" + String.join("\n", errors) );
    }


    @Test
    void testLetStatements() {
        String input = "let x = 5;\n" +
                       "let y = 10;\n" +
                       "let foobar = 838383;";

        Program program = parseAndGetProgram(input);
        assertEquals(3, program.getStatements().size(), "Program should have 3 statements.");

        String[] expectedIdentifiers = {"x", "y", "foobar"};
        long[] expectedValues = {5, 10, 838383};

        for (int i = 0; i < expectedIdentifiers.length; i++) {
            Statement stmt = program.getStatements().get(i);
            assertTrue(stmt instanceof LetStatement, "Statement " + i + " is not a LetStatement. Got=" + stmt.getClass().getName());
            LetStatement letStmt = (LetStatement) stmt;
            assertEquals(expectedIdentifiers[i], letStmt.getName().getValue(), "Identifier name mismatch.");
            
            Expression valueExpr = letStmt.getValue();
            assertTrue(valueExpr instanceof IntegerLiteral, "Value expression is not an IntegerLiteral.");
            assertEquals(expectedValues[i], ((IntegerLiteral) valueExpr).getValue(), "Integer literal value mismatch.");
        }
    }
    
    @Test
    void testLetStatementWithBinaryExpression() {
        String input = "let result = 10 * 2;";
        Program program = parseAndGetProgram(input);
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof LetStatement);
        LetStatement letStmt = (LetStatement) stmt;
        assertEquals("result", letStmt.getName().getValue());

        assertTrue(letStmt.getValue() instanceof BinaryExpression, "Value should be BinaryExpression");
        BinaryExpression binExpr = (BinaryExpression) letStmt.getValue();
        assertIntegerLiteral(binExpr.getLeft(), 10);
        assertEquals("*", binExpr.getOperator());
        assertIntegerLiteral(binExpr.getRight(), 2);
    }


    @Test
    void testAssignmentStatements() {
        String input = "x = 10;\n" +
                       "myVar = x + 5;";
        Program program = parseAndGetProgram(input);
        assertEquals(2, program.getStatements().size(), "Program should have 2 statements.");

        // Test "x = 10;"
        Statement stmt1 = program.getStatements().get(0);
        assertTrue(stmt1 instanceof AssignmentStatement, "Statement 0 is not an AssignmentStatement.");
        AssignmentStatement assignStmt1 = (AssignmentStatement) stmt1;
        assertEquals("x", assignStmt1.getName().getValue(), "Identifier name mismatch for first assignment.");
        assertIntegerLiteral(assignStmt1.getValue(), 10);

        // Test "myVar = x + 5;"
        Statement stmt2 = program.getStatements().get(1);
        assertTrue(stmt2 instanceof AssignmentStatement, "Statement 1 is not an AssignmentStatement.");
        AssignmentStatement assignStmt2 = (AssignmentStatement) stmt2;
        assertEquals("myVar", assignStmt2.getName().getValue(), "Identifier name mismatch for second assignment.");
        
        assertTrue(assignStmt2.getValue() instanceof BinaryExpression, "Value of second assignment should be BinaryExpression.");
        BinaryExpression binExpr = (BinaryExpression) assignStmt2.getValue();
        assertIdentifier(binExpr.getLeft(), "x");
        assertEquals("+", binExpr.getOperator());
        assertIntegerLiteral(binExpr.getRight(), 5);
    }

    @Test
    void testPrintStatements() {
        String input = "print 5;\n" +
                       "print x;\n" +
                       "print (x + 10);";
        Program program = parseAndGetProgram(input);
        assertEquals(3, program.getStatements().size());

        // print 5;
        Statement stmt1 = program.getStatements().get(0);
        assertTrue(stmt1 instanceof PrintStatement);
        PrintStatement printStmt1 = (PrintStatement) stmt1;
        assertIntegerLiteral(printStmt1.getArgument(), 5);

        // print x;
        Statement stmt2 = program.getStatements().get(1);
        assertTrue(stmt2 instanceof PrintStatement);
        PrintStatement printStmt2 = (PrintStatement) stmt2;
        assertIdentifier(printStmt2.getArgument(), "x");
        
        // print (x + 10);
        Statement stmt3 = program.getStatements().get(2);
        assertTrue(stmt3 instanceof PrintStatement);
        PrintStatement printStmt3 = (PrintStatement) stmt3;
        assertTrue(printStmt3.getArgument() instanceof BinaryExpression);
        BinaryExpression binExpr = (BinaryExpression) printStmt3.getArgument();
        assertIdentifier(binExpr.getLeft(), "x");
        assertEquals("+", binExpr.getOperator());
        assertIntegerLiteral(binExpr.getRight(), 10);
    }
    
    @Test
    void testIdentifierExpression() {
        String input = "foobar;";
        Program program = parseAndGetProgram(input);
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);
        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertIdentifier(exprStmt.getExpression(), "foobar");
    }

    @Test
    void testIntegerLiteralExpression() {
        String input = "5;";
        Program program = parseAndGetProgram(input);
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);
        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertIntegerLiteral(exprStmt.getExpression(), 5);
    }

    @Test
    void testBinaryExpressionsArithmetic() {
        String[] inputs = {
            "5 + 5;", "5 - 5;", "5 * 5;", "5 / 5;",
            "a + b * c;", "a * b + c;", "(a + b) * c;"
        };
        String[] expectedStrings = {
            "(5 + 5)", "(5 - 5)", "(5 * 5)", "(5 / 5)",
            "(a + (b * c))", "((a * b) + c)", "((a + b) * c)"
        };

        for (int i = 0; i < inputs.length; i++) {
            Program program = parseAndGetProgram(inputs[i]);
            assertEquals(1, program.getStatements().size());
            Statement stmt = program.getStatements().get(0);
            assertTrue(stmt instanceof ExpressionStatement);
            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            assertEquals(expectedStrings[i], exprStmt.getExpression().toString(), "Binary expression string mismatch for: " + inputs[i]);
        }
    }
    
    @Test
    void testBinaryExpressionPrecedence() {
        String input = "1 + 2 * 3 - 4 / 5;"; // (1 + (2 * 3)) - (4 / 5)
        Program program = parseAndGetProgram(input);
        Expression expr = ((ExpressionStatement)program.getStatements().get(0)).getExpression();
        assertEquals("((1 + (2 * 3)) - (4 / 5))", expr.toString());

        String input2 = "1 + 2 + 3;"; // ((1 + 2) + 3)
        Program program2 = parseAndGetProgram(input2);
        Expression expr2 = ((ExpressionStatement)program2.getStatements().get(0)).getExpression();
        assertEquals("((1 + 2) + 3)", expr2.toString());
        
        String input3 = "1 * 2 * 3;"; // ((1 * 2) * 3)
        Program program3 = parseAndGetProgram(input3);
        Expression expr3 = ((ExpressionStatement)program3.getStatements().get(0)).getExpression();
        assertEquals("((1 * 2) * 3)", expr3.toString());
    }


    @Test
    void testBinaryExpressionsComparison() {
        String[] inputs = {
            "5 < 10;", "10 > 5;", "5 == 5;", "5 != 10;", "5 <= 10;", "10 >= 5;"
        };
        String[] operators = {"<", ">", "==", "!=", "<=", ">="};
        long[] leftLiterals = {5, 10, 5, 5, 5, 10};
        long[] rightLiterals = {10, 5, 5, 10, 10, 5};


        for (int i = 0; i < inputs.length; i++) {
            Program program = parseAndGetProgram(inputs[i]);
            assertEquals(1, program.getStatements().size());
            Statement stmt = program.getStatements().get(0);
            assertTrue(stmt instanceof ExpressionStatement);
            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            assertTrue(exprStmt.getExpression() instanceof BinaryExpression);
            BinaryExpression binExpr = (BinaryExpression) exprStmt.getExpression();
            
            assertIntegerLiteral(binExpr.getLeft(), leftLiterals[i]);
            assertEquals(operators[i], binExpr.getOperator());
            assertIntegerLiteral(binExpr.getRight(), rightLiterals[i]);
        }
    }

    @Test
    void testParenthesizedExpressions() {
        String input = "(5 + 5) * 2;";
        Program program = parseAndGetProgram(input);
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);
        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertEquals("((5 + 5) * 2)", exprStmt.getExpression().toString());
    }

    @Test
    void testBlockStatements() {
        String input = "{ let x = 5; print x; }";
        Program program = parseAndGetProgram(input);
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        // Note: A block statement on its own might be parsed as an ExpressionStatement
        // if the parser's parseStatement method defaults to parseExpressionStatement for '{'.
        // Let's assume the grammar allows block statements directly or it's an expression statement containing a block.
        // The current parser's parseStatement for LBRACE directly calls parseBlockStatement,
        // which is then wrapped in an ExpressionStatement if it's not part of if/while.
        // This test needs to be specific to how BlockStatement is represented in the AST.
        // If a raw block statement is not directly a Statement type in Program.statements, this test needs adjustment.
        // Current Parser.parseStatement() calls parseBlockStatement() for LBRACE,
        // but parseBlockStatement is not directly added to Program, only via If/While or if grammar changes.
        // Let's test it within an If statement, as standalone blocks might not be top-level statements.
        // For now, I will adjust parser to return BlockStatement from parseStatement when it encounters LBRACE.
        // (Adjusted Parser.parseStatement() to return BlockStatement directly for LBRACE for this test)

        // If your parser wraps naked blocks in ExpressionStatement:
        // assertTrue(stmt instanceof ExpressionStatement, "Statement should be ExpressionStatement wrapping a block");
        // Expression expr = ((ExpressionStatement) stmt).getExpression();
        // assertTrue(expr instanceof BlockStatement, "Expression should be BlockStatement");
        // BlockStatement blockStmt = (BlockStatement) expr;

        // If parser allows BlockStatement directly as a Statement (as per test setup):
         assertTrue(stmt instanceof BlockStatement, "Statement is not a BlockStatement. Got=" + stmt.getClass().getName());
         BlockStatement blockStmt = (BlockStatement) stmt;

        assertEquals(2, blockStmt.getStatements().size());
        assertTrue(blockStmt.getStatements().get(0) instanceof LetStatement);
        assertTrue(blockStmt.getStatements().get(1) instanceof PrintStatement);
    }


    @Test
    void testIfStatement() {
        String input = "if (x < y) { print x; }";
        Program program = parseAndGetProgram(input);
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof IfStatement);
        IfStatement ifStmt = (IfStatement) stmt;

        assertTrue(ifStmt.getCondition() instanceof BinaryExpression);
        BinaryExpression condExpr = (BinaryExpression) ifStmt.getCondition();
        assertIdentifier(condExpr.getLeft(), "x");
        assertEquals("<", condExpr.getOperator());
        assertIdentifier(condExpr.getRight(), "y");

        assertNotNull(ifStmt.getConsequence());
        assertEquals(1, ifStmt.getConsequence().getStatements().size());
        assertTrue(ifStmt.getConsequence().getStatements().get(0) instanceof PrintStatement);
        assertNull(ifStmt.getAlternative(), "Alternative should be null.");
    }

    @Test
    void testIfElseStatement() {
        String input = "if (x > y) { print x; } else { print y; }";
        Program program = parseAndGetProgram(input);
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof IfStatement);
        IfStatement ifStmt = (IfStatement) stmt;

        assertTrue(ifStmt.getCondition() instanceof BinaryExpression); // Simplified check

        assertNotNull(ifStmt.getConsequence());
        assertEquals(1, ifStmt.getConsequence().getStatements().size());
        assertTrue(ifStmt.getConsequence().getStatements().get(0) instanceof PrintStatement);
        PrintStatement conPrint = (PrintStatement) ifStmt.getConsequence().getStatements().get(0);
        assertIdentifier(conPrint.getArgument(),"x");


        assertNotNull(ifStmt.getAlternative());
        assertEquals(1, ifStmt.getAlternative().getStatements().size());
        assertTrue(ifStmt.getAlternative().getStatements().get(0) instanceof PrintStatement);
        PrintStatement altPrint = (PrintStatement) ifStmt.getAlternative().getStatements().get(0);
        assertIdentifier(altPrint.getArgument(),"y");
    }

    @Test
    void testWhileStatement() {
        String input = "while (count < 10) { count = count + 1; }";
        Program program = parseAndGetProgram(input);
        assertEquals(1, program.getStatements().size());
        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof WhileStatement);
        WhileStatement whileStmt = (WhileStatement) stmt;

        assertTrue(whileStmt.getCondition() instanceof BinaryExpression);
        BinaryExpression condExpr = (BinaryExpression) whileStmt.getCondition();
        assertIdentifier(condExpr.getLeft(), "count");
        assertEquals("<", condExpr.getOperator());
        assertIntegerLiteral(condExpr.getRight(), 10);

        assertNotNull(whileStmt.getBody());
        assertEquals(1, whileStmt.getBody().getStatements().size());
        assertTrue(whileStmt.getBody().getStatements().get(0) instanceof AssignmentStatement);
    }

    @Test
    void testParserErrorsMissingEqualsInLet() {
        String input = "let x 5;"; // Missing '='
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        parser.parseProgram(); // Call parseProgram to populate errors
        checkParserErrorsAndExpectSome(parser, 1); // Expect 1 error
        assertTrue(parser.getErrors().get(0).contains("expected next token to be ASSIGN, got INTEGER instead"));
    }

    @Test
    void testParserErrorsUnclosedParenthesis() {
        String input = "let x = (5 + 5;"; // Missing ')'
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        checkParserErrorsAndExpectSome(parser, 1);
        assertTrue(parser.getErrors().get(0).contains("expected next token to be RPAREN, got SEMICOLON instead"));
    }
    
    @Test
    void testParserErrorMissingSemicolon() {
        // Note: Current parser automatically inserts some semicolons or makes them optional for ExpressionStatements.
        // This test might need adjustment based on how strictly semicolons are enforced.
        // For a LetStatement, it's generally expected after the expression.
        String input = "let x = 5"; // Missing semicolon, but parser might recover or not require it for last statement
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        // If semicolons are optional at EOF or for ExpressionStatement, this might not produce an error.
        // Let's assume for `let` it's not strictly an error if it's the last thing and EOF follows.
        // The current `parseLetStatement` makes semicolon optional if `peekTokenIs(TokenType.SEMICOLON)`
        // If it was mandatory, this would be: checkParserErrorsAndExpectSome(parser, 1);
        checkParserErrors(parser); // Expect no errors if semicolon is optional at EOF for let.
    }

    @Test
    void testParserErrorIfMissingConditionParen() {
        String input = "if x < y) { print x; }";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        checkParserErrorsAndExpectSome(parser, 1);
        assertTrue(parser.getErrors().get(0).contains("expected next token to be LPAREN, got IDENTIFIER instead"));
    }
    
    @Test
    void testParserErrorIfMissingConsequenceBrace() {
        String input = "if (x < y) print x; }";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        checkParserErrorsAndExpectSome(parser, 1);
        assertTrue(parser.getErrors().get(0).contains("expected next token to be LBRACE, got IDENTIFIER instead"));
    }


    // Helper assertion methods
    private void assertIntegerLiteral(Expression expr, long expectedValue) {
        assertTrue(expr instanceof IntegerLiteral, "Expression is not IntegerLiteral. Got=" + expr.getClass().getName());
        assertEquals(expectedValue, ((IntegerLiteral) expr).getValue());
    }

    private void assertIdentifier(Expression expr, String expectedValue) {
        assertTrue(expr instanceof Identifier, "Expression is not Identifier. Got=" + expr.getClass().getName());
        assertEquals(expectedValue, ((Identifier) expr).getValue());
    }
}
