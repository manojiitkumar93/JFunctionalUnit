## JFunctionalUnit
Main theme of JFunctionalUint is to find whether a method is implemented in a functional way i.e, is it a function. If a method is violating functioanl rules then JFunctionalUnit will point out at which places they are violated.

## What rules should a method follow to say it is functionally written:
- Always evaluate same result for same in-put.
- Method should not be a void.
- Method should not depend on any other external dependencies like IO operations.
- If a method is accessing any global variable, then it should be immutable object.
- If a method is calling some other mentods internally, then for it be be functional internally calling methods should be functional.
