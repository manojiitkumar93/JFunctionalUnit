package com.jfunc.validator;

import java.io.IOException;
import java.util.List;

public class Example1 {

    private final int x = 10;

    public Person example(int input, List<String> person1) throws IOException {
        example1(input);
        System.out.println(input);
        int p = 10 + x;
        Person person = new Person(input, "soe");
        incrementAge(person, p);
        return person;
    }

    private void incrementAge(Person person, int input) {
        person.setAge(person.getAge() + input);
    }

    public int example1(int input) {
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

