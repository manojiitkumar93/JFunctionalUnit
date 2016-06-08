package com.jfunc.model.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfunc.asm.MethodMetaData;
import com.jfunc.model.JFuncQueue;

public class JFuncQueueImpl implements JFuncQueue {

    private static JFuncQueueImpl instance = new JFuncQueueImpl();
    private BlockingQueue<MethodMetaData> functionalityTesterQueue = new LinkedBlockingQueue<MethodMetaData>();

    public static JFuncQueueImpl getInstance() {
        return instance;
    }

    @Override
    public void enqueue(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Null object cannot be added");
        }
        MethodMetaData tester = (MethodMetaData) object;
        functionalityTesterQueue.add(tester);
    }

    @Override
    public Object dequeue() {
        MethodMetaData tester = null;
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

}
