// JavaScript Functions and Lambda Expressions
// All examples follow strict mode rules

"use strict";

// Regular function declaration
function greet(name) {
  return "Hello, " + name + "!";
}

// Function expression (anonymous function assigned to variable)
const sayHello = function(name) {
  return "Hello, " + name + "!";
};

// Arrow function (lambda) - concise syntax
const greetArrow = (name) => "Hello, " + name + "!";

// Arrow function with no parameters
const sayHi = () => "Hi there!";

// Simple identity arrow function
const identity = x => x;

// Arrow function that adds x to itself
const double = x => x + x;

// Arrow function with multiple statements requires curly braces
const greetFormal = (name) => {
  const greeting = "Hello, " + name + "!";
  return greeting;
};

// Function with default parameters
function greetWithDefault(name = "Guest") {
  return "Hello, " + name + "!";
}

// Rest parameters (collecting remaining arguments into array)
function sum() {
  return arguments.reduce((total, num) => total + num, 0);
}

// Immediately Invoked Function Expression (IIFE)
(function() {
  const message = "This function runs immediately!";
  console.log(message);
})();

// Arrow function IIFE
(() => {
  const message = "This arrow function runs immediately!";
  console.log(message);
})();

// Higher-order function (function that takes/returns a function)
function multiplier(factor) {
  return function(number) {
    return number * factor;
  };
}

// Same as above but with arrow function
const multiplierArrow = (factor) => (number) => number * factor;

// ===== Functions that receive functions as parameters =====

// Function that takes another function as a parameter and applies it
function applyFunction(fn, value) {
  return fn(value);
}

// Process array elements with a callback function
function processArray(array, processor) {
  const result = [];
  for (let i = 0; i < array.length; i++) {
    result.push(processor(array[i]));
  }
  return result;
}

// Execute a function multiple times
function repeat(fn, times) {
  for (let i = 0; i < times; i++) {
    fn(i);
  }
}

// In strict mode, make sure parameter names are unique
function strictParameters(a, b) {
  // In non-strict mode you could have duplicates like (a, a)
  // which is not allowed in strict mode
  return a + b;
}

// Arguments object in strict mode
function strictArguments() {
  // In strict mode, arguments doesn't sync with named parameters
  // and doesn't contain caller or callee properties
  const args = Array.from(arguments);
  return args.join(', ');
}

// Example usage
console.log(greet("John"));                    // "Hello, John!"
console.log(sayHello("Alice"));                // "Hello, Alice!"
console.log(greetArrow("Bob"));                // "Hello, Bob!"
console.log(sayHi());                          // "Hi there!"
console.log(identity(42));                     // 42
console.log(double(7));                        // 14
console.log(greetWithDefault());               // "Hello, Guest!"
console.log(greetWithDefault("Charlie"));      // "Hello, Charlie!"
console.log(sum(1, 2, 3, 4, 5));               // 15

const doubleMultiplier = multiplier(2);
console.log(doubleMultiplier(5));              // 10

const triple = multiplierArrow(3);
console.log(triple(5));                        // 15

// Demonstrating functions as parameters
console.log(applyFunction(double, 4));         // 8
console.log(applyFunction(x => x * x, 5));     // 25

const numbers = [1, 2, 3, 4, 5];
console.log(processArray(numbers, double));    // [2, 4, 6, 8, 10]
console.log(processArray(numbers, x => x * x)); // [1, 4, 9, 16, 25]

repeat(index => {
  console.log(`Iteration ${index}`);
}, 3);
// Outputs:
// Iteration 0
// Iteration 1
// Iteration 2
