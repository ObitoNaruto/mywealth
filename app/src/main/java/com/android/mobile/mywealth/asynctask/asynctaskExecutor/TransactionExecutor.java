package com.android.mobile.mywealth.asynctask.asynctaskExecutor;

import android.util.Log;

import java.util.ArrayDeque;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class TransactionExecutor {

    static final String TAG = TransactionExecutor.class.getSimpleName();

    final ArrayDeque<Transaction> mTransactions = new ArrayDeque<Transaction>();

    /**
     * 当前正在执行的AsyncTask任务
     */
    volatile Transaction mActive;

    /**
     * 添加一个任务到Tape执行器中。Tape执行器将按照添加的先后顺序执行任务。
     *
     * @param transaction
     * @return 任务的ID
     */
    public String addTransaction(Transaction transaction) {
        synchronized (mTransactions) {
            mTransactions.offer(transaction);
        }
        if (mActive == null) {
            scheduleNext();
        } else {
            Log.v(TAG, "TransactionExecutor.execute(a transaction is running, so don't call scheduleNext())");
        }
        return transaction.id;
    }

    /**
     *
     *
     * @param id
     */
    public void removeTransaction(String id) {
        for (Transaction transaction : mTransactions) {
            if (transaction.id.equals(id)) {
                synchronized (mTransactions) {
                    mTransactions.remove(transaction);
                }
                break;
            }
        }
        if (null != mActive && mActive.id.equals(id)) {
            mActive = null;
        }
        if (mActive == null) {
            scheduleNext();
        } else {
            Log.v(TAG, "TransactionExecutor.execute(a transaction is running, so don't call scheduleNext())");
        }
    }

    /**
     * 执行下一个AsyncTask
     */
    private void scheduleNext() {
        Transaction transaction;
        synchronized (mTransactions) {
            mActive = mTransactions.poll();
            transaction = mActive;
        }
        if (mActive != null) {
            Log.d(TAG, "TransactionExecutor.scheduleNext()");
            transaction.run();
        } else {
            Log.d(TAG, "TransactionExecutor.scheduleNext(mTransactions is empty)");
        }
    }

    /**
     * 关闭执行器
     */
    public void shutdown() {
        mTransactions.clear();
    }
}
