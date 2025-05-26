package com.minicompiler.ast;

public interface Node {
    String tokenLiteral(); // Returns the literal value of the token it's associated with
    String toString();     // For debugging
}
