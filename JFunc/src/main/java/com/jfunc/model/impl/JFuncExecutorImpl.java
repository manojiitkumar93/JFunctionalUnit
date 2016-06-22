package com.jfunc.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jfunc.core.JFuncWorkerThread;
import com.jfunc.core.NonFunctionalityReason;
import com.jfunc.validator.JfuncConstants;
import com.jfunc.validator.RequirementsWrapper;

public class JFuncExecutorImpl {
    private static JFuncExecutorImpl instance = new JFuncExecutorImpl();
    private ExecutorService jFuncExecutorService;

    public static JFuncExecutorImpl getInstnace() {
        return instance;
    }

    public void startService(JFuncQueueImpl queue, NonFunctionalityReason nonFunctionalityReasonInstance) {
        jFuncExecutorService = Executors.newFixedThreadPool(JfuncConstants.THREADS);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int i = 0; i < queue.size(); i++) {
            RequirementsWrapper requirementsWrapper = (RequirementsWrapper) queue.dequeue();
            futures.add(jFuncExecutorService
                    .submit(new JFuncWorkerThread(requirementsWrapper, nonFunctionalityReasonInstance)));
        }

        // this makes sure any method which calls this method will wait till all the tasks in the
        // queue are finished
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jFuncExecutorService.shutdown();
    }


}
