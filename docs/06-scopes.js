/**
 * JavaScript Scopes
 * ----------------
 * 
 * Scope determines the visibility and lifetime of variables and functions in code.
 * All examples follow strict mode rules.
 */

// Enable strict mode
"use strict";

// 1. Global Scope
// Variables declared outside functions are globally accessible
// In strict mode, we should always declare variables properly
const globalVar = "I am a global variable";
let globalLet = "I am also global";
const globalConst = "I am a global constant";

/**
 * 2. Function Scope
 * 
 * Variables declared inside a function are only visible within that function
 */
function functionScopeExample() {
    const functionVar = "I am only visible in this function";
    
    console.log(functionVar); // Works
    console.log(globalVar);   // Works - global variables are accessible inside functions
    
    // Nested function
    function nestedFunction() {
        const nestedVar = "I am only visible in the nested function";
        console.log(functionVar); // Works - nested functions can see parent function variables
    }
    
    // Call the nested function
    nestedFunction();
}
// console.log(functionVar); // Error - functionVar is not visible outside the function

/**
 * 3. Block Scope
 * 
 * let and const create variables with block scope.
 * var ignores block scope but is still function-scoped.
 * 
 * Block scope applies to code within curly braces {}, including:
 * - if/else blocks
 * - for/while loops
 * - switch statements
 * - standalone blocks
 */
{
    // In strict mode, prefer let and const over var
    let blockVar = "I am only visible in this block"; // Changed from var to let
    let blockLet = "I am only visible in this block";
    const blockConst = "I am also only visible in this block";
}
// console.log(blockVar);    // Error - blockVar is not visible outside the block
// console.log(blockLet);    // Error - blockLet is not visible outside the block
// console.log(blockConst);  // Error - blockConst is not visible outside the block

// Example with conditional blocks
let ifVarWithVar; // In strict mode, declare before use
if (true) {
    let ifVar = "Only visible in this if block";
    ifVarWithVar = "Visible outside the if block";
}
// console.log(ifVar); // Error - ifVar is not visible outside the if block
console.log(ifVarWithVar); // Works - variable was declared outside

// Example with loop blocks
let loopVarWithVar; // In strict mode, declare before use
for (let i = 0; i < 3; i++) {
    // 'i' is only accessible within this loop
    let loopVar = "Only visible in this loop iteration";
    loopVarWithVar = "Visible outside the loop";
}
// console.log(i); // Error - 'i' is not visible outside the loop
// console.log(loopVar); // Error - loopVar is not visible outside the loop
console.log(loopVarWithVar); // Works - variable was declared outside

// Each loop iteration creates a new scope for let/const variables
const functions = [];
for (let i = 0; i < 3; i++) {
    // Each iteration has its own 'i' variable in its scope
    functions.push(function() { return i; });
}
// These will return 0, 1, 2 because each function captured its own 'i'
console.log(functions[0]()); // 0
console.log(functions[1]()); // 1
console.log(functions[2]()); // 2

// With let declared outside, all iterations share the same variable
const functionsWithSharedCounter = [];
let j = 0; // Declared outside loop
for (; j < 3; j++) {
    // All iterations share the same 'j' variable
    functionsWithSharedCounter.push(function() { return j; });
}
// These will all return 3 because they all captured the same 'j'
console.log(functionsWithSharedCounter[0]()); // 3
console.log(functionsWithSharedCounter[1]()); // 3
console.log(functionsWithSharedCounter[2]()); // 3

/**
 * 4. Lexical Scope
 * 
 * Functions have access to variables from their surrounding environment where they were defined.
 */
function outerFunction() {
    const outerVar = "I am in the outer function";
    
    function innerFunction() {
        console.log(outerVar); // Works - thanks to lexical scope
    }
    
    innerFunction();
}

/**
 * 5. Hoisting
 * 
 * Variable and function declarations are "hoisted" to the top of their scope.
 * In strict mode, variables must be declared before use.
 */
let hoistedVar; // In strict mode, declare before use
console.log(hoistedVar); // undefined
hoistedVar = "I am hoisted";

// function hoisting
hoistedFunction(); // Works - entire function is hoisted
function hoistedFunction() {
    console.log("This entire function is hoisted");
}

// let and const are also hoisted but not initialized - creating a "temporal dead zone"
// console.log(hoistedLet); // Error - hoistedLet is not initialized
let hoistedLet = "let is also hoisted but without initialization";

/**
 * 6. Closure
 * 
 * When a function maintains a reference to variables from its surrounding environment, 
 * even when called outside its original scope.
 */
function createCounter() {
    let count = 0; // Private variable
    
    return function() {
        count++; // Function remembers count due to closure
        return count;
    };
}

const counter = createCounter();
console.log(counter()); // 1
console.log(counter()); // 2
console.log(counter()); // 3
