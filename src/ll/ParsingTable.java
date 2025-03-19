package ll;

import utils.*;
import java.util.*;

public class ParsingTable {
    private final Map<TableEntry, Production> table;
    private final Grammar grammar;
    private final FirstFollowCalculator calculator;
    private final Symbol EPSILON = new Symbol("ε", true);
    private final Symbol EOF = new Symbol("$", true);

    public ParsingTable(Grammar grammar, FirstFollowCalculator calculator) {
        this.grammar = grammar;
        this.calculator = calculator;
        this.table = new HashMap<>();
        constructTable();
    }

    private void constructTable() {
        for (Production prod : grammar.getProductions()) {
            Symbol nonTerminal = prod.getLeftSide();
            List<Symbol> rightSide = prod.getRightSide();

            // Get FIRST set of right-hand side
            Set<Symbol> firstSet = calculator.getFirst(rightSide);

            // For each terminal in FIRST set, add production to table
            for (Symbol terminal : firstSet) {
                if (!terminal.equals(EPSILON)) {
                    TableEntry entry = new TableEntry(nonTerminal, terminal);
                    if (table.containsKey(entry)) {
                        throw new IllegalStateException("Grammar is not LL(1): Conflict at " + entry);
                    }
                    table.put(entry, prod);
                }
            }

            // If ε is in FIRST set, add production to FOLLOW entries
            if (firstSet.contains(EPSILON)) {
                Set<Symbol> followSet = calculator.getFollow(nonTerminal);
                for (Symbol terminal : followSet) {
                    TableEntry entry = new TableEntry(nonTerminal, terminal);
                    if (table.containsKey(entry)) {
                        throw new IllegalStateException("Grammar is not LL(1): Conflict at " + entry);
                    }
                    table.put(entry, prod);
                }
            }
        }
    }

    public Production getProduction(Symbol nonTerminal, Symbol terminal) {
        return table.get(new TableEntry(nonTerminal, terminal));
    }

    private static class TableEntry {
        private final Symbol nonTerminal;
        private final Symbol terminal;

        public TableEntry(Symbol nonTerminal, Symbol terminal) {
            this.nonTerminal = nonTerminal;
            this.terminal = terminal;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TableEntry)) return false;
            TableEntry other = (TableEntry) obj;
            return nonTerminal.equals(other.nonTerminal) && terminal.equals(other.terminal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nonTerminal, terminal);
        }

        @Override
        public String toString() {
            return "(" + nonTerminal + ", " + terminal + ")";
        }
    }
}