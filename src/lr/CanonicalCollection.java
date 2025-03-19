package lr;

import utils.*;
import java.util.*;

public class CanonicalCollection {
    private final Grammar grammar;
    private final List<ItemSet> states;
    private final Map<TransitionKey, Integer> transitions;

    public CanonicalCollection(Grammar grammar) {
        this.grammar = grammar;
        this.states = new ArrayList<>();
        this.transitions = new HashMap<>();
        constructCollection();
    }

    private void constructCollection() {
        // Create augmented grammar by adding a new start production: S' -> S
        Symbol augmentedStart = new Symbol(grammar.getStartSymbol().getName() + "'", false);
        Production startProduction = new Production(augmentedStart, 
                                                   Arrays.asList(grammar.getStartSymbol()));
        
        // Create initial item set with S' -> •S
        Set<Item> initialItems = new HashSet<>();
        initialItems.add(new Item(startProduction, 0));
        
        // Compute closure of initial item set
        Set<Item> closure = computeClosure(initialItems);
        ItemSet initialState = new ItemSet(closure, 0);
        
        states.add(initialState);
        
        // Process states until no new states are added
        for (int i = 0; i < states.size(); i++) {
            ItemSet state = states.get(i);
            
            // Get all symbols that appear after dot
            Set<Symbol> symbols = state.getNextSymbols();
            
            for (Symbol symbol : symbols) {
                // Compute GOTO(state, symbol)
                ItemSet nextState = computeGoto(state, symbol);
                
                // Check if state already exists
                int nextStateIndex = states.indexOf(nextState);
                if (nextStateIndex == -1) {
                    // Add new state
                    nextState = new ItemSet(nextState.getItems(), states.size());
                    states.add(nextState);
                    nextStateIndex = nextState.getStateNumber();
                }
                
                // Add transition
                transitions.put(new TransitionKey(state.getStateNumber(), symbol), nextStateIndex);
            }
        }
    }

    private Set<Item> computeClosure(Set<Item> items) {
        Set<Item> closure = new HashSet<>(items);
        boolean changed;
        
        do {
            changed = false;
            Set<Item> newItems = new HashSet<>();
            
            for (Item item : closure) {
                Symbol symbol = item.getSymbolAfterDot();
                
                if (symbol != null && !symbol.isTerminal()) {
                    // Get all productions with this non-terminal on the left side
                    for (Production prod : grammar.getProductionsFor(symbol)) {
                        Item newItem = new Item(prod, 0);
                        if (!closure.contains(newItem)) {
                            newItems.add(newItem);
                            changed = true;
                        }
                    }
                }
            }
            
            closure.addAll(newItems);
        } while (changed);
        
        return closure;
    }

    private ItemSet computeGoto(ItemSet state, Symbol symbol) {
        Set<Item> gotoItems = new HashSet<>();
        
        // Get all items where symbol appears after dot
        for (Item item : state.getItemsWithNextSymbol(symbol)) {
            gotoItems.add(item.advance());
        }
        
        // Compute closure
        Set<Item> closure = computeClosure(gotoItems);
        return new ItemSet(closure, -1); // State number will be assigned later
    }

    public List<ItemSet> getStates() {
        return new ArrayList<>(states);
    }

    public int getTransition(int stateNumber, Symbol symbol) {
        TransitionKey key = new TransitionKey(stateNumber, symbol);
        return transitions.getOrDefault(key, -1);
    }

    private static class TransitionKey {
        private final int stateNumber;
        private final Symbol symbol;

        public TransitionKey(int stateNumber, Symbol symbol) {
            this.stateNumber = stateNumber;
            this.symbol = symbol;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TransitionKey)) return false;
            TransitionKey other = (TransitionKey) obj;
            return stateNumber == other.stateNumber && symbol.equals(other.symbol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stateNumber, symbol);
        }
    }
}