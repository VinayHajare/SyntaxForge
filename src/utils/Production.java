package utils;

import java.util.ArrayList;
import java.util.List;

public class Production {
    private final Symbol leftSide;
    private final List<Symbol> rightSide;

    public Production(Symbol leftSide, List<Symbol> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = new ArrayList<>(rightSide);
    }

    public Symbol getLeftSide() {
        return leftSide;
    }

    public List<Symbol> getRightSide() {
        return new ArrayList<>(rightSide);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftSide).append(" ").append(Symbol.ARROW).append(" ");
        if (rightSide.isEmpty()) {
            sb.append(Symbol.EPSILON);
        } else {
            for (Symbol symbol : rightSide) {
                sb.append(symbol).append(" ");
            }
        }
        return sb.toString().trim();
    }
}