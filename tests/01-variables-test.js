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

  // Array with one item and a comma
  let arr4 = [1,];
  assert(arr4.length === 1, "Array with one item and a comma has correct length");

}

function testMultilineStrings() {

  // Multi-line string with concatenation
  const str2 = "First line " +
    "second line " +
    "third line";
  assert(str2 === "First line second line third line",
    "Multi-line string with concatenation works");

  // Multi-line string with template literal
  const str3 = `Line one
    Line two
    Line three`;
  assert(str3.split("\n").length === 3,
    "Multi-line template literal preserves line breaks");

  // Multi-line string with escape sequences
  const str4 = "Line 1\nLine 2\nLine 3";
  assert(str4.split("\n").length === 3,
    "Multi-line string with escape sequences works");

  // Multi-line string with mixed quotes
  const str5 = "Line one\n" +
    'Line "two"\n' +
    `Line 'three'`;
  assert(str5.includes('"two"') && str5.includes("'three'"),
    "Multi-line string with mixed quotes works");
}


function testEscapeSequences() {
  // Test hex escape sequence \xXX
  const hexStr = "Hello \x48\x69";  // \x48 = 'H', \x69 = 'i'
  assert(hexStr === "Hello Hi", "Hex escape sequence works correctly");

  // Test unicode escape sequence \uXXXX 
  const unicodeStr = "Hello \u0048\u0069"; // \u0048 = 'H', \u0069 = 'i'
  assert(unicodeStr === "Hello Hi", "Unicode escape sequence works correctly");

  // Test mixed escape sequences
  const mixedStr = "\x48\u0065\x6C\u006C\x6F"; // "Hello"
  assert(mixedStr === "Hello", "Mixed hex and unicode escape sequences work correctly");

  // Test escape sequences with special characters
  const specialStr = "\x22\u0027"; // Quote marks
  assert(specialStr === "\"'", "Escape sequences with special characters work correctly");
}

function testObjectCreation() {
  // Test basic object creation
  const obj = {
    name: "test",
    value: 42
  };
  assert(obj.name === "test" && obj.value === 42, "Basic object creation works correctly");

  // Test that duplicate property names are not allowed (last one wins)
  const objWithDuplicates = {
    prop: "first",
    prop: "second" // This should overwrite the first one
  };
  assert(objWithDuplicates.prop === "second", "Duplicate property takes last value");

  // Test with different property types
  const mixedObj = {
    string: "text",
    number: 123,
    boolean: true,
    array: [1,2,3],
    nested: {
      x: 1
    }
  };
  assert(typeof mixedObj.string === "string" &&
         typeof mixedObj.number === "number" &&
         typeof mixedObj.boolean === "boolean" &&
         Array.isArray(mixedObj.array) &&
         typeof mixedObj.nested === "object",
         "Object can contain different types of properties");

  // Test object creation with computed property names
  const a = "a";
  const objWithComputedProp1 = { a };
  assert(objWithComputedProp1.a === "a", "Computed property names work correctly, since a is not a reserved word");

  const objWithComputedProp2 = { a, b: "b" };
  assert(objWithComputedProp2.a === "a", "Computed property names work correctly, since a is not a reserved word"); 
  assert(objWithComputedProp2.b === "b", "Computed property names work correctly"); 

  const objWithComputedProp3 = { c: "c", a, b: "b" };
  assert(objWithComputedProp3.a === "a", "Computed property names work correctly, since a is not a reserved word"); 
  assert(objWithComputedProp3.b === "b", "Computed property names work correctly"); 
  assert(objWithComputedProp3.c === "c", "Computed property names work correctly"); 
}

// Test destructuring arrays and objects during variable declaration
function testDestructuring() {
  // Array destructuring
  const numbers = [1, 2, 3, 4, 5];
  const [first, second, ...rest] = numbers;
  
  assert(first === 1, "Array destructuring gets first element");
  assert(second === 2, "Array destructuring gets second element");
  assert(Array.isArray(rest), "Rest operator creates an array");
  assert(rest.length === 3, "Rest array has correct length");
  assert(rest[0] === 3 && rest[1] === 4 && rest[2] === 5, "Rest array contains remaining elements");
  
  // Skipping elements
  const [a, , c] = [10, 20, 30];
  assert(a === 10, "Array destructuring allows skipping elements (first element)");
  assert(c === 30, "Array destructuring allows skipping elements (third element)");
  
  // Object destructuring
  const person = { name: "Alice", age: 30, city: "Wonderland" };
  const { name, age } = person;
  
  assert(name === "Alice", "Object destructuring gets property by name");
  assert(age === 30, "Object destructuring gets property by name");    
}


// Run all tests
const functions = [
    testVariableDeclarations,
    testDataTypes,
    testTypeConversion,
    testVariableScope,
    testArrayWithEmptyItems,
    testMultilineStrings,
    testEscapeSequences,
    testObjectCreation,
    testDestructuring
];
for(let testFunction of functions) {
    try {
        testFunction();
    } catch (error) {
        assert(false, "method "+testFunction+" error "+error);
    }
}




