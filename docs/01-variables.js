/**
 * JavaScript Variables Documentation
 * 
 * This file demonstrates the ways to declare variables in JavaScript
 * using let and const declarations, and shows examples of different data types.
 * 
 * All examples follow strict mode rules.
 */

// ===== Variable Declarations =====

// 1. let - block-scoped, can be updated but not redeclared in same scope
let modernVariable = "I am a let variable";
modernVariable = "I can be updated but not redeclared in same scope";

// 2. const - block-scoped, cannot be updated or redeclared
const constantVariable = "I cannot be changed";
// constantVariable = "This would throw an error"; // Invalid

// ===== Strict Mode =====
// In strict mode:
// - Variables must be declared before use
// - Assigning to undeclared variables throws an error
// - Deleting variables or functions is not allowed
// - Duplicate parameter names are not allowed

// ===== Data Types =====

// 1. Boolean - true or false
const booleanTrue = true;
const booleanFalse = false;

// 2. Number - integers
const integer = 42;
const negativeInteger = -15;
const hexInteger = 0xFF;       // Hexadecimal: 255
const binaryInteger = 0b1010;  // Binary: 10

// 3. Number - floating point (decimal)
const decimal = 3.14159;
const scientificNotation = 5e3; // 5000

// 4. BigInt - for integers larger than Number can represent
const longNumber = 9007199254740991; // BigInt literal with 'n' suffix
const longHexNumber = 0xFFFFFFFFFFFFFF; // Hexadecimal BigInt

// 5. String - text
const singleQuoteString = 'This is a string with single quotes';
const doubleQuoteString = "This is a string with double quotes";
const templateString = `This is a template string with interpolation: ${integer}`;

// 6. Array (list)
const numberArray = [1, 2, 3, 4, 5];
const mixedArray = [1, 'text', true, null, {key: 'value'}];
const nestedArray = [1, [2, 3], [4, [5, 6]]];

// 7. Object - key-value pairs
const person = {
    name: 'John',
    age: 30,
    isEmployed: true,
    skills: ['JavaScript', 'HTML', 'CSS'],
    address: {
        street: 'Main St',
        city: 'New York'
    }
};

// 8. Special types
const nullValue = null;
const undefinedValue = undefined;

// 9. Symbol - unique identifiers
const uniqueId = Symbol('description');

// 10. Type conversion examples
const stringToNumber = Number('42');         // 42
const numberToString = String(42);           // '42'
const booleanToNumber = Number(true);        // 1
const objectToString = String({key: 'value'}); // '[object Object]'
const hexToDecimalStr = String(parseInt('FF', 16)); // '255'

// Real-world usage example
const config = {
    maxRetries: 3,
    timeout: 0xEA60, // 60000ms (60 seconds) in hexadecimal
    enabled: true,
    endpoints: ['api/data', 'api/users'],
    retryDelay: 1000,
    onSuccess: function() {
        console.log('Operation successful');
    }
};
