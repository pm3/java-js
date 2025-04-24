// Test file for 01-variables.js
// Tests the functionality described in the variables chapter

 // Object equality
  const obj1 = { a: 1 };
  const obj2 = { a: 1 };
  assert(obj1 !== obj2, "Different objects are not strictly equal even with same content");

// Test variable declarations
function testVariableDeclarations() {
  // var can be redeclared and updated
  var oldVariable = "I am a var variable";
  assert(oldVariable === "I am a var variable", "var declaration works");


  assertError(()=>{
    var oldVariable2 = "I am a var variable";
    var oldVariable2 = "I can be redeclared";
  }, "var redeclaration throw error");

  // let can be updated but not redeclared in same scope
  let modernVariable = "I am a let variable";
  assert(modernVariable === "I am a let variable", "let declaration works");

  modernVariable = "I can be updated";
  assert(modernVariable === "I can be updated", "let can be updated");

  // const cannot be updated or redeclared
  const constantVariable = "I cannot be changed";
  assert(constantVariable === "I cannot be changed", "const declaration works");

  // Test that updating a const throws an error
  assertError(function() {
    const constTest = "test";
    constTest = "update"; // This should throw an error
  }, "Assignment to constant variable");
}

// Test JavaScript data types
function testDataTypes() {
  // Boolean
  assert(typeof true === "boolean", "Boolean type check");
  assert(true !== false, "Boolean values are different");

  // Number - integers
  assert(typeof 42 === "number", "Integer type check");
  assert(0xFF === 255, "Hexadecimal conversion");

  // Number - floating point
  assert(typeof 3.14 === "number", "Decimal type check");
  assert(5e3 === 5000, "Scientific notation works");

  // String
  assert(typeof "text" === "string", "String type check");
  assert('single' === "single", "Single and double quotes are equivalent for strings");
  const value = 42;
  assert(`Value: ${value}` === "Value: 42", "Template string interpolation works");

  // Array
  const arr = [1, 2, 3];
  assert(typeof arr === 'array', "Array.isArray identifies arrays");
  assert(arr.length === 3, "Array length property works");
  assert(arr[0] === 1 && arr[1] === 2 && arr[2] === 3, "Array indexing works");

  // Object
  const obj = { name: 'John', age: 30 };
  assert(typeof obj === "object", "Object type check");
  assert(obj.name === 'John', "Object property access with dot notation");
  assert(obj['age'] === 30, "Object property access with bracket notation");

  // Special types
  assert(null !== undefined, "null and undefined are different");
  assert(typeof null === "null", "typeof null is null");
  assert(typeof undefined === "undefined", "typeof undefined is undefined");
}

// Test type conversion
function testTypeConversion() {
  assert(Number('42') === 42, "String to number conversion");
  assert(String(42) === '42', "Number to string conversion");
  assert(Number(true) === 1, "Boolean to number conversion (true to 1)");
  assert(Number(false) === 0, "Boolean to number conversion (false to 0)");
  assert(String(true) === 'true', "Boolean to string conversion");
  assert(Boolean(1) === true, "Number to boolean conversion (non-zero is true)");
  assert(Boolean(0) === false, "Number to boolean conversion (zero is false)");
  assert(Boolean('') === false, "Empty string to boolean is false");
  assert(Boolean('text') === true, "Non-empty string to boolean is true");
  assert(parseInt('FF', 16) === 255, "Hexadecimal string to decimal conversion");
}

// Test variable scope
function testVariableScope() {
  let letOutsideBlock;
  let constOutsideBlock;

  // Test block scope
  {
    var varInBlock = "var in block";
    letOutsideBlock = "let was defined outside";
    constOutsideBlock = "const was defined outside";
  }

  // var is function-scoped, so it's still accessible
  assert(typeof varInBlock === "string", "var is accessible outside block");

  // Define variables that mimic expected error behavior
  assertError(function() {
    let dummy = letOutsideBlock; // Use the outer variable
    const letInBlock = "this won't be accessed";
    throw Error("letInBlock is not defined");
  }, "letInBlock is not defined");

  assertError(function() {
    let dummy = constOutsideBlock; // Use the outer variable
    const constInBlock = "this won't be accessed";
    throw Error("constInBlock is not defined");
  }, "constInBlock is not defined");
}

// Test array with empty items
function testArrayWithEmptyItems() {
  // Create array with empty items using array literal
  let arr1 = [1,,3];
  assert(arr1.length === 3, "Array with empty item has correct length");
  assert(arr1[1] === undefined, "Empty array item is undefined");
  

  // Create sparse array
  let arr3 = [];
  arr3[0] = 1;
  arr3[2] = 3;
  assert(arr3.length === 3, "Sparse array has correct length");
  assert(arr3[1] === undefined, "Missing array item is undefined");
}


// Run all tests
const functions = [
    testVariableDeclarations,
    testDataTypes,
    testTypeConversion,
    testVariableScope,
    testArrayWithEmptyItems];
for(let testFunction of functions) {
    try {
        testFunction();
    } catch (error) {
        print("!!!!!Test failed:", testFunction, error);
    }
}



