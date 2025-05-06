function testClosureAccess() {
    let sum = 0;
    const
     y = 1;

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
    console.log(sum);
    return sum;
}

let avg = 0;
for(let i = 0; i < 40; i++) {
    const startTime = Date.now();
    testClosureAccess();
    const endTime = Date.now();
    console.log(`Task duration: ${endTime - startTime} milliseconds`);
    avg+=endTime-startTime;
}
console.log("avg", avg/40.0)
