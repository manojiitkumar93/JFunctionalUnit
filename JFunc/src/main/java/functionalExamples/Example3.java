package functionalExamples;

public class Example3 {
    private String name = null;
    private String country = null;
    private int age = -1;

    public Example3() {}

    public Example3(String name, int age, String country) {
        this.name = name;
        this.age = age;
        this.country = country;
    }

    // As this class is immutable object, so can all the get methods can be called as functions? If
    // we say they are functions but they violates the rule of function should take an input....

    public int getAge() {
        return this.age;
    }

    public String getCountry() {
        return this.country;
    }

    public String getName() {
        return this.name;
    }
}
