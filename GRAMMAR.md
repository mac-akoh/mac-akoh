# Mini-Language Grammar

This document defines the grammar for the mini-language.

## 1. Comments

Single-line comments start with `//` and extend to the end of the line.

Example:
```
// This is a single-line comment
let x = 10; // Another comment at the end of a line
```

## 2. Variable Declarations

Variables are declared using the `let` keyword, followed by the variable name, an equals sign, and an expression. Currently, all variables are of integer type. Variable names must start with a letter and can be followed by letters or digits.

Syntax:
`let <variable_name> = <expression>;`

Example:
```
let myVariable = 100;
let anotherVar = myVariable + 5;
```

BNF-like notation:
```bnf
variable_declaration ::= "let" IDENTIFIER "=" expression ";"
IDENTIFIER ::= LETTER (LETTER | DIGIT)*
LETTER ::= "a"..."z" | "A"..."Z"
DIGIT ::= "0"..."9"
```

## 3. Assignment

Assigns a new value to a previously declared variable.

Syntax:
`<variable_name> = <expression>;`

Example:
```
let x = 5;
x = x + 10; // x is now 15
```

BNF-like notation:
```bnf
assignment ::= IDENTIFIER "=" expression ";"
```

## 4. Arithmetic Expressions

Arithmetic expressions involve integers, variables, and the operators `+`, `-`, `*`, `/`. Parentheses `()` can be used for grouping. Standard operator precedence applies (`*`, `/` have higher precedence than `+`, `-`).

Syntax:
An expression can be a term, or an addition/subtraction of terms.
A term can be a factor, or a multiplication/division of factors.
A factor can be an integer, a variable, or an expression in parentheses.

Example:
```
let a = 10;
let b = 20;
let result = (a * 2) + (b / 3) - 5;
let complex = (a + b) * (100 / (result + 1));
```

BNF-like notation:
```bnf
expression ::= term (("+" | "-") term)*
term       ::= factor (("*" | "/") factor)*
factor     ::= INTEGER | IDENTIFIER | "(" expression ")"
INTEGER    ::= DIGIT+
```

## 5. If-Else Statements

Allows conditional execution of blocks of statements. The `else` block is optional.

Syntax:
```
if (<condition>) {
    <statements>
} else {
    <statements>
}
```
or
```
if (<condition>) {
    <statements>
}
```

Conditions are simple comparisons between two expressions.

Syntax for condition:
`<expression> <comparison_operator> <expression>`

Comparison operators: `<`, `>`, `==`, `!=`, `<=`, `>=`

Example:
```
let count = 5;
let limit = 10;
if (count < limit) {
    print(count);
    count = count + 1;
} else {
    print(limit);
}

if (count == 10) {
    print("Count reached the limit!");
}
```

BNF-like notation:
```bnf
if_statement ::= "if" "(" condition ")" "{" statement* "}"
                 ("else" "{" statement* "}")?
condition    ::= expression comparison_operator expression
comparison_operator ::= "<" | ">" | "==" | "!=" | "<=" | ">="
statement    ::= variable_declaration | assignment | if_statement | while_loop | print_statement | comment
```
*(Note: `statement*` means zero or more statements)*

## 6. While Loops

Executes a block of statements repeatedly as long as a condition is true.

Syntax:
```
while (<condition>) {
    <statements>
}
```

Conditions are the same as in if-statements.

Example:
```
let i = 0;
let sum = 0;
while (i < 5) {
    sum = sum + i;
    i = i + 1;
    // This is a comment inside a loop
}
print(sum); // Output will be the sum of numbers from 0 to 4
```

BNF-like notation:
```bnf
while_loop ::= "while" "(" condition ")" "{" statement* "}"
```

## 7. Print Statement

Prints the value of an expression to the output.

Syntax:
`print(<expression>);`

Example:
```
let val = 42;
print(val);
print(val * 2 + 1);
print((10 + 20) / 3);
```

BNF-like notation:
```bnf
print_statement ::= "print" "(" expression ")" ";"
```

## General Structure

A program is a sequence of statements.

BNF-like notation:
```bnf
program ::= statement*
```

This grammar defines the basic structure and syntax of the mini-language.
The language is statically typed (only integers for now) and expects semicolons at the end of most statements.
Whitespace (spaces, tabs, newlines) is generally ignored between tokens, except where it affects comment parsing or string literals (if they were supported).
```
