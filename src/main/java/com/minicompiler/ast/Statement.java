package com.minicompiler.ast;

public interface Statement extends Node {
    // Marker interface for statement nodes
    // All statement nodes must also implement a method called statementNode()
    // This is a common pattern in the Go interpreter this is based on,
    // but not strictly necessary in Java if using instanceof.
    // For now, we'll keep it simple.
}
