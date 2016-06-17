package com.jfunc.validator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class Example1 {

    private static Logger logger = Logger.getLogger("Logger");
    private final int x = 10;

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
        System.out.println(example1(input));
        return input;
    }

    public int method4(int input) {
        return privateMethod1(input);
    }

    public int method5(int input) {
        //method1(input);
        method2(input);
        Example3.print(input);
        Example2.method(input);
        return input;
    }

    private int privateMethod1(int input) {
        return input;
    }

    public int example(int input, List<String> person1) throws IOException {
        Example3.print(input);
        System.out.println(example1(input));
        int p = 10 + x;
        Person person = new Person(input, "soe");
        incrementAge(person, p);
        return input;
    }

    private void incrementAge(Person person, int input) {
        person.setAge(person.getAge() + input);
    }

    public int example1(int input) {
        System.out.println(input);
        // list.add("name");
        // Person person = new Person(input, "name");
        // person.setAge(person.getAge() + input);
        return input;
    }


}


class Person {

    private int age;
    private String name;

    public Person(int age, String name) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return this.age;
    }

    public String getName() {
        return this.name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

}

