package com.minicompiler.interpreter;

import java.util.Objects;

public class MCBoolean implements MCObject {
    private final boolean value;

    public static final MCBoolean TRUE = new MCBoolean(true);
    public static final MCBoolean FALSE = new MCBoolean(false);

    private MCBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCBoolean mcBoolean = (MCBoolean) o;
        return value == mcBoolean.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
