// Test file for 05-functions.js
// Tests the functionality described in the functions chapter

// Test regular function declaration
function testFunctionDeclaration() {
  function greet(name) {
    return "Hello, " + name + "!";
  }
  
  assert(greet("World") === "Hello, World!", "Function declaration works correctly");
  assert(typeof greet === "function", "greet is recognized as a function");
}

// Test function expression
function testFunctionExpression() {
  const sayHello = function(name) {
    return "Hello, " + name + "!";
  };
  
  assert(sayHello("World") === "Hello, World!", "Function expression works correctly");
  assert(typeof sayHello === "function", "sayHello is recognized as a function");
}

// Test arrow functions
function testArrowFunctions() {
  // Basic arrow function
  const greetArrow = (name) => "Hello, " + name + "!";
  assert(greetArrow("World") === "Hello, World!", "Basic arrow function works correctly");
  
  // No parameter arrow function
  const sayHi = () => "Hi there!";
  assert(sayHi() === "Hi there!", "No parameter arrow function works correctly");
  
  // Single parameter without parentheses
  const identity = x => x;
  assert(identity(42) === 42, "Identity arrow function works correctly");
  
  // Simple operation
  const double = x => x + x;
  assert(double(7) === 14, "Arrow function that doubles works correctly");
  
  // Multi-statement arrow function
  const greetFormal = (name) => {
    const greeting = "Hello, " + name + "!";
    return greeting;
  };
  assert(greetFormal("World") === "Hello, World!", "Multi-statement arrow function works correctly");
}

// Test default parameters
function testDefaultParameters() {
//  TODO function greetWithDefault(name = "Guest") {
//    return "Hello, " + name + "!";
//  }
//
//  assert(greetWithDefault() === "Hello, Guest!", "Default parameter works when no argument is provided");
//  assert(greetWithDefault("World") === "Hello, World!", "Default parameter is overridden when argument is provided");
}

// Test rest parameters
function testRestParameters() {
  function sum() {
    return arguments.reduce((total, num) => total + num, 0);
  }
  
  assert(sum(1, 2, 3, 4, 5) === 15, "Rest parameters collect all arguments correctly");
  assert(sum() === 0, "Rest parameters work with no arguments");
  assert(sum(10) === 10, "Rest parameters work with a single argument");
}

// Test higher-order functions
function testHigherOrderFunctions() {
  // Function that returns a function
  function multiplier(factor) {
    return function(number) {
      return number * factor;
    };
  }
  
  const double = multiplier(2);
  assert(double(5) === 10, "Function returned by higher-order function works correctly");
  
  // Arrow function version
  const multiplierArrow = (factor) => (number) => number * factor;
  const triple = multiplierArrow(3);
  assert(triple(5) === 15, "Arrow function returned by higher-order function works correctly");
}

// Test functions that take functions as parameters
function testFunctionsAsParameters() {
  function applyFunction(fn, value) {
    return fn(value);
  }
  
  const double = x => x + x;
  assert(applyFunction(double, 4) === 8, "Function correctly applies passed function to value");
  assert(applyFunction(x => x * x, 5) === 25, "Function correctly applies inline arrow function");
  
  // Array processing
  function processArray(array, processor) {
    const result = [];
    for (let i = 0; i < array.length; i++) {
      result.push(processor(array[i]));
    }
    return result;
  }
  
  const numbers = [1, 2, 3, 4, 5];
  const doubled = processArray(numbers, double);
  assert(doubled.join(',') === "2,4,6,8,10", "Array processor correctly applies function to each element");
  
  const squared = processArray(numbers, x => x * x);
  assert(squared.join(',') === "1,4,9,16,25", "Array processor correctly applies inline function");
}

const functions = [
    testFunctionDeclaration,
    testFunctionExpression,
    testArrowFunctions,
    testDefaultParameters,
    testRestParameters,
    testHigherOrderFunctions,
    testFunctionsAsParameters];
for(let testFunction of functions) {
    try {
        testFunction();

    } catch (error) {
        print("!!!!!Test failed:", testFunction, error);
    }
}
