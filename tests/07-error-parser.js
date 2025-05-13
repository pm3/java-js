/*parse-error*/
// Missing closing parenthesis
let x = (1 + 2;

/*parse-error*/
// Invalid variable name
let 1variable = 5;

/*parse-error*/
// Missing quotes in string
let str = hello world;

/*parse-error*/
// Unexpected token
let y = 1 2 3;

/*parse-error*/
// Invalid function declaration
function() {
  return 1;
}

/*parse-error*/
// Missing closing brace
if(true) {
  console.log("test"

/*parse-error*/
// Invalid property access
let obj2 = {};
obj2.;

/*parse-error*/
// Invalid for loop syntax
for(let i=0 i<5 i++) {
}

/*parse-error*/
// Unexpected end of input
let z = {

/*parse-error*/
// Invalid switch syntax
switch(x)
  case 1:
    break;
}

/*parse-error*/
// Invalid template literal
let template = `Hello ${name;

/*parse-error*/
// Invalid regex
let regex = /[a-z/;

/*parse-error*/
// Missing semicolons between statements
let a = 1 let b = 2

/*parse-error*/
// No separator between statements
a=1 b=2 a<b

/*parse-error*/
// Missing statement separator in for loop
for(let i=0; i<5 console.log(i)){}

/*parse-error*/
// Invalid line continuation
let longString = "This is a very \ long string"

/*parse-error*/
// Multiple statements on one line without separator
let x = 1 let y = 2 let z = 3

/*parse-error*/
// Invalid line break in expression
let sum = 1 +
* 2;

/*parse-error*/
// Invalid multiline string
let str = 'This is a
multiline string';

/*parse-error*/
// Invalid multiline string ending with \
const str1 = "This is a \
multi-line string \
using backslash";

/*parse-error*/
// Assignment to constant variable
const constTest = "test";
constTest = "update";

/*parse-error*/
// Missing line terminator
let a = 1 let b = 2 let c = 3

/*parse-error*/
// Invalid function parameters formatting
function test(a
b,c
) {}

/*parse-error*/
// Invalid string literal with unescaped newline
let str2 = "Hello
world";

/*parse-error*/
// Unterminated string literal
let str3 = "This string never ends

/*parse-error*/
// Invalid escape sequence in string - \z is not valid
let str4 = "Invalid escape \z sequence";

/*parse-error*/
// Invalid escape sequence in string - \k is not valid
let strK = "Invalid escape \k sequence";

/*parse-error*/
// Invalid escape sequence in string - \8 is not valid (only \0-\7 allowed)
let strNum = "Invalid escape \8 sequence";

/*parse-error*/
// Mixed quotes in string literal
let str5 = "This string has 'mixed" quotes';

/*parse-error*/
// String with invalid Unicode escape - non-hex digit
let str6 = "Invalid unicode \u123G";

/*parse-error*/
// String with invalid Unicode escape - too short
let str7 = "Invalid unicode \u12";

/*parse-error*/
// String with invalid Unicode escape - missing digits
let str8 = "Invalid unicode \u";

/*parse-error*/
// String with invalid hex escape - non-hex digit
let str9 = "Invalid hex escape \x3G";

/*parse-error*/
// String with invalid hex escape - too short
let str10 = "Invalid hex escape \x2";

/*parse-error*/
// enum is a future reserved word
const enum = 5;

/*parse-error*/
// await is a future reserved word
let await = 10;

/*parse-error*/
// SyntaxError in strict mode
eval('let y1 = 1; let y1 = 2;');

/*parse-error*/
// Missing initializer in const declaration
const x;
