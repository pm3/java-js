// Test file for 03-typed-expressions.js
// Tests the type conversion functionality described in the chapter


// Test addition type conversions
function testAdditionTypeConversion() {
  // Number + Number
  assert(5 + 3 === 8, "number + number = arithmetic sum");
  assert(5 + (-3) === 2, "number + negative number = arithmetic sum");
  assert(5 + 0 === 5, "number + 0 = original number");
  assert(isNaN(5 + NaN), "number + NaN = NaN");
  
  // String concatenation
  assert("hello" + "world" === "helloworld", "string + string = concatenation");
  assert("hello" + "world" + "today" === "helloworldtoday", "string + string + string = concatenation");
  assert("5" + "3" === "53", "string numerals concatenate as strings");
  assert("5" + 3 === "53", "string + number = string concatenation");
  assert(5 + "3" === "53", "number + string = string concatenation");
  assert("" + 123 === "123", "empty string + number = number converted to string");
  
  // Boolean conversions
  assert(true + true === 2, "true + true = 2 (1 + 1)");
  assert(false + false === 0, "false + false = 0 (0 + 0)");
  assert(true + false === 1, "true + false = 1 (1 + 0)");
  assert(true + 5 === 6, "true + 5 = 6 (1 + 5)");
  assert(false + 5 === 5, "false + 5 = 5 (0 + 5)");
  assert("x" + true === "xtrue", "string + boolean = string concatenation");
  assert(true + "x" === "truex", "boolean + string = string concatenation");
  
  // null and undefined
  assert(null + null === 0, "null + null = 0 (0 + 0)");
  assert(null + 5 === 5, "null + 5 = 5 (0 + 5)");
  assert(null + true === 1, "null + true = 1 (0 + 1)");
  assert(null + "hello" === "nullhello", "null + string = string concatenation");
  assert(isNaN(undefined + undefined), "undefined + undefined = NaN");
  assert(isNaN(undefined + 5), "undefined + 5 = NaN");
  assert(undefined + "hello" === "undefinedhello", "undefined + string = string concatenation");
  
  // Arrays
  assert([] + [] === "", "[] + [] = empty string");
  assert([1,2] + [3,4] === "1,23,4", "arrays convert to comma-separated strings");
  assert([] + 5 === "5", "empty array + number = number as string");
  assert([1,2] + 5 === "1,25", "array + number = array as string + number as string");
  assert([1] + true === "1true", "single item array + boolean = concatenated strings");
  
  // Single item arrays
  assert([1] + [2] === "12", "single item arrays convert without commas");
  assert([1] + [] === "1", "[1] + [] = '1'");
  assert([] + [1] === "1", "[] + [1] = '1'");
  assert([1] + 2 === "12", "[1] + 2 = '12'");
  assert(2 + [1] === "21", "2 + [1] = '21'");
  
  // Objects
  assert({} + "" === "[object Object]", "object converts to '[object Object]' string");
  assert("" + {} === "[object Object]", "string + object concatenates with '[object Object]'");
  //TODO assert("" + {toString:function() { return "custom"; }} === "custom", "object with custom toString() method");
}

// Test arithmetic operators (-, *, /)
function testArithmeticTypeConversion() {
  // Subtraction
  assert(5 - "2" === 3, "number - string = number (string converts to number)");
  assert("5" - 2 === 3, "string - number = number (string converts to number)");
  assert(true - 1 === 0, "boolean - number = number (boolean converts to number)");
  assert("10" - "5" === 5, "string - string = number (strings convert to numbers)");
  assert(isNaN("hello" - 5), "non-numeric string - number = NaN");
  
  // Multiplication
  assert(5 * "2" === 10, "number * string = number (string converts to number)");
  assert("5" * 2 === 10, "string * number = number (string converts to number)");
  assert(true * 5 === 5, "boolean * number = number (boolean converts to number)");
  assert("10" * "2" === 20, "string * string = number (strings convert to numbers)");
  
  // Division
  assert(10 / "2" === 5, "number / string = number (string converts to number)");
  assert("10" / 2 === 5, "string / number = number (string converts to number)");
  assert(true / 0.5 === 2, "boolean / number = number (boolean converts to number)");
  assert("10" / "2" === 5, "string / string = number (strings convert to numbers)");
  assert(isNaN(5 / undefined), "number / undefined = NaN");
}

// Test modulo type conversions
function testModuloTypeConversion() {
  assert(5 % 2 === 1, "number % number = remainder");
  assert("10" % 3 === 1, "string % number = number (string converts to number)");
  assert(10 % "3" === 1, "number % string = number (string converts to number)");
  assert(true % 2 === 1, "boolean % number = number (boolean converts to number)");
  assert(isNaN(5 % false), "number % false = NaN (modulo by 0)");
  assert(isNaN(5 % null), "number % null = NaN (modulo by 0)");
  assert(isNaN(5 % undefined), "number % undefined = NaN");
}

// Test comparison operators
function testComparisonTypeConversion() {
  // Numeric comparisons
  assert(5 > 3, "number > number works normally");
  assert("5" > 3, "string > number converts string to number");
  assert(3 < "5", "number < string converts string to number");
  
  // String comparisons
  assert("5" > "3", "string > string does lexicographical comparison ('5' > '3')");
  assert(!("10" > "3"), "lexicographical '10' is not greater than '3' (first char '1' < '3')");
  assert("a" < "b", "string < string compares alphabetically");
  assert("abc" < "abd", "compares strings character by character");
  
  // Boolean comparisons
  assert(true > false, "true > false (1 > 0)");
  assert(true > 0, "true > 0 (1 > 0)");
  assert(false < 1, "false < 1 (0 < 1)");
  assert(1 >= true, "1 >= true (1 >= 1)");
  assert(0 <= false, "0 <= false (0 <= 0)");
  
  // Object comparisons
  assert([] <= [], "empty arrays convert to 0 for comparison");
  assert([2] > [1], "single item arrays convert to numbers for comparison");
  assert([2] > 1, "array > number converts array to number if possible");
}

// Test equality operators (==, !=)
function testEqualityTypeConversion() {
  // Same type
  assert(5 == 5, "number == number with same value is true");
  assert(!(5 == 6), "number == number with different value is false");
  
  // Different types
  assert(5 == "5", "number == string converts string to number");
  assert(0 == false, "number == boolean converts boolean to number (0 == 0)");
  assert(1 == true, "number == boolean converts boolean to number (1 == 1)");
  assert(!(2 == true), "2 != true (2 != 1)");
  assert(null == undefined, "null == undefined is true by design");

  // Different object references are not equal
  const arr1 = [];
  const arr2 = [];
  assert(!(arr1 == arr2), "different array references are not equal with ==");
}

// Test strict equality operators (===, !==)
function testStrictEqualityTypeConversion() {
  // Same type and value
  assert(5 === 5, "number === number with same value is true");
  assert("hello" === "hello", "string === same string is true");
  
  // Same value but different types
  assert(!(5 === "5"), "number === string is false (different types)");
  assert(!(0 === false), "number === boolean is false (different types)");
  assert(!(null === undefined), "null === undefined is false (different types)");
  
  // Objects
  const obj1 = {};
  const obj2 = {};
  const objRef = obj1;
  assert(obj1 === objRef, "object === same object reference is true");
  assert(!(obj1 === obj2), "different object references are not equal with ===");
  
  // Arrays
  const arr1 = [];
  const arr2 = [];
  const arrRef = arr1;
  assert(arr1 === arrRef, "array === same array reference is true");
  assert(!(arr1 === arr2), "different array references are not equal with ===");
}

// Test logical NOT (!)
function testLogicalNOTTypeConversion() {
  assert(!true === false, "!true === false");
  assert(!false === true, "!false === true");
  
  // Conversion to boolean then NOT
  assert(!0 === true, "!0 === true (0 is falsy)");
  assert(!1 === false, "!1 === false (1 is truthy)");
  assert(!"" === true, "!'' === true (empty string is falsy)");
  assert(!"hello" === false, "!'hello' === false (non-empty string is truthy)");
  assert(!null === true, "!null === true (null is falsy)");
  assert(!undefined === true, "!undefined === true (undefined is falsy)");
  assert(!NaN === true, "!NaN === true (NaN is falsy)");
}

// Test logical AND (&&) and OR (||)
function testLogicalOperatorsTypeConversion() {
  // AND (&&) operator
  assert((true && true) === true, "true && true = true");
  assert((true && false) === false, "true && false = false");
  assert((false && true) === false, "false && true = false (short circuits)");
  
  // AND with non-boolean values
  assert((5 && 10) === 10, "truthy && truthy = last value");
  assert((0 && 10) === 0, "falsy && truthy = first value (short circuits)");
  assert((10 && 0) === 0, "truthy && falsy = last value");
  assert(("hello" && 5) === 5, "string && number = last value (all truthy)");
  assert((null && "hello") === null, "null && string = null (first falsy value)");
  
  // OR (||) operator
  assert((true || false) === true, "true || false = true");
  assert((false || true) === true, "false || true = true");
  assert((false || false) === false, "false || false = false");
  
  // OR with non-boolean values
  assert((5 || 10) === 5, "truthy || truthy = first value (short circuits)");
  assert((0 || 10) === 10, "falsy || truthy = first truthy value");
  assert((10 || 0) === 10, "truthy || falsy = first value (short circuits)");
  assert((null || undefined || 0 || "") === "", "all falsy = last value");
  assert((null || undefined || 5) === 5, "mixed falsy/truthy = first truthy value");
}

// Run all tests
const functions = [
    testAdditionTypeConversion,
    testArithmeticTypeConversion,
    testModuloTypeConversion,
    testComparisonTypeConversion,
    testEqualityTypeConversion,
    testStrictEqualityTypeConversion,
    testLogicalNOTTypeConversion,
    testLogicalOperatorsTypeConversion];

for(let testFunction of functions) {
    try {
        testFunction();

    } catch (error) {
        assert(false, "method "+testFunction+" error "+error);
    }
}
