package com.minicompiler;

import com.minicompiler.ast.Program;
import com.minicompiler.interpreter.Environment;
import com.minicompiler.interpreter.Interpreter;
import com.minicompiler.interpreter.MCObject;
import com.minicompiler.lexer.Lexer;
import com.minicompiler.parser.Parser;
import com.minicompiler.semantics.SemanticAnalyzer;
import com.minicompiler.semantics.SymbolTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final String DEFAULT_PROGRAM =
            "// Default program if no file is provided\n" +
            "let x = 10;\n" +
            "let y = 20;\n" +
            "if (x < y) {\n" +
            "    print(x + y); // Should print 30\n" +
            "    let z = x * y;\n" +
            "    y = z + 1; // y becomes 201\n" +
            "    print(y); // Should print 201\n" +
            "} else {\n" +
            "    print(y - x);\n" +
            "}\n" +
            "let count = 0;\n" +
            "while (count < 3) {\n" +
            "    print(count); // Should print 0, 1, 2\n" +
            "    count = count + 1;\n" +
            "    let inner_loop_var = count * 10;\n" +
            "    print(inner_loop_var); // Should print 10, 20, 30\n" +
            "}\n" +
            "print(count); // Should print 3\n" +
            "x = (1 + 2) * 3 / (1 + 1); // x becomes 4 (integer division)\n" +
            "print(x); // Should print 4\n" +
            "let val = 0;\n" +
            "if (val == 0) { print(1000); } else { print(2000); } // Should print 1000\n" +
            "if (val != 0) { print(3000); } else { print(4000); } // Should print 4000\n" +
            "\n" +
            "// To test errors, uncomment these lines in a file or the default program:\n" +
            "// print(a); // Semantic error: Undeclared variable test\n" +
            "// let x = 5; // Semantic error: Redeclaration test (if x already declared in scope)\n" +
            "// b = 100; // Semantic error: Assignment to undeclared test\n" +
            "// print(10 / 0); // Runtime error: division by zero\n" +
            "// let c = 1 + \"string\"; // Parser error (if strings not supported) or Semantic/Runtime type error\n";


    public static void main(String[] args) {
        String inputProgram;
        String programSource = "default hardcoded program";

        if (args.length > 0) {
            String filePath = args[0];
            programSource = "file: " + filePath;
            try {
                inputProgram = new String(Files.readAllBytes(Paths.get(filePath)));
                System.out.println("Reading program from: " + filePath);
            } catch (IOException e) {
                System.err.println("Error reading file '" + filePath + "': " + e.getMessage());
                System.exit(1); // General I/O error
                return;
            }
        } else {
            System.out.println("No file argument provided. Using default internal program.");
            inputProgram = DEFAULT_PROGRAM;
        }

        System.out.println("\nInput Program (" + programSource + "):");
        System.out.println("--------------------");
        System.out.println(inputProgram);
        System.out.println("--------------------\n");

        List<String> parserErrors = new ArrayList<>();
        List<String> semanticErrors = new ArrayList<>();
        List<String> runtimeErrors = new ArrayList<>();

        // --- Lexing and Parsing ---
        System.out.println("Lexing and Parsing...");
        Lexer lexer = new Lexer(inputProgram);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        parserErrors.addAll(parser.getErrors());

        if (!parserErrors.isEmpty()) {
            System.out.println("Parser Errors:");
            for (String error : parserErrors) {
                System.out.println("- " + error);
            }
            System.exit(10); // Exit code for parser errors
            return;
        }
        System.out.println("Parsing completed successfully.");

        // --- Semantic Analysis ---
        System.out.println("\nPerforming Semantic Analysis...");
        SymbolTable globalTable = new SymbolTable();
        // Pass a separate list for semantic errors to distinguish them
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(semanticErrors);
        semanticAnalyzer.analyze(program, globalTable);

        if (!semanticErrors.isEmpty()) {
            System.out.println("Semantic Errors:");
            for (String error : semanticErrors) {
                System.out.println("- " + error);
            }
            System.exit(20); // Exit code for semantic errors
            return;
        }
        System.out.println("Semantic analysis completed successfully.");

        // --- Interpreting Program ---
        System.out.println("\nInterpreting Program...");
        System.out.println("--------------------");
        System.out.println("Program Output:");

        // Pass a separate list for runtime errors
        Interpreter interpreter = new Interpreter(runtimeErrors);
        Environment globalEnv = new Environment();
        MCObject result = interpreter.eval(program, globalEnv);

        System.out.println("--------------------");

        if (!runtimeErrors.isEmpty()) {
            System.out.println("Runtime Errors:");
            for (String error : runtimeErrors) {
                System.out.println("- " + error);
            }
            if (result != null && result.type() == com.minicompiler.interpreter.ObjectType.ERROR) {
                 // Ensure the final error object (if any) is also noted, though newError should add it to the list.
                 // System.out.println("- Final evaluation resulted in: " + result.inspect());
            }
            System.exit(30); // Exit code for runtime errors
            return;
        }

        System.out.println("Program executed successfully.");
        if (result != null && result.type() != com.minicompiler.interpreter.ObjectType.NULL) {
            System.out.println("Final value of the program: " + result.inspect());
        }
    }
}
