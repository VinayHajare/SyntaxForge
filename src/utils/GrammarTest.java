// GrammarTest.java
package utils;

import java.nio.charset.StandardCharsets;

public class GrammarTest {
    public static void main(String[] args) {
        try {
            // Ensure console can handle UTF-8
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            
            // Read the grammar from file
            Grammar grammar = GrammarReader.readGrammar("D:\\Vinay Hajare\\Eclipse Project\\Parsers\\src\\utils\\grammar.txt");
            
            // Print grammar components
            System.out.println("Grammar Analysis:");
            System.out.println("\nStart Symbol: " + grammar.getStartSymbol());
            
            System.out.println("\nNon-terminals:");
            for (Symbol nonTerminal : grammar.getNonTerminals()) {
                System.out.println("- " + nonTerminal.getName());
            }
            
            System.out.println("\nTerminals:");
            for (Symbol terminal : grammar.getTerminals()) {
                System.out.println("- " + terminal.getName());
            }
            
            System.out.println("\nProductions:");
            for (Production prod : grammar.getProductions()) {
                System.out.println(prod.toString());
            }
            
            // Test getting productions for specific non-terminals
            System.out.println("\nProductions for E:");
            Symbol eSymbol = new Symbol("E", false);
            for (Production prod : grammar.getProductionsFor(eSymbol)) {
                System.out.println(prod.toString());
            }
            
            // Validate that the grammar components are correctly identified
            validateGrammar(grammar);
            
        } catch (Exception e) {
            System.err.println("Error testing grammar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void validateGrammar(Grammar grammar) {
        // Verify expected components
        assert grammar.getStartSymbol().getName().equals("E") : "Wrong start symbol";
        assert grammar.getNonTerminals().size() == 5 : "Wrong number of non-terminals";
        assert grammar.getTerminals().size() == 4 : "Wrong number of terminals";
        
        // Check if specific symbols exist
        boolean hasE = false;
        boolean hasId = false;
        for (Symbol s : grammar.getNonTerminals()) {
            if (s.getName().equals("E")) hasE = true;
        }
        for (Symbol s : grammar.getTerminals()) {
            if (s.getName().equals("id")) hasId = true;
        }
        
        assert hasE : "Missing E non-terminal";
        assert hasId : "Missing id terminal";
        
        System.out.println("\nValidation completed successfully!");
    }
}