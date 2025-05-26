package com.minicompiler.interpreter;

import java.util.Objects;

public class MCError implements MCObject {
    private final String message;

    public MCError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public ObjectType type() {
        return ObjectType.ERROR; // Ensure ObjectType enum has ERROR
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCError mcError = (MCError) o;
        return Objects.equals(message, mcError.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
