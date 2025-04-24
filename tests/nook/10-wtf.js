    assert([] + [] === "", "Array plus array is empty string");
    assert([] + {} === "[object Object]", "Array plus object gives [object Object]");
    assert({} + {} === "[object Object][object Object]", "Object plus object concatenates their string representations");
    assert(+"" === 0, "Empty string converts to 0");


    assert([2,3] == "2,3", "Array auto-converts to string for comparison");

    // Array weirdness
    assert([] == false, "Empty array equals false");
    assert([] == ![], "Empty array equals negated empty array");
    assert([2,3] == "2,3", "Array auto-converts to string for comparison");

    // Type coercion madness
    assert({} + [] === 0, "Object plus array is 0");

    // Numeric conversion weirdness
    assert(+[] === 0, "Empty array converts to 0");
    assert(+{} === NaN, "Object converts to NaN");
    assert(+"123" === 123, "String number converts correctly");


  assert(!{} === false, "!{} === false (object is truthy)");
  assert(![] === false, "![] === false (array is truthy even if empty)");

    // Equality fun
    assert(null == undefined, "null equals undefined with ==");
    assert(null !== undefined, "null not strictly equals undefined");
    assert("0" == false, "String zero equals false");
    assert("false" != false, "String 'false' does not equal boolean false");

    // NaN behavior
    assert(NaN !== NaN, "NaN is not equal to itself");
    //assert(Object.is(NaN, NaN), "But Object.is can tell NaN equals NaN");

    // Math oddities
    assert(0.1 + 0.2 !== 0.3, "0.1 + 0.2 is not exactly 0.3");
    assert(9999999999999999 === 10000000000000000, "Large integers lose precision");

    // Operator precedence surprises
    assert(1 < 2 < 3 === true, "Chained comparison 1<2<3 is true");
    assert(3 > 2 > 1 === false, "But 3>2>1 is false!");

  assert([] % 2 === 0, "empty array % number = 0 % number");
  assert([5] % 2 === 1, "single item array % number = number % number");

  assert("" == 0, "empty string == 0 is true ('' converts to 0)");

  // Arrays and objects
  assert([] == 0, "empty array == 0 is true ([] converts to 0)");
  assert([1] == 1, "[1] == 1 is true ([1] converts to '1' then to 1)");
