package com.minicompiler.semantics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SymbolTable {
    private final Map<String, Symbol> symbols;
    private final SymbolTable outer; // For handling scopes

    public SymbolTable() {
        this.symbols = new HashMap<>();
        this.outer = null; // No outer scope for the global table
    }

    public SymbolTable(SymbolTable outer) {
        this.symbols = new HashMap<>();
        this.outer = outer;
    }

    /**
     * Defines a new symbol (variable) in the current scope.
     * All variables are currently of type "INTEGER".
     *
     * @param name The name of the variable.
     * @return true if successful, false if already defined in the current scope.
     */
    public boolean define(String name) {
        if (symbols.containsKey(name)) {
            return false; // Already defined in this scope
        }
        symbols.put(name, new Symbol(name, "INTEGER"));
        return true;
    }

    /**
     * Resolves a symbol by name, looking in the current scope and then in outer scopes.
     *
     * @param name The name of the symbol to resolve.
     * @return An Optional containing the Symbol if found, otherwise an empty Optional.
     */
    public Optional<Symbol> resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return Optional.of(symbol);
        }
        if (outer != null) {
            return outer.resolve(name);
        }
        return Optional.empty();
    }
    
    /**
     * Checks if a symbol is defined specifically in the current scope.
     * Does not check outer scopes.
     * @param name The name of the symbol.
     * @return true if defined in the current scope, false otherwise.
     */
    public boolean isDefinedInCurrentScope(String name) {
        return symbols.containsKey(name);
    }


    public SymbolTable getOuter() {
        return outer;
    }
}
