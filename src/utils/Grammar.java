package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grammar {
    private final Set<Symbol> terminals;
    private final Set<Symbol> nonTerminals;
    private final List<Production> productions;
    private final Symbol startSymbol;

    public Grammar(Set<Symbol> terminals, Set<Symbol> nonTerminals, 
                  List<Production> productions, Symbol startSymbol) {
        this.terminals = new HashSet<>(terminals);
        this.nonTerminals = new HashSet<>(nonTerminals);
        this.productions = new ArrayList<>(productions);
        this.startSymbol = startSymbol;
        
        validateGrammar();
    }

    private void validateGrammar() {
        // Check if start symbol is a non-terminal
        if (!nonTerminals.contains(startSymbol)) {
            throw new IllegalArgumentException("Start symbol must be a non-terminal");
        }

        // Check if all symbols in productions are defined
        for (Production prod : productions) {
            if (!nonTerminals.contains(prod.getLeftSide())) {
                throw new IllegalArgumentException(
                    "Left side of production must be a non-terminal: " + prod.getLeftSide());
            }

            for (Symbol symbol : prod.getRightSide()) {
                if (!terminals.contains(symbol) && !nonTerminals.contains(symbol)) {
                    throw new IllegalArgumentException(
                        "Undefined symbol in production: " + symbol);
                }
            }
        }
    }

    public Set<Symbol> getTerminals() {
        return new HashSet<>(terminals);
    }

    public Set<Symbol> getNonTerminals() {
        return new HashSet<>(nonTerminals);
    }

    public List<Production> getProductions() {
        return new ArrayList<>(productions);
    }

    public List<Production> getProductionsFor(Symbol nonTerminal) {
        List<Production> result = new ArrayList<>();
        for (Production prod : productions) {
            if (prod.getLeftSide().equals(nonTerminal)) {
                result.add(prod);
            }
        }
        return result;
    }

    public Symbol getStartSymbol() {
        return startSymbol;
    }
}
