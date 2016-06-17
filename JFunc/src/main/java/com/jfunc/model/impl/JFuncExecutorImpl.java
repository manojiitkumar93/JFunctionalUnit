package com.jfunc.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jfunc.core.JFuncWorkerThread;
import com.jfunc.core.NonFunctionalityReason;
import com.jfunc.validator.JfuncConstants;

public class JFuncExecutorImpl {
    private static JFuncExecutorImpl instance = new JFuncExecutorImpl();
    private ExecutorService jFuncExecutorService;

    public static JFuncExecutorImpl getInstnace() {
        return instance;
    }

    public void startService(JFuncQueueImpl queue, NonFunctionalityReason nonFunctionalityReasonInstance) {
        jFuncExecutorService = Executors.newFixedThreadPool(JfuncConstants.THREADS);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        int iterationCount = (JfuncConstants.THREADS >= queue.size()) ? queue.size() : JfuncConstants.THREADS;
        for (int i = 0; i < iterationCount; i++) {
            futures.add(jFuncExecutorService
                    .submit(new JFuncWorkerThread(JFuncQueueImpl.getInstance(), nonFunctionalityReasonInstance)));
        }
        // this makes sure any method which calls this method will wait till all the tasks in the
        // queue
        // are finished
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
