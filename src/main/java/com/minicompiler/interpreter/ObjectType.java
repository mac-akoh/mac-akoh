package com.minicompiler.interpreter;

public enum ObjectType {
    INTEGER,
    BOOLEAN,
    NULL,
    RETURN_VALUE, // For function return values (future)
    ERROR;        // For runtime errors

    @Override
    public String toString() {
        return this.name();
    }
}
