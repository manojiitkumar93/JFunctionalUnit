package com.jfunc.model.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        for (int i = 0; i < JfuncConstants.THREADS; i++) {
            jFuncExecutorService.execute(new JFuncWorkerThread(JFuncQueueImpl.getInstance()));
        }
    }


}
