package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GrammarReader {
    private static final String PRODUCTION_SEPARATOR = "->";
    private static final String ALTERNATE_SEPARATOR = "|";

    public static Grammar readGrammar(String filePath) throws IOException {
        Set<Symbol> terminals = new HashSet<>();
        Set<Symbol> nonTerminals = new HashSet<>();
        List<Production> productions = new ArrayList<>();
        Symbol startSymbol = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstProduction = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                String[] parts = line.split(PRODUCTION_SEPARATOR);
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid production format: " + line);
                }

                String leftSide = parts[0].trim();
                String[] alternatives = parts[1].trim().split("\\" + ALTERNATE_SEPARATOR);

                // Create non-terminal for left side
                Symbol leftSymbol = new Symbol(leftSide, false);
                nonTerminals.add(leftSymbol);

                if (firstProduction) {
                    startSymbol = leftSymbol;
                    firstProduction = false;
                }

                // Process each alternative in the right side
                for (String alternative : alternatives) {
                    alternative = alternative.trim();
                    if (alternative.isEmpty()) {
                        // This is an epsilon production
                        productions.add(new Production(leftSymbol, new ArrayList<>()));
                        continue;
                    }

                    String[] rightSideSymbols = alternative.split("\\s+");
                    List<Symbol> rightSide = new ArrayList<>();

                    for (String sym : rightSideSymbols) {
                        // Handle epsilon explicitly
                        if (sym.equals("ε") || sym.equals("epsilon")) {
                            continue; // Skip epsilon, resulting in empty right side
                        }

                        // Assume symbols starting with uppercase are non-terminals
                        boolean isTerminal = !Character.isUpperCase(sym.charAt(0));
                        Symbol symbol = new Symbol(sym, isTerminal);

                        if (isTerminal) {
                            terminals.add(symbol);
                        } else {
                            nonTerminals.add(symbol);
                        }

                        rightSide.add(symbol);
                    }

                    productions.add(new Production(leftSymbol, rightSide));
                }
            }
        }

        if (startSymbol == null) {
            throw new IllegalArgumentException("No productions found in grammar file");
        }

        return new Grammar(terminals, nonTerminals, productions, startSymbol);
    }
}