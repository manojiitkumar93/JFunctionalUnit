package com.jfunc.model.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfunc.model.JFuncQueue;
import com.jfunc.validator.RequirementsWrapper;

public class JFuncQueueImpl implements JFuncQueue {

    private static JFuncQueueImpl instance = new JFuncQueueImpl();
    private BlockingQueue<RequirementsWrapper> functionalityTesterQueue =
            new LinkedBlockingQueue<RequirementsWrapper>();

    public static JFuncQueueImpl getInstance() {
        return instance;
    }

    @Override
    public void enqueue(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Null object cannot be added");
        }
        RequirementsWrapper tester = (RequirementsWrapper) object;
        functionalityTesterQueue.add(tester);
    }

    @Override
    public Object dequeue() {
        RequirementsWrapper tester = null;
        try {
            tester = functionalityTesterQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tester;
    }

    @Override
    public boolean isEmpty() {
        return functionalityTesterQueue.isEmpty();
    }

    public int size() {
        return functionalityTesterQueue.size();
    }

}
