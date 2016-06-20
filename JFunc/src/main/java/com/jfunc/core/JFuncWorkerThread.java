package com.jfunc.core;

import com.jfunc.model.impl.JFuncQueueImpl;
import com.jfunc.validator.RequirementsWrapper;
import com.jfunc.validator.ValidatorUtil;

public class JFuncWorkerThread implements Runnable {

    private JFuncQueueImpl queue;
    private NonFunctionalityReason nonFunctionalityReason;

    public JFuncWorkerThread(JFuncQueueImpl queue,NonFunctionalityReason nonFunctionalityReasonInstance) {
        this.queue = queue;
        this.nonFunctionalityReason = nonFunctionalityReasonInstance;
    }

    @Override
    public void run() {
        while (!queue.isEmpty()) {
            RequirementsWrapper requirementsWrapper = (RequirementsWrapper) queue.dequeue();
            ValidatorUtil.validate(requirementsWrapper.getClassMetaData(),nonFunctionalityReason, requirementsWrapper.skipLogStatements(),
                    requirementsWrapper.skipPrintStatements());
        }
    }

}
