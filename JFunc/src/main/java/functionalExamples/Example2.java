package functionalExamples;

import java.util.HashMap;
import java.util.Map;


public class Example2 {
    private Counter counter = new Counter();

    // This method just accessing counter object but does not modifying it, but its return value
    // depends on the count of the input user which may be changed by some other method
    // [changeUserCounter]. Can it be a function?
    public String example(String user) {
        int count = counter.getCount(user);
        StringBuilder sb = new StringBuilder();
        sb.append(user).append("=").append(count);
        return sb.toString();
    }

    public void changeUserCount(String user) {
        counter.decrement(user);
    }
}


class Counter {

    private Map<String, Integer> userCount = new HashMap<String, Integer>();

    public void increment(String user) {
        if (userCount.containsKey(user)) {
            userCount.put(user, userCount.get(user) + 1);
        }
    }

    public void decrement(String user) {
        if (userCount.containsKey(user)) {
            userCount.put(user, userCount.get(user) - 1);
        }
    }

    public int getCount(String user) {
        return this.userCount.get(user);
    }

    public void addUser(String user) {
        if (!userCount.containsKey(user)) {
            userCount.put(user, 0);
        }
    }

    public void removeUser(String user) {
        if (userCount.containsKey(user)) {
            userCount.remove(user);
        }
    }

}

