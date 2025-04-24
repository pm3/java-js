  // Create sparse array
  let arr3 = [];
  arr3[0] = 1;
  arr3[2] = 3;
  assert(arr3.length === 3, "Sparse array has correct length");
  assert(arr3[1] === undefined, "Missing array item is undefined");
