package com.jfunc.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Example1 {

    private static Logger logger = Logger.getLogger("Logger");
    private final int x = 10;
    private final List<Integer> listOfNumbers = new ArrayList<>();
    private int y = 10;

    public int method1(int input) {
        System.out.println(input);
        logger.info("input " + input);
        return input;
    }

    public int method2(int input) {
        method1(input);
        return input;
    }

    public int method3(int input) {
        Example3.print(input);
        System.out.println(privateMethod2(input));
        return input;
    }

    public int method4(int input) {
        return privateMethod1(input);
    }

    public int method5(int input) {
        // method1(input);
        method2(input);
        Example3.print(input);
        Example2.method(input);
        return input;
    }

    public int method6(int input) {
        input = input + x;
        return input;

    }

    public int method7(int input) {
        listOfNumbers.add(input);
        return input;
    }

    public int method8(int input) {
        input = input + y;
        return input;
    }

    public int method9() {
        int input = y;
        return input;
    }

    public void method10(int input) {
        if (input > 0) {
            input = input - 1;
            method10(input);
        } else
            System.out.println(input);
    }

    private int privateMethod1(int input) {
        return input;
    }

    private int privateMethod2(int input) {
        System.out.println(input);
        return input;
    }
}
