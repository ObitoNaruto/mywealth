package com.android.mobile.mywealth.asynctask.asynctaskExecutor;

import android.util.Log;

import java.util.concurrent.Executor;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
//����ִ�У���ֻ����executeSerially(..)����ִ�е�����Ĵ����߼�����û���Լ����̳߳أ�
//* ���ǹ�����{@link com.android.mobile.mywealth.asynctask.asynctaskExecutor.AsyncTaskExecutor.SCHEDULED_EXECUTOR}���̳߳ء�
public class SerialExecutor extends StandardPipeline{
    static final String TAG = "SerialExecutor";

    public SerialExecutor(Executor executor) {
        super(executor);
    }

    /**
     * ִ�к���
     *
     * @param task  AsyncTask
     */
    public void execute(final NamedRunnable task) {
        Log.v(TAG, "SerialExecutor.execute()");
        addTask(task);
        start();
    }

    /**
     * �ر�ִ����
     */
    public void shutdown() {
        stop();
        mTasks.clear();
        mTasks = null;
    }
}
