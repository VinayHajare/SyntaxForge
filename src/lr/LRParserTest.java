package lr;

import utils.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class LRParserTest {
    public static void main(String[] args) {
        try {
            // Ensure console can handle UTF-8
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            
            // Read the grammar from file
            Grammar grammar = GrammarReader.readGrammar("D:\\Vinay Hajare\\Eclipse Project\\Parsers\\src\\utils\\grammar.txt");
            System.out.println("Grammar loaded successfully!");
            
            // Display the grammar
            System.out.println("\n=== Grammar ===");
            for (Production production : grammar.getProductions()) {
                System.out.println(production);
            }
            
            // Create canonical collection and display states
            System.out.println("\n=== LR(0) Items and Canonical Collection ===");
            CanonicalCollection collection = new CanonicalCollection(grammar);
            for (ItemSet state : collection.getStates()) {
                System.out.println(state);
            }
            
            // Test the parsing table construction
            try {
                LRParsingTable parsingTable = new LRParsingTable(grammar, collection);
                System.out.println("LR parsing table constructed successfully.");
                System.out.println("Grammar is SLR(1)!");
                
                // Display some parts of the parsing table for verification
                displayParsingTableSample(parsingTable, collection, grammar);
                
                // Create the parser
                LRParser parser = new LRParser(grammar);
                
                // Test parsing with various inputs
                String[][] testCases = {
                    {"id", "Success"},                // Simple id
                    {"id + id", "Success"},           // Addition
                    {"id * id", "Success"},           // Multiplication
                    {"id + id * id", "Success"},      // Addition and multiplication
                    {"(id + id) * id", "Success"},  // Parenthesized expression
                    {"(id +)", "Failure"},          // Invalid expression
                    {"*id", "Failure"}               // Invalid expression
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
            } catch (IllegalStateException e) {
                System.out.println("\nGrammar is not SLR(1): " + e.getMessage());
                return;
            }
            
        } catch (Exception e) {
            System.err.println("Error testing LR parser: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void displayParsingTableSample(LRParsingTable parsingTable, 
                                                 CanonicalCollection collection,
                                                 Grammar grammar) {
        System.out.println("\n=== Parsing Table Sample ===");
        System.out.println("State | Action (id, +, *, (, )) | Goto (E, T, F, E', T')");
        System.out.println("--------------------------------------------------");
        
        Symbol id = new Symbol("id", true);
        Symbol plus = new Symbol("+", true);
        Symbol mult = new Symbol("*", true);
        Symbol lParen = new Symbol("(", true);
        Symbol rParen = new Symbol(")", true);
        Symbol eof = new Symbol("$", true);
        
        Symbol E = new Symbol("E", false);
        Symbol EPrime = new Symbol("E'", false);
        Symbol T = new Symbol("T", false);
        Symbol TPrime = new Symbol("T'", false);
        Symbol F = new Symbol("F", false);
        
        for (int i = 0; i < collection.getStates().size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-5d | ", i));
            
            // Actions
            sb.append("id:");
            appendAction(sb, parsingTable.getAction(i, id));
            sb.append(", +:");
            appendAction(sb, parsingTable.getAction(i, plus));
            sb.append(", *:");
            appendAction(sb, parsingTable.getAction(i, mult));
            sb.append(", (:");
            appendAction(sb, parsingTable.getAction(i, lParen));
            sb.append(", ):");
            appendAction(sb, parsingTable.getAction(i, rParen));
            sb.append(", $:");
            appendAction(sb, parsingTable.getAction(i, eof));
            
            sb.append(" | ");
            
            // Gotos
            sb.append("E:");
            appendGoto(sb, parsingTable.getGoto(i, E));
            sb.append(", T:");
            appendGoto(sb, parsingTable.getGoto(i, T));
            sb.append(", F:");
            appendGoto(sb, parsingTable.getGoto(i, F));
            sb.append(", E':");
            appendGoto(sb, parsingTable.getGoto(i, EPrime));
            sb.append(", T':");
            appendGoto(sb, parsingTable.getGoto(i, TPrime));
            
            System.out.println(sb.toString());
        }
    }
    
    private static void appendAction(StringBuilder sb, LRParsingTable.Action action) {
        if (action == null) {
            sb.append("err");
        } else {
            sb.append(action.toString());
        }
    }
    
    private static void appendGoto(StringBuilder sb, int gotoValue) {
        if (gotoValue == -1) {
            sb.append("-");
        } else {
            sb.append(gotoValue);
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