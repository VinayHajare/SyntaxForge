package ll;

import utils.*;
import java.util.*;

public class LLParser {
    private final Grammar grammar;
    private final ParsingTable parsingTable;
    private final Symbol EOF = new Symbol("$", true);

    public LLParser(Grammar grammar) {
        this.grammar = grammar;
        FirstFollowCalculator calculator = new FirstFollowCalculator(grammar);
        this.parsingTable = new ParsingTable(grammar, calculator);
    }

    public boolean parse(List<Symbol> input) {
        // Add EOF to input
        input = new ArrayList<>(input);
        input.add(EOF);
        
        // Initialize stack with EOF and start symbol
        Stack<Symbol> stack = new Stack<>();
        stack.push(EOF);
        stack.push(grammar.getStartSymbol());
        
        int inputIndex = 0;
        
        while (!stack.isEmpty()) {
            Symbol top = stack.peek();
            Symbol currentInput = input.get(inputIndex);
            
            if (top.isTerminal()) {
                if (top.equals(currentInput)) {
                    stack.pop();
                    inputIndex++;
                } else {
                    return false; // Parsing error
                }
            } else {
                Production prod = parsingTable.getProduction(top, currentInput);
                if (prod == null) {
                    return false; // Parsing error
                }
                
                stack.pop();
                // Push right-hand side symbols in reverse order (only if not epsilon)
                List<Symbol> rightSide = prod.getRightSide();
                if (!rightSide.isEmpty()) {
                    for (int i = rightSide.size() - 1; i >= 0; i--) {
                        stack.push(rightSide.get(i));
                    }
                }
                // If it's an epsilon production, we just pop the non-terminal without pushing anything
            }
        }
        
        return inputIndex == input.size();
    }
}