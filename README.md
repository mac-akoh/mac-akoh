# Mini-Compiler for Arithmetic, If, and While

This project is a mini-compiler for a simple imperative language. It supports:
*   Variable declarations (`let`)
*   Variable assignments
*   Integer arithmetic operations (`+`, `-`, `*`, `/`)
*   Comparison operations (`<`, `>`, `==`, `!=`, `<=`, `>=`)
*   `if-else` conditional statements
*   `while` loops
*   `print` statements for outputting values.

The compiler processes source code through several stages:
1.  **Lexical Analysis (Lexer):** Converts source code into a stream of tokens.
2.  **Parsing (Parser):** Builds an Abstract Syntax Tree (AST) from the token stream.
3.  **Semantic Analysis:** Checks the AST for semantic errors (e.g., undeclared variables, type mismatches - though currently only integers are supported).
4.  **Interpretation (Interpreter):** Executes the AST directly.

The project is built using Java and Maven.

### Prerequisites

*   **Java Development Kit (JDK):** Version 11 or higher.
*   **Apache Maven:** For building the project and managing dependencies.
*   **Visual Studio Code (VS Code):** Recommended editor.
*   **VS Code Extension Pack for Java:** (Or individual extensions: Language Support for Java(TM) by Red Hat, Debugger for Java, Test Runner for Java, Maven for Java). This provides a seamless development experience in VS Code.

### Setup in VS Code

1.  **Clone the repository:**
    Replace `<repository_url>` with the actual URL of your repository.
    ```bash
    git clone <repository_url>
    cd <repository_directory_name> # e.g., cd mini-compiler
    ```
2.  **Open in VS Code:**
    *   Launch Visual Studio Code.
    *   Go to "File" > "Open Folder..." and select the cloned repository directory.
    *   VS Code, with the Java extensions installed, should automatically recognize it as a Maven project. Allow it to configure or import if prompted. This may take a few moments as it downloads dependencies and sets up the Java language server.

### Building the Project

*   The project is built using Apache Maven. VS Code's Java extension pack (specifically the "Maven for Java" extension) often handles this automatically when you open the project or save changes to `pom.xml`.
*   To build manually from the VS Code terminal (Ctrl+` or View > Terminal):
    ```bash
    mvn clean install
    ```
    This command will:
    *   `clean`: Remove any previous build artifacts (like the `target/` directory).
    *   `install`: Compile the source code, run unit tests, and package the application into a JAR file. The resulting JAR (e.g., `mini-compiler-1.0-SNAPSHOT.jar`) will be located in the `target/` directory.

### Running the Compiler/Interpreter

The main class for the application is `com.minicompiler.Main`.

*   **With a source file:**
    1.  Create a file with your mini-language code (e.g., `my_program.mc`). Refer to `GRAMMAR.md` for language syntax.
        Example `my_program.mc`:
        ```java
        // Your mini-language code
        let x = 10;
        let y = 20;
        let sum = 0;
        if (x < y) {
          sum = x + y;
          print sum; // Expected output: 30
        } else {
          print y;
        }

        let count = 0;
        while (count < 3) {
          print count; // Expected output: 0, 1, 2 (each on a new line)
          count = count + 1;
        }
        ```
    2.  Run from the VS Code terminal (ensure you have built the project first using `mvn clean install`):
        ```bash
        java -cp target/mini-compiler-1.0-SNAPSHOT.jar com.minicompiler.Main my_program.mc
        ```
        (Replace `mini-compiler-1.0-SNAPSHOT.jar` with the actual JAR file name from your `target/` directory if it differs, though `mini-compiler` is the artifactId in the `pom.xml`).

*   **With the default built-in sample program:**
    *   If you run the main class without any arguments, it will execute a pre-defined sample program.
    *   Run from the VS Code terminal:
        ```bash
        java -cp target/mini-compiler-1.0-SNAPSHOT.jar com.minicompiler.Main
        ```

*   **Running directly in VS Code (via Run and Debug panel):**
    *   Open `src/main/java/com/minicompiler/Main.java` in VS Code.
    *   You should see "Run" and "Debug" code lenses appear above the `main` method. Click "Run" to execute with the default internal program.
    *   To run with a specific file argument:
        1.  Go to the "Run and Debug" view (Ctrl+Shift+D or click the play icon with a bug in the activity bar).
        2.  If you don't have a `launch.json` file, click on "create a launch.json file" and select "Java".
        3.  Modify the generated `launch.json` (or create one under `.vscode/launch.json`) to add program arguments. Example configurations:
            ```json
            {
                "version": "0.2.0",
                "configurations": [
                    {
                        "type": "java",
                        "name": "Launch Main with File",
                        "request": "launch",
                        "mainClass": "com.minicompiler.Main",
                        "projectName": "mini-compiler", // Ensure this matches your Maven project's artifactId
                        "args": "my_program.mc" // Path to your source file (e.g., relative to workspace root)
                    },
                    {
                        "type": "java",
                        "name": "Launch Main (Default Program)",
                        "request": "launch",
                        "mainClass": "com.minicompiler.Main",
                        "projectName": "mini-compiler"
                    }
                ]
            }
            ```
            *Note: `projectName` should match the `<artifactId>` in your `pom.xml` (which is `mini-compiler`). You might need to adjust the path in `"args"` to be absolute or correctly relative to the workspace folder.*
        4.  Select the desired configuration (e.g., "Launch Main with File") from the dropdown in the "Run and Debug" panel and click the green play button (Start Debugging).

### Language Grammar

*   For a detailed description of the mini-language syntax, keywords, and examples, please refer to the `GRAMMAR.md` file located in the root of the project.

### Running Tests

*   Unit tests are written using JUnit 5 and are located in the `src/test/java` directory.
*   **From VS Code Test Explorer:**
    *   The "Test Explorer" view (usually a beaker icon in the activity bar, or accessible via "View" > "Test") will list all discovered tests.
    *   You can run individual tests, test classes, or all tests using the play buttons within this view. Results will be displayed directly in VS Code.
*   **From the Maven command line (in VS Code terminal):**
    *   To run all tests:
        ```bash
        mvn test
        ```
    *   Maven will compile the test classes and execute them. A summary of the test results will be displayed in the terminal. Test reports can also be found in `target/surefire-reports/`.

This `README.md` provides a comprehensive guide to setting up, building, running, and testing the mini-compiler. Remember to replace placeholder values like `<repository_url>` with actual information.I have created the `README.md` file in the root of the project with the specified instructions.
The content covers:
- A brief project description.
- Prerequisites (JDK 11+, Maven, VS Code with Java Extension Pack).
- Setup instructions for cloning and opening in VS Code.
- Building the project using Maven, both automatically via VS Code and manually.
- Running the compiler/interpreter:
    - With a source file via command line.
    - With the default built-in sample program via command line.
    - Directly in VS Code using code lenses and `launch.json` configurations for file arguments.
- A reference to `GRAMMAR.md` for language syntax.
- Instructions for running tests using the VS Code Test Explorer and Maven from the command line.

The instructions are detailed and aim to be clear and accurate for a user to get started with the project.
I will now submit the subtask report.
