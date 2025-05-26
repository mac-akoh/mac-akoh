package com.minicompiler.semantics;

import java.util.Objects;

public class Symbol {
    private final String name;
    private final String type; // For now, "INTEGER"
    // Potentially: private int scopeLevel;

    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Symbol{" +
               "name='" + name + '\'' +
               ", type='" + type + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(name, symbol.name) &&
               Objects.equals(type, symbol.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
