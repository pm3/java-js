/**
 * JavaScript Statements Documentation
 * 
 * This file demonstrates the various types of statements in JavaScript
 * including conditional statements, loops, and other control flow structures.
 * 
 * All examples follow strict mode rules.
 */

// Enable strict mode
"use strict";

// ===== Conditional Statements =====

// 1. if statement - executes a block of code if the condition is true
if (true) {
    // Code to execute when condition is true
    const insideBlock = true;
}

// 2. if...else statement - executes one block if true, another if false
if (false) {
    // Code to execute when condition is true
    const insideIf = true;
} else {
    // Code to execute when condition is false
    const insideElse = true;
}

// 3. if...else if...else statement - for multiple conditions
if (false) {
    // Code for first condition
    const condition1 = true;
} else if (true) {
    // Code for second condition
    const condition2 = true;
} else {
    // Code if no conditions are met
    const noCondition = true;
}

// 4. ternary operator - shorthand conditional expression
const age = 20;
const status = age >= 18 ? 'adult' : 'minor';

// 5. switch statement - selects one of many code blocks to execute
const fruit = 'apple';
switch (fruit) {
    case 'orange':
        // Code to execute if fruit === 'orange'
        break;
    case 'apple':
        // Code to execute if fruit === 'apple'
        break;
    default:
        // Code to execute if fruit doesn't match any case
}

// ===== Loop Statements =====

// 1. for loop - repeats until a condition is false
for (let i = 0; i < 5; i++) {
    // Code to execute on each iteration
    const value = i * 2;
}

// 2. for...in loop - iterates over all enumerable properties of an object
const person = { name: 'John', age: 30 };
for (const property in person) {
    // property will be 'name', then 'age'
    // person[property] will be 'John', then 30
    const propValue = person[property];
}

// 3. for...of loop - iterates over iterable objects (arrays, strings, etc.)
const colors = ['red', 'green', 'blue'];
for (const color of colors) {
    // color will be 'red', then 'green', then 'blue'
    const upperColor = color+" "+color;
}

// 4. while loop - repeats as long as condition is true
let count = 0;
while (count < 5) {
    // Code to execute on each iteration
    const squared = count * count;
    count++;
}

// 5. do...while loop - executes once, then repeats while condition is true
let num = 0;
do {
    // Code to execute on each iteration
    const isEven = num % 2 === 0;
    num++;
} while (num < 5);

// 6. break statement - exits a loop or switch statement
for (let i = 0; i < 10; i++) {
    if (i === 5) {
        break; // exits the loop when i is 5
    }
}

// 7. continue statement - skips the current iteration of a loop
for (let i = 0; i < 10; i++) {
    if (i % 2 === 0) {
        continue; // skips even numbers
    }
    // This code only runs for odd numbers
    const oddNumber = i;
}

// ===== Error Handling =====

// 1. try...catch statement - handles errors
try {
    // Code that might throw an error
    throw new Error('Something went wrong');
} catch (error) {
    // Code to handle the error
    console.error(error);
}

// 2. try...finally statement - ensures cleanup code runs
try {
    // Code that might throw an error
    console.log('Attempting operation...');
    // Some operation that may fail
    const result = 42;
} finally {
    // Cleanup code that always executes, even if an error occurs
    console.log('Cleanup complete');
    // This code always runs, regardless of whether an error occurred
}

// 3. try...catch...finally statement - handles errors and ensures cleanup
try {
    // Code that might throw an error
    throw new Error('Operation failed');
} catch (error) {
    // Code to handle the error
    console.error('Caught error:', error);
} finally {
    // Cleanup code that always executes
    console.log('Finishing up, regardless of success or failure');
}

// 4. Nested try...catch - handling different error types
try {
    try {
        // Code that might throw an error
        throw new TypeError('Invalid type');
    } catch (typeError) {
        // Only catches TypeError
        if (typeError instanceof TypeError) {
            console.error('Type error:', typeError);
        } else {
            // Re-throw other errors
            throw typeError;
        }
    }
} catch (otherError) {
    // Catches any errors not caught by the inner catch
    console.error('Other error:', otherError);
}

// ===== Other Statements =====

// 1. block statement - groups statements within curly braces
{
    const localVar = 'only available inside this block';
    // More statements here
}

// 2. empty statement - provides no statement when one is required
; // Does nothing

// 3. throw statement - creates a custom error
 throw 'This is an error message'; // Commented out to prevent execution

// Real-world usage example - a simple command interpreter
const command = 'SORT';
let result;

switch (command.toUpperCase()) {
    case 'LIST':
        result = 'Listing items...';
        break;
    case 'SORT':
        result = 'Sorting items...';
        break;
    case 'FILTER':
        result = 'Filtering items...';
        break;
    default:
        result = 'Unknown command';
}

// Using loops with conditionals for data processing
const data = [1, 2, null, 3, undefined, 4, 5];
const processedData = [];

for (let i = 0; i < data.length; i++) {
    // Skip null or undefined values
    if (data[i] == null) {
        continue;
    }
    
    // Transform data
    if (data[i] % 2 === 0) {
        processedData.push(data[i] * 2); // Double even numbers
    } else {
        processedData.push(data[i]); // Keep odd numbers as is
    }
    
    // Stop if we've processed enough items
    if (processedData.length >= 5) {
        break;
    }
}
