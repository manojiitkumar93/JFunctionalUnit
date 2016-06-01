package com.jfunc.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jfunc.core.JFuncWorkerThread;
import com.jfunc.model.JFuncExecutor;
import com.jfunc.validator.JfuncConstants;

public class JFuncExecutorImpl implements JFuncExecutor {
    private static JFuncExecutorImpl instance = new JFuncExecutorImpl();
    private ExecutorService jFuncExecutorService;

    public static JFuncExecutorImpl getInstnace() {
        return instance;
    }

    @Override
    public void startService() {
        jFuncExecutorService = Executors.newFixedThreadPool(JfuncConstants.THREADS);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (int i = 0; i < JfuncConstants.THREADS; i++) {
            futures.add(jFuncExecutorService.submit(new JFuncWorkerThread(JFuncQueueImpl.getInstance())));
        }
        // this makes sure any method which calls this method will wait till all the tasks in the queue
        // are finished
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
