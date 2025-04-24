// Test file for 02-expressions.js
// Tests the functionality described in the expressions chapter

// Test arithmetic operators
function testArithmeticOperators() {
  // Basic arithmetic
  assert(5 + 3 === 8, "Addition works");
  assert(10 - 4 === 6, "Subtraction works");
  assert(6 * 7 === 42, "Multiplication works");
  assert(20 / 4 === 5, "Division works");
  assert(17 % 3 === 2, "Modulus works");
  //TODO assert(2 ** 3 === 8, "Exponentiation works");
  
  // Unary operators
  assert(+"42" === 42, "Unary plus converts string to number");
  assert(-42 === -42, "Unary minus negates number");
  
  // Increment/decrement
  let num = 5;
  let postInc = num++;
  assert(postInc === 5 && num === 6, "Post-increment returns original then increments");
  
  let num2 = 5;
  let preInc = ++num2;
  assert(preInc === 6 && num2 === 6, "Pre-increment increments then returns value");
  
  let num3 = 8;
  let postDec = num3--;
  assert(postDec === 8 && num3 === 7, "Post-decrement returns original then decrements");
  
  let num4 = 8;
  let preDec = --num4;

  assert(preDec === 7 && num4 === 7, "Pre-decrement decrements then returns value");
  
  // Assignment operators
  let val = 10;
  val += 5;
  assert(val === 15, "+= assignment works");
  
  val -= 3;
  assert(val === 12, "-= assignment works");
  
  val *= 2;
  assert(val === 24, "*= assignment works");
  
  val /= 6;
  assert(val === 4, "/= assignment works");
  
  val %= 3;
  assert(val === 1, "%= assignment works");
}

// Test relational operators
function testRelationalOperators() {
  // Equality operators
  assert(5 == "5", "== tests equality with type conversion");
  assert(5 === 5, "=== tests strict equality without type conversion");
  assert(5 !== "5", "!== tests strict inequality");
  assert(5 != 6, "!= tests inequality with type conversion");
  
  // Comparison operators
  assert(10 > 5, "> works for greater than");
  assert(10 >= 10, ">= works for greater than or equal to");
  assert(5 < 10, "< works for less than");
  assert(5 <= 5, "<= works for less than or equal to");
  
  // Object equality
  const obj1 = { a: 1 };
  const obj2 = { a: 1 };
  assert(obj1 !== obj2, "Different objects are not strictly equal even with same content");
  
  const sameObj = obj1;
  assert(obj1 === sameObj, "Same object reference is strictly equal");
  
  // Special equality cases
  assert(null == undefined, "null == undefined is true");
  assert(null !== undefined, "null !== undefined is true");
}

// Test logical operators
function testLogicalOperators() {
  // Basic operators
  assert(true && true, "&& returns true when both operands are true");
  assert(!(false && true), "&& returns false when one operand is false");
  assert(true || false, "|| returns true when one operand is true");
  assert(!false, "! negates boolean value");
  
  // Short-circuit evaluation
  let evaluated = false;
  const result1 = false && (evaluated = true);
  assert(!evaluated && result1 === false, "&& short-circuits when first operand is false");
  
  let evaluated2 = false;
  const result2 = true || (evaluated2 = true);
  assert(!evaluated2 && result2 === true, "|| short-circuits when first operand is true");
  
  // Logical operators with non-boolean values
  assert((0 && "anything") === 0, "&& returns first falsy value");
  assert(("" || "default") === "default", "|| returns first truthy value");
  assert((42 && "value") === "value", "&& returns last value when all are truthy");
  
  // Nullish coalescing
  assert((null ?? "default") === "default", "?? returns right side when left is null");
  assert((undefined ?? "default") === "default", "?? returns right side when left is undefined");
  assert((0 ?? "default") === 0, "?? returns left side when it's not null/undefined even if falsy");
  
  // Optional chaining
  const user = { profile: { name: "John" } };
  assert(user?.profile?.name === "John", "Optional chaining works for existing properties");
  assert(user?.settings?.theme === undefined, "Optional chaining returns undefined for non-existent properties");
  user?.settings?.value = 3;
  user?.settings?.close(true);
}

  // Optional chaining with array access and function calls
  const arr = ["a", "b", "c"];
  assert(arr?.[1] === "b", "Optional chaining works with array access");
  assert(arr?.[5] === undefined, "Optional chaining returns undefined for non-existent array indices");

  const obj = {
    method: function(x) { return x * 2; }
  };
  assert(obj?.method?.(5) === 10, "Optional chaining works with method calls");
  assert(obj?.nonexistent?.(5) === undefined, "Optional chaining returns undefined for non-existent methods");
  
  const nullObj = null;
  assert(nullObj?.[0] === undefined, "Optional chaining with array access returns undefined when object is null");
  assert(nullObj?.(5) === undefined, "Optional chaining with function call returns undefined when object is null");


// Test string concatenation
function testStringConcatenation() {
  // Basic concatenation
  assert("Hello" + " " + "World" === "Hello World", "String concatenation with + works");
  
  // Template literals
  const name = "Alice";
  assert(`Hello, ${name}!` === "Hello, Alice!", "Template literals with interpolation work");
  assert(`2 + 2 = ${2 + 2}` === "2 + 2 = 4", "Template literals with expressions work");
  
  // Concatenation with other types
  assert("Number: " + 42 === "Number: 42", "Concatenating string with number works");
  assert("Boolean: " + true === "Boolean: true", "Concatenating string with boolean works");
  
  // Array join

  assert(["Apple", "Banana", "Cherry"].join(", ") === "Apple, Banana, Cherry", "Array join method works");
}

// Test length operator
function testLengthOperator() {
  // String length
  assert("Hello".length === 5, "String length property works");
  
  // Array length
  assert([1, 2, 3, 4, 5].length === 5, "Array length property works");
  
//  // Changing array length
//  let arr = [1, 2, 3];
//  arr.length = 5;
//  assert(arr.length === 5, "Increasing array length works");
//  assert(arr[3] === undefined, "New array elements are undefined");
//
//  arr.length = 2;
//  assert(arr.length === 2, "Decreasing array length works");
//  assert(arr[2] === undefined, "Truncated elements are removed");
}

// Test operator precedence
function testOperatorPrecedence() {
  assert(3 + 4 * 5 === 23, "Multiplication has higher precedence than addition");
  assert((3 + 4) * 5 === 35, "Parentheses override default precedence");
  assert(3 + 4 * 5 / 2 === 13, "Multiplication and division have same precedence (left to right)");
  assert(!(3 > 2 && 2 < 1), "Comparison operators have higher precedence than logical AND");
  assert(3 > 2 || (2 < 1 && 0 < 1) === true, "Logical AND has higher precedence than logical OR");
}

// Test practical calculation
function testCalculation() {
  function calculateTotal(items, tax, discount) {
    const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const discountAmount = discount ? subtotal * (discount / 100) : 0;
    const taxAmount = (subtotal - discountAmount) * (tax / 100);
    const total = subtotal - discountAmount + taxAmount;

    return {
      subtotal: parseFloat(subtotal),
      discount: parseFloat(discountAmount),
      tax: parseFloat(taxAmount),
      total: parseFloat(total)
    };
  }
  
  const orderItems = [
    { name: "Product 1", price: 10.99, quantity: 2 },
    { name: "Product 2", price: 24.99, quantity: 1 },
    { name: "Product 3", price: 5.49, quantity: 3 }
  ];
  
  const result = calculateTotal(orderItems, 8.25, 10);
  
  // Calculate expected values
  const expectedSubtotal = 10.99 * 2 + 24.99 * 1 + 5.49 * 3;
  const expectedDiscount = expectedSubtotal * 0.1;
  const expectedTax = (expectedSubtotal - expectedDiscount) * 0.0825;
  const expectedTotal = expectedSubtotal - expectedDiscount + expectedTax;
  
  assert(Math.abs(result.subtotal - parseFloat(expectedSubtotal.toFixed(2))) < 0.01, 
         "Subtotal calculation is correct");
  assert(Math.abs(result.discount - parseFloat(expectedDiscount.toFixed(2))) < 0.01, 
         "Discount calculation is correct");
  assert(Math.abs(result.tax - parseFloat(expectedTax.toFixed(2))) < 0.01, 
         "Tax calculation is correct");
  assert(Math.abs(result.total - parseFloat(expectedTotal.toFixed(2))) < 0.01, 
         "Total calculation is correct");
}

//Run all tests
const functions = [testArithmeticOperators,
    testRelationalOperators,
    testLogicalOperators,
    testStringConcatenation,
    testLengthOperator,
    testOperatorPrecedence,
    //testCalculation
    ];

for(let testFunction of functions) {
    try {
        testFunction();

    } catch (error) {
        assert(false, "method "+testFunction+" error "+error);
    }
}
