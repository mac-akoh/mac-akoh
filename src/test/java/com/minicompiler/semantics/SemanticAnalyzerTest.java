package com.minicompiler.semantics;

import com.minicompiler.ast.Program;
import com.minicompiler.lexer.Lexer;
import com.minicompiler.parser.Parser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzerTest {

    private List<String> analyzeAndGetErrors(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        if (!parser.getErrors().isEmpty()) {
            fail("Parser errors found, cannot proceed with semantic analysis:\n" + String.join("\n", parser.getErrors()));
        }

        List<String> semanticErrors = new ArrayList<>();
        SymbolTable globalTable = new SymbolTable();
        SemanticAnalyzer analyzer = new SemanticAnalyzer(semanticErrors);
        analyzer.analyze(program, globalTable);
        return semanticErrors;
    }

    @Test
    void testValidProgramNoErrors() {
        String input = "let x = 5;\n" +
                       "let y = 10;\n" +
                       "x = x + y;\n" +
                       "print x;";
        List<String> errors = analyzeAndGetErrors(input);
        assertTrue(errors.isEmpty(), "Expected no semantic errors, but found: \n" + String.join("\n", errors));
    }

    @Test
    void testUndeclaredVariableAssignment() {
        String input = "x = 5;";
        List<String> errors = analyzeAndGetErrors(input);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Error: Variable 'x' not declared"), "Incorrect error message: " + errors.get(0));
    }

    @Test
    void testUndeclaredVariableInExpression() {
        String input = "let x = y + 5;";
        List<String> errors = analyzeAndGetErrors(input);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Error: Variable 'y' not declared"), "Incorrect error message: " + errors.get(0));
    }
    
    @Test
    void testUndeclaredVariableInPrint() {
        String input = "print z;";
        List<String> errors = analyzeAndGetErrors(input);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Error: Variable 'z' not declared"), "Incorrect error message: " + errors.get(0));
    }

    @Test
    void testRedeclaredVariable() {
        String input = "let x = 5;\n" +
                       "let x = 10;";
        List<String> errors = analyzeAndGetErrors(input);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Error: Variable 'x' already declared in this scope"), "Incorrect error message: " + errors.get(0));
    }

    @Test
    void testScopeRuleOuterAccessibleInInner() {
        String input = "let x = 5;\n" +
                       "if (x == 5) {\n" +
                       "  print x;\n" +
                       "  let y = 10;\n" +
                       "  x = y;\n" + // x from outer scope
                       "}";
        List<String> errors = analyzeAndGetErrors(input);
        assertTrue(errors.isEmpty(), "Expected no semantic errors, but found: \n" + String.join("\n", errors));
    }

    @Test
    void testScopeRuleInnerNotAccessibleInOuter() {
        String input = "let x = 5;\n" +
                       "if (x == 5) {\n" +
                       "  let y = 10;\n" +
                       "  print y;\n" +
                       "}\n" +
                       "print y; // y should not be accessible here";
        List<String> errors = analyzeAndGetErrors(input);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Error: Variable 'y' not declared"), "Incorrect error message: " + errors.get(0));
    }

    @Test
    void testScopeRuleRedeclarationInInnerScopeAllowed() {
        // Shadowing: declaring a variable in an inner scope that already exists in an outer scope.
        // This is typically allowed.
        String input = "let x = 5;\n" +
                       "if (x == 5) {\n" +
                       "  let x = 10; // Shadowing outer x\n" +
                       "  print x; // Should print inner x (10)\n" +
                       "}\n" +
                       "print x; // Should print outer x (5)";
        List<String> errors = analyzeAndGetErrors(input);
        assertTrue(errors.isEmpty(), "Expected no semantic errors for variable shadowing, but found: \n" + String.join("\n", errors));
    }
    
    @Test
    void testScopeRuleRedeclarationInSameInnerScope() {
        String input = "let x = 5;\n" +
                       "if (x == 5) {\n" +
                       "  let y = 10;\n" +
                       "  let y = 20; // Redeclaration in the same inner scope\n" +
                       "}";
        List<String> errors = analyzeAndGetErrors(input);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Error: Variable 'y' already declared in this scope"), "Incorrect error message: " + errors.get(0));
    }


    @Test
    void testWhileLoopScope() {
        String input = "let x = 0;\n" +
                       "while (x < 5) {\n" +
                       "  let inner = x * 2;\n" +
                       "  print inner;\n" +
                       "  x = x + 1;\n" +
                       "}\n" +
                       "// print inner; // This would be an error";
        List<String> errors = analyzeAndGetErrors(input);
        assertTrue(errors.isEmpty(), "Expected no semantic errors, but found: \n" + String.join("\n", errors));
    }
    
    @Test
    void testWhileLoopInnerVarNotAccessibleOuter() {
        String input = "let x = 0;\n" +
                       "while (x < 1) {\n" +
                       "  let inner_while = 100;\n" +
                       "  x = x + 1;\n" +
                       "}\n" +
                       "print inner_while;";
        List<String> errors = analyzeAndGetErrors(input);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Error: Variable 'inner_while' not declared"), "Incorrect error message: " + errors.get(0));
    }
    
    @Test
    void testIfElseScopeIndependent() {
        String input = "let common = 0;\n" +
                       "if (common == 0) {\n" +
                       "  let varInIf = 1;\n" +
                       "  print varInIf;\n" +
                       "} else {\n" +
                       "  let varInElse = 2;\n" +
                       "  print varInElse;\n" +
                       "  // print varInIf; // This would be an error\n" +
                       "}\n" +
                       "// print varInIf;   // This would be an error\n" +
                       "// print varInElse; // This would be an error";
        List<String> errors = analyzeAndGetErrors(input);
        assertTrue(errors.isEmpty(), "Expected no semantic errors for independent if/else scopes, but found: \n" + String.join("\n", errors));
    }

    @Test
    void testIfElseScopeVarAccessError() {
        String input = "let common = 0;\n" +
                       "if (common == 0) {\n" +
                       "  let varInIf = 1;\n" +
                       "} else {\n" +
                       "  let varInElse = 2;\n" +
                       "  print varInIf; // Error: varInIf not in this scope\n" +
                       "}";
        List<String> errors = analyzeAndGetErrors(input);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Error: Variable 'varInIf' not declared"), "Incorrect error message: " + errors.get(0));
    }
}
