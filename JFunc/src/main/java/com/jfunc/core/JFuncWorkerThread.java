package com.jfunc.core;

import com.jfunc.asm.MethodMetaData;
import com.jfunc.model.impl.JFuncQueueImpl;
import com.jfunc.validator.ValidatorUtil;

public class JFuncWorkerThread implements Runnable {

    private JFuncQueueImpl queue;

    public JFuncWorkerThread(JFuncQueueImpl queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            MethodMetaData methodMetaData = (MethodMetaData) queue.dequeue();
            ValidatorUtil.validate(methodMetaData);
        }

    }

}
