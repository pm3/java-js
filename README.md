
# java-js

**java-js** is a minimalist implementation of a JavaScript subset for the **JVM (Java Virtual Machine)** environment. The engine is syntax-compatible with a selected subset of JavaScript that can be executed in a JavaScript engine, but not vice versa.

---
## Collaboration with Claude 3.7 (Anthropic)

This project was developed in collaboration with the Claude 3.7 language model from Anthropic.

The division of work between Claude 3.7 and Peter was as follows:

| Component     | Claude 3.7 | Peter |
|---------------|------------|-------|
| Lexer         | 90%        | 10%   |
| Parser        | 80%        | 20%   |
| AstNodes      | 70%        | 30%   |
| Types & Ops   | 70%        | 30%   |
| SDK Functions | 80%        | 20%   |
| Tests         | 100%       | 0%    |
| Documentation | 90%        | 10%   |

Claude 3.7 was instrumental in rapidly prototyping and implementing core logic, while Peter focused on architectural decisions, integration, and refining the generated code where needed.

---

## 📑 Description and Features

All scripts run in `"use strict"` mode and the engine uses native Java objects.

### Supported Types:
- `null`
- `undefined`
- `boolean`
- `number` (`int`, `long`, `double`)
- `array` (Java `List`)
- `object` (Java `Map`)
- `function` (custom `JsFunction` or native `BiFunction<Scope,List<Object>, Object>>`)
- `Error`

### Supported Syntax and Features:

- **Variables:** `let`, `const`
- **Operators:** `=`, `+=`, `-=`, `*=`, `/=`, `%=`, `+`, `-`, `*`, `/`, `%`
- **Unary Operations:** `+`, `-`, `!`, `typeof`
- **Increment/Decrement:** `++`, `--` (prefix and postfix)
- **Conditionals:** `if/else`, `switch/case/default`, ternary operator `a ? b : c`
- **Null Coalescing:** `a ?? b`
- **Loops:** `while`, `do-while`, `for`, `for-in`, `for-of`
- **Optional chaining:**
  ```javascript
  a?.prop
  a?.[prop]
  a?.(arg)
  ```
- **Literal Strings:**
  ```javascript
  `text ${expression}`
  ```
- **Function Definitions:**
  ```javascript
  function name(args) { }
  var a = function(args) { }
  ```
- **Lambda/Arrow Functions:**
  ```javascript
  var f1 = (a) => a + a;
  var f2 = () => 1;
  var f3 = () => { var a = 1; return a; };
  var f4 = x => x * x;
  ```
- **Type Conversion:**  
  `Boolean(val)`, `Number(val)`, `String(val)`
- **JSON Support:**  
  `JSON.stringify(obj)`, `JSON.parse(str)`
---

## 🚫 Not Supported:
- `var` keyword, use `let` or `const` instead
- `new` operator
- `async/await`
- `class`, `instanceof`
- `import/export`
- `fetch`
- `regex` - no regex object, only `String` parameter in `String.match` or `String.matchAll`
- some JavaScript WTF behaviors (like `[] == 0`, etc.)

---

## ⚠️ Differences from JavaScript:

- `typeof []` returns `"array"`
- `typeof null` returns `"null"`
- Certain JavaScript-specific quirks (WTF moments) are removed or unified

---

## 🛠️ Installation and Execution
```xml
<dependency>
   <groupId>eu.aston</groupId>
   <artifactId>java-js</artifactId>
   <version>1.2.0</version>
</dependency>
```

---

## 📖 Example Usage

```java
import eu.aston.javajs.*;
import eu.aston.javajs.AstNodes.ASTNode;
import eu.aston.javajs.types.JsSdk;

String script = """           
                var a = 5;
                let b = 10;
                const sum = (x, y) => x + y;
                
                var result = sum(a, b);
                """;
Scope rootScope = JsSdk.createRootScope();
JsLexer lexer = new JsLexer(script);
JsParser parser = new JsParser(lexer.tokenize());
ASTNode programNode = parser.parse();
programNode.exec(rootScope);
```

## Compile Once, Run Many

One of the key advantages of the compiled `AstNode` program is its stateless and thread-safe design. This means that once a program is compiled, it can be executed multiple times—across different threads—without any risk of shared state corruption or race conditions.

```java
JsLexer lexer = new JsLexer(script);
JsParser parser = new JsParser(lexer.tokenize());
ASTNode programNode = parser.parse();

for(int i=0; i<10; i++){
     Scope rootScope = JsSdk.createRootScope();
     programNode.exec(rootScope);
}
```
## 📚 Documentation

Detailed information about the script syntax and features is located in the `docs/` directory, which contains the following chapters:

- **docs/01-variables.js** — variable declarations
- **docs/02-expressions.js** — expressions and operations
- **docs/03-typed-expressions.js** — typed expressions
- **docs/04-statements.js** — control flow statements
- **docs/05-functions.js** — function definition and calling
- **docs/06-scopes.js** — working with variable scopes
---

## 👨‍💻 Author

Peter Molnár

---

## 📄 License

Apache License 2.0
