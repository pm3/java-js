/**
 * Scope Tests
 * Testing the behavior of JavaScript scopes
 */

// Global scope test variables
let globalLet = "global let";
const globalConst = "global const";

// 1. Test Global Scope
function testGlobalScope() {
  assert(globalLet === "global let", "Global let is accessible");
  assert(globalConst === "global const", "Global const is accessible");
}

// 2. Test Function Scope
function testFunctionScope() {
  function testFunction() {
    let functionLet = "function let";
    return functionLet;
  }
  
  assert(testFunction() === "function let", "Function returns its local variable");
  
  // If we could access functionLet here, this would throw an error
  assertError(() => {
    if (typeof functionLet !== 'undefined') {
      return functionLet;
    }
    throw Error('functionLet is not defined');
  }, "functionLet is not defined");
}

// 3. Test Block Scope
function testBlockScope() {
  {
    let blockLet = "block let";
    const blockConst = "block const";
  }
  
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
    let count = 0;
    
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

// Run all tests
const functions = [
    testGlobalScope,
    testFunctionScope,
    testBlockScope,
    testLexicalScope,
    testHoisting,
    testFunctionHoisting,
    testClosures,
  ];
for(let testFunction of functions) {
    try {
        testFunction();
    } catch (error) {
        assert(false, "method "+testFunction+" error "+error);
    }
}
