package com.minicompiler.interpreter;

import java.util.Objects;

public class MCNull implements MCObject {
    public static final MCNull INSTANCE = new MCNull();

    private MCNull() {
        // Private constructor for singleton
    }

    @Override
    public ObjectType type() {
        return ObjectType.NULL;
    }

    @Override
    public String inspect() {
        return "null";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MCNull;
    }

    @Override
    public int hashCode() {
        return Objects.hash("null"); // Consistent hash code for the singleton
    }
}
