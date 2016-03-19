package functionalExamples;

public class Example5 {

    private boolean isEven;
    private int number;

    public Example5(int number) {
        init(number);
    }

    // as this method is used to set the global variables only once can it be called as a function?
    private void init(int number) {
        if (number % 2 == 0) {
            isEven = true;
        }
        this.number = number;
    }

    public int getNumber() {
        return this.number;
    }

    public boolean isEven() {
        return this.isEven;
    }

}
