package com.android.mobile.mywealth.asynctask.asynctaskExecutor;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class StandardPipeline implements PipeLine{
    /**
     * ����ִ�еĻص��ӿ�
     */
    public interface IScheduleNext {
        /**
         * ����ִ�еĻص�����
         */
        void scheduleNext();
    }

    /**
     * Log TAG
     */
    static final String TAG = "StandardPipeline";

    /**
     * ����ִ�еĻ������
     */
    protected ArrayList<NamedRunnable> mTasks = new ArrayList<NamedRunnable>();

    /**
     * ����ִ�еĻص��ӿ�
     */
    final IScheduleNext next = new IScheduleNext() {
        @Override
        public void scheduleNext() {
            Log.v(TAG, "StandardPipeline.scheduleNext()");
            if (StandardPipeline.this.mIsStart) {
                StandardPipeline.this.executeNext();
            }
        }
    };

    /**
     * ��ǰ����ִ�е�AsyncTask����
     */
    volatile NamedRunnable mActive;

    protected volatile boolean mIsStart = false;

    private Executor mExecutor;

    public StandardPipeline() {
        this(null);
    }
    public StandardPipeline(Executor executor) {
        mExecutor = executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        mExecutor = executor;
    }

    @Override
    public void addTask(Runnable task, String threadName) {
        addTask(task, threadName, 0);
    }

    @Override
    public void addTask(Runnable task, String threadName, int weight) {
        NamedRunnable _task = NamedRunnable.TASK_POOL.obtain(task, threadName, weight);
        addTask(_task);
    }

    public void addTask(final NamedRunnable task) {
        Log.v(TAG, "StandardPipeline.addTask()");
        if (null == mTasks) {
            throw new RuntimeException("The StandardPipeline has already stopped.");
        } else {
            task.setScheduleNext(next);
            synchronized (mTasks) {
                int index = 0;
                if (!mTasks.isEmpty()) {
                    for (index = mTasks.size() - 1; index >= 0; index--) {
                        if (task.mWeight <= mTasks.get(index).mWeight) {
                            index += 1;
                            break;
                        }
                    }
                    index = index < 0 ? 0 : index;
                }
                mTasks.add(index, task);
            }
        }
        if (mIsStart) {
            doStart();
        }
    }

    @Override
    public void start() {
        Log.v(TAG, "StandardPipeline.start()");
        if (null == mExecutor) {
            throw new RuntimeException("StandardPipeline start failed : The StandardPipeline's Execturo is null.");
        }
        mIsStart = true;
        doStart();
    }

    /**
     *
     */
    protected void doStart() {
        if (mActive == null) {
            executeNext();
        } else {
            Log.v(TAG, "StandardPipeline.start(a task is running, so don't call scheduleNext())");
        }
    }

    /**
     * ִ����һ��AsyncTask
     */
    private void executeNext() {
        synchronized (mTasks) {
            if (!mTasks.isEmpty()) {
                mActive = mTasks.remove(0);
            } else {
                mActive = null;
                Log.v(TAG, "mTasks is empty.");
            }
        }
        if (mActive != null) {
            Log.d(TAG, "StandardPipeline.scheduleNext()");
            if (null != mExecutor) {
                mExecutor.execute(mActive);
            } else {
                throw new RuntimeException("The StandardPipeline's Executor is null.");
            }
        } else {
            Log.d(TAG, "StandardPipeline.scheduleNext(mTasks is empty)");
        }
    }

    /**
     * �ر�ִ����
     */
    @Override
    public void stop() {
        mIsStart = false;
    }
}
