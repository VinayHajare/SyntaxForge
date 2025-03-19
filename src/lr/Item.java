package lr;

import utils.*;
import java.util.*;

public class Item {
    private final Production production;
    private final int dotPosition;

    public Item(Production production, int dotPosition) {
        this.production = production;
        this.dotPosition = dotPosition;
    }

    public Production getProduction() {
        return production;
    }

    public int getDotPosition() {
        return dotPosition;
    }

    public boolean isComplete() {
        return dotPosition >= production.getRightSide().size();
    }

    public Symbol getSymbolAfterDot() {
        if (isComplete()) {
            return null;
        }
        return production.getRightSide().get(dotPosition);
    }

    public Item advance() {
        if (isComplete()) {
            throw new IllegalStateException("Cannot advance a complete item");
        }
        return new Item(production, dotPosition + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) return false;
        Item other = (Item) obj;
        return production.equals(other.production) && dotPosition == other.dotPosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(production, dotPosition);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(production.getLeftSide()).append(" → ");
        
        List<Symbol> rightSide = production.getRightSide();
        for (int i = 0; i < rightSide.size(); i++) {
            if (i == dotPosition) {
                sb.append("• ");
            }
            sb.append(rightSide.get(i)).append(" ");
        }
        
        if (dotPosition == rightSide.size()) {
            sb.append("•");
        }
        
        return sb.toString().trim();
    }
}