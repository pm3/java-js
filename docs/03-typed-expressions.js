/**
 * JavaScript Type Conversion Documentation
 * =======================================
 * 
 * This document explains how JavaScript converts types during various operations.
 * All examples follow strict mode rules.
 * 
 * Contents:
 * ---------
 * 1. Addition (+)
 * 2. Subtraction, Multiplication, Division (-, *, /)
 * 3. Modulo (%)
 * 4. Comparison Operators (<, >, <=, >=)
 * 5. Equality Operators (==, !=)
 * 6. Strict Equality Operators (===, !==)
 * 7. Logical NOT (!)
 * 8. Logical AND (&&) and OR (||)
 */

// Enable strict mode
"use strict";

/**
 * Addition (+) - Detailed Type Conversion Table
 * --------------------------------------------
 * The addition operator performs different operations based on the types of its operands:
 * 
 * 1. String Priority: If either operand is a string, the other is converted to string (concatenation)
 * 2. Object/Array: Objects are first converted to primitives via valueOf() or toString()
 * 3. Non-string primitives: Converted to numbers before addition
 * 
 * Type Conversion Rules for Addition:
 * ----------------------------------
 * 
 * | Left Operand | Right Operand | Result                                     |
 * |--------------|---------------|-------------------------------------------|
 * | number       | number        | number (arithmetic sum)                    |
 * | string       | any type      | string (concatenation)                     |
 * | any type     | string        | string (concatenation)                     |
 * | boolean      | number        | number (boolean converts to 0 or 1)        |
 * | number       | boolean       | number (boolean converts to 0 or 1)        |
 * | boolean      | boolean       | number (booleans convert to 0 or 1)        |
 * | null         | number        | number (null converts to 0)                |
 * | number       | null          | number (null converts to 0)                |
 * | undefined    | any type      | NaN (undefined converts to NaN)            |
 * | any type     | undefined     | NaN (undefined converts to NaN)            |
 * | object/array | non-string    | Depends on object's primitive conversion   |
 * | non-string   | object/array  | Depends on object's primitive conversion   |
 * 
 * Examples with Numbers:
 * ---------------------
 * 5 + 3         = 8        // number + number = arithmetic sum
 * 5 + (-3)      = 2        // number + negative number = arithmetic sum
 * 5 + 0         = 5        // number + 0 = original number 
 * 5 + NaN       = NaN      // any operation with NaN results in NaN
 * 
 * Examples with Strings:
 * ---------------------
 * "hello" + "world" = "helloworld"  // string + string = concatenation
 * "5" + "3"         = "53"          // string + string = concatenation
 * "5" + 3           = "53"          // string + number = string concatenation
 * 5 + "3"           = "53"          // number + string = string concatenation
 * "" + 123          = "123"         // empty string + number = number as string
 * 
 * Examples with Booleans:
 * ----------------------
 * true + true    = 2        // true converts to 1, so 1 + 1 = 2
 * false + false  = 0        // false converts to 0, so 0 + 0 = 0
 * true + false   = 1        // 1 + 0 = 1
 * true + 5       = 6        // true converts to 1, so 1 + 5 = 6
 * false + 5      = 5        // false converts to 0, so 0 + 5 = 5
 * "x" + true     = "xtrue"  // string + boolean = string concatenation
 * true + "x"     = "truex"  // boolean + string = string concatenation
 * 
 * Examples with null/undefined:
 * ----------------------------
 * null + null       = 0        // null converts to 0, so 0 + 0 = 0
 * null + 5          = 5        // null converts to 0, so 0 + 5 = 5
 * null + true       = 1        // null (0) + true (1) = 1
 * null + "hello"    = "nullhello" // null + string = string concatenation
 * undefined + undefined = NaN  // undefined converts to NaN
 * undefined + 5     = NaN      // operations with undefined result in NaN
 * undefined + "hello" = "undefinedhello" // undefined + string = string concatenation
 * 
 * Examples with Arrays:
 * --------------------
 * [] + []         = ""        // empty arrays convert to empty strings
 * [1,2] + [3,4]   = "1,23,4"  // arrays convert to comma-separated strings
 * [] + 5          = "5"       // empty array becomes "", then ""+5="5" 
 * [1,2] + 5       = "1,25"    // array becomes "1,2", then "1,2"+5="1,25"
 * [1] + true      = "1true"   // [1] becomes "1", then "1"+true="1true"
 * 
 * Single Item Arrays (special case):
 * ---------------------------------
 * [1] + [2]       = "12"      // single item arrays convert to strings without commas
 * [1] + []        = "1"       // [1] becomes "1", [] becomes "", result is "1"
 * [] + [1]        = "1"       // [] becomes "", [1] becomes "1", result is "1"
 * [1] + 2         = "12"      // [1] becomes "1", then "1"+2="12"
 * 2 + [1]         = "21"      // 2 is converted to string when added to array
 * [1] + true      = "1true"   // [1] becomes "1", true becomes "true"
 * [1] + null      = "1null"   // [1] becomes "1", null becomes "null"
 * [1] + undefined = "1undefined" // [1] becomes "1", undefined becomes "undefined"
 * [{}] + 1        = "[object Object]1" // object in array converts to string
 * [[]] + 1        = "1"       // nested empty array converts to empty string
 * 
 * Examples with Objects:
 * ---------------------
 * ({}) + ({})     = "[object Object][object Object]"  // objects convert to "[object Object]"
 * ({}) + 5        = "[object Object]5"                // object + number
 * ({}) + true     = "[object Object]true"             // object + boolean
 * ({}) + [1,2]    = "[object Object]1,2"              // object + array
 * 
 * Special Cases (strict mode):
 * -------------------------
 * ({}) + []       = "[object Object]"  // In strict mode, {} is always treated as an object literal
 * [] + ({})       = "[object Object]"  // empty array becomes "", then concatenates with object
 */

/**
 * Subtraction, Multiplication, Division (-, *, /)
 * ----------------------------------------------
 * - Operands are converted to numbers before operation
 * 
 * Examples:
 * 5 - "2"    = 3         // string converts to number
 * "5" - 2    = 3         // string converts to number
 * true - 1   = 0         // boolean converts to number (true -> 1)
 * 5 * "2"    = 10        // string converts to number
 * "10" / 2   = 5         // string converts to number
 * "10" / "2" = 5         // strings convert to numbers
 */

/**
 * Modulo (%)
 * ----------
 * - The modulo operator returns the remainder after division
 * - Both operands are converted to numbers before operation
 * - Follows the same type conversion rules as other arithmetic operators
 * 
 * Examples:
 * 5 % 2        = 1         // 5 divided by 2 gives remainder 1
 * "10" % 3     = 1         // string "10" converts to number 10
 * 10 % "3"     = 1         // string "3" converts to number 3
 * true % 2     = 1         // true converts to 1, which gives remainder 1
 * 5 % false    = NaN       // false converts to 0, modulo by 0 is NaN
 * 5 % null     = NaN       // null converts to 0, modulo by 0 is NaN
 * 5 % undefined = NaN      // undefined converts to NaN
 * [] % 2       = 0         // empty array converts to 0, remainder is 0
 * [5] % 2      = 1         // [5] converts to 5, remainder is 1
 * ({}) % 2     = NaN       // object converts to NaN
 */

/**
 * Comparison operators (<, >, <=, >=)
 * -----------------------------------
 * - If both operands are strings, lexicographical (dictionary) comparison is performed
 * - Otherwise, operands are converted to numbers before comparison
 * 
 * Examples:
 * 5 > 3      = true      // number comparison
 * "5" > 3    = true      // string "5" converts to number 5
 * "5" > "3"  = true      // lexicographical comparison ("5" comes after "3")
 * "10" > "3" = false     // lexicographical ("1" comes before "3")
 * "10" > 3   = true      // string "10" converts to number 10
 */

/**
 * Equality operators (==, !=)
 * --------------------------
 * - If operands are of different types, type conversion occurs
 * - The abstract equality algorithm follows these rules:
 *   1. If types are the same, compare values directly
 *   2. null == undefined (and vice versa)
 *   3. For number and string, convert string to number
 *   4. For boolean and non-boolean, convert boolean to number
 *   5. For object and primitive, convert object to primitive
 * 
 * Examples:
 * 5 == 5     = true      // same type and value
 * 5 == "5"   = true      // string "5" converts to number 5
 * 0 == false = true      // boolean false converts to number 0
 * 1 == true  = true      // boolean true converts to number 1
 * null == undefined = true // special case
 */

/**
 * Strict Equality operators (===, !==)
 * -----------------------------------
 * - No type conversion occurs
 * - Both operands must be of same type and same value to be equal
 * 
 * Examples:
 * 5 === 5     = true     // same type and value
 * 5 === "5"   = false    // different types
 * 0 === false = false    // different types
 * null === undefined = false // different types
 */

/**
 * Logical NOT (!)
 * ---------------
 * - Operand is first converted to boolean
 * - Result is the opposite boolean value
 * 
 * Conversion to boolean:
 * - false, 0, "", null, undefined, NaN convert to false
 * - Everything else converts to true
 * 
 * Examples:
 * !true     = false
 * !false    = true
 * !0        = true      // 0 is falsy
 * !1        = false     // 1 is truthy
 * !""       = true      // empty string is falsy
 * !"hello"  = false     // non-empty string is truthy
 * !null     = true      // null is falsy
 * !undefined = true     // undefined is falsy
 */

/**
 * Logical AND (&&) and OR (||)
 * ----------------------------
 * - Returns one of the operands (not necessarily a boolean)
 * - Evaluates operands from left to right
 * 
 * AND (&&):
 * - Returns first falsy operand encountered
 * - If all operands are truthy, returns the last operand
 * 
 * OR (||):
 * - Returns first truthy operand encountered
 * - If all operands are falsy, returns the last operand
 * 
 * Examples:
 * true && false = false
 * "hello" && 0  = 0     // returns 0 (first falsy value)
 * 5 && "hello"  = "hello" // returns "hello" (last value, all truthy)
 * 
 * false || true = true
 * 0 || "hello"  = "hello" // returns "hello" (first truthy value)
 * 0 || ""       = ""     // returns "" (last value, all falsy)
 */
