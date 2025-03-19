package lr;

import utils.*;
import java.util.*;

public class LRParsingTable {
    private final Grammar grammar;
    private final CanonicalCollection canonicalCollection;
    private final Map<ActionKey, Action> actionTable;
    private final Map<GotoKey, Integer> gotoTable;
    private final Symbol EOF = new Symbol("$", true);

    public LRParsingTable(Grammar grammar, CanonicalCollection canonicalCollection) {
        this.grammar = grammar;
        this.canonicalCollection = canonicalCollection;
        this.actionTable = new HashMap<>();
        this.gotoTable = new HashMap<>();
        constructTable();
    }

    private void constructTable() {
        // For simplicity, we'll implement SLR(1) parsing
        // Get all states from canonical collection
        List<ItemSet> states = canonicalCollection.getStates();
        
        for (ItemSet state : states) {
            int stateNum = state.getStateNumber();
            
            // Process each item in the state
            for (Item item : state.getItems()) {
                Symbol symbolAfterDot = item.getSymbolAfterDot();
                
                if (symbolAfterDot != null) {
                    // Case 1: [A -> α•aβ] => shift
                    if (symbolAfterDot.isTerminal()) {
                        int nextState = canonicalCollection.getTransition(stateNum, symbolAfterDot);
                        if (nextState != -1) {
                            ActionKey key = new ActionKey(stateNum, symbolAfterDot);
                            Action action = new Action(ActionType.SHIFT, nextState);
                            checkConflict(key, action);
                            actionTable.put(key, action);
                        }
                    } 
                    // For GOTO table
                    else {
                        int nextState = canonicalCollection.getTransition(stateNum, symbolAfterDot);
                        if (nextState != -1) {
                            gotoTable.put(new GotoKey(stateNum, symbolAfterDot), nextState);
                        }
                    }
                } else {
                    // Case 2: [A -> α•] => reduce
                    Production prod = item.getProduction();
                    Symbol leftSide = prod.getLeftSide();
                    
                    // Handle accepting state specially
                    if (leftSide.getName().equals(grammar.getStartSymbol().getName() + "'")) {
                        ActionKey key = new ActionKey(stateNum, EOF);
                        Action action = new Action(ActionType.ACCEPT, 0);
                        checkConflict(key, action);
                        actionTable.put(key, action);
                    } else {
                        // Find production index for reduce action
                        int prodIndex = findProductionIndex(prod);
                        
                        // For SLR(1), we use FOLLOW set
                        Set<Symbol> followSet = computeFollowSet(leftSide);
                        for (Symbol symbol : followSet) {
                            ActionKey key = new ActionKey(stateNum, symbol);
                            Action action = new Action(ActionType.REDUCE, prodIndex);
                            checkConflict(key, action);
                            actionTable.put(key, action);
                        }
                    }
                }
            }
        }
    }

    private void checkConflict(ActionKey key, Action newAction) {
        if (actionTable.containsKey(key)) {
            Action existingAction = actionTable.get(key);
            if (!existingAction.equals(newAction)) {
                throw new IllegalStateException("Grammar is not SLR(1): Conflict at state " +
                                               key.stateNumber + " for symbol " + key.symbol);
            }
        }
    }

    private int findProductionIndex(Production prod) {
        List<Production> productions = grammar.getProductions();
        for (int i = 0; i < productions.size(); i++) {
            if (productions.get(i).equals(prod)) {
                return i;
            }
        }
        return -1;
    }

    private Set<Symbol> computeFollowSet(Symbol nonTerminal) {
        // This is a simplified version
        // In a real parser, you should use a proper FOLLOW set calculation
        Set<Symbol> followSet = new HashSet<>();
        followSet.add(EOF);
        for (Symbol terminal : grammar.getTerminals()) {
            followSet.add(terminal);
        }
        return followSet;
    }

    public Action getAction(int state, Symbol symbol) {
        return actionTable.get(new ActionKey(state, symbol));
    }

    public int getGoto(int state, Symbol nonTerminal) {
        return gotoTable.getOrDefault(new GotoKey(state, nonTerminal), -1);
    }

    private static class ActionKey {
        private final int stateNumber;
        private final Symbol symbol;

        public ActionKey(int stateNumber, Symbol symbol) {
            this.stateNumber = stateNumber;
            this.symbol = symbol;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ActionKey)) return false;
            ActionKey other = (ActionKey) obj;
            return stateNumber == other.stateNumber && symbol.equals(other.symbol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stateNumber, symbol);
        }
    }

    private static class GotoKey {
        private final int stateNumber;
        private final Symbol symbol;

        public GotoKey(int stateNumber, Symbol symbol) {
            this.stateNumber = stateNumber;
            this.symbol = symbol;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof GotoKey)) return false;
            GotoKey other = (GotoKey) obj;
            return stateNumber == other.stateNumber && symbol.equals(other.symbol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stateNumber, symbol);
        }
    }

    public static class Action {
        private final ActionType type;
        private final int value;

        public Action(ActionType type, int value) {
            this.type = type;
            this.value = value;
        }

        public ActionType getType() {
            return type;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Action)) return false;
            Action other = (Action) obj;
            return type == other.type && value == other.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value);
        }

        @Override
        public String toString() {
            return type + "(" + value + ")";
        }
    }

    public enum ActionType {
        SHIFT, REDUCE, ACCEPT, ERROR
    }
}