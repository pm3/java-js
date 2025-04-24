/**
 * Scope Tests
 * Testing the behavior of JavaScript scopes
 */

// Global scope test variables
var globalVar = "global var";
let globalLet = "global let";
const globalConst = "global const";

// 1. Test Global Scope
function testGlobalScope() {
  assert(globalVar === "global var", "Global var is accessible");
  assert(globalLet === "global let", "Global let is accessible");
  assert(globalConst === "global const", "Global const is accessible");
}

// 2. Test Function Scope
function testFunctionScope() {
  function testFunction() {
    var functionVar = "function var";
    return functionVar;
  }
  
  assert(testFunction() === "function var", "Function returns its local variable");
  
  // If we could access functionVar here, this would throw an error
  assertError(() => {
    if (typeof functionVar !== 'undefined') {
      return functionVar;
    }
    throw Error('functionVar is not defined');
  }, "functionVar is not defined");
}

// 3. Test Block Scope
function testBlockScope() {
  {
    var blockVar = "block var";
    let blockLet = "block let";
    const blockConst = "block const";
  }
  
  // var ignores block scope
  assert(blockVar === "block var", "var ignores block scope");
  
  // let and const respect block scope
  assertError(() => {
    if (typeof blockLet !== 'undefined') {
      return blockLet;
    }
    throw Error('blockLet is not defined');
  }, "blockLet is not defined");
  
  assertError(() => {
    if (typeof blockConst !== 'undefined') {
      return blockConst;
    }
    throw Error('blockConst is not defined');
  }, "blockConst is not defined");
}

// 4. Test Lexical Scope
function testLexicalScope() {
  function outerFunction() {
    const outerVar = "outer value";
    
    function innerFunction() {
      return outerVar;
    }
    
    return innerFunction();
  }
  
  assert(outerFunction() === "outer value", "Inner function has access to outer variable");
}

// 5. Test Hoisting
function testHoisting() {
  // hoistedVar is declared below but hoisted
  assert(typeof hoistedVar === 'undefined', "hoistedVar is undefined before declaration");
  assert(typeof hoistedVar?.next === 'undefined', "hoistedVar?.next is undefined before declaration");
  var hoistedVar = "hoisted";
  
  // Trying to access a let before declaration will cause error
  // This is wrapped in a function because the error would happen at parse time
  function accessBeforeDeclaration() {
    hoistedLet += "changed";
  }
  
  assertError(() => {
    accessBeforeDeclaration();
  }, "Cannot access 'hoistedLet' before initialization");
  
  let hoistedLet = "hoisted let";
}

// 6. Test Function Hoisting
function testFunctionHoisting() {
  // We can call a function before its declaration
  assert(hoistedFunction() === "hoisted function", "Function declaration is hoisted");
  
  function hoistedFunction() {
    return "hoisted function";
  }
}

// 7. Test Closures
function testClosures() {
  function createCounter() {
    var count = 0;
    
    return function() {
      count++;
      return count;
    };
  }
  
  const counter = createCounter();
  assert(counter() === 1, "First count is 1");
  assert(counter() === 2, "Second count is 2");
  assert(counter() === 3, "Third count is 3");
  
  // A new counter has its own scope
  const counter2 = createCounter();
  assert(counter2() === 1, "New counter starts at 1");
  
  // First counter still maintains its own count
  assert(counter() === 4, "Original counter continues from 4");
}

// Test duplicate variable declarations
function testDuplicateDeclarations() {

  // Test duplicate var declarations - should throw error
  assertError(() => {
    var x = 1;
    var x = 2; // SyntaxError in strict mode
  }, "Identifier 'x' has already been declared");

  // Test duplicate let declarations - should throw error
  assertError(() => {
    let y = 1;
    let y = 2; // SyntaxError in strict mode
  }, "Identifier 'y' has already been declared");

  // Test duplicate const declarations - should throw error  
  assertError(() => {
    const z = 1;
    const z = 2; // SyntaxError in strict mode
  }, "Identifier 'z' has already been declared");

  // Test var/let conflict
  assertError(() => {
    var a = 1;
    let a = 2; // SyntaxError - can't redeclare with let
  }, "Identifier 'a' has already been declared");

  // Test var/const conflict
  assertError(() => {
    var b = 1;
    const b = 2; // SyntaxError - can't redeclare with const
  }, "Identifier 'b' has already been declared");
}




// Run all tests
const functions = [
    testGlobalScope,
    testFunctionScope,
    testBlockScope,
    testLexicalScope,
    testHoisting,
    testFunctionHoisting,
    testClosures,
    testDuplicateDeclarations
  ];
for(let testFunction of functions) {
    try {
        testFunction();

    } catch (error) {
        assert(false, "method "+testFunction+" error "+error);
    }
}
