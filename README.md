
# micro-script-jvm

**micro-script-jvm** is a minimalist implementation of a JavaScript subset for the **JVM (Java Virtual Machine)** environment. The engine is syntax-compatible with a selected subset of JavaScript that can be executed in a JavaScript engine, but not vice versa.

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
- `function` (custom `JsFunction`)
- `Error`

### Supported Syntax and Features:

- **Variables:** `var`, `let`, `const`
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

- `new` operator
- `async/await`
- `class`, `instanceof`
- `import/export`
- `fetch`
- some JavaScript WTF behaviors (like `[] == 0`, etc.)

---

## ⚠️ Differences from JavaScript:

- `typeof []` returns `"array"`
- Certain JavaScript-specific quirks (WTF moments) are removed or unified:
   - Array auto-converts to string for comparison
   - Empty array equals false
   - Empty array equals negated empty array
   - Object plus array is 0
   - Empty array converts to 0
   - `!{} === false`
   - `![] === false`
   - `NaN` is not equal to itself
   - Large integers lose precision
   - `empty array % number = 0 % number`
   - `single item array % number = number % number`
   - `empty array == 0` is true (`[]` converts to `0`)
   - `[1] == 1` is true (`[1]` converts to '1' then to 1)

---

## 🛠️ Installation and Execution
```xml
<dependency>
   <groupId>eu.aston</groupId>
   <artifactId>micro-script-jvm</artifactId>
   <version>1.0</version>
</dependency>
```

---

## 📖 Example Usage

```java
import eu.aston.javajs.*;
String script = """           
                var a = 5;
                let b = 10;
                const sum = (x, y) => x + y;
                
                var result = sum(a, b);
                """;
Scope rootScope = new Scope();
JsSdk.defineFunctions(rootScope);
JsLexer lexer = new JsLexer(script);
JsParser parser = new JsParser(lexer.tokenize());
var rootNode = parser.parse();
rootNode.exec(rootScope);

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
