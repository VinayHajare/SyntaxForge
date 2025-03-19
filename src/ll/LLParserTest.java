package ll;

import utils.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class LLParserTest {
    public static void main(String[] args) {
        try {
        	// Ensure console can handle UTF-8
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            
            // Read the grammar from file
            Grammar grammar = GrammarReader.readGrammar("D:\\Vinay Hajare\\Eclipse Project\\Parsers\\src\\utils\\grammar.txt");
            System.out.println("Grammar loaded successfully!");
            
            // Test FIRST and FOLLOW sets
            FirstFollowCalculator calculator = new FirstFollowCalculator(grammar);
            
            System.out.println("\n=== FIRST Sets ===");
            for (Symbol nonTerminal : grammar.getNonTerminals()) {
                Set<Symbol> firstSet = calculator.getFirst(nonTerminal);
                System.out.print("FIRST(" + nonTerminal + ") = { ");
                printSymbolSet(firstSet);
                System.out.println(" }");
            }
            
            System.out.println("\n=== FOLLOW Sets ===");
            for (Symbol nonTerminal : grammar.getNonTerminals()) {
                Set<Symbol> followSet = calculator.getFollow(nonTerminal);
                System.out.print("FOLLOW(" + nonTerminal + ") = { ");
                printSymbolSet(followSet);
                System.out.println(" }");
            }
            
            // Test the parsing table construction
            try {
                ParsingTable parsingTable = new ParsingTable(grammar, calculator);
                System.out.println("\nParsing table constructed successfully.");
                System.out.println("Grammar is LL(1)!");
            } catch (IllegalStateException e) {
                System.out.println("\nGrammar is not LL(1): " + e.getMessage());
                return;
            }
            
            // Create the parser
            LLParser parser = new LLParser(grammar);
            
            // Test parsing with various inputs
            String[][] testCases = {
                {"id", "Success"},                // Simple id
                {"id + id", "Success"},           // Addition
                {"id * id", "Success"},           // Multiplication
                {"id + id * id", "Success"},      // Addition and multiplication
                {"( id + id ) * id", "Success"},  // Parenthesized expression
                {"( id + )", "Failure"},          // Invalid expression
                {"* id", "Failure"}               // Invalid expression
            };
            
            System.out.println("\n=== Parsing Test Cases ===");
            for (String[] testCase : testCases) {
                String input = testCase[0];
                String expectedResult = testCase[1];
                
                List<Symbol> inputSymbols = tokenizeInput(input, grammar);
                boolean result = parser.parse(inputSymbols);
                
                System.out.println("Input: " + input);
                System.out.println("Expected: " + expectedResult);
                System.out.println("Actual: " + (result ? "Success" : "Failure"));
                System.out.println("Result: " + (expectedResult.equals(result ? "Success" : "Failure") ? "✓" : "✗"));
                System.out.println();
            }
            
        } catch (Exception e) {
            System.err.println("Error testing LL parser: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printSymbolSet(Set<Symbol> symbols) {
        boolean first = true;
        for (Symbol symbol : symbols) {
            if (!first) {
                System.out.print(", ");
            }
            System.out.print(symbol);
            first = false;
        }
    }
    
    private static List<Symbol> tokenizeInput(String input, Grammar grammar) {
        List<Symbol> symbols = new ArrayList<>();
        String[] tokens = input.split("\\s+");
        
        for (String token : tokens) {
            boolean found = false;
            for (Symbol terminal : grammar.getTerminals()) {
                if (terminal.getName().equals(token)) {
                    symbols.add(terminal);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                // Create a new terminal for this token
                Symbol terminal = new Symbol(token, true);
                symbols.add(terminal);
            }
        }
        
        return symbols;
    }
}