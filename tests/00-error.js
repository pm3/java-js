  // Test 'this' binding with call()
  function getValueFn() {
    return this.value;
  }
  const obj3 = { value: 100 };
  assert(getValueFn.call(obj3) === 100, "'this' can be explicitly bound using call()");
