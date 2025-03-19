package lr;

import utils.*;
import java.util.*;

public class LRParser {
    private final Grammar grammar;
    private final LRParsingTable parsingTable;
    private final Symbol EOF = new Symbol("$", true);

    public LRParser(Grammar grammar) {
        this.grammar = grammar;
        CanonicalCollection collection = new CanonicalCollection(grammar);
        this.parsingTable = new LRParsingTable(grammar, collection);
    }

    public boolean parse(List<Symbol> input) {
        // Add EOF to input
        input = new ArrayList<>(input);
        input.add(EOF);
        
        // Initialize stacks
        Stack<Integer> stateStack = new Stack<>();
        Stack<Symbol> symbolStack = new Stack<>();
        stateStack.push(0);
        
        int inputIndex = 0;
        
        while (true) {
            int currentState = stateStack.peek();
            Symbol currentSymbol = input.get(inputIndex);
            
            LRParsingTable.Action action = parsingTable.getAction(currentState, currentSymbol);
            
            if (action == null) {
                System.out.println("Error: No action defined for state " + currentState + 
                                  " and symbol " + currentSymbol);
                return false;
            }
            
            switch (action.getType()) {
                case SHIFT:
                    // Shift: Push symbol and next state
                    symbolStack.push(currentSymbol);
                    stateStack.push(action.getValue());
                    inputIndex++;
                    break;
                    
                case REDUCE:
                    // Reduce: Apply production rule
                    Production prod = grammar.getProductions().get(action.getValue());
                    
                    // Pop states and symbols for right-hand side
                    for (int i = 0; i < prod.getRightSide().size(); i++) {
                        stateStack.pop();
                        symbolStack.pop();
                    }
                    
                    // Push left-hand side non-terminal
                    symbolStack.push(prod.getLeftSide());
                    
                    // GOTO
                    int nextState = parsingTable.getGoto(stateStack.peek(), prod.getLeftSide());
                    stateStack.push(nextState);
                    break;
                    
                case ACCEPT:
                    // Parsing successful
                    return true;
                    
                case ERROR:
                    System.out.println("Error: Invalid syntax at symbol " + currentSymbol);
                    return false;
                    
                default:
                    System.out.println("Error: Unknown action type");
                    return false;
            }
        }
    }
}