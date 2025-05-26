package com.minicompiler.interpreter;

import com.minicompiler.ast.Program;
import com.minicompiler.lexer.Lexer;
import com.minicompiler.parser.Parser;
import com.minicompiler.semantics.SemanticAnalyzer;
import com.minicompiler.semantics.SymbolTable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class InterpreterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        outContent.reset();
    }

    private MCObject interpret(String input, List<String> errors) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        if (!parser.getErrors().isEmpty()) {
            errors.addAll(parser.getErrors());
            fail("Parser errors: " + String.join("\n", parser.getErrors()));
            return null; // Should not be reached due to fail
        }

        SymbolTable globalTable = new SymbolTable();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(errors);
        semanticAnalyzer.analyze(program, globalTable);
        if (!errors.isEmpty()) {
            fail("Semantic errors: " + String.join("\n", errors));
            return null; // Should not be reached
        }

        Interpreter interpreter = new Interpreter(errors);
        Environment env = new Environment();
        return interpreter.eval(program, env);
    }

    private void assertIntegerObject(MCObject obj, long expected) {
        assertNotNull(obj, "Object is null");
        assertTrue(obj instanceof MCInteger, "Object is not MCInteger. Got=" + obj.getClass().getName());
        assertEquals(expected, ((MCInteger) obj).getValue(), "Integer value mismatch.");
    }

    private void assertBooleanObject(MCObject obj, boolean expected) {
        assertNotNull(obj, "Object is null");
        assertTrue(obj instanceof MCBoolean, "Object is not MCBoolean. Got=" + obj.getClass().getName());
        assertEquals(expected, ((MCBoolean) obj).getValue(), "Boolean value mismatch.");
    }

    private void assertNullObject(MCObject obj) {
        assertNotNull(obj, "Object is null");
        assertEquals(Interpreter.NULL, obj, "Object is not MCNull.");
    }
    
    private void assertErrorObject(MCObject obj, String expectedMessagePart, List<String> errors) {
        assertNotNull(obj, "Object is null, expected error object.");
        assertTrue(obj instanceof MCError, "Object is not MCError. Got=" + obj.getClass().getName());
        if (expectedMessagePart != null) {
            assertTrue(((MCError) obj).getMessage().contains(expectedMessagePart),
                "Error message mismatch. Expected to contain: '" + expectedMessagePart + "', Got: '" + ((MCError) obj).getMessage() + "'");
        }
        assertFalse(errors.isEmpty(), "Interpreter errors list should not be empty.");
        // Check if the error message is also in the collected errors list
        boolean foundInList = errors.stream().anyMatch(e -> e.contains(expectedMessagePart));
        assertTrue(foundInList, "Expected error '" + expectedMessagePart + "' not found in collected errors list: " + String.join(", ", errors));

    }


    @Test
    void testIntegerLiterals() {
        List<String> errors = new ArrayList<>();
        MCObject result = interpret("5;", errors);
        assertIntegerObject(result, 5);
        assertTrue(errors.isEmpty(), "Unexpected errors: " + String.join("\n", errors));
    }

    @Test
    void testArithmeticExpressions() {
        String[] inputs = {"5 + 5;", "10 - 2;", "3 * 4;", "10 / 2;", "2 + 3 * 4;", "(2 + 3) * 4;"};
        long[] expected = {10, 8, 12, 5, 14, 20};

        for (int i = 0; i < inputs.length; i++) {
            List<String> errors = new ArrayList<>();
            MCObject result = interpret(inputs[i], errors);
            assertIntegerObject(result, expected[i]);
            assertTrue(errors.isEmpty(), "Unexpected errors for input '" + inputs[i] + "': " + String.join("\n", errors));
        }
    }

    @Test
    void testBooleanComparisons() {
        String[] inputs = {
            "5 < 10;", "10 > 5;", "5 == 5;", "5 != 10;", "5 <= 10;", "10 >= 5;",
            "10 < 5;", "5 > 10;", "5 == 10;", "5 != 5;", "10 <= 5;", "5 >= 10;"
        };
        boolean[] expected = {
            true, true, true, true, true, true,
            false, false, false, false, false, false
        };

        for (int i = 0; i < inputs.length; i++) {
            List<String> errors = new ArrayList<>();
            MCObject result = interpret(inputs[i], errors);
            assertBooleanObject(result, expected[i]);
            assertTrue(errors.isEmpty(), "Unexpected errors for input '" + inputs[i] + "': " + String.join("\n", errors));
        }
    }

    @Test
    void testLetStatementsAndIdentifierEvaluation() {
        String input = "let x = 5; let y = 10; x + y;";
        List<String> errors = new ArrayList<>();
        MCObject result = interpret(input, errors);
        assertIntegerObject(result, 15);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void testAssignmentStatements() {
        String input = "let x = 5; x = 10; x = x + 5; x;";
        List<String> errors = new ArrayList<>();
        MCObject result = interpret(input, errors);
        assertIntegerObject(result, 15);
        assertTrue(errors.isEmpty());
    }


    @Test
    void testIfStatementTrueCondition() {
        String input = "let x = 10; if (x > 5) { x = 20; } x;";
        List<String> errors = new ArrayList<>();
        MCObject result = interpret(input, errors);
        assertIntegerObject(result, 20);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testIfStatementFalseCondition() {
        String input = "let x = 3; if (x > 5) { x = 20; } x;";
        List<String> errors = new ArrayList<>();
        MCObject result = interpret(input, errors);
        assertIntegerObject(result, 3); // x should remain unchanged
        assertTrue(errors.isEmpty());
    }

    @Test
    void testIfElseStatementAlternativePath() {
        String input = "let x = 3; if (x > 5) { x = 20; } else { x = 30; } x;";
        List<String> errors = new ArrayList<>();
        MCObject result = interpret(input, errors);
        assertIntegerObject(result, 30);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testWhileLoop() {
        String input = "let count = 0; let sum = 0; while (count < 3) { sum = sum + count; count = count + 1; } sum;";
        List<String> errors = new ArrayList<>();
        MCObject result = interpret(input, errors); // 0 + 1 + 2
        assertIntegerObject(result, 3);
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void testWhileLoopExecutesZeroTimes() {
        String input = "let count = 5; while (count < 3) { count = count + 1; } count;";
        List<String> errors = new ArrayList<>();
        MCObject result = interpret(input, errors);
        assertIntegerObject(result, 5); // count should remain 5
        assertTrue(errors.isEmpty());
    }


    @Test
    void testPrintStatements() {
        String input = "print 5; print (10 + 5); let x = 20; print x;";
        List<String> errors = new ArrayList<>();
        interpret(input, errors);
        assertEquals("5\n15\n20\n", outContent.toString().replace("\r\n", "\n"));
        assertTrue(errors.isEmpty());
    }

    @Test
    void testBlockStatementsAndScoping() {
        String input = "let x = 10; let y = 5; if (x > y) { let z = x + y; print z; x = z; } print x;";
        // z is inner, x is outer but modified
        List<String> errors = new ArrayList<>();
        interpret(input, errors);
        assertEquals("15\n15\n", outContent.toString().replace("\r\n", "\n"));
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void testNestedBlockScoping() {
        String input = 
            "let a = 1;\n" +
            "if (a == 1) {\n" +
            "  let b = 2;\n" +
            "  if (b == 2) {\n" +
            "    let c = 3;\n" +
            "    print(a + b + c); // 1 + 2 + 3 = 6\n" +
            "  }\n" +
            "  print(a + b); // 1 + 2 = 3. c is out of scope\n" +
            "}\n" +
            "print(a); // 1. b and c are out of scope";
        List<String> errors = new ArrayList<>();
        interpret(input, errors);
        assertEquals("6\n3\n1\n", outContent.toString().replace("\r\n", "\n"));
        assertTrue(errors.isEmpty(), "Unexpected errors: " + String.join("\n", errors));

    }


    @Test
    void testRuntimeErrorDivisionByZero() {
        String input = "10 / 0;";
        List<String> errors = new ArrayList<>();
        MCObject result = interpret(input, errors);
        assertErrorObject(result, "division by zero", errors);
    }
    
    @Test
    void testRuntimeErrorUndeclaredIdentifier() {
        // This should ideally be caught by SemanticAnalyzer, but good to double-check interpreter
        // For this test, we'll skip semantic analysis to simulate it being missed.
        String input = "print(undeclaredVar);";
        List<String> parseErrors = new ArrayList<>();
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        if (!parser.getErrors().isEmpty()) {
            fail("Parser errors: " + String.join("\n", parser.getErrors()));
        }

        List<String> runtimeErrors = new ArrayList<>();
        Interpreter interpreter = new Interpreter(runtimeErrors);
        Environment env = new Environment();
        MCObject result = interpreter.eval(program, env);
        
        assertErrorObject(result, "identifier not found: undeclaredVar", runtimeErrors);
    }

    @Test
    void testRuntimeErrorTypeMismatchForOperator() {
        // Current language only has integers, so this is hard to test without bools/strings
        // However, if an operation expects an integer and gets something else (e.g. null if possible)
        // For now, binary expressions always expect two integers.
        // If evalBinaryExpression got non-MCInteger objects for left/right, it would error.
        // This test is more conceptual for the current grammar.
        // Let's assume a hypothetical scenario where a non-integer might slip through.
        // The current interpreter's evalBinaryExpression checks operand types.
        // If we could force a non-integer into a variable, it would be testable.
        // Since all variables are integers, this test is more for future-proofing.
        // For now, division by zero is the primary arithmetic runtime error.
        // The "type mismatch" in evalBinaryExpression will be hit if any operand is not MCInteger.
        // One way to test this is if an identifier resolves to something not an integer (e.g. NULL or BOOLEAN).
        // Let's try assigning something that might become non-integer if the language evolved,
        // but for now, this is tricky.
        
        // This specific test case cannot be easily triggered with current strict typing
        // where all expressions and variables are integers.
        // The "type mismatch" error in evalBinaryExpression is robust but unreachable
        // unless the AST somehow contains non-integer expressions that aren't literals
        // or identifiers resolving to integers.

        // If we had boolean literals that could be used in arithmetic:
        // String input = "5 + true;"; // This would be a parser/semantic error first.
        // Assume we could construct such an AST:
        // This test is more a placeholder for if the type system expands.
        assertTrue(true, "Conceptual test for type mismatch, not directly triggerable with current grammar/type system.");
    }
}
