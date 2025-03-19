package ll;

import utils.*;
import java.util.*;

public class FirstFollowCalculator {
    private final Grammar grammar;
    private final Map<Symbol, Set<Symbol>> firstSets;
    private final Map<Symbol, Set<Symbol>> followSets;
    private final Symbol EPSILON = new Symbol("ε", true);

    public FirstFollowCalculator(Grammar grammar) {
        this.grammar = grammar;
        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
        computeFirstSets();
        computeFollowSets();
    }

    private void computeFirstSets() {
        // Initialize FIRST sets
        for (Symbol terminal : grammar.getTerminals()) {
            Set<Symbol> firstSet = new HashSet<>();
            firstSet.add(terminal);
            firstSets.put(terminal, firstSet);
        }
        
        for (Symbol nonTerminal : grammar.getNonTerminals()) {
            firstSets.put(nonTerminal, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (Production prod : grammar.getProductions()) {
                Symbol leftSide = prod.getLeftSide();
                List<Symbol> rightSide = prod.getRightSide();
                
                // Handle empty production (epsilon)
                if (rightSide.isEmpty()) {
                    if (firstSets.get(leftSide).add(EPSILON)) {
                        changed = true;
                    }
                    continue;
                }

                // Calculate FIRST set for the production
                Set<Symbol> productionFirst = calculateFirstOfSequence(rightSide);
                if (firstSets.get(leftSide).addAll(productionFirst)) {
                    changed = true;
                }
            }
        } while (changed);
    }

    private Set<Symbol> calculateFirstOfSequence(List<Symbol> sequence) {
        Set<Symbol> result = new HashSet<>();
        
        // If sequence is empty, return ε
        if (sequence.isEmpty()) {
            result.add(EPSILON);
            return result;
        }

        Symbol firstSymbol = sequence.get(0);
        result.addAll(firstSets.get(firstSymbol));
        
        int i = 0;
        while (i < sequence.size() && firstSets.get(sequence.get(i)).contains(EPSILON)) {
            if (i + 1 < sequence.size()) {
                result.addAll(firstSets.get(sequence.get(i + 1)));
                result.remove(EPSILON);
            }
            i++;
        }
        
        // If all symbols can derive ε, add it to result
        if (i == sequence.size()) {
            result.add(EPSILON);
        }
        
        return result;
    }

    private void computeFollowSets() {
        // Initialize FOLLOW sets
        for (Symbol nonTerminal : grammar.getNonTerminals()) {
            followSets.put(nonTerminal, new HashSet<>());
        }
        
        // Add $ to FOLLOW set of start symbol
        followSets.get(grammar.getStartSymbol()).add(new Symbol("$", true));

        boolean changed;
        do {
            changed = false;
            for (Production prod : grammar.getProductions()) {
                List<Symbol> rightSide = prod.getRightSide();
                
                for (int i = 0; i < rightSide.size(); i++) {
                    Symbol current = rightSide.get(i);
                    
                    // Skip terminals
                    if (current.isTerminal()) continue;
                    
                    Set<Symbol> followSet = followSets.get(current);
                    
                    // If this is the last symbol
                    if (i == rightSide.size() - 1) {
                        if (followSet.addAll(followSets.get(prod.getLeftSide()))) {
                            changed = true;
                        }
                    } else {
                        // Calculate FIRST of remaining sequence
                        List<Symbol> remainingSymbols = rightSide.subList(i + 1, rightSide.size());
                        Set<Symbol> firstOfRemaining = calculateFirstOfSequence(remainingSymbols);
                        
                        // Add all non-epsilon symbols
                        firstOfRemaining.remove(EPSILON);
                        if (followSet.addAll(firstOfRemaining)) {
                            changed = true;
                        }
                        
                        // If FIRST contains epsilon, add FOLLOW of left side
                        if (calculateFirstOfSequence(remainingSymbols).contains(EPSILON)) {
                            if (followSet.addAll(followSets.get(prod.getLeftSide()))) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);
    }

    public Set<Symbol> getFirst(Symbol symbol) {
        return new HashSet<>(firstSets.get(symbol));
    }

    public Set<Symbol> getFollow(Symbol nonTerminal) {
        return new HashSet<>(followSets.get(nonTerminal));
    }

    public Set<Symbol> getFirst(List<Symbol> sequence) {
        return calculateFirstOfSequence(sequence);
    }
}