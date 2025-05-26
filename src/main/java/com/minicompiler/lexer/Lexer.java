package com.minicompiler.lexer;

import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final String input;
    private int position;      // current position in input (points to current char)
    private int readPosition;  // current reading position in input (after current char)
    private char ch;           // current char under examination
    private int line;          // current line number
    private int column;        // current column number

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("let", TokenType.LET);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("print", TokenType.PRINT);
    }

    public Lexer(String input) {
        this.input = input;
        this.line = 1;
        this.column = 0;
        readChar(); // Initialize ch, position, readPosition, and column
    }

    private void readChar() {
        if (readPosition >= input.length()) {
            ch = 0; // ASCII NUL, represents EOF
        } else {
            ch = input.charAt(readPosition);
        }
        position = readPosition;
        readPosition += 1;
        column += 1;
    }

    private char peekChar() {
        if (readPosition >= input.length()) {
            return 0;
        } else {
            return input.charAt(readPosition);
        }
    }

    public Token nextToken() {
        Token token;

        skipWhitespaceAndComments();

        int startColumn = column;

        switch (ch) {
            case '=':
                if (peekChar() == '=') {
                    readChar();
                    token = new Token(TokenType.EQ, "==", line, startColumn);
                } else {
                    token = new Token(TokenType.ASSIGN, String.valueOf(ch), line, startColumn);
                }
                break;
            case '+':
                token = new Token(TokenType.PLUS, String.valueOf(ch), line, startColumn);
                break;
            case '-':
                token = new Token(TokenType.MINUS, String.valueOf(ch), line, startColumn);
                break;
            case '*':
                token = new Token(TokenType.MULTIPLY, String.valueOf(ch), line, startColumn);
                break;
            case '/':
                token = new Token(TokenType.DIVIDE, String.valueOf(ch), line, startColumn);
                break;
            case '<':
                if (peekChar() == '=') {
                    readChar();
                    token = new Token(TokenType.LTE, "<=", line, startColumn);
                } else {
                    token = new Token(TokenType.LT, String.valueOf(ch), line, startColumn);
                }
                break;
            case '>':
                if (peekChar() == '=') {
                    readChar();
                    token = new Token(TokenType.GTE, ">=", line, startColumn);
                } else {
                    token = new Token(TokenType.GT, String.valueOf(ch), line, startColumn);
                }
                break;
            case '!':
                if (peekChar() == '=') {
                    readChar();
                    token = new Token(TokenType.NEQ, "!=", line, startColumn);
                } else {
                    token = new Token(TokenType.ILLEGAL, String.valueOf(ch), line, startColumn);
                }
                break;
            case '(':
                token = new Token(TokenType.LPAREN, String.valueOf(ch), line, startColumn);
                break;
            case ')':
                token = new Token(TokenType.RPAREN, String.valueOf(ch), line, startColumn);
                break;
            case '{':
                token = new Token(TokenType.LBRACE, String.valueOf(ch), line, startColumn);
                break;
            case '}':
                token = new Token(TokenType.RBRACE, String.valueOf(ch), line, startColumn);
                break;
            case ';':
                token = new Token(TokenType.SEMICOLON, String.valueOf(ch), line, startColumn);
                break;
            case 0: // EOF
                token = new Token(TokenType.EOF, "", line, startColumn);
                break;
            default:
                if (isLetter(ch)) {
                    String literal = readIdentifier();
                    TokenType type = keywords.getOrDefault(literal, TokenType.IDENTIFIER);
                    return new Token(type, literal, line, startColumn); // return early, readIdentifier calls readChar
                } else if (isDigit(ch)) {
                    String literal = readNumber();
                    return new Token(TokenType.INTEGER, literal, line, startColumn); // return early
                } else {
                    token = new Token(TokenType.ILLEGAL, String.valueOf(ch), line, startColumn);
                }
        }

        readChar(); // Move to the next character
        return token;
    }

    private void skipWhitespaceAndComments() {
        while (ch != 0) {
            if (ch == ' ' || ch == '\t' || ch == '\r') {
                readChar();
            } else if (ch == '\n') {
                readChar();
                line++;
                column = 0; // Reset column at new line
            } else if (ch == '/' && peekChar() == '/') {
                // Skip single-line comment
                while (ch != '\n' && ch != 0) {
                    readChar();
                }
                if (ch == '\n') { // If comment ended with a newline
                    line++;
                    column = 0;
                    readChar(); // Consume the newline
                }
            } else {
                break;
            }
        }
    }

    private String readIdentifier() {
        int startPos = position;
        while (isLetter(ch) || isDigit(ch)) {
            readChar();
        }
        return input.substring(startPos, position);
    }

    private String readNumber() {
        int startPos = position;
        while (isDigit(ch)) {
            readChar();
        }
        return input.substring(startPos, position);
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
