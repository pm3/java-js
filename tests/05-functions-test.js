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

// Test calling non-function property as a function
function testNonFunctionPropertyCall() {
  const obj = {
    number: 42,
    text: "hello"
  };

  assertError(function() {
    obj.number(); // Try to call number as a function
  }, "obj.number is not a function");

  assertError(function() {
    obj.text(); // Try to call string as a function  
  }, "obj.text is not a function");
}
// Test 'this' operator behavior
function testThisOperator() {
  // Test 'this' in object method
  const obj = {
    value: 42,
    getValue: function() {
      return this.value;
    }
  };
  assert(obj.getValue() === 42, "'this' refers to object in method");

  // Test 'this' in arrow function
  assertError(()=>{
      const obj2 = {
        value: 42,
        getValue: () => this.value
      };
      obj2.getValue();
  });

  // Test 'this' binding with call()
  function getValueFn() {
    return this.value;
  }
  const obj3 = { value: 100 };
  assert(getValueFn.call(obj3) === 100, "'this' can be explicitly bound using call()");
}

// Test JSON parse and stringify
function testJsonOperations() {
    // Test parsing simple values
    assert(JSON.parse("42") === 42, "Parse number");
    assert(JSON.parse('"hello"') === "hello", "Parse string");
    assert(JSON.parse("true") === true, "Parse boolean");
    assert(JSON.parse("null") === null, "Parse null");

    // Test parsing arrays
    const arr = JSON.parse('[1,2,3]');
    assert(Array.isArray(arr) && arr.length === 3 && arr[0] === 1, "Parse array");

    // Test parsing objects
    const obj = JSON.parse('{"name":"John","age":30}');
    assert(obj.name === "John" && obj.age === 30, "Parse object");

    // Test parsing nested structures
    const nested = JSON.parse('{"arr":[1,2,{"x":true}]}');
    assert(nested.arr[2].x === true, "Parse nested structure");

    // Test stringify simple values
    assert(JSON.stringify(42) === "42", "Stringify number");
    assert(JSON.stringify("hello") === '"hello"', "Stringify string");
    assert(JSON.stringify(true) === "true", "Stringify boolean");
    assert(JSON.stringify(null) === "null", "Stringify null");

    // Test stringify array
    assert(JSON.stringify([1,2,3]) === "[1,2,3]", "Stringify array");

    // Test stringify object
    const testObj = {name: "John", age: 30};
    assert(JSON.stringify(testObj) === '{"name": "John","age": 30}', "Stringify object");

    // Test stringify nested structure
    const nestedObj = {arr: [1, 2, {x: true}]};
    assert(JSON.stringify(nestedObj) === '{"arr": [1,2,{"x": true}]}', "Stringify nested structure");

    // Test that stringify ignores functions
    const objWithFunction = {
        name: "Test",
        age: 25,
        sayHello: function() { return "Hello"; },
        greet: () => "Hi"
    };
    const objWithFunctionJson = JSON.stringify(objWithFunction);
    print(objWithFunctionJson);
    assert(JSON.parse(objWithFunctionJson).length === 2, "Stringify should ignore functions");

    // Test array with functions
    const arrayWithFunctions = [1, function(){}, "test", () => {}];
    assert(JSON.stringify(arrayWithFunctions) === '[1,null,"test",null]', "Stringify should convert functions to null in arrays");

    // Test error cases
    assertError(() => {
        JSON.parse("{invalid json}");
    }, "Invalid JSON should throw error");

    // Test more error cases for invalid JSON format
    assertError(() => {
        JSON.parse("[1,2,"); // Unclosed array
    }, "Unclosed array should throw error");

    assertError(() => {
        JSON.parse('{"key":}'); // Missing value
    }, "Missing value should throw error");

    assertError(() => {
        JSON.parse('{"key"'); // Unclosed object
    }, "Unclosed object should throw error");

    assertError(() => {
        JSON.parse('"unclosed string'); // Unclosed string
    }, "Unclosed string should throw error");

    assertError(() => {
        JSON.parse('{"a":1 "b":2}'); // Missing comma
    }, "Missing comma should throw error");

    assertError(() => {
        JSON.parse('{"a" 1}'); // Missing colon
    }, "Missing colon should throw error");

    assertError(() => {
        JSON.parse('tru'); // Invalid boolean
    }, "Invalid boolean should throw error");

    assertError(() => {
        JSON.parse('nul'); // Invalid null
    }, "Invalid null should throw error");

    assertError(() => {
        JSON.parse('12.34.56'); // Invalid number format
    }, "Invalid number format should throw error");

    assertError(() => {
        JSON.parse('[1,2,,3]'); // Extra comma in array
    }, "Extra comma in array should throw error");
}

const functions = [
    testFunctionDeclaration,
    testFunctionExpression,
    testArrowFunctions,
    testDefaultParameters,
    testRestParameters,
    testHigherOrderFunctions,
    testFunctionsAsParameters,
    testNonFunctionPropertyCall,
    testThisOperator,
    testJsonOperations
];
for(let testFunction of functions) {
    try {
        testFunction();

    } catch (error) {
        assert(false, "method "+testFunction+" error "+error);
    }
}
