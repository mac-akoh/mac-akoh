package com.minicompiler.lexer;

public enum TokenType {
    // Keywords
    LET,      // let
    IF,       // if
    ELSE,     // else
    WHILE,    // while
    PRINT,    // print

    // Identifiers
    IDENTIFIER, // variable names, function names, etc.

    // Literals
    INTEGER,  // 123, 0, 999

    // Operators
    ASSIGN,   // =
    PLUS,     // +
    MINUS,    // -
    MULTIPLY, // *
    DIVIDE,   // /

    LT,       // <
    GT,       // >
    EQ,       // ==
    NEQ,      // !=
    LTE,      // <=
    GTE,      // >=

    // Punctuation
    LPAREN,   // (
    RPAREN,   // )
    LBRACE,   // {
    RBRACE,   // }
    SEMICOLON,// ;

    // Special
    EOF,      // End Of File
    ILLEGAL   // Unrecognized character or token
}
