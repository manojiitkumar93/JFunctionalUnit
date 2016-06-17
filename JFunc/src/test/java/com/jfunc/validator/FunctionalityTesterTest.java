package com.jfunc.validator;

import org.junit.Test;

public class FunctionalityTesterTest {

    private String filePath = "com/jfunc/validator/Example1.class";

    @Test
    public void test_Method_withPrintAndLogStatements() throws Exception {
        FunctionalityTester functionalityTester = new FunctionalityTester(filePath);
        String reasonsString = functionalityTester.testMethod("method1");
        System.out.println(reasonsString);

        // skip log statements
        reasonsString = functionalityTester.testMethod("method1", true, false);
        System.out.println(reasonsString);

        // skip print statements
        reasonsString = functionalityTester.testMethod("method1", false, true);
        System.out.println(reasonsString);

        // skip both log and print statements
        reasonsString = functionalityTester.testMethod("method1", true, true);
        System.out.println(reasonsString);
    }

    @Test
    public void test_Method_callingNonFunctionalMethods() throws Exception {
        FunctionalityTester functionalityTester = new FunctionalityTester(filePath);
        String reasonsString = functionalityTester.testMethod("method2", false, false);
        System.out.println(reasonsString);

        // skipping log and prints statements works only for the input method
        reasonsString = functionalityTester.testMethod("method2", true, false);
        System.out.println(reasonsString);
    }

    @Test
    public void test_Method_callingFunctionalMethods() throws Exception {
        FunctionalityTester functionalityTester = new FunctionalityTester(filePath);
        String reasonsString = functionalityTester.testMethod("method4");
        System.out.println(reasonsString);
    }

    @Test
    public void test_Method_callingNonFunctionalMethodsFromOtherClass() throws Exception {
        FunctionalityTester functionalityTester = new FunctionalityTester(filePath);
        String reasonsString = functionalityTester.testMethod("method3", false, false);
        System.out.println(reasonsString);

        // skipping log and prints statements works only for the input method
        reasonsString = functionalityTester.testMethod("method3", true, true);
        System.out.println(reasonsString);
    }

    @Test
    public void test_Method_CallingNonFunctionalMethodFromItsOwnClassAndOtherClasses() throws Exception {
        FunctionalityTester functionalityTester = new FunctionalityTester(filePath);
        String reasonsString = functionalityTester.testMethod("method5", false, false);
        System.out.println(reasonsString);
    }

    @Test
    public void test_Method_RefferingFinalPrimitiveFields() throws Exception {
        FunctionalityTester functionalityTester = new FunctionalityTester(filePath);
        String reasonsString = functionalityTester.testMethod("method6");
        System.out.println(reasonsString);
    }

    @Test
    public void test_Method_RefferingFinalNonPrimitiveFields() throws Exception {
        FunctionalityTester functionalityTester = new FunctionalityTester(filePath);
        String reasonsString = functionalityTester.testMethod("method7");
        System.out.println(reasonsString);
    }

    @Test
    public void test_Method_RefferingFields() throws Exception {
        FunctionalityTester functionalityTester = new FunctionalityTester(filePath);
        String reasonsString = functionalityTester.testMethod("method8");
        System.out.println(reasonsString);
    }
}
