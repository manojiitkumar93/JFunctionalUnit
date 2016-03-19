package functionalExamples;

import java.util.HashMap;
import java.util.Map;

public class Example4 {

    private final Map<String, Cache> map = new HashMap<String, Cache>();

    // This method access the immutable map and adds elements to it. Can it be called a function?
    public Cache getCache(String cacheName, Configuration config) {
        Cache cache = map.get(cacheName);
        if (cache == null) {
            if (config == null) {
                cache = new Cache(cacheName, new Configuration());
            }
            map.put(cacheName, cache);
        }
        return cache;
    }
}


class Cache {
    private String cacheName;
    private Configuration config;

    public Cache(String cacheName, Configuration config) {
        this.cacheName = cacheName;
        this.config = config;
    }

    public String getName() {
        return this.cacheName;
    }

    public Configuration getConfiguration() {
        return this.config;
    }
}


class Configuration {
    private String evictionPolicy;
    private int max_elements;

    private final String defaultEvictionPolicy = "LRU";
    private final int defaultMaxElementsToBeStored = 100;

    public Configuration() {
        new Configuration(defaultEvictionPolicy, defaultMaxElementsToBeStored);
    }

    public Configuration(String evictionPolicy, int max_elements) {
        this.evictionPolicy = evictionPolicy;
        this.max_elements = max_elements;
    }

    public String getEvictionPolicy() {
        return this.evictionPolicy;
    }

    public int getMaxElementsCanBeStored() {
        return this.max_elements;
    }

}
