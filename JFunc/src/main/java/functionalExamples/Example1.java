package functionalExamples;


public class Example1 {

    // This method it internally calls another void method, so can it be called as a function? If we
    // say void method is not a function then function which is calling a void method ?
    public Person example(int input) {
        Person person = new Person(input, "name");
        incrementAge(person, 12);
        return person;
    }

    private void incrementAge(Person person, int input) {
        person.setAge(person.getAge() + input);
    }
 
    // This is method is similar to above method "exmaple", as it follows all the rules to be called
    // as a function, so should "example" is also a function? Reason for this example is we split
    // one method into multiple methods for readability of the code..
    public Person example1(int input) {
        Person person = new Person(input, "name");
        person.setAge(person.getAge() + input);
        return person;
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

