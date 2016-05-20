package com.android.mobile.mywealth.asynctask.asynctaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public interface PipeLine {
    /**
     * Set Executor
     *
     * @param executor Executor
     */
    void setExecutor(Executor executor);

    /**
     * Add a task into the StandardPipeline.
     * @param task          The Task.
     * @param threadName    ThreadName.
     */
    void addTask(Runnable task, String threadName);

    /**
     * Add a task into the StandardPipeline.
     * @param task          The Task.
     * @param threadName    ThreadName.
     * @param wight         The task's wight
     */
    void addTask(Runnable task, String threadName, int wight);

    /**
     * Start to execute
     */
    void start();

    /**
     * Stop to execute
     */
    public void stop();
}
