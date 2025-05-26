package com.minicompiler.ast;

public interface Expression extends Node {
    // Marker interface for expression nodes
    // All expression nodes must also implement a method called expressionNode()
    // Similar to Statement.java, this is for pattern matching convenience.
}
