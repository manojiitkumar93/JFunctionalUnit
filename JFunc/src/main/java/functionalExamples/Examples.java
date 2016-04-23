package functionalExamples;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Examples {

    private static final Logger logger = Logger.getLogger("Logger");
    private final List<Integer> list = new ArrayList<>();
    private static final int count = 0;

    // void methods are not functions
    public void voidMethod(Object input) {

    }

    // abstract methods are not functions
    public abstract String abstratMethod(Object inout);

    // methods having log and print statements are not functions
    public String logAndPrintStatements(String input) {
        System.out.println(input);
        logger.info("Some log info");
        return input;
    }

    // method accessing global final primitive types are functions
    public int accessingGlobalPrimitiveTypes(int input) {
        input = input + count;
        return input;
    }

    // method accessing even final objects are not functions
    public int accessingGlobalObject(int input) {
        input = input + list.size();
        return input;
    }
    

}
