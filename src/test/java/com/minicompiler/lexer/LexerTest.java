package com.minicompiler.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

public class LexerTest {

    private void assertTokens(String input, List<Token> expectedTokens) {
        Lexer lexer = new Lexer(input);
        int i = 0;
        for (Token expectedToken : expectedTokens) {
            Token actualToken = lexer.nextToken();
            assertEquals(expectedToken.getType(), actualToken.getType(), "Token type mismatch at index " + i + " for literal '" + expectedToken.getLiteral() + "'");
            assertEquals(expectedToken.getLiteral(), actualToken.getLiteral(), "Token literal mismatch at index " + i + " for type " + expectedToken.getType());
            // Line and column checks can be added if exact positioning is critical for these tests
            i++;
        }
        assertEquals(TokenType.EOF, lexer.nextToken().getType(), "Expected EOF at the end of input.");
    }

    @Test
    void testSimpleSequence() {
        String input = "let x = 5;";
        List<Token> expected = Arrays.asList(
                new Token(TokenType.LET, "let", 1, 1),
                new Token(TokenType.IDENTIFIER, "x", 1, 5),
                new Token(TokenType.ASSIGN, "=", 1, 7),
                new Token(TokenType.INTEGER, "5", 1, 9),
                new Token(TokenType.SEMICOLON, ";", 1, 10)
        );
        assertTokens(input, expected);
    }

    @Test
    void testKeywords() {
        String input = "let if else while print";
        List<Token> expected = Arrays.asList(
                new Token(TokenType.LET, "let", 1, 1),
                new Token(TokenType.IF, "if", 1, 5),
                new Token(TokenType.ELSE, "else", 1, 8),
                new Token(TokenType.WHILE, "while", 1, 13),
                new Token(TokenType.PRINT, "print", 1, 19)
        );
        assertTokens(input, expected);
    }

    @Test
    void testOperators() {
        String input = "= + - * / < > == != <= >=";
        List<Token> expected = Arrays.asList(
                new Token(TokenType.ASSIGN, "=", 1, 1),
                new Token(TokenType.PLUS, "+", 1, 3),
                new Token(TokenType.MINUS, "-", 1, 5),
                new Token(TokenType.MULTIPLY, "*", 1, 7),
                new Token(TokenType.DIVIDE, "/", 1, 9),
                new Token(TokenType.LT, "<", 1, 11),
                new Token(TokenType.GT, ">", 1, 13),
                new Token(TokenType.EQ, "==", 1, 15),
                new Token(TokenType.NEQ, "!=", 1, 18),
                new Token(TokenType.LTE, "<=", 1, 21),
                new Token(TokenType.GTE, ">=", 1, 24)
        );
        assertTokens(input, expected);
    }

    @Test
    void testIdentifiersAndIntegers() {
        String input = "foobar 123 baz 4567";
        List<Token> expected = Arrays.asList(
                new Token(TokenType.IDENTIFIER, "foobar", 1, 1),
                new Token(TokenType.INTEGER, "123", 1, 8),
                new Token(TokenType.IDENTIFIER, "baz", 1, 12),
                new Token(TokenType.INTEGER, "4567", 1, 16)
        );
        assertTokens(input, expected);
    }
    
    @Test
    void testIdentifiersWithUnderscoreAndDigits() {
        String input = "var_1 _myVar var2_ val_ V1";
        List<Token> expected = Arrays.asList(
            new Token(TokenType.IDENTIFIER, "var_1", 1,1),
            new Token(TokenType.IDENTIFIER, "_myVar", 1,7),
            new Token(TokenType.IDENTIFIER, "var2_", 1,14),
            new Token(TokenType.IDENTIFIER, "val_", 1,20),
            new Token(TokenType.IDENTIFIER, "V1", 1,25)
        );
        assertTokens(input, expected);
    }


    @Test
    void testPunctuation() {
        String input = "(){};";
        List<Token> expected = Arrays.asList(
                new Token(TokenType.LPAREN, "(", 1, 1),
                new Token(TokenType.RPAREN, ")", 1, 2),
                new Token(TokenType.LBRACE, "{", 1, 3),
                new Token(TokenType.RBRACE, "}", 1, 4),
                new Token(TokenType.SEMICOLON, ";", 1, 5)
        );
        assertTokens(input, expected);
    }

    @Test
    void testWhitespaceAndComments() {
        String input = "let x = 5; // This is a comment\n" +
                       "// Another comment\n" +
                       "let y = 10;";
        List<Token> expected = Arrays.asList(
                new Token(TokenType.LET, "let", 1, 1),
                new Token(TokenType.IDENTIFIER, "x", 1, 5),
                new Token(TokenType.ASSIGN, "=", 1, 7),
                new Token(TokenType.INTEGER, "5", 1, 9),
                new Token(TokenType.SEMICOLON, ";", 1, 10),
                new Token(TokenType.LET, "let", 3, 1), // After comments, line number increases
                new Token(TokenType.IDENTIFIER, "y", 3, 5),
                new Token(TokenType.ASSIGN, "=", 3, 7),
                new Token(TokenType.INTEGER, "10", 3, 9),
                new Token(TokenType.SEMICOLON, ";", 3, 11)
        );
        assertTokens(input, expected);
    }
    
    @Test
    void testCommentAtEndOfFile() {
        String input = "let x = 5; // This is a comment at EOF";
        List<Token> expected = Arrays.asList(
                new Token(TokenType.LET, "let", 1, 1),
                new Token(TokenType.IDENTIFIER, "x", 1, 5),
                new Token(TokenType.ASSIGN, "=", 1, 7),
                new Token(TokenType.INTEGER, "5", 1, 9),
                new Token(TokenType.SEMICOLON, ";", 1, 10)
        );
        assertTokens(input, expected);
    }

    @Test
    void testOnlyWhitespaceAndComments() {
        String input = "// comment 1\n   // comment 2\n\t\t";
        List<Token> expected = Arrays.asList(
                // No tokens expected other than EOF
        );
        assertTokens(input, expected);
    }


    @Test
    void testComplexProgram() {
        String input = "let five = 5;\n" +
                       "let ten = 10;\n" +
                       "let add = fn(x, y) {\n" + // Assuming 'fn' would be an IDENTIFIER for now
                       "  x + y;\n" +
                       "};\n" +
                       "let result = add(five, ten);\n" +
                       "if (result > 10) {\n" +
                       "  print(result);\n" +
                       "} else {\n" +
                       "  print(0);\n" +
                       "}\n" +
                       "10 == 10;\n" +
                       "10 != 9;";
        List<Token> expected = Arrays.asList(
                new Token(TokenType.LET, "let", 1, 1),
                new Token(TokenType.IDENTIFIER, "five", 1, 5),
                new Token(TokenType.ASSIGN, "=", 1, 10),
                new Token(TokenType.INTEGER, "5", 1, 12),
                new Token(TokenType.SEMICOLON, ";", 1, 13),

                new Token(TokenType.LET, "let", 2, 1),
                new Token(TokenType.IDENTIFIER, "ten", 2, 5),
                new Token(TokenType.ASSIGN, "=", 2, 9),
                new Token(TokenType.INTEGER, "10", 2, 11),
                new Token(TokenType.SEMICOLON, ";", 2, 13),

                new Token(TokenType.LET, "let", 3, 1),
                new Token(TokenType.IDENTIFIER, "add", 3, 5),
                new Token(TokenType.ASSIGN, "=", 3, 9),
                new Token(TokenType.IDENTIFIER, "fn", 3, 11), // 'fn' as IDENTIFIER
                new Token(TokenType.LPAREN, "(", 3, 13),
                new Token(TokenType.IDENTIFIER, "x", 3, 14),
                new Token(TokenType.IDENTIFIER, "y", 3, 17), // Assuming comma is skipped or handled as punctuation if added
                new Token(TokenType.RPAREN, ")", 3, 18),
                new Token(TokenType.LBRACE, "{", 3, 20),

                new Token(TokenType.IDENTIFIER, "x", 4, 3),
                new Token(TokenType.PLUS, "+", 4, 5),
                new Token(TokenType.IDENTIFIER, "y", 4, 7),
                new Token(TokenType.SEMICOLON, ";", 4, 8),

                new Token(TokenType.RBRACE, "}", 5, 1),
                new Token(TokenType.SEMICOLON, ";", 5, 2),

                new Token(TokenType.LET, "let", 6, 1),
                new Token(TokenType.IDENTIFIER, "result", 6, 5),
                new Token(TokenType.ASSIGN, "=", 6, 12),
                new Token(TokenType.IDENTIFIER, "add", 6, 14),
                new Token(TokenType.LPAREN, "(", 6, 17),
                new Token(TokenType.IDENTIFIER, "five", 6, 18),
                new Token(TokenType.IDENTIFIER, "ten", 6, 24), // Assuming comma
                new Token(TokenType.RPAREN, ")", 6, 27),
                new Token(TokenType.SEMICOLON, ";", 6, 28),

                new Token(TokenType.IF, "if", 7, 1),
                new Token(TokenType.LPAREN, "(", 7, 4),
                new Token(TokenType.IDENTIFIER, "result", 7, 5),
                new Token(TokenType.GT, ">", 7, 12),
                new Token(TokenType.INTEGER, "10", 7, 14),
                new Token(TokenType.RPAREN, ")", 7, 16),
                new Token(TokenType.LBRACE, "{", 7, 18),

                new Token(TokenType.PRINT, "print", 8, 3),
                new Token(TokenType.LPAREN, "(", 8, 8),
                new Token(TokenType.IDENTIFIER, "result", 8, 9),
                new Token(TokenType.RPAREN, ")", 8, 15),
                new Token(TokenType.SEMICOLON, ";", 8, 16),

                new Token(TokenType.RBRACE, "}", 9, 1),
                new Token(TokenType.ELSE, "else", 9, 3),
                new Token(TokenType.LBRACE, "{", 9, 8),

                new Token(TokenType.PRINT, "print", 10, 3),
                new Token(TokenType.LPAREN, "(", 10, 8),
                new Token(TokenType.INTEGER, "0", 10, 9),
                new Token(TokenType.RPAREN, ")", 10, 10),
                new Token(TokenType.SEMICOLON, ";", 10, 11),

                new Token(TokenType.RBRACE, "}", 11, 1),

                new Token(TokenType.INTEGER,"10", 12, 1),
                new Token(TokenType.EQ, "==", 12, 4),
                new Token(TokenType.INTEGER, "10", 12, 7),
                new Token(TokenType.SEMICOLON, ";", 12, 9),

                new Token(TokenType.INTEGER,"10", 13, 1),
                new Token(TokenType.NEQ, "!=", 13, 4),
                new Token(TokenType.INTEGER, "9", 13, 7),
                new Token(TokenType.SEMICOLON, ";", 13, 8)
        );
        // Note: The 'fn(x, y)' part and commas are not formally part of the current grammar.
        // 'fn', 'x', 'y' will be tokenized as IDENTIFIERs. Commas would be ILLEGAL or need to be added to TokenType.
        // For this test, I'm assuming commas are skipped like whitespace or are not present.
        // If commas were present and not skipped, they'd be ILLEGAL or need a new TokenType.
        // Current lexer skips unknown characters if they are not part of other tokens.
        // Let's assume fn(x y) for simplicity as per current lexer behavior for identifiers.
        // The provided 'complex' input has commas, which current lexer would treat as illegal or skip if not handled.
        // I will adjust the input to be `fn(x y)` for this test to pass with current lexer
        // or acknowledge that commas would be ILLEGAL tokens.
        // The provided grammar does not include function definitions or commas.
        // My Lexer treats unknown characters as ILLEGAL.
        // The complex test above assumes `fn(x, y)` where `,` is not a token.
        // Let's adjust the input to match the grammar, or test for ILLEGAL comma.
        // The current lexer would make ',' an ILLEGAL token.
        // I will modify the complex test to not use commas or 'fn' as they are not in grammar.

        String simplifiedComplexInput = "let five = 5;\n" +
                       "let ten = 10;\n" +
                       "// let add = fn(x y) { x + y; }; // Removed fn for now\n" +
                       "let result = five + ten;\n" + // Simplified
                       "if (result > 10) {\n" +
                       "  print(result);\n" +
                       "} else {\n" +
                       "  print(0);\n" +
                       "}\n" +
                       "10 == 10;\n" +
                       "10 != 9;";
        
        List<Token> simplifiedExpected = Arrays.asList(
            new Token(TokenType.LET, "let", 1, 1),
            new Token(TokenType.IDENTIFIER, "five", 1, 5),
            new Token(TokenType.ASSIGN, "=", 1, 10),
            new Token(TokenType.INTEGER, "5", 1, 12),
            new Token(TokenType.SEMICOLON, ";", 1, 13),

            new Token(TokenType.LET, "let", 2, 1),
            new Token(TokenType.IDENTIFIER, "ten", 2, 5),
            new Token(TokenType.ASSIGN, "=", 2, 9),
            new Token(TokenType.INTEGER, "10", 2, 11),
            new Token(TokenType.SEMICOLON, ";", 2, 13),

            // Line 3 is a comment
            
            new Token(TokenType.LET, "let", 4, 1),
            new Token(TokenType.IDENTIFIER, "result", 4, 5),
            new Token(TokenType.ASSIGN, "=", 4, 12),
            new Token(TokenType.IDENTIFIER, "five", 4, 14),
            new Token(TokenType.PLUS, "+", 4, 19),
            new Token(TokenType.IDENTIFIER, "ten", 4, 21),
            new Token(TokenType.SEMICOLON, ";", 4, 24),

            new Token(TokenType.IF, "if", 5, 1),
            new Token(TokenType.LPAREN, "(", 5, 4),
            new Token(TokenType.IDENTIFIER, "result", 5, 5),
            new Token(TokenType.GT, ">", 5, 12),
            new Token(TokenType.INTEGER, "10", 5, 14),
            new Token(TokenType.RPAREN, ")", 5, 16),
            new Token(TokenType.LBRACE, "{", 5, 18),

            new Token(TokenType.PRINT, "print", 6, 3),
            new Token(TokenType.LPAREN, "(", 6, 8),
            new Token(TokenType.IDENTIFIER, "result", 6, 9),
            new Token(TokenType.RPAREN, ")", 6, 15),
            new Token(TokenType.SEMICOLON, ";", 6, 16),

            new Token(TokenType.RBRACE, "}", 7, 1),
            new Token(TokenType.ELSE, "else", 7, 3),
            new Token(TokenType.LBRACE, "{", 7, 8),

            new Token(TokenType.PRINT, "print", 8, 3),
            new Token(TokenType.LPAREN, "(", 8, 8),
            new Token(TokenType.INTEGER, "0", 8, 9),
            new Token(TokenType.RPAREN, ")", 8, 10),
            new Token(TokenType.SEMICOLON, ";", 8, 11),

            new Token(TokenType.RBRACE, "}", 9, 1),

            new Token(TokenType.INTEGER,"10", 10, 1),
            new Token(TokenType.EQ, "==", 10, 4),
            new Token(TokenType.INTEGER, "10", 10, 7),
            new Token(TokenType.SEMICOLON, ";", 10, 9),

            new Token(TokenType.INTEGER,"10", 11, 1),
            new Token(TokenType.NEQ, "!=", 11, 4),
            new Token(TokenType.INTEGER, "9", 11, 7),
            new Token(TokenType.SEMICOLON, ";", 11, 8)
        );
        assertTokens(simplifiedComplexInput, simplifiedExpected);
    }


    @Test
    void testIllegalTokens() {
        String input = "let x = 5 $ 10;"; // '$' is illegal
        List<Token> expected = Arrays.asList(
                new Token(TokenType.LET, "let", 1, 1),
                new Token(TokenType.IDENTIFIER, "x", 1, 5),
                new Token(TokenType.ASSIGN, "=", 1, 7),
                new Token(TokenType.INTEGER, "5", 1, 9),
                new Token(TokenType.ILLEGAL, "$", 1, 11),
                new Token(TokenType.INTEGER, "10", 1, 13),
                new Token(TokenType.SEMICOLON, ";", 1, 15)
        );
        assertTokens(input, expected);
    }
    
    @Test
    void testMultipleIllegalTokens() {
        String input = "let a = @#%;";
        List<Token> expected = Arrays.asList(
            new Token(TokenType.LET, "let", 1, 1),
            new Token(TokenType.IDENTIFIER, "a", 1, 5),
            new Token(TokenType.ASSIGN, "=", 1, 7),
            new Token(TokenType.ILLEGAL, "@", 1, 9),
            new Token(TokenType.ILLEGAL, "#", 1, 10),
            new Token(TokenType.ILLEGAL, "%", 1, 11),
            new Token(TokenType.SEMICOLON, ";", 1, 12)
        );
        assertTokens(input, expected);
    }


    @Test
    void testEOF() {
        String input = "";
        List<Token> expected = Arrays.asList(); // No tokens before EOF
        assertTokens(input, expected);

        String input2 = "let x = 1;";
         List<Token> expected2 = Arrays.asList(
                new Token(TokenType.LET, "let", 1, 1),
                new Token(TokenType.IDENTIFIER, "x", 1, 5),
                new Token(TokenType.ASSIGN, "=", 1, 7),
                new Token(TokenType.INTEGER, "1", 1, 9),
                new Token(TokenType.SEMICOLON, ";", 1, 10)
        );
        assertTokens(input2, expected2);
    }
}
