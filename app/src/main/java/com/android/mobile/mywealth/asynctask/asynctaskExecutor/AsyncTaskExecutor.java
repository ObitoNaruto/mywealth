package com.android.mobile.mywealth.asynctask.asynctaskExecutor;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ���߳��������ִ����
 * Created by xinming.xxm on 2016/5/6.
 */
public class AsyncTaskExecutor {

    public static final String TAG = AsyncTaskExecutor.class.getSimpleName();

    /**
     *  -------------------�̳߳�����-----------------
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();//����������
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;//�̴߳��̸߳���
    // private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 3 + 1;

    /**
     * �̳߳ع�����
     */
    private static final ThreadFactory THREADFACTORY = new ThreadFactory() {
        /**
         * �̳߳ع�����������������Ϊ�߳�����
         */
        private final AtomicInteger mCount = new AtomicInteger(0);

        /**
         * �����߳�
         */
        @Override
        public Thread newThread(Runnable r) {
            String name = "AsyncTaskExecutor" + "_thread_" + mCount.incrementAndGet();
            Log.w(TAG, "ThreadFactory.newThread(" + name + ")");
            Thread thread = new Thread(r, name);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    };

    /**
     * ����ִ�������Ե�����
     */
    final TransactionExecutor TRANSACTION_EXECUTOR = new TransactionExecutor();

    /**
     * ����ִ�У������Լ����̳߳أ�����execute(..)����ִ�е�������첽�߼���
     */
    final ThreadPoolExecutor PARALLEL_EXECUTOR =
            (ThreadPoolExecutor) Executors.newCachedThreadPool(THREADFACTORY);

    /**
     * ����ִ�У���ֻ����executeSerially(..)����ִ�е�����Ĵ����߼�����û���Լ����̳߳أ����ǹ�����SCHEDULED_EXECUTOR���̳߳ء�
     */
    final SerialExecutor SERIAL_EXECUTOR = new SerialExecutor(PARALLEL_EXECUTOR);

    /**
     * Scheduleִ�У������Լ����̳߳أ�����schedule(..),scheduleAtFixedRate(..),scheduleWithFixedDelay(..)����ִ�е�������첽�߼���
     */
    final ScheduledThreadPoolExecutor SCHEDULED_EXECUTOR =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(CORE_POOL_SIZE, THREADFACTORY);

    /**
     * ����
     */
    public static AsyncTaskExecutor INSTANCE = new AsyncTaskExecutor();

    /**
     * ���췽��������ģʽ�����Է�������Ϊprivate
     */
    private AsyncTaskExecutor() {
        // SCHEDULED_EXECUTOR.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        SCHEDULED_EXECUTOR.setKeepAliveTime(60L, TimeUnit.SECONDS);
        SCHEDULED_EXECUTOR.allowCoreThreadTimeOut(true);
        SCHEDULED_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        PARALLEL_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * ����ģʽ��ȡ��ǰ����
     *
     * @return  AsyncTaskExecutor
     */
    public static AsyncTaskExecutor getInstance() {
        return INSTANCE;
    }

    public final Executor getExecutor() {
        return PARALLEL_EXECUTOR;
    }

    /**
     * ��ʹ��<br>
     * executeSerially(Runnable task, String threadName)
     *
     * @param task
     */
    @Deprecated
    public void executeSerially(Runnable task) {
        executeSerially(task, "");
    }

    public void executeSerially(Runnable task, String threadName) {
        Log.v(TAG, "AsyncTaskExecutor.executeSerially(Runnable, threadName=" + threadName + ")");
        SERIAL_EXECUTOR.execute(NamedRunnable.TASK_POOL.obtain(task, threadName));
    }

    /**
     * ��ʹ��<br>
     * execute(Runnable task, String threadName)
     *
     * @param task
     */
    @Deprecated
    public void execute(Runnable task) {
        execute(task, "");
    }

    public void execute(Runnable task, String threadName) {
        Log.v(TAG, "AsyncTaskExecutor.execute(Runnable, threadName=" + threadName + ")");
        PARALLEL_EXECUTOR.execute(NamedRunnable.TASK_POOL.obtain(task, threadName));
    }

    /**
     * ��ʹ��<br>
     * schedule(Runnable task, String threadName, long delay, TimeUnit unit)
     *
     * @param task
     */
    @Deprecated
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return schedule(task, "", delay, unit);
    }

    public ScheduledFuture<?> schedule(Runnable task, String threadName, long delay, TimeUnit unit) {
        Log.v(TAG, "AsyncTaskExecutor.schedule(Runnable, threadName=" + threadName + ")");
        return SCHEDULED_EXECUTOR.schedule(NamedRunnable.TASK_POOL.obtain(task, threadName), delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        Log.v(TAG, "AsyncTaskExecutor.scheduleAtFixedRate(Runnable)");
        return SCHEDULED_EXECUTOR.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        return SCHEDULED_EXECUTOR.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    public void shutdown() {
        TRANSACTION_EXECUTOR.shutdown();
        SERIAL_EXECUTOR.shutdown();
        PARALLEL_EXECUTOR.shutdown();
        SCHEDULED_EXECUTOR.shutdown();
    }

    public String addTransaction(Transaction tr) {
        return TRANSACTION_EXECUTOR.addTransaction(tr);
    }

    public void removeTransaction(String id) {
        TRANSACTION_EXECUTOR.removeTransaction(id);
    }
}
