/**
 * JavaScript Expressions Documentation
 * 
 * This file demonstrates the various types of expressions in JavaScript
 * including operators, concatenation, and operator precedence.
 * 
 * All examples follow strict mode rules.
 */

// ===== 2.5.1 Arithmetic Operators =====

// Basic arithmetic operators
const addition = 5 + 3;           // 8
const subtraction = 10 - 4;       // 6
const multiplication = 6 * 7;     // 42
const division = 20 / 4;          // 5
const modulus = 17 % 3;           // 2 (remainder of division)
const exponentiation = 2 ** 3;    // 8 (2 raised to the power of 3)

// Unary operators
const unaryPlus = +"-42";         // -42 (converts string to number)
const unaryMinus = -42;           // Negates the value
const increment = 5;
let postIncrement = increment++;  // postIncrement = 5, increment = 6
let preIncrement = ++increment;   // preIncrement = 7, increment = 7
const decrement = 8;
let postDecrement = decrement--;  // postDecrement = 8, decrement = 7
let preDecrement = --decrement;   // preDecrement = 6, decrement = 6

// Assignment operators
let value = 10;
value += 5;                       // value = value + 5 (15)
value -= 3;                       // value = value - 3 (12)
value *= 2;                       // value = value * 2 (24)
value /= 6;                       // value = value / 6 (4)
value %= 3;                       // value = value % 3 (1)

// ===== 2.5.2 Relational Operators =====

// Comparison operators
const equal = 5 == "5";           // true (equality, with type conversion)
const strictEqual = 5 === "5";    // false (strict equality, no type conversion)
const notEqual = 5 != "6";        // true (inequality)
const strictNotEqual = 5 !== 5;   // false (strict inequality)
const greaterThan = 10 > 5;       // true
const lessThan = 10 < 20;         // true
const greaterOrEqual = 10 >= 10;  // true
const lessOrEqual = 5 <= 4;       // false

// Equality with objects and arrays
const obj1 = { a: 1 };
const obj2 = { a: 1 };
const objEqual = obj1 == obj2;    // false (different objects)
const objStrictEqual = obj1 === obj2; // false (different objects)

const sameObj = obj1;
const sameObjEqual = obj1 === sameObj; // true (same object reference)

const arr1 = [1, 2, 3];
const arr2 = [1, 2, 3];
const arrEqual = arr1 == arr2;    // false (different arrays)

// Special equality cases
const nullUndefinedEqual = null == undefined;  // true
const nullUndefinedStrictEqual = null === undefined; // false

// ===== 2.5.3 Logical Operators =====

// Logical operators
const and = true && false;        // false (both must be true)
const or = true || false;         // true (at least one must be true)
const not = !true;                // false (negation)

// Short-circuit evaluation
// In strict mode we need to declare variables before use
let undefinedVariable;
const shortCircuitAnd = false && undefinedVariable; // false (second operand not evaluated)
const shortCircuitOr = true || undefinedVariable; // true (second operand not evaluated)

// Logical operators with non-boolean values
const logicalWithNumbers = 0 && 5;     // 0 (first falsy value)
const logicalOrWithNumbers = 0 || 5;   // 5 (first truthy value)
const logicalWithStrings = "" || "Hello"; // "Hello" (first truthy value)

// Nullish coalescing operator
const nullish = null ?? "default";     // "default" (used when left side is null or undefined)
const nullishWithZero = 0 ?? "default"; // 0 (0 is not null or undefined)

// Optional chaining
const user = {
    profile: {
        name: "John"
    }
};
const userName = user?.profile?.name;  // "John" (safe access to nested properties)
const nonExistentProp = user?.settings?.darkMode; // undefined (no error thrown)

// ===== 2.5.4 Concatenation =====

// String concatenation
const string1 = "Hello";
const string2 = "World";
const concatWithPlus = string1 + " " + string2;  // "Hello World"

// String template literals
const name = "Alice";
const greeting = `Hello, ${name}!`;  // "Hello, Alice!"
const calculation = `2 + 2 = ${2 + 2}`; // "2 + 2 = 4"

// Concatenation with non-string values
const concatWithNumber = "The answer is: " + 42;  // "The answer is: 42"
const concatWithBoolean = "Is active: " + true;   // "Is active: true"
const concatWithObject = "User: " + { name: "Bob" }; // "User: [object Object]"
const concatWithArray = "Items: " + [1, 2, 3];    // "Items: 1,2,3"

// Join method for arrays
const joinArray = ["Apple", "Banana", "Cherry"].join(", "); // "Apple, Banana, Cherry"

// ===== 2.5.5 The Length Operator =====

// String length
const helloLength = "Hello".length;  // 5

// Array length
const arrayLength = [1, 2, 3, 4, 5].length;  // 5

// Changing array length
let dynamicArray = [1, 2, 3];
dynamicArray.length = 5;         // [1, 2, 3, empty × 2]
const expandedArrayLength = dynamicArray.length;  // 5

dynamicArray.length = 2;         // [1, 2]
const truncatedArrayLength = dynamicArray.length;  // 2

// Using length in loops
const fruits = ["Apple", "Banana", "Cherry"];
for (let i = 0; i < fruits.length; i++) {
    // Process each fruit
    const fruit = fruits[i];
}

// ===== 2.5.6 Precedence =====

// Operator precedence examples
const precedence1 = 3 + 4 * 5;     // 23 (multiplication before addition)
const precedence2 = (3 + 4) * 5;   // 35 (parentheses change precedence)

// Multiple operators with different precedence
const precedence3 = 3 + 4 * 5 / 2; // 13 (multiplication and division before addition)
const precedence4 = 3 > 2 && 2 < 1; // false (comparison before logical AND)
const precedence5 = 3 > 2 || 2 < 1 && 0 < 1; // true (AND before OR)

// Assignment vs equality
let x = 5;
const precedence6 = x = 3;         // 3 (assignment)
const precedence7 = x == 3;        // true (equality test)
const precedence8 = x === 3;       // true (strict equality test)

// Operator precedence table (from highest to lowest)
/*
1. Grouping                      ( ... )
2. Member access                 . ...
3. Computed member access        [ ... ]
4. Function call                 ... ( ... )
5. Increment/Decrement           ... ++ / ... --
6. Logical NOT                   ! ...
7. Exponentiation                ... ** ...
8. Multiplication/Division       ... * ... / ... % ...
9. Addition/Subtraction          ... + ... / ... - ...
10. Relational operators         ... < ... / ... > ... / ... <= ... / ... >= ...
11. Equality operators           ... == ... / ... != ... / ... === ... / ... !== ...
12. Logical AND                  ... && ...
13. Logical OR                   ... || ...
14. Nullish coalescing           ... ?? ...
15. Conditional (ternary)        ... ? ... : ...
16. Assignment                   ... = ... / ... += ... / ... -= ... etc.
17. Comma                        ... , ...
*/

// Real-world usage example
function calculateTotal(items, tax, discount) {
    // Uses multiple operators with attention to precedence
    const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const discountAmount = discount ? subtotal * (discount / 100) : 0;
    const taxAmount = (subtotal - discountAmount) * (tax / 100);
    const total = subtotal - discountAmount + taxAmount;
    
    return {
        subtotal: subtotal.toFixed(2),
        discount: discountAmount.toFixed(2),
        tax: taxAmount.toFixed(2),
        total: total.toFixed(2)
    };
}

// Example usage of the function with sample data
const orderItems = [
    { name: "Product 1", price: 10.99, quantity: 2 },
    { name: "Product 2", price: 24.99, quantity: 1 },
    { name: "Product 3", price: 5.49, quantity: 3 }
];

const orderSummary = calculateTotal(orderItems, 8.25, 10); // 8.25% tax, 10% discount 