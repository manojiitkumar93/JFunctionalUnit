## JFunctionalUnit
Main theme of JFunctionalUint is to find whether a method is implemented in a functional way i.e, is it a function. If a method is violating functioanl rules then JFunctionalUnit will point out at which places they are violated.

## What rules should a method follow to say it is functionally written:
- Does it accessing any global variables,even if it accesses any global variables those should be constants.
- Always evaluate same result for same in-put.
- Method should not be a void.
