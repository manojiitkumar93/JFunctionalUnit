## JFunctionalUnit
Main theme of JFunctionalUint is to find whether a method is implemented in a functional way i.e, is it a function. If a method is violating functioanl rules then JFunctionalUnit will point out at which places they are violated.

## What rules should a method follow to say it is functionally written:
- Always evaluate same result for same in-put.
- Method should not be a void.
- Method should take arguments.
- Method should not depend on any other external dependencies like IO operations.
- If a method is accessing any global variable, then it should be immutable object.
- If a method is calling some other mentods internally, then for it be be functional internally calling methods should be functional.

### Methods which are not called as functions
- Void Methods
``` 
    public void method1(Object input){
       instruction1;
       instruction2;
    }
    
``` 
- Methods with IO operations (Log,Print statements, File operations, Data-Base operations, etc)
```
 public int method2(int input) {
        System.out.println(input);
        logger.info("input " + input);
        return input;
    }
```
- Methods calling  (Log,Print statements, File operations, Data-Base operations, etc)
```
 public int method3(int input) {
        System.out.println(input);
        logger.info("input " + input);
        return input;
    }
```
- Methods calling another method which is not a function
```
 public int method4(int input) {
       instruction1;
       instruction2;
       output = privateMethod(input);
        return output;
    }
    
  // this method is not a function as it involves in IO operation.
  private int privateMethod(int input){
      instruction1;
      logger.info("some details about the process"+input);
      return output;
```
- Methods reffering global variables.
```
public class SomeClass{
  private static int count = 0;
  private static final List<Integer> list = new ArrayList<>();
  // Method accessing non-final objects
 public int method4(int input) {
        count = count +1;
        instruction1;
        instruction2;
        return output;
    }
   // Method accessing final non-primitive objects
 public int method5(int input) {
        list.add(input)
        instruction1;
        instruction2;
        return output;
    }
}
```
- Methods with no arguments
``` 
    public Object method6(){
       instruction1;
       instruction2;
       return output
    }
``` 
- Abstract and Interface methods
```
 public interface SomeClass{
 // methods in inteface are not functions
 public Object method(Object input);
}

public abstract SomeClass{
 // abstract methods are not functions
 public abstract Object method(Object input);
}
```
### What we can expect from first version...
JFunctionalUnit indentifies all the above mentioned examples as non-functional methods (For IO operations identifies only print and log statements). And for a method which is internally calling other methods, it tests only those methods which are implemented by the user (not methods reffered from other dependencies).


###How to use it..
- Add JFunctionalUint jar as your dependency.
- Instantiate FunctionalityTester class...
```
//filePath should specify packageFolder with .class file
String filePath = "pacakageFolder/SomeClass.class" 
FunctionalityTester ft = new FunctionalityTester(filePath)

// Tests the method
String reasonsString = functionalityTester.testMethod("method1");
System.out.println(reasonsString);

// Tests the method by skipping log statements
reasonsString = functionalityTester.testMethod("method1", true, false);
System.out.println(reasonsString);

// Tests the method by skipping print statements
reasonsString = functionalityTester.testMethod("method1", false, true);
System.out.println(reasonsString);

// Tests the method by skipping print and log statementsts
reasonsString = functionalityTester.testMethod("method1", true, true);
System.out.println(reasonsString);
```
- Test overloading methods : To test overloading method, user has to pass both method name and argument class names list. For example : 
```
public int method(List<Integer> list,int number){
    instruction1;
    instruction2;
    return output;
}

// To test above overloading method user need to pass the inputs as..
List<String> argumentsClassNameList = new ArrayList<>();
 argumentsClassNameList.add("java.util.List");
 argumentsClassNameList.add("int");
 reasonsString = functionalityTester.testMethod("method",argumentsClassNameList);
 System.out.println(reasonsString);
 
 //NOTE arguments list should have the arguments class names in order similar to the order in which method has. Because compiler recognizes same overloading method with arguments in different order as two different methods i.e
 
 public int method1(List<Integer> list,int number){
    instruction1;
    instruction2;
    return output;
}

public int method1(int number,List<Integer> list){
    instruction1;
    instruction2;
    return output;
}
```


    
