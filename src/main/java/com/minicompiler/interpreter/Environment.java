package com.minicompiler.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Environment {
    private final Map<String, MCObject> store;
    private final Environment outer;

    public Environment() {
        this.store = new HashMap<>();
        this.outer = null;
    }

    public Environment(Environment outer) {
        this.store = new HashMap<>();
        this.outer = outer;
    }

    public MCObject define(String name, MCObject value) {
        store.put(name, value);
        return value;
    }

    public Optional<MCObject> assign(String name, MCObject value) {
        if (store.containsKey(name)) {
            store.put(name, value);
            return Optional.of(value);
        }
        if (outer != null) {
            return outer.assign(name, value);
        }
        return Optional.empty(); // Variable not declared
    }

    public Optional<MCObject> get(String name) {
        if (store.containsKey(name)) {
            return Optional.ofNullable(store.get(name));
        }
        if (outer != null) {
            return outer.get(name);
        }
        return Optional.empty(); // Variable not found
    }

    public Environment getOuter() {
        return outer;
    }
}
