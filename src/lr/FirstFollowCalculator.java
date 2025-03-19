package lr;

import java.util.*;
import utils.*;

public class FirstFollowCalculator {
    private final Grammar grammar;
    private final Map<Symbol, Set<Symbol>> firstSets;
    private final Map<Symbol, Set<Symbol>> followSets;
    private final Symbol EPSILON;

    public FirstFollowCalculator(Grammar grammar) {
        this.grammar = grammar;
        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
        this.EPSILON = new Symbol("ε", true);
        
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
            
            for (Production production : grammar.getProductions()) {
                Symbol leftSide = production.getLeftSide();
                List<Symbol> rightSide = production.getRightSide();
                
                // Rule 1: If X -> ε is a production, then add ε to FIRST(X)
                if (rightSide.isEmpty() || (rightSide.size() == 1 && rightSide.get(0).equals(EPSILON))) {
                    if (firstSets.get(leftSide).add(EPSILON)) {
                        changed = true;
                    }
                    continue;
                }
                
                // Apply rules for non-empty right-hand sides
                Set<Symbol> firstOfRightSide = getFirstOfSequence(rightSide);
                
                if (firstSets.get(leftSide).addAll(firstOfRightSide)) {
                    changed = true;
                }
            }
        } while (changed);
    }

    private void computeFollowSets() {
        // Initialize FOLLOW sets
        for (Symbol nonTerminal : grammar.getNonTerminals()) {
            followSets.put(nonTerminal, new HashSet<>());
        }
        
        // Rule 1: Add $ to FOLLOW(S) where S is the start symbol
        Symbol startSymbol = grammar.getStartSymbol();
        followSets.get(startSymbol).add(new Symbol("$", true));
        
        boolean changed;
        do {
            changed = false;
            
            for (Production production : grammar.getProductions()) {
                Symbol leftSide = production.getLeftSide();
                List<Symbol> rightSide = production.getRightSide();
                
                for (int i = 0; i < rightSide.size(); i++) {
                    Symbol currentSymbol = rightSide.get(i);
                    
                    if (!currentSymbol.isTerminal() && !currentSymbol.equals(EPSILON)) {
                        // Non-terminal
                        
                        if (i == rightSide.size() - 1) {
                            // Rule 2: If A -> αB, then everything in FOLLOW(A) is in FOLLOW(B)
                            if (followSets.get(currentSymbol).addAll(followSets.get(leftSide))) {
                                changed = true;
                            }
                        } else {
                            // Rule 3: If A -> αBβ, then first(β) - {ε} is in FOLLOW(B)
                            List<Symbol> beta = rightSide.subList(i + 1, rightSide.size());
                            Set<Symbol> firstOfBeta = getFirstOfSequence(beta);
                            
                            // Add FIRST(β) - {ε} to FOLLOW(B)
                            boolean hasEpsilon = firstOfBeta.remove(EPSILON);
                            if (followSets.get(currentSymbol).addAll(firstOfBeta)) {
                                changed = true;
                            }
                            
                            // Rule 4: If A -> αBβ and ε is in FIRST(β), then FOLLOW(A) is in FOLLOW(B)
                            if (hasEpsilon) {
                                if (followSets.get(currentSymbol).addAll(followSets.get(leftSide))) {
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (changed);
    }

    private Set<Symbol> getFirstOfSequence(List<Symbol> sequence) {
        Set<Symbol> first = new HashSet<>();
        
        if (sequence.isEmpty()) {
            first.add(EPSILON);
            return first;
        }
        
        Symbol firstSymbol = sequence.get(0);
        if (firstSymbol.isTerminal()) {
            first.add(firstSymbol);
            return first;
        }
        
        // Copy the FIRST set of the first symbol
        first.addAll(firstSets.get(firstSymbol));
        
        // Check if we need to calculate FIRST of the rest of the sequence
        int i = 0;
        while (i < sequence.size() && firstSets.get(sequence.get(i)).contains(EPSILON)) {
            first.remove(EPSILON);
            i++;
            
            if (i < sequence.size()) {
                Symbol nextSymbol = sequence.get(i);
                if (nextSymbol.isTerminal()) {
                    first.add(nextSymbol);
                    break;
                } else {
                    first.addAll(firstSets.get(nextSymbol));
                }
            } else {
                first.add(EPSILON);
            }
        }
        
        return first;
    }

    public Set<Symbol> getFirst(Symbol symbol) {
        return new HashSet<>(firstSets.get(symbol));
    }

    public Set<Symbol> getFollow(Symbol nonTerminal) {
        if (!followSets.containsKey(nonTerminal)) {
            throw new IllegalArgumentException("Symbol " + nonTerminal + " is not a non-terminal");
        }
        return new HashSet<>(followSets.get(nonTerminal));
    }
}