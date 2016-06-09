package com.jfunc.core;

import com.jfunc.model.impl.JFuncQueueImpl;
import com.jfunc.validator.RequirementsWrapper;
import com.jfunc.validator.ValidatorUtil;

public class JFuncWorkerThread implements Runnable {

    private JFuncQueueImpl queue;

    public JFuncWorkerThread(JFuncQueueImpl queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            RequirementsWrapper requirementsWrapper = (RequirementsWrapper) queue.dequeue();
            ValidatorUtil.validate(requirementsWrapper.getClassMetaData(), requirementsWrapper.skipLogStatements(),
                    requirementsWrapper.skipPrintStatements());
        }
    }

}
