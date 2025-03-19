package utils;


public class Symbol {
    private final String name;
    private final boolean isTerminal;
    public static final String EPSILON = "ε";
    public static final String ARROW = "→";

    public Symbol(String name, boolean isTerminal) {
        this.name = name;
        this.isTerminal = isTerminal;
    }

    public String getName() {
        return name;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Symbol)) return false;
        Symbol other = (Symbol) obj;
        return name.equals(other.name) && isTerminal == other.isTerminal;
    }

    @Override
    public int hashCode() {
        return 24 * name.hashCode() + (isTerminal ? 1 : 0);
    }

    @Override
    public String toString() {
        return name.equals("epsilon") ? EPSILON : name;
    }
}