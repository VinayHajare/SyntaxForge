package lr;

import utils.*;
import java.util.*;

public class ItemSet {
    private final Set<Item> items;
    private final int stateNumber;

    public ItemSet(Set<Item> items, int stateNumber) {
        this.items = new HashSet<>(items);
        this.stateNumber = stateNumber;
    }

    public Set<Item> getItems() {
        return new HashSet<>(items);
    }

    public int getStateNumber() {
        return stateNumber;
    }

    public Set<Symbol> getNextSymbols() {
        Set<Symbol> symbols = new HashSet<>();
        for (Item item : items) {
            Symbol symbol = item.getSymbolAfterDot();
            if (symbol != null) {
                symbols.add(symbol);
            }
        }
        return symbols;
    }

    public Set<Item> getItemsWithNextSymbol(Symbol symbol) {
        Set<Item> result = new HashSet<>();
        for (Item item : items) {
            if (item.getSymbolAfterDot() != null && item.getSymbolAfterDot().equals(symbol)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemSet)) return false;
        ItemSet other = (ItemSet) obj;
        return items.equals(other.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State ").append(stateNumber).append(":\n");
        for (Item item : items) {
            sb.append("  ").append(item).append("\n");
        }
        return sb.toString();
    }
}
