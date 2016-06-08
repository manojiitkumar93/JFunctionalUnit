package com.jfunc.model;

public interface JFuncQueue {

    /**
     * Adds object to the queue.
     * 
     */
    public void enqueue(Object object);

    /**
     * returns the object from the queue by dequing the queue.
     * 
     */
    public Object dequeue();

    /**
     * Checks whether queue is empty.
     * 
     * @return boolean
     */
    public boolean isEmpty();

}
