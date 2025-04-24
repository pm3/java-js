// Test file for 04-statements.js
// Tests the functionality described in the statements chapter

// Test conditional statements
function testConditionalStatements() {
  // if statement
  let resultIf = "";
  if (true) {
    resultIf = "executed";
  }
  assert(resultIf === "executed", "if statement executes when condition is true");
  
  resultIf = "";
  if (false) {
    resultIf = "executed";
  }
  assert(resultIf === "", "if statement doesn't execute when condition is false");
  
  // if...else statement
  let resultIfElse = "";
  if (true) {
    resultIfElse = "if";
  } else {
    resultIfElse = "else";
  }
  assert(resultIfElse === "if", "if block executes when condition is true");
  
  resultIfElse = "";
  if (false) {
    resultIfElse = "if";
  } else {
    resultIfElse = "else";
  }
  assert(resultIfElse === "else", "else block executes when condition is false");
  
  // if...else if...else statement
  let resultIfElseIf = "";
  if (true) {
    resultIfElseIf = "first";
  } else if (true) {
    resultIfElseIf = "second";
  } else {
    resultIfElseIf = "else";
  }
  assert(resultIfElseIf === "first", "first true condition executes in if...else if chain");
  
  resultIfElseIf = "";
  if (false) {
    resultIfElseIf = "first";
  } else if (true) {
    resultIfElseIf = "second";
  } else {
    resultIfElseIf = "else";
  }
  assert(resultIfElseIf === "second", "second true condition executes when first is false");
  
  resultIfElseIf = "";
  if (false) {
    resultIfElseIf = "first";
  } else if (false) {
    resultIfElseIf = "second";
  } else {
    resultIfElseIf = "else";
  }
  assert(resultIfElseIf === "else", "else block executes when all conditions are false");
  
  // ternary operator
  const ageTernary1 = 20;
  const statusTernary1 = ageTernary1 >= 18 ? 'adult' : 'minor';
  assert(statusTernary1 === 'adult', "ternary operator returns first value when condition is true");
  
  const ageTernary2 = 16;
  const statusTernary2 = ageTernary2 >= 18 ? 'adult' : 'minor';
  assert(statusTernary2 === 'minor', "ternary operator returns second value when condition is false");
  
  // switch statement
  let resultSwitch = "";
  const fruitSwitch = 'apple';
  switch (fruitSwitch) {
    case 'orange':
      resultSwitch = 'orange';
      break;
    case 'apple':
      resultSwitch = 'apple';
      break;
    default:
      resultSwitch = 'other';
  }
  assert(resultSwitch === 'apple', "switch statement executes matching case");
  
  resultSwitch = "";
  const fruitSwitch2 = 'banana';
  switch (fruitSwitch2) {
    case 'orange':
      resultSwitch = 'orange';
      break;
    case 'apple':
      resultSwitch = 'apple';
      break;
    default:
      resultSwitch = 'other';
  }
  assert(resultSwitch === 'other', "switch statement executes default case when no match");
}

// Test loop statements
function testLoopStatements() {
  // for loop
  let forResult = 0;
  for (let i = 0; i < 5; i++) {
    forResult += i;
  }
  assert(forResult === 10, "for loop executes the expected number of times (0+1+2+3+4=10)");
  
  // for...in loop
  const personForIn = { name: 'John', age: 30 };
  const forInResult = [];
  for (const property in personForIn) {
    forInResult.push(property);
  }
  assert(forInResult.includes('name') && forInResult.includes('age'), 
         "for...in loop iterates over object properties");
  
  // for...of loop
  const colorsForOf = ['red', 'green', 'blue'];
  const forOfResult = [];
  for (const color of colorsForOf) {
    forOfResult.push(color);
  }
  assert(forOfResult.join(',') === 'red,green,blue',
         "for...of loop iterates over array elements");
  
  // while loop
  let whileCount = 0;
  let whileResult = 0;
  while (whileCount < 5) {
    whileResult += whileCount;
    whileCount++;
  }
  assert(whileResult === 10, "while loop executes expected number of times");
  
  // do...while loop
  let doWhileCount = 0;
  let doWhileResult = 0;
  do {
    doWhileResult += doWhileCount;
    doWhileCount++;
  } while (doWhileCount < 5);
  assert(doWhileResult === 10, "do...while loop executes expected number of times");
  
  // break statement
  let breakResult = 0;
  for (let i = 0; i < 10; i++) {
    if (i === 5) {
      break;
    }
    breakResult += i;
  }
  assert(breakResult === 10, "break statement exits the loop at specified condition (0+1+2+3+4=10)");
  
  // continue statement
  let continueResult = 0;
  for (let i = 0; i < 5; i++) {
    if (i % 2 === 0) {
      continue;
    }
    continueResult += i;
  }
  assert(continueResult === 4, "continue statement skips iterations (1+3=4)");
}

// Test error handling
function testErrorHandling() {
  // try...catch statement
  let tryCatchResult = "";
  try {
    throw Error('Something went wrong');
    tryCatchResult = "try";
  } catch (error) {
    tryCatchResult = "catch";
  }
  assert(tryCatchResult === "catch", "catch block executes when error is thrown in try");
  
  // try...finally statement
  let tryFinallyResult = [];
  try {
    tryFinallyResult.push("try");
  } finally {
    tryFinallyResult.push("finally");
  }
  assert(tryFinallyResult.join('-') === "try-finally", 
         "finally block always executes after try block");
  
  // try...catch...finally statement
  let tryCatchFinallyResult = [];
  try {
    throw Error('Test error');
    tryCatchFinallyResult.push("try");
  } catch (error) {
    tryCatchFinallyResult.push("catch");
  } finally {
    tryCatchFinallyResult.push("finally");
  }
  assert(tryCatchFinallyResult.join('-') === "catch-finally", 
         "catch and finally blocks execute in order when error occurs");

  // throw statement
  assertError(() => {
    throw Error('Custom error');
  }, 'Custom error');
}

// Test other statements
function testOtherStatements() {
  // block statement and variable scope
  let blockResult = "outside";
  {
    const localVar = "inside";
    blockResult = localVar;
  }
  assert(blockResult === "inside", "block statement creates a scope for statements");
  
  // Testing variable scope
  assertError(() => {
    {
      const blockScopedVar = "block scoped";
    }
    // This should throw an error - trying to access out of scope variable
    print(blockScopedVar);
  }, "blockScopedVar is not defined");
  
  // Combined statements example - command interpreter
  function processCommand(cmd) {
    switch (cmd.toUpperCase()) {
      case 'LIST':
        return 'Listing items...';
      case 'SORT':
        return 'Sorting items...';
      case 'FILTER':
        return 'Filtering items...';
      default:
        return 'Unknown command';
    }
  }
  
  assert(processCommand('SORT') === 'Sorting items...', 
         "combined switch statement works for command interpreter");
  assert(processCommand('unknown') === 'Unknown command', 
         "default case handles unknown commands");
  
  // Testing data processing example
  function processData(data) {
    const processed = [];
    
    for (let i = 0; i < data.length; i++) {
      // Skip null or undefined values
      if (data[i] == null) {
        continue;
      }
      
      // Transform data
      if (data[i] % 2 === 0) {
        processed.push(data[i] * 2); // Double even numbers
      } else {
        processed.push(data[i]); // Keep odd numbers as is
      }
      
      // Stop if we've processed enough items
      if (processed.length >= 3) {
        break;
      }
    }
    
    return processed;
  }
  
  const testData = [1, 2, null, 3, undefined, 4, 5];
  const expectedResult = [1, 4, 3]; // 1 (kept), 2*2=4 (doubled), 3 (kept), then break
  const actualResult = processData(testData);
    assert(actualResult.length === expectedResult.length, "data processing with loops and conditionals works correctly");

  for(let i=0; i<expectedResult.length; i++){
    assert(actualResult[i] === expectedResult[i], "data processing with loops and conditionals works correctly "+i);
  }
}

// Test multi-line statements
function testMultiLineStatements() {
  // Multi-line object literal
  const person = {
    name: "John",
    age: 30,
    address: {
      street: "123 Main St",
      city: "Anytown",
      country: "USA"
    }
  };
  assert(person.name === "John" && person.address.city === "Anytown",
    "Multi-line object literal works correctly");

  // Multi-line array
  const numbers = [
    1,
    2,
    3,
    4,
    5
  ];
  assert(numbers.length === 5 && numbers[4] === 5,
    "Multi-line array works correctly");

  // Multi-line function with multiple statements
  function calculateArea(width, height) {
    const area = width * height;
    const perimeter = 2 * (width + height);
    
    return {
      area: area,
      perimeter: perimeter
    };
  }
  const result = calculateArea(5, 3);
  assert(result.area === 15 && result.perimeter === 16,
    "Multi-line function works correctly");

  // Multi-line template string
  const template = `
    This is a multi-line
    template string with
    ${3 + 4} as calculated value
  `;
  assert(template.includes("7") && template.split("\n").length > 3,
    "Multi-line template string works correctly");

  // Multi-line if-else statement
  let value = 10;
  if (value > 5) {
    value += 2;
    value *= 2;
    value -= 5;
  } else {
    value -= 2;
    value /= 2;
    value += 5;
  }
  assert(value === 19,
    "Multi-line if-else statement works correctly");
}

// Test future reserved words
function testFutureReservedWords() {
  // Test that future reserved words cannot be used as identifiers
  assertError(function() {
    const enum = 5; // enum is a future reserved word
  }, "Unexpected token 'enum'");

  assertError(function() {
    let await = 10; // await is a future reserved word
  }, "Unexpected token 'await'");

  assertError(function() {
    var implements = "test"; // implements is a future reserved word
  }, "Unexpected token 'implements'");

  assertError(function() {
    const interface = {}; // interface is a future reserved word 
  }, "Unexpected token 'interface'");

  assertError(function() {
    let package = []; // package is a future reserved word
  }, "Unexpected token 'package'");

  assertError(function() {
    var private = true; // private is a future reserved word
  }, "Unexpected token 'private'");

  assertError(function() {
    const protected = "test"; // protected is a future reserved word
  }, "Unexpected token 'protected'");

  assertError(function() {
    let public = 42; // public is a future reserved word
  }, "Unexpected token 'public'");
}


// Run all tests
const functions = [
    testConditionalStatements,
    testLoopStatements,
    testErrorHandling,
    testOtherStatements];
for(let testFunction of functions) {
    try {
        testFunction();

    } catch (error) {
        assert(false, "method "+testFunction+" error "+error);
    }
}
