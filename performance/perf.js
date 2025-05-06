function testClosureAccess() {
    let sum = 0;
    const y = 1;

    function outer() {
        for (let i = 0; i < 50000; i++) {
            let x = i;
            function inner() {
                sum += x;  // closure access to `x`
            }
            x+=3-y;
            inner();
        }
    }

    outer();
    //print(sum);
    return sum;
}
testClosureAccess();
