# 🔠 SyntaxForge: A Parser Generator Framework 🔍

## Project Overview

SyntaxForge is a comprehensive parser generator framework that implements various parsing techniques used in compiler design. The project serves as both an educational tool for understanding parsing algorithms and a practical utility for building parsers.

### 🎯 Features

- ✅ Grammar specification through simple text files
- ✅ LL(1) Parser implementation
  - Left recursion elimination
  - Left factoring
  - FIRST and FOLLOW set computation
  - Parsing table construction
  - Predictive parsing
- ✅ LR(0)/SLR Parser implementation
  - Items and states generation
  - ACTION and GOTO table construction
  - Shift-reduce parsing
- ✅ Parse tree visualization
- ✅ Detailed error reporting

## 📚 Project Structure

```
src/
├── utils/        # Common utilities shared between parsers
│   ├── Grammar.java              # Grammar representation
│   ├── Production.java           # Production rules
│   ├── Symbol.java               # Grammar symbols (terminals/non-terminals)
│   ├── GrammarReader.java        # File parser for grammar specifications
│   └── ParseTree.java            # Parse tree representation
│
├── ll/           # LL(1) parser implementation
│   ├── LLParser.java             # Main LL parser class
│   ├── FirstFollowCalculator.java # FIRST/FOLLOW set computation
│   ├── LeftRecursionEliminator.java # Left recursion handling
│   ├── LeftFactorer.java         # Left factoring implementation
│   └── ParsingTable.java         # LL(1) parsing table
│
└── lr/           # LR parser implementation
    ├── LRParser.java             # Main LR parser class
    ├── Item.java                 # LR(0) items
    ├── State.java                # States in the LR state machine
    ├── ActionTable.java          # ACTION table
    └── GotoTable.java            # GOTO table
```

## 🚀 Getting Started

### Prerequisites

- Java JDK 11 or higher
- Maven or Gradle (for building)

### Installation

```bash
# Clone the repository
git clone https://github.com/VinayHajare/SyntaxForge.git
cd SyntaxForge

# Build the project
mvn clean install
```

### Usage

#### Specifying Grammars

Create a grammar file with production rules:

```
# Example grammar file: arithmetic.txt
E -> T E'
E' -> + T E' | ε
T -> F T'
T' -> * F T' | ε
F -> ( E ) | id
```

#### Running the LL(1) Parser

```bash
java -cp target/syntaxforge.jar com.syntaxforge.Main --parser ll --grammar path/to/grammar.txt --input "id + id * id"
```

#### Running the LR Parser

```bash
java -cp target/syntaxforge.jar com.syntaxforge.Main --parser lr --grammar path/to/grammar.txt --input "id + id * id"
```

## 📝 Grammar Format

- Each production must be on a separate line
- Use `->`to separate the left and right sides of a production
- Use `|` to separate alternatives
- Use `ε` or `epsilon` for the empty string
- Use `#` for comments

## 🔄 Workflow Example

1. Parse the grammar file
2. Check and eliminate left recursion and perform left factoring (for LL)
3. Compute FIRST and FOLLOW sets (for LL)
4. Build parsing tables
5. Parse the input string
6. Generate and display the parse tree

## 🔮 Future Work

- 🧩 Operator precedence parser implementation
- 🔧 LALR(1) parser implementation
- 📊 Automatic syntax diagram generation
- 🔍 Recovery strategies for syntax errors
- 🖥️ Integration with lexical analyzer to form a complete front-end
- 🧪 Unit testing framework for grammar validation
- 📱 GUI for interactive parse tree visualization
- 📦 AST (Abstract Syntax Tree) construction
- 🔌 Plugin system for custom semantic actions

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📚 References

- Compilers: Principles, Techniques, and Tools (Dragon Book)
- Modern Compiler Implementation in Java
- Engineering a Compiler
