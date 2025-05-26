package com.minicompiler.interpreter;

import java.util.Objects;

public class MCInteger implements MCObject {
    private final long value;

    public MCInteger(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.INTEGER;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCInteger mcInteger = (MCInteger) o;
        return value == mcInteger.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
